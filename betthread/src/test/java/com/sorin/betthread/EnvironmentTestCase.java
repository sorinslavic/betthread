package com.sorin.betthread;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentTestCase {
	
	@Test
	public void testDefault() {
		Environment.load(new String[] {});
		assertEquals(Runtime.getRuntime().availableProcessors(), Environment.getEnv().getNumOfThreads());
		assertEquals(Environment.PORT, Environment.getEnv().getPort());
		assertEquals(Environment.SESSION_TIMEOUT_MINUTES * 60 * 1000, Environment.getEnv().getTimeoutMilis());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidParameterFormat() {
		Environment.load(new String[] {"-port:8080"});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidParameterValue() {
		Environment.load(new String[] {"-port=eightyeighty"});
	}
	
	@Test
	public void testValidateArgParams() {
		Environment.load(new String[] {"-port=123", "-numOfThreads=321", "-timeoutMinutes=2"});
		assertEquals(321, Environment.getEnv().getNumOfThreads());
		assertEquals(123, Environment.getEnv().getPort());
		assertEquals(2 * 60 * 1000, Environment.getEnv().getTimeoutMilis());
	}
}
