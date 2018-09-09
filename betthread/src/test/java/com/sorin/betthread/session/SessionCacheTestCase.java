package com.sorin.betthread.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

public class SessionCacheTestCase {
	// must be HEX int representation 
	private static final String SESSION_KEY = "123ABC";
	private static final int CUSTOMER_ID = 123;
	
	private static final int SINGLE_SESSION_ID = 17;
	private static final int DUPLICATE_SESSION_ID = 999;
	
	// mocks without a mocking framework
	private SessionIdGenerator singleIdGenerator = new SessionIdGenerator() {
		@Override
		public int getNewSessionId() {
			return SINGLE_SESSION_ID;
		}
	};
	
	private SessionIdGenerator duplicateIdGenerator = new SessionIdGenerator() {
		private Queue<Integer> queue = new LinkedList<>(Arrays.asList(SINGLE_SESSION_ID, SINGLE_SESSION_ID, SINGLE_SESSION_ID, DUPLICATE_SESSION_ID));
		
		@Override
		public int getNewSessionId() {
			return queue.poll();
		}
	};

	private SessionIdGenerator errorIdGenerator = new SessionIdGenerator() {
		private Queue<Integer> queue = new LinkedList<>(Arrays.asList(SINGLE_SESSION_ID, SINGLE_SESSION_ID));
		
		// this will error out because when the queue is emptied it will return null that cannot be autounbox into int
		@Override
		public int getNewSessionId() {
			return queue.poll();
		}
	};
	
	private SessionCache cache;
	
	@Before
	public void setup() {
		cache = new SessionCache();
	}
	
	@Test
	public void testGetSessionEmtpy() {
		assertNull(cache.getSession(SESSION_KEY));
	}
	
	@Test
	public void testCreateSession() {
		createSessionWithId(singleIdGenerator, SINGLE_SESSION_ID);
	}

	@Test
	public void testCreateSessionDuplicateSessionId() {
		createSessionWithId(duplicateIdGenerator, SINGLE_SESSION_ID);
		// init the previous test state ... kind of makes the other create session test redundant
		createSessionWithId(duplicateIdGenerator, DUPLICATE_SESSION_ID);		
	}
	
	@Test(expected = NullPointerException.class)
	public void testExceptionFromDuplicateSessionId() {
		createSessionWithId(errorIdGenerator, SINGLE_SESSION_ID);
		createSessionWithId(errorIdGenerator, SINGLE_SESSION_ID);		
	}
	
	private void createSessionWithId(SessionIdGenerator generator, int id) {
		Session session = cache.createSession(generator, CUSTOMER_ID);
		
		assertEquals(id, session.getSessionId());
		assertEquals(CUSTOMER_ID, session.getCustomerId());
		assertTrue(session.isValid());
		
		assertSame(session, cache.getSession(session.getSessionKey()));		
	}
}
