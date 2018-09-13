package com.sorin.betthread.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.sorin.betthread.Log;

/**
 * Singleton implementation.
 * 
 * Store the placed Bets in Concurrent access Map.
 * 
 * For each BetOfferId - we will keep an entry in a synchronized hash map with the value
 * representing the list (Sortet Set actually) - of stakes that were placed.
 * 
 * @author Sorin.Slavic
 *
 */
public class InMemoryBetRepository implements BetRepository {
	private static final Log log = new Log(InMemoryBetRepository.class);
	
	private static final InMemoryBetRepository instance = new InMemoryBetRepository();
	
	/**
	 * The stakes are kept in descending natural order
	 */
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
						stakes.add(new CustomerStake(customerId, stake));
						
						// FIXME - does it really need to be synchronized ?
						// isn't the map already synchronized ?
						return Collections.synchronizedSortedSet(stakes);
					} else {
						Optional<CustomerStake> optionalCs = set.stream().filter(cs -> cs.getCustomerId() == customerId).findAny();

						// we try to maintain "uniqueness" of customer stake set data
						// but we do not "trust" the Set implementation to do it ... 
						// since we will want to override certain values if already present
						if (optionalCs.isPresent()) {
							CustomerStake existingCs = optionalCs.get();
							if (existingCs.getStake() < stake) {
								// FIXME - would it be easier to "update" the customer stake value ?
								// that would lower the number of in memory objects - but it would not keep
								// the set sorted ... or would it ?
								set.remove(existingCs);
								set.add(new CustomerStake(customerId, stake));
							}
						} else {
							set.add(new CustomerStake(customerId, stake));	
						}
						return set;
					}
				});
	}
	
	@Override
	public SortedSet<CustomerStake> getBetOfferIdTopStakes(int betOfferId) {
		SortedSet<CustomerStake> data = betOfferData.get(betOfferId);
		if (data != null)
			return Collections.unmodifiableSortedSet(data);
		
		return null;
	}

	public void clear() {
		log.info("clear - clearing bet repository data");
		betOfferData.clear();
	}
}
