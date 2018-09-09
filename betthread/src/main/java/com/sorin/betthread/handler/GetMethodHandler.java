package com.sorin.betthread.handler;

import static com.sorin.betthread.BetThreadApp.CHARSET;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;
import com.sorin.betthread.session.SessionIdGenerator;
import com.sun.net.httpserver.HttpExchange;

public class GetMethodHandler {

	private SessionCache sessionCache;
	private SessionIdGenerator generator = new SessionIdGenerator();
	
	public void handle(HttpExchange exchange) throws HttpStatusCodeException, IOException {
		String path = exchange.getRequestURI().getPath();
		// the path is of format: 
		// /<customerid>/session 
		// /<betofferid>/highstakes
		
		if (path.endsWith("session")) {
			int customerId = Integer.valueOf(getFirstPartOfPath(path));
			Session session = sessionCache.createSession(generator, customerId);
			
			writeResponse(exchange, session.getSessionKey());
		} else if (path.endsWith("highstakes")) {
			
		} else {
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_NOT_FOUND, "Path: " + path + " is not a known endpoint.");
		}
		
		System.out.println(exchange.getRequestURI());
		URI uri = exchange.getRequestURI();
		System.out.println(uri.getPath());
		
		InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), CHARSET);
		int asciiChar = reader.read();
		while (asciiChar != -1) {

			System.out.println(asciiChar + "x" + (char) asciiChar);
			asciiChar = reader.read();
		}
		
		
	}
	
	private void writeResponse(HttpExchange exchange, String response) throws IOException {
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 4);
		exchange.getResponseBody().write(response.getBytes());
	}
	
	private String getFirstPartOfPath(String path) throws HttpStatusCodeException {
		// FIXME maybe path.split("/") is faster
		
		if (! path.startsWith("/"))
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Path: " + path + " does not start with slash.");
		
		int slash = path.indexOf("/", 1);
		if (slash == -1)
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Path: " + path + "  does not follow known pattern.");
		
		return path.substring(1, slash);
	}

}
