package com.sorin.betthread.repository;

import java.util.SortedSet;

public interface BetRepository {

	void placeStake(int stake, int customerId, int betOfferId);

	public SortedSet<CustomerStake> getBetOfferIdTopStakes(int betOfferId);
	
	public void clear();
}
