package messaging.client;

import static messaging.protos.util.MessageUtil.createErrorMessage;
import static org.zeromq.ZMQ.context;
import static org.zeromq.ZMQ.getVersionString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import messaging.protos.AddressProtos.Address;
import messaging.protos.ContactProtos.Contact;
import messaging.protos.ErrorMessageProtos.ErrorMessage;
import messaging.protos.PersonProtos.Person;
import messaging.util.Properties;

/**
 * The class ProtobufSubscriber is a ZeroMQ subscriber which accepts random
 * protobuf messages of various types from a TCP ZeroMQ socket, and pretty
 * prints them as JSON.
 *
 * @author T.Silverman
 *
 */
public class ProtobufSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufSubscriber.class);
	private static final List<Class<? extends Message>> acceptedClasses = Arrays
			.asList(Address.class, Contact.class, Person.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private Set<String> connAddresses;
	private final int numOfMessages;
	private final String name;

	/**
	 * Initialize a default subscriber on {@code tcp://localhost:5556}
	 */
	public ProtobufSubscriber(String name, int numOfMessages) {
		super();
		assert numOfMessages > 0 : "number of messages to publish must be greater than zero!";
		this.numOfMessages = numOfMessages;
		this.name = name;
		this.connAddresses = Stream.of(props.getProperty("TCP_5556", "tcp://localhost:5556"))
				.collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * the run loop of this subscriber listens for published protocol buffer
	 * messages and prints them as json
	 */
	public void run() {
		logger.debug("subscriber starting... current 0MQ version is {}", getVersionString());
		try (Context context = context(1); Socket subscriber = context.socket(SocketType.SUB);) {
			connAddresses.forEach(subscriber::connect);
			logger.info("SUBSCRIBER-{} IS EXPECTED TO RECEIVE {} MESSAGES", name, numOfMessages);
			subscriber.subscribe(new byte[0]);
			int count = 0;
			while (!shutdown && count < numOfMessages) {
				try {
					byte[] data = subscriber.recv(ZMQ.DONTWAIT); // don't wait, but get data from publisher
					if (data != null) {
						Message message = unpack(data);
						logger.info(
								"#{} SUBSCRIBER-{} Received: {}",
									count + 1,
									name,
									JsonFormat.printer().print(message));
						count++;
					}
				} catch (Exception ex) {
					logger.error("{}", ex);
				}
			}
			shutdown();
		}
	}

	/**
	 * checks if the message in {@code any} can be handled
	 *
	 * @param any the message received
	 * @return true is the class {@code any} represents, can be handled by this
	 *         subscriber
	 */
	private boolean canHandle(Any any) {
		return null != any && acceptedClasses.stream().anyMatch(any::is);
	}

	/**
	 * retrieves the class packed in the given {@code any} message or
	 * {@link ErrorMessage} class if {@code any} doesn't contain any of the
	 * {@link #acceptedClasses}
	 *
	 * @param any the received message
	 * @return the class in the given {@code any} message or {@link ErrorMessage}
	 *         class if {@code any} doesn't contain any of the
	 *         {@link #acceptedClasses}
	 */
	private Class<? extends Message> getPackedClass(Any any) {
		return canHandle(any) ? acceptedClasses.stream().filter(any::is).findAny().orElse(ErrorMessage.class)
				: ErrorMessage.class;
	}

	/**
	 * A method to unpack the received bytes to a {@link Message} object
	 *
	 * @param data the bytes array
	 * @return a {@link Message} object or {@link ErrorMessage} if the {@code data}
	 *         cannot be unpacked
	 */
	private Message unpack(byte[] data) {
		try {
			logger.debug("received {} of data bytes", data.length);
			Any any = Any.parseFrom(data);
			Class<? extends Message> packed = getPackedClass(any);
			return packed != ErrorMessage.class ? any.unpack(packed) : createErrorMessage("", "");
		} catch (Exception ex) {
			logger.error("{}", ex);
			return createErrorMessage(ex.getMessage(), ex.getClass().getName());
		}
	}

	/**
	 * A method to shutdown this subscriber
	 */
	public void shutdown() {
		logger.debug("subscriber shutting down...");
		this.shutdown = true;
	}

}
