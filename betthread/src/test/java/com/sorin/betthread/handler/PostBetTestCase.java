package com.sorin.betthread.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.sorin.betthread.MockHttpExchange;
import com.sorin.betthread.repository.BetRepository;
import com.sorin.betthread.repository.InMemoryBetRepository;
import com.sorin.betthread.session.MockSessionCache;

public class PostBetTestCase {

	private static final int BET_OFFER_ID = 12312;
	private static final int STAKE = 2342;
	private static final String SESSION_KEY = "123AA";
	
	private MockSessionCache sessionCache;
	private BetRepository betRepository;
	private DispatchHandler handler;
	
	@Before
	public void setup() {
		sessionCache = new MockSessionCache();
		betRepository = InMemoryBetRepository.getInstance();
		
		handler = new DispatchHandler(sessionCache, betRepository);
	}
	
	@Test
	public void placeBetWithIncorrectBetOfferId() throws Exception {
		MockHttpExchange exchange = new MockHttpExchange("POST", new URI("/" + BET_OFFER_ID + "XXX/stake?sessionkey=" + SESSION_KEY), String.valueOf(STAKE));
		handler.handle(exchange);

		assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, exchange.getResponseCode());
	}
	
	@Test
	public void placeBet() throws Exception {
		MockHttpExchange exchange = new MockHttpExchange("POST", new URI("/" + BET_OFFER_ID + "/stake?sessionkey=" + SESSION_KEY), String.valueOf(STAKE));
		handler.handle(exchange);

		assertEquals(HttpURLConnection.HTTP_OK, exchange.getResponseCode());
		assertTrue(sessionCache.wasCalledFor(SESSION_KEY));
		assertEquals(1, betRepository.getBetOfferIdTopStakes(BET_OFFER_ID).size());
	}
	
	@Test
	public void placeBetForMissingSession() throws Exception {
		MockHttpExchange exchange = new MockHttpExchange("POST", new URI("/" + BET_OFFER_ID + "/stake?sessionkey=" + SESSION_KEY), String.valueOf(STAKE));
		sessionCache.clear();
		handler.handle(exchange);

		assertEquals(HttpURLConnection.HTTP_FORBIDDEN, exchange.getResponseCode());
		assertTrue(sessionCache.wasCalledFor(SESSION_KEY));
		assertNull(betRepository.getBetOfferIdTopStakes(BET_OFFER_ID));
	}
	
	@Test
	public void placeBetForInvalidSession() throws Exception {
		MockHttpExchange exchange = new MockHttpExchange("POST", new URI("/" + BET_OFFER_ID + "/stake?sessionkey=" + SESSION_KEY), String.valueOf(STAKE));
		
		// ugh ... we should use a mock for Session - buuuuut - this also works
		Thread.sleep(MockSessionCache.TIMEOUT + 100);
		
		handler.handle(exchange);

		assertEquals(HttpURLConnection.HTTP_FORBIDDEN, exchange.getResponseCode());
		assertTrue(sessionCache.wasCalledFor(SESSION_KEY));
		assertNull(betRepository.getBetOfferIdTopStakes(BET_OFFER_ID));
	}
}
