package com.sorin.betthread.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InMemoryBetRepositoryTestCase {

	private static final int STAKE = 1;
	private static final int CUSTOMER_ID = 12;
	private static final int BET_OFFER_ID = 123;
	
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
		betRepo.placeStake(STAKE, CUSTOMER_ID, BET_OFFER_ID);
		
		assertEquals(1, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).size());
		
		assertEquals(STAKE, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getStake());
		assertEquals(CUSTOMER_ID, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getCustomerId());
	}
	
	@Test
	public void validateTopBetIsKep() throws Exception {
		
		int maxStake = 98765;
		List<Integer> stakes = Arrays.asList(123, 54, 999, 1234, maxStake, 54, 99, 999, 1234, 984, 99, 123);
		for (int stake : stakes) {
			betRepo.placeStake(stake, CUSTOMER_ID, BET_OFFER_ID);
		}
		
		assertEquals(1, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).size());
		
		assertEquals(maxStake, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getStake());
		assertEquals(CUSTOMER_ID, betRepo.getBetOfferIdTopStakes(BET_OFFER_ID).first().getCustomerId());
	}
}
