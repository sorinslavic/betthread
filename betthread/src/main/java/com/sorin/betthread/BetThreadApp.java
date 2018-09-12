package com.sorin.betthread;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import com.sorin.betthread.handler.DispatchHandler;
import com.sorin.betthread.repository.InMemoryBetRepository;
import com.sorin.betthread.session.ConcurrentSessionCache;
import com.sun.net.httpserver.HttpServer;

public class BetThreadApp {
	private static final Log log = new Log(BetThreadApp.class);
	
	public static final Charset CHARSET = Charset.forName("US-ASCII"); // move to property	
	public static final int SESSION_TIMEOUT_MILIS = 1000 * 60 * 10; // 10 minutes - move to property
	
	public static void main(String[] args) throws Exception {
		log.info("main - starting web app on port 8001");
		InetSocketAddress socket = new InetSocketAddress("localhost", 8001); // move port to property
		HttpServer server = HttpServer.create(socket, 0);
		
		int numOfProcessors = Runtime.getRuntime().availableProcessors();
		log.debug("main - identfieid numOfProcessors: " + numOfProcessors + "; this is the size of the poool");
		server.setExecutor(Executors.newFixedThreadPool(numOfProcessors));
		
		server.createContext("/", new DispatchHandler(ConcurrentSessionCache.getInstance(), InMemoryBetRepository.getInstance()));
		
		server.start();
	}

}
