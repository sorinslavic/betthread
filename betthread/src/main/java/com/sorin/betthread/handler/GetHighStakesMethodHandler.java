package com.sorin.betthread.handler;

import java.util.Collection;
import java.util.stream.Collectors;

import com.sorin.betthread.Log;
import com.sorin.betthread.repository.BetRepository;
import com.sorin.betthread.repository.CustomerStake;

/**
 * Retrieve the top 20 highest stakes currently stored in the {@link BetRepository}
 * for the given betOfferId;
 * @author Sorin.Slavic
 *
 */
public class GetHighStakesMethodHandler {
	private static final Log log = new Log(GetHighStakesMethodHandler.class);

	private final BetRepository betRepo;
	
	public GetHighStakesMethodHandler(BetRepository betRepo) {
		this.betRepo = betRepo;
	}
	
	public String perform(String path) throws HttpStatusCodeException {		
		// /<betofferid>/highstakes
		int betOfferId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
		log.debug("perform - get highest stakes for betOfferId: " + betOfferId);
		
		Collection<CustomerStake> stakes = betRepo.getBetOfferIdTopStakes(betOfferId);
		if (stakes == null)
			return "";
		
		// the CustomerStake#toString is equivalent to this - but I do not want to mix internal toString implementations
		// with the view layer logic - meh - it's the little things
		return stakes.stream().limit(20).map(cs -> cs.getCustomerId() + "=" + cs.getStake()).collect(Collectors.joining(","));
	}

}
