package com.sorin.betthread.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.sorin.betthread.Environment;
import com.sorin.betthread.Log;
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
	private static final Log log = new Log(DispatchHandler.class);
	
	private final SessionCache sessionCache;
	private final BetRepository betRepo;
	
	public DispatchHandler(SessionCache sessionCache,
			BetRepository betRepo) {
		this.sessionCache = sessionCache;
		this.betRepo = betRepo;
	}
	
	// TODO - maybe move the handlers to be initialized in the constructor as to be reused
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// called each time on a separate thread
		
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		
		try (InputStream is = exchange.getRequestBody();
				OutputStream os = exchange.getResponseBody()) {
			
			try {
				String method = exchange.getRequestMethod();
				String path = exchange.getRequestURI().getPath();
				
				log.info("handle - dispatch exchange: " + method + " " + path);
				
				if ("GET".equals(method)) {
					// the path is of format: 
					// /<customerid>/session 
					// /<betofferid>/highstakes

					if (path.endsWith("session")) {
						// because of how the SessionIdGenerator is implemented we are "forced" to create a new one 
						// for each exchange - because the SessionIdGenerator needs to be created on the thread it is used
						// because of the ThreadLocal optimization
						GetSessionMethodHandler getHandler = new GetSessionMethodHandler(sessionCache, new SessionIdGenerator(), Environment.getEnv());
						writeResponse(exchange, getHandler.perform(path));
					} else if (path.endsWith("highstakes")) {
						GetHighStakesMethodHandler highStakesHandler = new GetHighStakesMethodHandler(betRepo);
						writeResponse(exchange, highStakesHandler.perform(path));
					} else {
						throw new HttpStatusCodeException(HttpURLConnection.HTTP_NOT_FOUND, "Path: " + path + " is not a known endpoint.");
					}
				} else if ("POST".equals(method)) {
					// handle paths of pattern: /<betofferid>/stake
					// query should be ?sessionkey=<sessionkey>
					
					if (! path.endsWith("stake")) {
						throw new HttpStatusCodeException(HttpURLConnection.HTTP_NOT_FOUND, "Path: " + path + " is not a known endpoint.");
					}
					
					PostBetOfferMethodHandler postHandler = new PostBetOfferMethodHandler(sessionCache, betRepo);
					postHandler.perform(path, exchange.getRequestURI().getQuery(), is);

					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				} else {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
				}
			} catch (HttpStatusCodeException e) {
				log.error("handle - HTTP status code exception: " + e.getMessage(), e);
				handleException(exchange, e.getStatusCode(), e.getMessage());				
			} catch (Exception e) {
				log.error("handle - unexpected internal server exception: " + e.getMessage(), e);
				handleException(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal server error - cause: " + e.getMessage());
			}
		} 
	}

	private void writeResponse(HttpExchange exchange, String response) throws IOException {
		log.debug("writeResponse - returning status OK and response: " + response);
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