package com.sorin.betthread.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InMemoryBetRepositoryTestCase {

	private static final int CUSTOMER_ID = 12;
	private static final int BET_OFFER_ID = 123;
	
	private static final int MAX_STAKE = 98765;
	private static final List<Integer> stakes = Arrays.asList(123, 54, 999, 1234, MAX_STAKE, 54, 99, 999, 1234, 984, 99, 12);
	
	private InMemoryBetRepository betRepo;
	
	private Field internalMapField;
	
	@Before
	public void setup() throws Exception {
		betRepo = InMemoryBetRepository.getInstance();
		
		internalMapField = InMemoryBetRepository.class.getDeclaredField("betOfferData");
		internalMapField.setAccessible(true);
	}
	
	@After
	public void cleanUp() throws Exception {
		betRepo.clear();
	}
	
	@Test
	public void validateNoData() throws Exception {
		
		assertTrue(((Map<?,?>) internalMapField.get(betRepo)).isEmpty());
	}
	
	@Test
	public void validateSingleBet() throws Exception {
		betRepo.placeStake(MAX_STAKE, CUSTOMER_ID, BET_OFFER_ID);
		
		assertEquals(1, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).size());
		
		assertEquals(MAX_STAKE, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getStake());
		assertEquals(CUSTOMER_ID, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getCustomerId());
	}
	
	@Test
	public void validateTopBetIsKept() throws Exception {
		placeAllBets(Collections.singletonList(CUSTOMER_ID));
		
		assertEquals(1, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).size());
		
		assertEquals(MAX_STAKE, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getStake());
		assertEquals(CUSTOMER_ID, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getCustomerId());
	}
	
	@Test
	public void validateMultipleCustomers() throws Exception {
		List<Integer> customers = Arrays.asList(10, 11, 12, 13, 1, 2, 3, 5, 14, 15, 16, 17, 28);
		placeAllBets(customers);
		
		assertEquals(customers.size(), betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).size());
		
		for (int customerId : customers) {
			assertEquals(MAX_STAKE, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID)
					.stream().filter(cs -> cs.getCustomerId() == customerId).findFirst().get().getStake());
			assertEquals(customerId, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID)
					.stream().filter(cs -> cs.getCustomerId() == customerId).findFirst().get().getCustomerId());
		}
	}
	
	private void placeAllBets(List<Integer> customers) {
		for (int customerId : customers) {
			for (int stake : stakes) {
				betRepo.placeStake(stake, customerId, BET_OFFER_ID);
			}
		}
	}
	
	@Test
	public void validateCustomerAndMultipleBets() {
		List<Integer> betOffers = Arrays.asList(10, 11, 12, 13, 1, 2, 3, 5, 14, 15, 16, 17, 28);

		for (int betOfferId : betOffers) {
			for (int stake : stakes) {
				betRepo.placeStake(stake, CUSTOMER_ID, betOfferId);
			}
		}
		
		for (int betOfferId : betOffers) {
			assertEquals(1, betRepo.getBetOfferIdTopStakes(betOfferId).size());
			
			assertEquals(MAX_STAKE, betRepo.getBetOfferIdTopStakes(betOfferId).first().getStake());
			assertEquals(CUSTOMER_ID, betRepo.getBetOfferIdTopStakes(betOfferId).first().getCustomerId());
		}
	}
}
