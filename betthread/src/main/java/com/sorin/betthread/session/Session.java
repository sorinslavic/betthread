package com.sorin.betthread.session;

/**
 * Session object representation.
 * Each session is identified by a sessionId/sessionKey - the customerId it was created for
 * and the expirationDate.
 * 
 * The timeoutMilis is actually an environment constant - but since we allow runtime parameterization for it, it 
 * will need to be included here. FIXME - not really - the {@link #isValid()} method could get that
 * timeout as a parameter.
 * 
 * The expirationDate is not a final field because each valid session is "refreshed" every time it is used - as to
 * extend the period it is valid for.
 * 
 * The sessionId and sessionKey have a "special" relationship.
 * The sessionKey is actually the HEX representation of the sessionId long value :)
 * I believe - not really tested - that this will yield better performance inside the HashMap.
 * 
 * @author Sorin.Slavic
 *
 */
public class Session {
	private final long sessionId;
	private final String sessionKey;
	private final int customerId;
	private final int timeoutMilis; // faster than working with Dates ?
	
	private long expirationDate;
	
	public Session(long sessionId, int customerId, int timeoutMilis) {
		this.sessionId = sessionId;
		this.sessionKey = getKeyFromId(sessionId);
		this.customerId = customerId;
		this.timeoutMilis = timeoutMilis;
		
		updateExpiration();
	}

	public long getSessionId() {
		return sessionId;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public int getCustomerId() {
		return customerId;
	}

	public int getTimeoutMilis() {
		return timeoutMilis;
	}
	
	// should this be synchronized ? FIXME
	public void updateExpiration() {
		this.expirationDate = System.currentTimeMillis() + timeoutMilis;
	}
	
	// TODO - if false - we should remove the session from the map to clear the cache
	public boolean isValid() {
		return this.expirationDate >= System.currentTimeMillis();
	}
	
	public static final long getIdFromKey(String key) throws InvalidSessionKeyException {
		try {
			return Long.parseLong(key, 16);
		} catch (Exception e) {
			throw new InvalidSessionKeyException("Key " + key + " could not be parsed to a valid session id", e);
		}
	}
	
	public static final String getKeyFromId(long id) {
		return Long.toHexString(id).toUpperCase();
	}
}
