package com.sorin.betthread.repository;

import java.util.SortedSet;

/**
 * General contract for the BetRepository features;
 * 
 * We can add a stake for a particular customer and bet offer id.
 * We can retrieved the currently existing bets for a bet offer sorted descending from highest stake to lowest.
 * 
 * @author Sorin.Slavic
 *
 */
public interface BetRepository {

	/**
	 * Place a new stake for this customerId on the betOfferId.
	 * @param stake
	 * @param customerId
	 * @param betOfferId
	 */
	void placeStake(int stake, int customerId, int betOfferId);

	/**
	 * Return all of the stakes placed for the specific betOfferId ordered from
	 * highest stake value to lowest.
	 * Each customer should only appear once - with his highest stake.
	 * 
	 * @param betOfferId
	 * @return a sorted set of customer stakes
	 */
	public SortedSet<CustomerStake> getBetOfferIdTopStakes(int betOfferId);
	
}
