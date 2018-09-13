package com.sorin.betthread.session;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>For each handler thread we will create a different SessionIdGenerator.
 * The algorithm is the same - independent of thread - but we want to avoid sharing the
 * same resources and objects for this purely computaional task.</p>
 * 
 * <p>Per java.util.Random javadoc:</br>
 * Instances of java.util.Random are threadsafe. However, the concurrent use of the same 
 * java.util.Random instance across threads may encounter contention and consequent poor performance. 
 * Consider instead using java.util.concurrent.ThreadLocalRandom in multithreaded designs. </p>
 * 
 * <p>We are following that advice and will be using ThreadLocalRandom.</p>
 * 
 * FIXME - is {@link UUID#randomUUID()} to powerfull ? do we need more "unique" session ids ?
 * 
 * FIXME - {@link Session} and {@link SessionIdGenerator} aren't all that well separate. Both are aware
 * of the fact that we generate LONG values and then convert to HEX.
 * @author Sorin.Slavic
 *
 */
public class SessionIdGenerator {
	// FIXME is thread local a problem with our thread pool ?
	// when an existing thread is provided from the pool - will it use this same thread local objects ?
	private final ThreadLocalRandom random;
	// could also be UUID
	// could also be AtomicInteger
	
	public SessionIdGenerator() {
		this.random = ThreadLocalRandom.current();
	}
	
	/**
	 * Generate a session id that is basically a random long number - uniformly distributed between 0 and {@link Long#MAX_VALUE}.
	 * @return a "new" session id
	 */
	public long getNewSessionId() {
		return random.nextLong(Long.MAX_VALUE);
	}
}
