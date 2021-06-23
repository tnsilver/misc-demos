package messaging.server;

import static messaging.util.TCPConstants.*;
import static org.zeromq.ZMQ.context;
import static org.zeromq.ZMQ.getVersionString;

import java.math.BigInteger;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
import messaging.util.PrimeUtil;
import messaging.util.Properties;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.util.JsonFormat;

/**
 * The class RepPrimeServer replies to request from ReqPrimeClient to generate
 * the next prime number
 *
 * @author T.Silverman
 *
 */
public class RepPrimeServer {

	private static final Logger logger = LoggerFactory.getLogger(RepPrimeServer.class);
	private final Properties props = Properties.getInstance();
	private boolean shutdown = false;
	private final String address;
	private final Predicate<NextPrimeRequest> isKillSignal = msg -> msg.getIsErrors()
			&& msg.getErrorMessage().equals(KILL_SIGNAL);

	public RepPrimeServer() {
		super();
		this.address = props.getProperty(REQ_REP_TCP_5556, "tcp://localhost:5556");
	}

	public void run() {
		logger.debug("server starting... current 0MQ version is {}", getVersionString());
		try (Context context = context(1); Socket client = context.socket(SocketType.REP);) {
			client.bind(address);
			while (!shutdown) {
				byte[] data = client.recv(0);
				Any any = Any.parseFrom(data);
				// if (any.is(NextPrimeRequest.class)) {
				NextPrimeRequest msg = any.unpack(NextPrimeRequest.class);
				if (isKillSignal.test(msg)) {
					shutdown();
					break;
				}
				BigInteger start = new BigInteger(msg.getStart());
				logger.debug("SERVER received request #{}: {}", msg.getSeq(), JsonFormat.printer().print(msg));
				BigInteger next = PrimeUtil.nextPrime(start);
				msg = NextPrimeRequest.newBuilder(msg).setNext(next.toString()).build();
				logger.info("SERVER responding #{}: {}", msg.getSeq(), JsonFormat.printer().print(msg));
				any = Any.pack(msg);
				client.send(any.toByteArray(), 0);
				Thread.sleep(props.getIntProperty("SERVER_WAIT_MS", 1000));
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
		logger.info("server shutting down...");
		this.shutdown = true;
	}

}
