package com.sorin.betthread.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConcurrentSessionCacheTestCase {
	// must be HEX int representation 
	private static final String SESSION_KEY = "123ABC";
	private static final int CUSTOMER_ID = 123;
	
	private static final long SINGLE_SESSION_ID = 17;
	private static final long DUPLICATE_SESSION_ID = 999;
	
	// mocks without a mocking framework
	private SessionIdGenerator singleIdGenerator = new SessionIdGenerator() {
		@Override
		public long getNewSessionId() {
			return SINGLE_SESSION_ID;
		}
	};
	
	private SessionIdGenerator duplicateIdGenerator = new SessionIdGenerator() {
		private Queue<Long> queue = new LinkedList<>(Arrays.asList(SINGLE_SESSION_ID, SINGLE_SESSION_ID, SINGLE_SESSION_ID, DUPLICATE_SESSION_ID));
		
		@Override
		public long getNewSessionId() {
			return queue.poll();
		}
	};

	private SessionIdGenerator errorIdGenerator = new SessionIdGenerator() {
		private Queue<Long> queue = new LinkedList<>(Arrays.asList(SINGLE_SESSION_ID, SINGLE_SESSION_ID));
		
		// this will error out because when the queue is emptied it will return null that cannot be autounbox into int
		@Override
		public long getNewSessionId() {
			return queue.poll();
		}
	};
	
	private ConcurrentSessionCache cache;
	
	@Before
	public void setup() {
		cache = ConcurrentSessionCache.getInstance();
	}
	
	@After
	public void cleanUp() {
		// FIXME - this method was added to simply allow these tests :(
		cache.clear();
	}
	
	@Test
	public void testGetSessionEmtpy() throws Exception {
		assertNull(cache.getSession(SESSION_KEY));
	}
	
	@Test
	public void testCreateSession() throws Exception {
		createSessionWithId(singleIdGenerator, SINGLE_SESSION_ID);
	}

	@Test
	public void testCreateSessionDuplicateSessionId() throws Exception {
		createSessionWithId(duplicateIdGenerator, SINGLE_SESSION_ID);
		// init the previous test state ... kind of makes the other create session test redundant
		createSessionWithId(duplicateIdGenerator, DUPLICATE_SESSION_ID);		
	}
	
	@Test(expected = NullPointerException.class)
	public void testExceptionFromDuplicateSessionId() throws Exception {
		createSessionWithId(errorIdGenerator, SINGLE_SESSION_ID);
		createSessionWithId(errorIdGenerator, SINGLE_SESSION_ID);		
	}
	
	private void createSessionWithId(SessionIdGenerator generator, long id) throws InvalidSessionKeyException {
		Session session = cache.createSession(generator, CUSTOMER_ID);
		
		assertEquals(id, session.getSessionId());
		assertEquals(CUSTOMER_ID, session.getCustomerId());
		assertTrue(session.isValid());
		
		assertSame(session, cache.getSession(session.getSessionKey()));		
	}
}
