package com.sorin.betthread.session;

public class Session {
	private final int sessionId;
	private final String sessionKey;
	private final int customerId;
	private final int timeoutMilis; // faster than working with Dates ?
	
	private long expirationDate;
	
	public Session(int sessionId, int customerId, int timeoutMilis) {
		this.sessionId = sessionId;
		this.sessionKey = getKeyFromId(sessionId);
		this.customerId = customerId;
		this.timeoutMilis = timeoutMilis;
		
		updateExpiration();
	}

	public int getSessionId() {
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
	
	public static final int getIdFromKey(String key) {
		return Integer.parseInt(key, 16);
	}
	
	public static final String getKeyFromId(int id) {
		return Integer.toHexString(id).toUpperCase();
	}
}
