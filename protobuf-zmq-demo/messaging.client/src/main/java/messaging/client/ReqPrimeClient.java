package messaging.client;

import static messaging.util.TCPConstants.KILL_SIGNAL;
import static messaging.util.TCPConstants.REQ_REP_TCP_5556;
import static org.zeromq.ZMQ.context;
import static org.zeromq.ZMQ.getVersionString;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

import messaging.util.PrimeUtil;
import messaging.util.Properties;

/**
 * The class ReqPrimeClient request the next prime number from the
 * RepPrimeServer in a REQ/REP model
 *
 * @author T.Silverman
 *
 */
public class ReqPrimeClient {

	private static final Logger logger = LoggerFactory.getLogger(ReqPrimeClient.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private final String address;
	private final int numOfDigits = 50, numOfPrimes;
	private final AtomicInteger seq = new AtomicInteger();

	public ReqPrimeClient(int numOfPrimes) {
		assert numOfPrimes > 0 : "number of primes to request must be greater than zero!";
		this.numOfPrimes = numOfPrimes;
		this.address = props.getProperty(REQ_REP_TCP_5556, "tcp://localhost:5556");
	}

	public void run() {
		logger.debug("client starting... current 0MQ version is {}", getVersionString());
		try (Context context = context(1); Socket server = context.socket(SocketType.REQ);) {
			server.connect(address);
			String start = PrimeUtil.randomNum(numOfDigits).toString();
			NextPrimeRequest msg = NextPrimeRequest.newBuilder().setSeq(0).build();
			while (!shutdown) {
				// send
				boolean last = (seq.incrementAndGet() >= numOfPrimes);
				msg = NextPrimeRequest.newBuilder(msg)
						.clearNext()
						.setSeq(seq.get())
						.setStart(start)
						.setIsErrors(last)
						.setErrorMessage(last ? KILL_SIGNAL : "")
							.build();
				Any any = Any.pack(msg);
				logger.info("CLIENT requesting #{}: {}", msg.getSeq(), JsonFormat.printer().print(msg));
				server.send(any.toByteArray());
				if (last) {
					shutdown();
					break;
				}
				// receive
				byte[] data = server.recv(0);
				any = Any.parseFrom(data);
				// if (any.is(NextPrimeRequest.class)) {
				msg = any.unpack(NextPrimeRequest.class);
				start = msg.getNext();
				logger.debug("CLIENT received response #{}: {}", msg.getSeq(), JsonFormat.printer().print(msg));
				// }
			}
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
	}

	/**
	 * A method to shutdown this subscriber
	 */
	public void shutdown() {
		logger.info("client shutting down...");
		this.shutdown = true;
	}

}
