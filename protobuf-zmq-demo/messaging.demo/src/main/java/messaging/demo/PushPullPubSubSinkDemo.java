package messaging.demo;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.broker.ProtobufPrimesSink;
import messaging.client.ProtobufPrimesWorker;
import messaging.server.PushPullPrimesVent;

/**
 * The class PushPullPubSubSinkDemo is A demonstration that runs a
 * {@code PushPullPrimesVent} (ventilator) that PUSHes requests to generate
 * prime numbers to a number of {@code ProtobufPrimesWorker} workers which PULL
 * the request. The workers report to a {@code ProtobufPrimesSink} sink via a
 * PUSH and the sink PUBLISHES reports back to the vent that is also a
 * SUBSCRIBER. Workers also SUBSCRIBE to the sink's controller socket to listen
 * for KILL command to synchronize the end of process. The sink prints the
 * collected prime numbers at the end of the demo.
 *
 *
 * @author T.Silverman
 *type filter text
 */
public class PushPullPubSubSinkDemo {

	private static final Logger logger = LoggerFactory.getLogger(PushPullPubSubSinkDemo.class);

	/*
	 * Runs a Vent (ventilator) that pushes requests to generate prime numbers to a
	 * number of workers who pull the request. The workers report to a sink via a
	 * PUSH and the sink PUBLISHES reports back to the vent. Workers also SUBSCRIBE
	 * to the sink's controller to listen for KILL command to synchronize the end of
	 * process. The sink prints the collected prime numbers at the end of the demo.
	 *
	 */
	public static void main(String[] args) throws Exception {
		int primes = 21; // number of primes to generate
		int digits = 128; // number of minimum digits of each prime
		// create thread pool
		ExecutorService pool = Executors.newCachedThreadPool();
		// create tasks list
		List<Runnable> tasks = Arrays.asList(
				new Runnable[] { () -> new ProtobufPrimesWorker("worker-1").run(),
						() -> new ProtobufPrimesWorker("worker-2").run(),
						() -> new ProtobufPrimesWorker("worker-3").run(),
						() -> new ProtobufPrimesSink().run(),
						() -> new PushPullPrimesVent(primes, digits).run() });
		// obtain reference to future results
		List<Future<?>> futures = tasks.stream().map(task -> pool.submit(task)).collect(Collectors.toList());
		BitSet bits = new BitSet(futures.size()); // map complete results to bits
		while (bits.nextClearBit(0) < futures.size()) { // iterate on the future results until all are done
			for (int i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone())
					bits.set(i); // set the corresponding bit
				try {
					Thread.sleep(50); // allow some time for tasks to complete
				} catch (InterruptedException ignore) {
				}
			}
		}
		logger.info("DEMO DONE!");
		pool.shutdown();
	}

}
