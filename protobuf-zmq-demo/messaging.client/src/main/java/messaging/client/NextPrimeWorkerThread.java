package messaging.client;

import static messaging.util.TCPConstants.KILL_SIGNAL;
import static org.zeromq.ZMQ.getVersionString;

import java.math.BigInteger;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Error;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
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
public class NextPrimeWorkerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(NextPrimeWorkerThread.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private final String address;
	private final String name;
	private final Context context;
	private final Predicate<NextPrimeRequest> isKillSignal = msg -> msg.getIsErrors()
			&& msg.getErrorMessage().equals(KILL_SIGNAL);

	/**
	 * initialize a worker with a name
	 */
	public NextPrimeWorkerThread(String name, Context context) {
		super();
		this.name = name;
		this.context = context;
		this.address = String.format(props.getProperty("INPROC", "inproc://%s"), "workers");
	}

	/**
	 * This run loop runs in a loop and pulls {@link NextPrimeRequest} messages from
	 * the {@code PushPullPrimesVent}, asking this worker to generate a prime number
	 * that is consecutive to the prime number given in the request. The worker
	 * PUSHes the results to the {@code ProtobufPrimesSink} and also listens for
	 * KILL messages via the controller SUBSCRIPTION to the sink.
	 */
	@Override
	public void run() {
		logger.debug("worker {} starting... 0MQ version is {}", name, getVersionString());
		Socket socket = null;
		try {
			socket = context.socket(SocketType.REP);
			socket.connect(address);
			while (!shutdown && !Thread.currentThread().isInterrupted()) {
				byte[] data = null;
				try {
					data = socket.recv(ZMQ.DONTWAIT);
					if (data != null) {
						Any any = Any.parseFrom(data);
						NextPrimeRequest msg = any.unpack(NextPrimeRequest.class);
						if (isKillSignal.test(msg)) {
							shutdown();
							break;
						}
						BigInteger start = new BigInteger(msg.getStart());
						BigInteger next = PrimeUtil.nextPrime(start);
						msg = NextPrimeRequest.newBuilder(msg).setNext(next.toString()).build();
						logger.info("WORKER-{} sending to sink: {}", name, JsonFormat.printer().print(msg));
						any = Any.pack(msg);
						socket.send(any.toByteArray(), 0);
					}
				} catch (ZMQException zmqex) {
					if (zmqex.getErrorCode() == Error.ETERM.getCode()) {
						shutdown();
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("{}", ex);
		} finally {
			if (socket != null) {
				socket.setLinger(0);
				socket.close();
			}
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
