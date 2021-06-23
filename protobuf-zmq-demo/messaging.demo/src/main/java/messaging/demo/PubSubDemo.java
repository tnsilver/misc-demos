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
import messaging.client.ProtobufSubscriber;
import messaging.server.ProtobufPublisher;

/**
 * The class PubSubDemo is A demonstration that runs a ZeroMQ publisher which
 * publishes protobuf packed messages and a ZeroMQ subscriber to pretty print
 * the messages as JSON after unpacking them.
 *
 * @author T.Silverman
 *
 */
public class PubSubDemo {

	private static final Logger logger = LoggerFactory.getLogger(PubSubDemo.class);

	/*
	 * Runs a ZeroMQ publisher that publishes protobuf packed (bytes) messages and
	 * two ZeroMQ subscribers to print the messages after unpacking them.
	 */
	public static void main(String[] args) throws Exception {
		int numOfMessages = 10;
		// create thread pool
		ExecutorService pool = Executors.newCachedThreadPool();
		// create tasks list
		List<Runnable> tasks = Arrays.asList(
				new Runnable[] { () -> new ProtobufPublisher(numOfMessages).run(),
						() -> new ProtobufSubscriber("1", numOfMessages).run(),
						() -> new ProtobufSubscriber("2", numOfMessages).run() });
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
