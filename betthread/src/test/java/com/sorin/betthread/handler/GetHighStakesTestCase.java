package com.sorin.betthread.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;

import com.sorin.betthread.repository.CustomerStake;
import com.sun.net.httpserver.HttpExchange;

public class GetHighStakesTestCase extends AbstractGetMethodHandlerTest {

	private static final int BET_OFFER_ID = 1234;
	
	@Test
	public void verifyNoStakes() throws Exception {
		performAndValidateEmpty();
	}
	
	@Test
	public void verifyEmptyStakes() throws Exception {
		mockBetRepo.initStakes(Collections.emptySortedSet());
		performAndValidateEmpty();
	}
	
	private void performAndValidateEmpty() throws IOException, URISyntaxException {
		HttpExchange exchange = handle("/" + BET_OFFER_ID + "/highstakes");
		
		assertEquals(HttpURLConnection.HTTP_OK, exchange.getResponseCode());
		String csv = getContentFromResponseBody(exchange);
		
		assertEquals("", csv);
		assertEquals(1, mockBetRepo.getCapturedBetOfferIds().size());
		assertTrue(mockBetRepo.getCapturedBetOfferIds().contains(BET_OFFER_ID));
	}
	
	@Test
	public void verifyCsvStakes() throws Exception {
		mockBetRepo.initStakes(new TreeSet<>(Arrays.asList(
				new CustomerStake(123, 3210),
				new CustomerStake(999, 8888))));
		
		HttpExchange exchange = handle("/" + BET_OFFER_ID + "/highstakes");

		assertEquals(HttpURLConnection.HTTP_OK, exchange.getResponseCode());
		String csv = getContentFromResponseBody(exchange);

		assertEquals("123=3210,999=8888", csv);
		assertEquals(1, mockBetRepo.getCapturedBetOfferIds().size());
		assertTrue(mockBetRepo.getCapturedBetOfferIds().contains(BET_OFFER_ID));
	}
}
