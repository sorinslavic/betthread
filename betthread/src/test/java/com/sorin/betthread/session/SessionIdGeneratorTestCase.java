package com.sorin.betthread.session;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.sorin.betthread.Log;

public class SessionIdGeneratorTestCase {
	private static final Log log = new Log(SessionIdGeneratorTestCase.class);
	
	private static final int THREADS_COUNT = 10;
	private static final int GENERATE_COUNT = 500000;
	
	private static final int SESSION_IDS_COUNT = THREADS_COUNT * GENERATE_COUNT;
	
	// reasonably unique I would say is 99.99%;
	// only 1 of every 1,000,000 might be a duplicate
	private static final double REASONABLY_UNIQUE_PERCENTAGE = 99.9999d;
	// this is obviously dependent on the number of ids we generate
	// if we were to generate Long#MAX_VALUE ids - then there are good changes that none of them are unique :)
	
	@Test
	public void validateGeneratedSessionIdsAreDistinct() throws InterruptedException {	
		log.debug("validateGeneratedSessionIdsAreDistinct - preparing to generate: " + SESSION_IDS_COUNT + " sessions");
		Set<Long> sessionIdSet = Collections.synchronizedSet(new HashSet<>());
		AtomicInteger duplicates = new AtomicInteger(0);
		
		List<Thread> list = new ArrayList<>();
		for (int t = 0; t < THREADS_COUNT; t ++) {
			list.add(new Thread(() -> { 
				SessionIdGenerator generator = new SessionIdGenerator();
				log.debug("run - using generator: " + generator);
				
				for (int i = 0; i < GENERATE_COUNT; i ++) {
					if (! sessionIdSet.add(generator.getNewSessionId()))
						duplicates.incrementAndGet();
				}
			}));
		}

		list.forEach(t -> t.start());
		list.forEach(t -> { // ugh
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		log.debug("validateGeneratedSessionIdsAreDistinct - identified " + duplicates.get() + " duplicate sessions;");
		double percentage = ((SESSION_IDS_COUNT - duplicates.get()) * 100d) / SESSION_IDS_COUNT;
		// if the generated percentage is higher - then we have a problem
		assertTrue("only " + percentage + " of the generated session ids were unique" , percentage > REASONABLY_UNIQUE_PERCENTAGE);
	}
}