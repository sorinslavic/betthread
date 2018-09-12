package com.sorin.betthread.session;

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
