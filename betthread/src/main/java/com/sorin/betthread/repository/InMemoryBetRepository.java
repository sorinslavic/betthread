package com.sorin.betthread.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.sorin.betthread.Log;
import com.sorin.betthread.handler.DispatchHandler;

public class InMemoryBetRepository implements BetRepository {
	private static final Log log = new Log(InMemoryBetRepository.class);
	
	private static final InMemoryBetRepository instance = new InMemoryBetRepository();
	
	private static final Comparator<CustomerStake> REVERSED_STAKE_COMPARATOR = Comparator.reverseOrder();
	
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
						SortedSet<CustomerStake> stakes = new TreeSet<>(REVERSED_STAKE_COMPARATOR);
						CustomerStake cs = new CustomerStake(customerId, stake);
						stakes.add(cs);
						return Collections.synchronizedSortedSet(stakes);
					} else {
						Optional<CustomerStake> optionalCs = set.stream().filter(cs -> cs.getCustomerId() == customerId).findAny();
						if (optionalCs.isPresent()) {
							CustomerStake existingCs = optionalCs.get();
							if (existingCs.getStake() < stake) {
								set.remove(existingCs);
								set.add(new CustomerStake(customerId, stake));
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
		log.info("clear - clearing bet repository data");
		betOfferData.clear();
	}
}
