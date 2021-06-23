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
import messaging.client.ReqPrimeClient;
import messaging.server.RepPrimeServer;

/**
 * The class ReqRepDemo is a simple demo of a REQ/REP communication model
 * between a {@code ReqPrimeClient} client requesting the next X digits long
 * prime number, and a {@code RepPrimeServer} server that replies.
 *
 * @author T.Silverman
 *
 */
public class ReqRepDemo {

	private static final Logger logger = LoggerFactory.getLogger(ReqRepDemo.class);

	/*
	 * demo of a REQ/REP communication model between a client requesting the next X
	 * digits long prime number, and a server
	 */
	public static void main(String[] args) throws Exception {
		int numOfPrimes = 20;
		// create thread pool
		ExecutorService pool = Executors.newCachedThreadPool();
		// create tasks list
		List<Runnable> tasks = Arrays.asList(
				new Runnable[] { () -> new ReqPrimeClient(numOfPrimes).run(), () -> new RepPrimeServer().run() });
		// obtain reference to future results
		List<Future<?>> futures = tasks.stream().map(task -> pool.submit(task)).collect(Collectors.toList());
		BitSet bits = new BitSet(futures.size()); // map complete results to bits
		while (bits.nextClearBit(0) < futures.size()) { // iterate on the future results until all are done
			for (int i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone())
					bits.set(i); // set the corresponding bit
				try {
					Thread.sleep(300); // allow some time for tasks to complete
				} catch (InterruptedException ignore) {
				}
			}
		}
		logger.info("DEMO DONE!");
		pool.shutdown();
	}

}
