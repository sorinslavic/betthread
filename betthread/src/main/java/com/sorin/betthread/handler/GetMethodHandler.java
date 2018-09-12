package com.sorin.betthread.handler;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;
import com.sorin.betthread.session.SessionIdGenerator;

public class GetMethodHandler {

	private final SessionCache sessionCache;
	private final SessionIdGenerator generator;
	
	public GetMethodHandler(SessionCache sessionCache,
			SessionIdGenerator generator) {
		this.sessionCache = sessionCache;
		this.generator = generator;
	}
	
	public String perform(String path) throws HttpStatusCodeException, IOException {
		// the path is of format: 
		// /<customerid>/session 
		// /<betofferid>/highstakes?sessionkey=<key>
		
		if (path.endsWith("session")) {
			int customerId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
			Session session = sessionCache.createSession(generator, customerId);
			
			return session.getSessionKey();
		} else if (path.endsWith("highstakes")) {
			int customerId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
			
			return "oups !";
		} else {
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_NOT_FOUND, "Path: " + path + " is not a known endpoint.");
		}
	}

}
