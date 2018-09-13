package com.sorin.betthread;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import com.sorin.betthread.handler.DispatchHandler;
import com.sorin.betthread.repository.InMemoryBetRepository;
import com.sorin.betthread.session.ConcurrentSessionCache;
import com.sun.net.httpserver.HttpServer;

/**
 * Main method class;</br>
 * </br>
 * Start an HTTP Server on the {@link Environment#getPort()};</br>
 * Requests will be handled on a separate threads configured and managed by a ThreadPool with size {@link Environment#getNumOfThreads()}</br>
 * </br></br>
 * To start the application we can run:</br>
 * <code>
 * java -jar betthread.jar -port=8001 -numOfThreads=4 -timeoutMinutes=3 
 * </code>
 * </br></br>
 * The defaults noted in {@link Environment} will be used if arguments are not set</br>
 * <code>java -jar betthread.jar</code>
 * 
 * @author Sorin.Slavic
 *
 */
public class BetThreadApp {
	private static final Log log = new Log(BetThreadApp.class);
	
	public static final Charset CHARSET = Charset.forName("US-ASCII");
	
	public static void main(String[] args) throws Exception {
		Environment.load(args);

		log.info("main - starting web app on port " + Environment.getEnv().getPort());
		
		InetSocketAddress socket = new InetSocketAddress("localhost", Environment.getEnv().getPort());
		HttpServer server = HttpServer.create(socket, 0);
		
		log.debug("main - building thread pool with size: " + Environment.getEnv().getNumOfThreads());
		server.setExecutor(Executors.newFixedThreadPool(Environment.getEnv().getNumOfThreads()));
		
		server.createContext("/", new DispatchHandler(ConcurrentSessionCache.getInstance(), InMemoryBetRepository.getInstance()));
		
		server.start();
		log.info("main - server started - awaiting requests ...");
	}

}
