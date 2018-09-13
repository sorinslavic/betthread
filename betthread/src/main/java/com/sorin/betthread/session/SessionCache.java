package com.sorin.betthread.session;

import com.sorin.betthread.Environment;

/**
 * 
 * Interface for Session operations.
 * 
 * We can either create a new Session - for a particular customerId,
 * or we an retrieve an existing session.
 * 
 * @author Sorin.Slavic
 *
 */
public interface SessionCache {

	/**
	 * Using the given {@link SessionIdGenerator} - create a new sessionkey and session object
	 * for the particular customerId. The session will be valid based on the {@link Environment#getTimeoutMilis()} property
	 * @param generator
	 * @param customerId
	 * @param env
	 * @return a new session created for this customer
	 */
	Session createSession(SessionIdGenerator generator, int customerId, Environment env);

	/**
	 * @param sessionKey - "correct" sessionKey value; If the format does not match 
	 * our current implementation - an {@link InvalidSessionKeyException} is thrown
	 * @return the existing Session associated for the given sessionKey
	 * @throws InvalidSessionKeyException
	 */
	Session getSession(String sessionKey) throws InvalidSessionKeyException;

}
