package com.sorin.betthread.handler;

import com.sorin.betthread.Environment;
import com.sorin.betthread.Log;
import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;
import com.sorin.betthread.session.SessionIdGenerator;

/**
 * Create a new "Session" for the specified customerID that will be kept in the session cache.
 *  
 * @author Sorin.Slavic
 *
 */
public class GetSessionMethodHandler {
	private static final Log log = new Log(GetSessionMethodHandler.class);

	private final SessionCache sessionCache;
	private final SessionIdGenerator generator;
	private final Environment env;
	
	public GetSessionMethodHandler(SessionCache sessionCache,
			SessionIdGenerator generator, Environment env) {
		this.sessionCache = sessionCache;
		this.generator = generator;
		this.env = env;
	}
	
	public String perform(String path) throws HttpStatusCodeException {
		// /<customerid>/session 
		int customerId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
		log.debug("perform - create session for customerId: " + customerId);
		
		Session session = sessionCache.createSession(generator, customerId, env);
			
		return session.getSessionKey();
	}

}
