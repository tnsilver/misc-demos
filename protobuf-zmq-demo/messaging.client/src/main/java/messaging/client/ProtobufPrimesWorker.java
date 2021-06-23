package messaging.client;

import static messaging.util.TCPConstants.KILL_SIGNAL;
import static messaging.util.TCPConstants.PUB_SUB_TCP_5559;
import static messaging.util.TCPConstants.PUSH_PULL_TCP_5557;
import static messaging.util.TCPConstants.PUSH_PULL_TCP_5558;
import static org.zeromq.ZMQ.context;
import static org.zeromq.ZMQ.getVersionString;

import java.math.BigInteger;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

import messaging.util.PrimeUtil;
import messaging.util.Properties;

/**
 * The class ProtobufPrimesWorker runs in a loop and pulls
 * {@link NextPrimeRequest} messages from the {@code PushPullPrimesVent}, asking
 * this worker to generate a prime number that is consecutive to the prime
 * number given in the request. The worker PUSHes the results to the
 * {@code ProtobufPrimesSink} and also listens for KILL messages via the
 * controller SUBSCRIPTION to the sink.
 *
 * @author T.Silverman
 *
 */
public class ProtobufPrimesWorker {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufPrimesWorker.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private String ventAddress, sinkAddress, controlAddress;
	private String name;
	private final Predicate<NextPrimeRequest> isKillSignal = msg -> msg.getIsErrors()
			&& msg.getErrorMessage().equals(KILL_SIGNAL);

	/**
	 * initialize a worker with a name
	 */
	public ProtobufPrimesWorker(String name) {
		super();
		this.name = name;
		this.ventAddress = props.getProperty(PUSH_PULL_TCP_5557, "tcp://localhost:5557");
		this.sinkAddress = props.getProperty(PUSH_PULL_TCP_5558, "tcp://localhost:5558");
		this.controlAddress = props.getProperty(PUB_SUB_TCP_5559, "tcp://localhost:5559");
	}

	/**
	 * This run loop runs in a loop and pulls {@link NextPrimeRequest} messages from
	 * the {@code PushPullPrimesVent}, asking this worker to generate a prime number
	 * that is consecutive to the prime number given in the request. The worker
	 * PUSHes the results to the {@code ProtobufPrimesSink} and also listens for
	 * KILL messages via the controller SUBSCRIPTION to the sink.
	 */
	public void run() {
		logger.debug("worker {} starting... 0MQ version is {}", name, getVersionString());
		try (Context context = context(1);
				Socket fromVent = context.socket(SocketType.PULL);
				Socket toSink = context.socket(SocketType.PUSH);
				Socket controller = context.socket(SocketType.SUB);) {
			fromVent.connect(ventAddress);
			toSink.connect(sinkAddress);
			controller.connect(controlAddress);
			controller.subscribe(ZMQ.SUBSCRIPTION_ALL);
			Poller poller = context.poller(2);
			poller.register(fromVent, Poller.POLLIN); // poll messages from vent
			poller.register(controller, Poller.POLLIN); // poll messages from sink
			while (!shutdown) {
				poller.poll();
				if (poller.pollin(0)) {
					byte[] data = fromVent.recv(ZMQ.DONTWAIT);
					if (data != null) {
						Any any = Any.parseFrom(data);
						// if (any.is(NextPrimeRequest.class)) {
						NextPrimeRequest msg = any.unpack(NextPrimeRequest.class);
						BigInteger start = new BigInteger(msg.getStart());
						BigInteger next = PrimeUtil.nextPrime(start);
						msg = NextPrimeRequest.newBuilder(msg).setNext(next.toString()).build();
						logger.info("WORKER-{} sending to sink: {}", name, JsonFormat.printer().print(msg));
						any = Any.pack(msg);
						toSink.send(any.toByteArray(), 0);
						// }
					}
				}
				if (poller.pollin(1)) { // any message from controller is "KILL"
					byte[] data = controller.recv(ZMQ.DONTWAIT);
					if (data != null) {
						Any any = Any.parseFrom(data);
						// if (any.is(NextPrimeRequest.class)) {
						NextPrimeRequest msg = any.unpack(NextPrimeRequest.class);
						if (isKillSignal.test(msg))
							shutdown();
						// }
					}
				}
			}
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	/**
	 * A method to shutdown this subscriber
	 */
	public void shutdown() {
		logger.info("worker {} is shutting down...", name);
		this.shutdown = true;
	}

}
