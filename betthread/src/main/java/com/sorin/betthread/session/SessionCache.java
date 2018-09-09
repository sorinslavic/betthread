package com.sorin.betthread.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sorin.betthread.BetThreadApp;

// TODO - keep 2 maps - one with active one with inactive
public class SessionCache {
	private final Map<Integer, Session> sessionMap = new ConcurrentHashMap<>();

	public Session createSession(SessionIdGenerator generator, int customerId) {
		int sessionId = generator.getNewSessionId();
		Session session = new Session(sessionId, customerId, BetThreadApp.SESSION_TIMEOUT_MILIS);
		
		// FIXME - validate session is unique
		// atomic operations - per javadoc
		if (sessionMap.putIfAbsent(sessionId, session) != null) // not null value means they key already existed
			return createSession(generator, customerId);
		
		return session;
	}
	
	public Session getSession(String sessionKey) {
		return sessionMap.get(Session.getIdFromKey(sessionKey));
	}
}
