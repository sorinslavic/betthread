package com.sorin.betthread.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;

import com.sorin.betthread.MockHttpExchange;
import com.sorin.betthread.repository.InMemoryBetRepository;
import com.sorin.betthread.session.ConcurrentSessionCache;
import com.sun.net.httpserver.HttpExchange;

public abstract class AbstractGetMethodHandlerTest {

	private DispatchHandler handler;
	
	@Before
	public void setup() {
		// FIXME - should mock session cache
		handler = new DispatchHandler(ConcurrentSessionCache.getInstance(), InMemoryBetRepository.getInstance());
	}
	
	public MockHttpExchange handle(String path) throws IOException, URISyntaxException {
		MockHttpExchange mockExchange = new MockHttpExchange("GET", new URI(path), "");
		handler.handle(mockExchange);
		
		return mockExchange;
	}
	
	public static String getContentFromResponseBody(HttpExchange exchange) {
		return ((MockHttpExchange) exchange).getActualOutput();
	}
}
