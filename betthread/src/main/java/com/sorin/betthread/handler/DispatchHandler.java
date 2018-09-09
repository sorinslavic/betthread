package com.sorin.betthread.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DispatchHandler implements HttpHandler {
	
	private final GetMethodHandler getHandler;
//	private final PostMethodHandler postHandler;
	
	public DispatchHandler() {
		this.getHandler = new GetMethodHandler();		
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try (InputStream is = exchange.getRequestBody();
				OutputStream os = exchange.getResponseBody()) {
			
			try {
				String method = exchange.getRequestMethod();
				
				if ("GET".equals(method)) {
					getHandler.handle(exchange);
				} else if ("POST".equals(method)) {
					
				} else {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
				}
			} catch (HttpStatusCodeException e) {
				handleException(exchange, e.getStatusCode(), e.getMessage());				
			} catch (Exception e) {
				handleException(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
			}
		} 
	}
	
	private void handleException(HttpExchange exchange, int statusCode, String message) throws IOException {
		if (message != null && ! message.isEmpty()) {
			exchange.sendResponseHeaders(statusCode, message.length());
			exchange.getResponseBody().write(message.getBytes());
		} else {
			exchange.sendResponseHeaders(statusCode, 0);
		}
	}
}