package com.sorin.betthread.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sorin.betthread.Environment;
import com.sorin.betthread.Log;

/**
 * Singleton implementation of a "Concurrent" session cache.
 * The data is stored in a {@link ConcurrentHashMap} that will link
 * {@link Session#getSessionId()} to actual {@link Session} objects.
 * 
 * The interface methods expose the String sessionkey - but the implementation 
 * is optimized to use the numeric value sessionid.
 * 
 * @author Sorin.Slavic
 *
 */
public class ConcurrentSessionCache implements SessionCache {
	private static final Log log = new Log(ConcurrentSessionCache.class);
	
	private static final ConcurrentSessionCache instance = new ConcurrentSessionCache();

	// TODO - keep 2 maps - one with active one with inactive
	private final Map<Long, Session> sessionMap;
	
	// FIXME - maybe the Environment should have been injected here
	// this eager initialization does not really allow that 
	private ConcurrentSessionCache() {
		this.sessionMap = new ConcurrentHashMap<>();
	}
	
	public static ConcurrentSessionCache getInstance() {
		return instance;
	}

	@Override
	public Session createSession(SessionIdGenerator generator, int customerId, Environment env) {
		long sessionId = generator.getNewSessionId();
		Session session = new Session(sessionId, customerId, env.getTimeoutMilis());
		
		// FIXME - validate session is unique
		// atomic operations - per javadoc
		if (sessionMap.putIfAbsent(sessionId, session) != null) // not null value means they key already existed
			return createSession(generator, customerId, env);
		
		return session;
	}

	@Override
	public Session getSession(String sessionKey) throws InvalidSessionKeyException {
		return sessionMap.get(Session.getIdFromKey(sessionKey));
	}

	public void clear() {
		log.info("clear - clearing bet repository data");
		sessionMap.clear();
	}
}
