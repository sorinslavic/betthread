package com.sorin.betthread.session;

public interface SessionCache {

	Session createSession(SessionIdGenerator generator, int customerId);

	Session getSession(String sessionKey) throws InvalidSessionKeyException;

	void clear();
}
