package com.sorin.betthread.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sorin.betthread.BetThreadApp;

// TODO - keep 2 maps - one with active one with inactive
public class ConcurrentSessionCache implements SessionCache {
	private static final ConcurrentSessionCache instance = new ConcurrentSessionCache();
	
	private final Map<Integer, Session> sessionMap;
	
	private ConcurrentSessionCache() {
		this.sessionMap = new ConcurrentHashMap<>();
	}
	
	public static ConcurrentSessionCache getInstance() {
		return instance;
	}

	@Override
	public Session createSession(SessionIdGenerator generator, int customerId) {
		int sessionId = generator.getNewSessionId();
		Session session = new Session(sessionId, customerId, BetThreadApp.SESSION_TIMEOUT_MILIS);
		
		// FIXME - validate session is unique
		// atomic operations - per javadoc
		if (sessionMap.putIfAbsent(sessionId, session) != null) // not null value means they key already existed
			return createSession(generator, customerId);
		
		return session;
	}

	@Override
	public Session getSession(String sessionKey) throws InvalidSessionKeyException {
		return sessionMap.get(Session.getIdFromKey(sessionKey));
	}

	@Override
	public void clear() {
		sessionMap.clear();
	}
}
