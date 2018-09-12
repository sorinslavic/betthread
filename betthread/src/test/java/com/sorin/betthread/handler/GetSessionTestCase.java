package com.sorin.betthread.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.ConcurrentSessionCache;
import com.sun.net.httpserver.HttpExchange;

public class GetSessionTestCase extends AbstractGetMethodHandlerTest {

	private static final int CUSTOMER_ID = 1234;
	
	@Test
	public void verifySessionIsCreated() throws Exception {
		HttpExchange exchange = handle("/" + CUSTOMER_ID + "/session");
		
		assertEquals(HttpURLConnection.HTTP_OK, exchange.getResponseCode());
		
		// validate the returned session key is a valid on that was placed in the cache
		String sessionKey = getContentFromResponseBody(exchange);
		// using the singleton here does not really allow us to mock it :(
		Session session = ConcurrentSessionCache.getInstance().getSession(sessionKey); 
		assertNotNull(session);
		assertEquals(CUSTOMER_ID, session.getCustomerId());
	}
}
