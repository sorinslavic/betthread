package com.sorin.betthread.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sorin.betthread.Log;


public class SessionTestCase {
	private static final Log log = new Log(SessionTestCase.class);
	
	@Test
	public void validateSessionIdToKeyTransform() {
		assertEquals("1", new Session(1, 123, 500).getSessionKey());
		assertEquals("A", new Session(10, 123, 500).getSessionKey());
		assertEquals("F", new Session(15, 123, 500).getSessionKey());
		assertEquals("10", new Session(16, 123, 500).getSessionKey());
		assertEquals("CACA", new Session(51914, 123, 500).getSessionKey());
		assertEquals("7FFFFFFF", new Session(Integer.MAX_VALUE, 123, 500).getSessionKey());
	}
	
	@Test
	public void validateSessionKeyToIdTransform() throws InvalidSessionKeyException {
		assertEquals(1, Session.getIdFromKey("1"));
		assertEquals(10, Session.getIdFromKey("A"));
		assertEquals(15, Session.getIdFromKey("F"));
		assertEquals(16, Session.getIdFromKey("10"));
		assertEquals(51914, Session.getIdFromKey("CACA"));
		assertEquals(Integer.MAX_VALUE, Session.getIdFromKey("7FFFFFFF"));
	}
	
	@Test
	public void validateExpiration() {
		assertTrue("5 second session is valid", new Session(1, 123, 5000).isValid());
		assertFalse("expired session is invalid", new Session(1, 123, -1).isValid());
	}
	
	@Test
	public void validateExpirationUpdate() throws InterruptedException {
		log.debug("validateExpirationUpdate - test will sleep in order for session to expire");
		Session session = new Session(1, 123, 500);
		Thread.sleep(600); // ugh 
		
		assertFalse("session is invalid after time passes", session.isValid());
		session.updateExpiration();
		assertTrue("session is valid after reset", session.isValid());
		// FIXME validate that the expiration was actually reset to 500 milis in the future
	}
}
