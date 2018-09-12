package com.sorin.betthread.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;

import com.sorin.betthread.Log;
import com.sorin.betthread.MockHttpExchange;
import com.sorin.betthread.repository.MockBetRepository;
import com.sorin.betthread.session.ConcurrentSessionCache;
import com.sun.net.httpserver.HttpExchange;

public abstract class AbstractGetMethodHandlerTest {
	private static final Log log = new Log(AbstractGetMethodHandlerTest.class);
	
	protected MockBetRepository mockBetRepo;
	
	private DispatchHandler handler;
	
	@Before
	public void setup() {
		mockBetRepo = new MockBetRepository();
		
		// FIXME - should mock session cache
		handler = new DispatchHandler(ConcurrentSessionCache.getInstance(), mockBetRepo);
	}
	
	public MockHttpExchange handle(String path) throws IOException, URISyntaxException {
		log.info("handle - perform test GET for url: " + path);
		
		MockHttpExchange mockExchange = new MockHttpExchange("GET", new URI(path), "");
		handler.handle(mockExchange);
		
		return mockExchange;
	}
	
	public static String getContentFromResponseBody(HttpExchange exchange) {
		return ((MockHttpExchange) exchange).getActualOutput();
	}
}
