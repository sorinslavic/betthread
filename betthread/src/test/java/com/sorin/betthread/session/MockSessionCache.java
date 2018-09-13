package com.sorin.betthread.session;

import java.util.ArrayList;
import java.util.List;

import com.sorin.betthread.Environment;

public class MockSessionCache implements SessionCache {
	public static int SESSION_ID = 1;
	public static int CUSTOMER_ID = 2;
	public static int TIMEOUT = 500;
	
	public List<String> capturedArguments = new ArrayList<>();
	
	private Session session;
	
	public MockSessionCache() {
		session = new Session(SESSION_ID, CUSTOMER_ID, TIMEOUT);
	}

	@Override
	public Session createSession(SessionIdGenerator generator, int customerId, Environment env) {
		return session;
	}

	@Override
	public Session getSession(String sessionKey) throws InvalidSessionKeyException {
		capturedArguments.add(sessionKey);
		
		return session;
	}
	
	public Session getTargetSession() {
		return session;
	}
	
	public void clear() {
		this.session = null;
	}
	
	public boolean wasCalledFor(String sessionKey) {
		return capturedArguments.contains(sessionKey);
	}
}
