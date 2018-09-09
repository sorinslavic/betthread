package com.sorin.betthread.handler;

public class HttpStatusCodeException extends Exception {
	private static final long serialVersionUID = -2344256806680704883L;

	private final int statusCode;
	
	public HttpStatusCodeException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}
	
	public HttpStatusCodeException(int statusCode, Exception e) {
		super(e);
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}
