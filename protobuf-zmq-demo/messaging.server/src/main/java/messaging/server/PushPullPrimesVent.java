package messaging.server;

import static messaging.util.TCPConstants.KILL_SIGNAL;
import static messaging.util.TCPConstants.PUB_SUB_TCP_5559;
import static messaging.util.TCPConstants.PUSH_PULL_TCP_5557;
import static org.zeromq.ZMQ.context;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
import messaging.util.PrimeUtil;
import messaging.util.Properties;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

/**
 * The class PushPullPrimesVent pushes next prime requests to
 * {@code ProtobufPrimesWorker} workers on tcp://localhost:5557 and subscribes
 * to the sink {@code ProtobufPrimesSink} controller on tcp://localhost:5559
 *
 * @author T.Silverman
 *
 */
public class PushPullPrimesVent {

	private static final Logger logger = LoggerFactory.getLogger(PushPullPrimesVent.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private String workersAddress, sinkAddress;
	private final int numOfPrimes;
	private final int numOfDigits;
	private final AtomicInteger count = new AtomicInteger();

	/**
	 * initialize a worker with a name
	 */
	public PushPullPrimesVent(int numOfPrimes, int numOfDigits) {
		super();
		assert numOfPrimes > 0 && numOfDigits > 0 : "numbers of primes and numOfDigits must be greater than zero!";
		assert numOfPrimes > 0 : "numOfPrimes cannot be less than 1";
		assert numOfDigits > 0 : "numOfDigits cannot be less than 1";
		this.numOfPrimes = Math.abs(numOfPrimes);
		this.numOfDigits = Math.abs(numOfDigits);
		this.workersAddress = props.getProperty(PUSH_PULL_TCP_5557, "tcp://localhost:5557");
		this.sinkAddress = props.getProperty(PUB_SUB_TCP_5559, "tcp://localhost:5559");
	}

	public void run() {
		logger.info("\n\nSERVER GENERATING {} CONSECUTIVE PRIMES WITH {} DIGITS\n\n", numOfPrimes, numOfDigits);
		try (Context context = context(1);
				Socket toWorkers = context.socket(SocketType.PUSH);
				Socket controller = context.socket(SocketType.SUB);) {
			toWorkers.bind(workersAddress);
			controller.connect(sinkAddress);
			controller.subscribe(new byte[0]);
			long serverWaitMillis = props.getIntProperty("SERVER_WAIT_MS", 1500);
			logger.info("VENT waiting {}ms for workers to connect...", serverWaitMillis);
			Thread.sleep(serverWaitMillis); // allow workers to connect
			String start = PrimeUtil.randomNum(numOfDigits).toString();
			NextPrimeRequest msg = NextPrimeRequest.newBuilder()
					.setSeq(count.incrementAndGet())
					.setStart(start)
					.build();
			logger.info("VENT sending to workers: {}", JsonFormat.printer().print(msg));
			toWorkers.send(Any.pack(msg).toByteArray(), ZMQ.DONTWAIT);
			while (!shutdown) {
				byte[] data = controller.recv(ZMQ.DONTWAIT); // get bytes from the publisher
				if (null == data) continue;
				Any any = Any.parseFrom(data);
				msg = any.unpack(NextPrimeRequest.class);
				count.incrementAndGet();
				if (count.get() > numOfPrimes) {
					msg = NextPrimeRequest.newBuilder(msg)
							.setSeq(count.get())
							.clearNext()
							.setIsErrors(true)
							.setErrorMessage(KILL_SIGNAL)
							.build();
					shutdown();
				} else
					msg = NextPrimeRequest.newBuilder(msg).clearNext().setSeq(count.get()).build();
				logger.info("VENT sending to workers: {}", JsonFormat.printer().print(msg));
				any = Any.pack(msg);
				toWorkers.send(any.toByteArray(), ZMQ.DONTWAIT);
				Thread.sleep(props.getIntProperty("/*SERVER_WAIT_MS*/", 600));
			}
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	/**
	 * A method to shutdown this subscriber
	 */
	public void shutdown() {
		logger.info("vent is shutting down...");
		this.shutdown = true;
	}

}
