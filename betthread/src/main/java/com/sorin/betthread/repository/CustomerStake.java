package com.sorin.betthread.repository;

/**
 * Simple immutable POJO for a Customer Stake record;
 * 
 * {@link Comparable} implementation assures this object is used correctly and that we can sort
 * automatically any collection of CustomerStake.
 * 
 * @author Sorin.Slavic
 *
 */
public class CustomerStake implements Comparable<CustomerStake> {
	private final int customerId;
	private final int stake;
	
	public CustomerStake(int customerId, int stake) {
		this.customerId = customerId;
		this.stake = stake;
	}
	
	public int getCustomerId() {
		return customerId;
	}

	public int getStake() {
		return stake;
	}

	// need to sort both using stake and customerId - because we do not 
	// want to think the objects are equal - because compareTo = 0 for the same stake
	// even if the customerId is different
	@Override
	public int compareTo(CustomerStake o) {
		int stakeDiff = this.stake - o.stake;
		
		if (stakeDiff == 0)
			return this.customerId - o.customerId;
		
		return stakeDiff;
	}
		
	@Override
	public String toString() {
		return customerId + "=" + stake;
	}
}
