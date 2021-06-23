package messaging.server;

import static messaging.protos.util.MessageUtil.defaultAddress;
import static messaging.protos.util.MessageUtil.randomAddress;
import static messaging.protos.util.MessageUtil.randomContact;
import static messaging.protos.util.MessageUtil.randomPerson;
import static org.zeromq.ZMQ.context;
import static org.zeromq.ZMQ.getVersionString;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.util.Properties;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

/**
 * The class ProtobufPublisher is a ZeroMQ publisher which generates random
 * protobuf messages of various types, and publishes them over TCP via an
 * optional number of ZeroMQ sockets.
 *
 * @author T.Silverman
 *
 */
public class ProtobufPublisher {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufPublisher.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private Set<String> bindAddresses;
	private final int numOfMessages;

	/**
	 * initialize a default publisher on {@code tcp://localhost:5556} and
	 * {@code ipc://demo}
	 */
	public ProtobufPublisher(int numOfMessages) {
		super();
		assert numOfMessages > 0 : "number of messages to publish must be greater than zero!";
		this.numOfMessages = numOfMessages;
		this.bindAddresses = Stream
				.of(props.getProperty("TCP_5556", "tcp://localhost:5556"), props.getProperty("IPC_DEMO", "ipc://demo"))
				.collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * The run loop method of this server submits random protobuf messages on a
	 * publish/subscribe model
	 */
	public void run() {
		logger.debug("publisher starting... current 0MQ version is {}", getVersionString());
		int count = 0;
		try (Context context = context(1); Socket publisher = context.socket(SocketType.PUB);) {
			bindAddresses.forEach(publisher::bind);
			logger.info("PUBLISHER WILL PUBLISH {} RANDOM MESSAGES", numOfMessages);
			while (!shutdown && count < numOfMessages) {
				Message proto = getRandomProto();
				logger.info("#{} PUBLISHER publishing: {}",count+1, JsonFormat.printer().print(proto));
				publisher.send(Any.pack(proto).toByteArray());
				Thread.sleep(props.getIntProperty("SERVER_WAIT_MS", 800));
				count++;
			}
			shutdown();
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	public void shutdown() {
		logger.debug("publisher shutting down...");
		this.shutdown = true;
	}

	private Message getRandomProto() {
		int rand = ThreadLocalRandom.current().nextInt(1, 4);
		Message message = defaultAddress();
		switch (rand) {
			case 1:
				message = randomAddress();
				break;
			case 2:
				message = randomContact();
				break;
			case 3:
				message = randomPerson();
				break;
			default:
				break;
		}
		return message;
	}

}
