package com.sorin.betthread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class MockHttpExchange extends HttpExchange {
	
	private final String method;
	private final URI uri;
	
	private final ByteArrayOutputStream actualOutputStream;
	private final ByteArrayInputStream actualInputStream;
	
	private int actualStatusCode;
	private long actualContentLength;
	
	public MockHttpExchange(String method, URI uri,
			String content) {
		this.method = method;
		this.uri = uri;
		this.actualOutputStream = new ByteArrayOutputStream();
		this.actualInputStream = new ByteArrayInputStream(content.getBytes());
	}
	
	public String getActualOutput() {
		return actualOutputStream.toString();
	}
	
	public int getActualStatusCode() {
		return actualStatusCode;
	}
	
	public long getActualContentLength() {
		return actualContentLength;
	}
	
	@Override
	public void setStreams(InputStream arg0, OutputStream arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendResponseHeaders(int arg0, long arg1) throws IOException {
		actualStatusCode = arg0;
		actualContentLength = arg1;
	}
	
	@Override
	public Headers getResponseHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getResponseCode() {
		return actualStatusCode;
	}
	
	@Override
	public OutputStream getResponseBody() {
		return actualOutputStream;
	}
	
	@Override
	public URI getRequestURI() {
		return uri;
	}
	
	@Override
	public String getRequestMethod() {
		return method;
	}
	
	@Override
	public Headers getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public InputStream getRequestBody() {
		return actualInputStream;
	}
	
	@Override
	public InetSocketAddress getRemoteAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public HttpPrincipal getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public InetSocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public HttpContext getHttpContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}