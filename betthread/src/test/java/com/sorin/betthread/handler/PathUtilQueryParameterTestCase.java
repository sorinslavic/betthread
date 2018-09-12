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
public class PathUtilQueryParameterTestCase {

	@Rule
	public ExpectedException expectedExceptionRule = ExpectedException.none();
	
	private final String fullPath;
	private final String firstPart;
	
	public PathUtilQueryParameterTestCase(String fullPath, String firstPart, boolean expectException) {
		this.fullPath = fullPath;
		this.firstPart = firstPart;
		
		if (expectException)
			expectedExceptionRule.expect(HttpStatusCodeException.class);
	}
	
	@Parameters(name = "first part of {0}")
	public static List<Object[]> getParameters() {
		return Arrays.asList(new Object [][] {
			{"/betOfferId/stake?sessionkey=key", "betOfferId", false},
			{"/customerid/session", "customerid", false},
			{"/betOfferId/highstakes", "betOfferId", false},
			{"betOfferId/highstakes", null, true},
			{"/betOfferId-highstakes", null, true},
		});
	}
	
	@Test
	public void validatePathParser() throws HttpStatusCodeException {
		assertEquals(firstPart, PathUtil.getFirstPartOfPath(fullPath));
	}
}
