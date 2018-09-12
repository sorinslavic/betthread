package com.sorin.betthread.repository;

public class CustomerStake {
	private final int customerId;
	private int stake;
	
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
	
	public void setStake(int stake) {
		this.stake = stake;
	}
	
	@Override
	public String toString() {
		return customerId + "=" + stake;
	}
}
