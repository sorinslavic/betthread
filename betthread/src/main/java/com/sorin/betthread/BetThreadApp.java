package com.sorin.betthread;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import com.sorin.betthread.handler.DispatchHandler;
import com.sun.net.httpserver.HttpServer;

public class BetThreadApp {
	
	public static final Charset CHARSET = Charset.forName("US-ASCII"); // move to property	
	public static final int SESSION_TIMEOUT_MILIS = 1000 * 60 * 10; // 10 minutes - move to property
	
	public static void main(String[] args) throws Exception {
		InetSocketAddress socket = new InetSocketAddress("localhost", 8001); // move port to property
		HttpServer server = HttpServer.create(socket, 0);
		
		server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		
		server.createContext("/", new DispatchHandler());
		
		server.start();
	}

}
