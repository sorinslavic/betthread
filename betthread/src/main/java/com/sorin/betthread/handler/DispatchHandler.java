package com.sorin.betthread.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sorin.betthread.repository.BetRepository;
import com.sorin.betthread.session.SessionCache;
import com.sorin.betthread.session.SessionIdGenerator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Works as a dispatcher - redirecting GET / POST method to the correct handler implementations 
 * @author Sorin.Slavic
 *
 */
public class DispatchHandler implements HttpHandler {
	private final SessionCache sessionCache;
	private final BetRepository betRepo;
	
	public DispatchHandler(SessionCache sessionCache,
			BetRepository betRepo) {
		this.sessionCache = sessionCache;
		this.betRepo = betRepo;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// called each time on a separate thread
		// the SessionIdGenerator is "thread dependent"
		
		
		try (InputStream is = exchange.getRequestBody();
				OutputStream os = exchange.getResponseBody()) {
			
			try {
				String method = exchange.getRequestMethod();
				
				if ("GET".equals(method)) {
					// because of how the SessionIdGenerator is implemented we are "forced" to create a new one 
					// for each exchange - because the SessionIdGenerator needs to be created on the thread it is used
					// because of the ThreadLocal optimization
					GetMethodHandler getHandler = new GetMethodHandler(sessionCache, new SessionIdGenerator());
					writeResponse(exchange, getHandler.perform(exchange.getRequestURI().getPath()));
				} else if ("POST".equals(method)) {
					PostBetOfferMethodHandler postHandler = new PostBetOfferMethodHandler(sessionCache, betRepo);
					postHandler.perform(exchange.getRequestURI().getPath(), exchange.getRequestURI().getQuery(), is);

					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				} else {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
				}
			} catch (HttpStatusCodeException e) {
				handleException(exchange, e.getStatusCode(), e.getMessage());				
			} catch (Exception e) {
				handleException(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal server error - cause: " + e.getMessage());
			}
		} 
	}

	private void writeResponse(HttpExchange exchange, String response) throws IOException {
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
		exchange.getResponseBody().write(response.getBytes());
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