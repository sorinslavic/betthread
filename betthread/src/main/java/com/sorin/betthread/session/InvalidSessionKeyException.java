package com.sorin.betthread.session;

public class InvalidSessionKeyException extends Exception {
	private static final long serialVersionUID = 7753863822492213045L;

	public InvalidSessionKeyException(String message) {
		super(message);
	}
	
	public InvalidSessionKeyException(String message, Exception e) {
		super(message, e);
	}
}
