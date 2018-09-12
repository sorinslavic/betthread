package com.sorin.betthread.handler;

import com.sorin.betthread.Log;
import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;
import com.sorin.betthread.session.SessionIdGenerator;

public class GetSessionMethodHandler {
	private static final Log log = new Log(GetSessionMethodHandler.class);

	private final SessionCache sessionCache;
	private final SessionIdGenerator generator;
	
	public GetSessionMethodHandler(SessionCache sessionCache,
			SessionIdGenerator generator) {
		this.sessionCache = sessionCache;
		this.generator = generator;
	}
	
	public String perform(String path) throws HttpStatusCodeException {
		// /<customerid>/session 
		int customerId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
		log.debug("perform - create session for customerId: " + customerId);
		
		Session session = sessionCache.createSession(generator, customerId);
			
		return session.getSessionKey();
	}

}
