package com.sorin.betthread.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MockBetRepository implements BetRepository {

	private SortedSet<CustomerStake> stakes = new TreeSet<>();
	private List<Integer> capturedBetOfferIds = new ArrayList<>();
	
	@Override
	public void placeStake(int stake, int customerId, int betOfferId) {
		// nope
	}

	@Override
	public SortedSet<CustomerStake> getBetOfferIdTopStakes(int betOfferId) {
		capturedBetOfferIds.add(betOfferId);
		return stakes;
	}
	
	public void initStakes(SortedSet<CustomerStake> stakes) {
		this.stakes = stakes;
	}
	
	public List<Integer> getCapturedBetOfferIds() {
		return capturedBetOfferIds;
	}

}
