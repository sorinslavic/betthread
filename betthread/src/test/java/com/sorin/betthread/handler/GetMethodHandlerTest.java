package com.sorin.betthread.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

import com.sorin.betthread.MockHttpExchange;

public class GetMethodHandlerTest {

	@Test
	public void test() throws IOException, URISyntaxException, NoSuchAlgorithmException {
		MockHttpExchange mockExchange = new MockHttpExchange("GET", new URI("/1234/session"), "");
		new DispatchHandler().handle(mockExchange);
		
		System.out.println(mockExchange.getActualOutput());
		System.out.println(mockExchange.getActualContentLength());
		System.out.println(mockExchange.getActualStatusCode());
		
		System.out.println(UUID.randomUUID().toString());
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		System.out.println(messageDigest.digest(UUID.randomUUID().toString().getBytes()));
	}
}
