package messaging.broker;

import static messaging.util.TCPConstants.KILL_SIGNAL;
import static messaging.util.TCPConstants.PUB_SUB_TCP_5559;
import static messaging.util.TCPConstants.PUSH_PULL_TCP_5558;
import static org.zeromq.ZMQ.getVersionString;


import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.protos.NextPrimeRequestProtos.NextPrimeRequest;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import messaging.util.Properties;

/**
 * The class ProtobufPrimesSink is a sink that is able to collect
 * {@link NextPrimeRequest} results from {@code ProtobufPrimesWorker} workers
 * and is able to signal the workers to quit their work. The sink also reports
 * back to the {@code PushPullPrimesVent} via a subscription about progress and
 * kill commands.
 *
 * @author T.Silverman
 *
 */
public class ProtobufPrimesSink {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufPrimesSink.class);
	private final Properties props;
	private String workersAddress, controlAddress;
	private Map<Integer, BigInteger> cache;
	private AtomicInteger seq;
	private boolean shutdown = false;
	private final Predicate<NextPrimeRequest> isKillSignal = msg -> msg.getIsErrors()
			&& msg.getErrorMessage().equals(KILL_SIGNAL);

	public ProtobufPrimesSink() {
		super();
		props = Properties.getInstance();
		seq = new AtomicInteger();
		cache = new LinkedHashMap<>();
		this.workersAddress = props.getProperty(PUSH_PULL_TCP_5558, "tcp://localhost:5558");
		this.controlAddress = props.getProperty(PUB_SUB_TCP_5559, "tcp://localhost:5559");
	}

	public void run() {
		logger.debug("sink starting... current 0MQ version is {}", getVersionString());
		try (Context context = ZMQ.context(1);
				Socket fromWorkers = context.socket(SocketType.PULL);
				Socket controller = context.socket(SocketType.PUB);) {
			fromWorkers.bind(workersAddress);
			controller.bind(controlAddress);
			while (!shutdown) {
				byte[] data = fromWorkers.recv(ZMQ.DONTWAIT);
				if (null != data)
					try {
						Any any = Any.parseFrom(data);
						//if (!any.is(NextPrimeRequest.class)) continue;
						NextPrimeRequest msg = any.unpack(NextPrimeRequest.class);
						if (isKillSignal.test(msg))
							shutdown();
						else
							cache.put(seq.incrementAndGet(), new BigInteger(msg.getNext()));
						msg = NextPrimeRequest.newBuilder(msg).clearNext().setSeq(msg.getSeq()).setStart(msg.getNext()).build();
						logger.info("SINK publishing {}", JsonFormat.printer().print(msg));
						any = Any.pack(msg);
						controller.send(any.toByteArray(), 0); // publish request
					} catch (InvalidProtocolBufferException ex) {
						logger.error("{}", ex);
					}
			}
			if (shutdown) {
				logger.debug("sink done collecting results");
				printCache();
			}
		}
	}

	private void printCache() {
		StringBuilder builder = new StringBuilder("PRINTING " + cache.size() + " PRIME NUMBER RESULTS:");
		builder.append(System.lineSeparator())
				.append("---------------------------------")
				.append(System.lineSeparator());
		cache.entrySet().forEach(e -> {
			builder.append(e.getKey()).append(" -> ").append(e.getValue()).append(System.lineSeparator());
		});
		logger.info("{}{}", System.lineSeparator(), builder.toString());
	}

	public void shutdown() {
		logger.debug("sink shutting down...");
		this.shutdown = true;
	}

}
