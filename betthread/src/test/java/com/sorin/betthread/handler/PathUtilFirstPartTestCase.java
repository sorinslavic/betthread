package com.sorin.betthread.handler;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PathUtilFirstPartTestCase {

	@Rule
	public ExpectedException expectedExceptionRule = ExpectedException.none();
	
	private final String fullQuery;
	private final String parameter;
	private final String value;
	
	public PathUtilFirstPartTestCase(String fullQuery, String parameter, String value, 
			boolean expectException) {
		this.fullQuery = fullQuery;
		this.parameter = parameter;
		this.value = value;
		
		if (expectException)
			expectedExceptionRule.expect(HttpStatusCodeException.class);
	}
	
	@Parameters(name = "first part of {0}")
	public static List<Object[]> getParameters() {
		return Arrays.asList(new Object [][] {
			{"sessionkey=key", "sessionkey", "key", false},
			{"sessionkey-equals-key", "sessionkey", "key", true},
			{"sessionkeyMissing=key", "sessionkey", "key", true}
		});
	}
	
	@Test
	public void validatePathParser() throws HttpStatusCodeException {
		assertEquals(value, PathUtil.getParameterValue(fullQuery, parameter));
	}
}
