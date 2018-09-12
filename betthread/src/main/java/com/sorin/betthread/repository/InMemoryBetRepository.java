package com.sorin.betthread.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBetRepository implements BetRepository {
	private static final InMemoryBetRepository instance = new InMemoryBetRepository();
	
	private static final Comparator<CustomerStake> stakeComparator = (cs1, cs2) -> {
		return cs1.getStake() - cs2.getStake();
	};
	
	private final Map<Integer, SortedSet<CustomerStake>> betOfferData;
		
	public static final InMemoryBetRepository getInstance() {
		return instance;
	}
	
	private InMemoryBetRepository() {
		betOfferData = new ConcurrentHashMap<>();
	}

	@Override
	public void placeStake(int stake, int customerId, int betOfferId) {
		// per ConcurrentHashMap implementation #compute is performed atomically
		betOfferData.compute(betOfferId, 
				(key, set) -> {
					if (set == null) {
						SortedSet<CustomerStake> stakes = new TreeSet<>(stakeComparator);
						CustomerStake cs = new CustomerStake(customerId, stake);
						stakes.add(cs);
						return Collections.synchronizedSortedSet(stakes);
					} else {
						Optional<CustomerStake> existingCs = set.stream().filter(cs -> cs.getCustomerId() == customerId).findAny();
						if (existingCs.isPresent()) {
							if (existingCs.get().getStake() < stake) {
								existingCs.get().setStake(stake);
							}
						} else {
							CustomerStake cs = new CustomerStake(customerId, stake);
							set.add(cs);	
						}
						return set;
					}
				});
	}
	
	public SortedSet<CustomerStake> getBetOfferIdTopStakes(int betOfferId) {
		return betOfferData.get(betOfferId);
	}

	@Override
	public void clear() {
		betOfferData.clear();
	}
}
