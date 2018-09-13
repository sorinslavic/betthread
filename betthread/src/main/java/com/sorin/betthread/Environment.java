package com.sorin.betthread;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Singleton to load and keep the runtime command line parameters. The parameters are expected to be set in the form of: "-key=value"</br>
 * We handle 3 parameters:</br>
 * </br>
 * <ul>
 * <li> port - what port to open the http server on; default is 8001 </li>
 * <li> numOfThreads - how many threads to configure in the pool to handle requests; default is how many processors the current runtime has available </li>
 * <li> timeoutMinutes - session key validity duration in minutes; default is 10 minutes </li>
 * </ul>
 * @author Sorin.Slavic
 *
 */
public class Environment {
	private static final Log log = new Log(Environment.class);
	private static Environment environment;

	public static final Charset CHARSET = Charset.forName("US-ASCII");
	public static final int SESSION_TIMEOUT_MINUTES = 10;
	public static final int PORT = 8001;
	public static final int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors();
	
	private final int port;
	private final int timeoutMilis;
	private final int numOfThreads;

	private Environment(int port,
			int timeoutMilis, int numOfThreads) {
		this.port = port;
		this.timeoutMilis = timeoutMilis;
		this.numOfThreads = numOfThreads;
		
		log.info("Environment built: " + this.toString());
	}
	
	public static Environment getEnv() {
		if (environment == null)
			throw new RuntimeException("Environment not loaded ! Method called too soon!");
		
		return environment;
	}
	
	/**
	 * Load the Environment instance based on the given command line arguments
	 * @param args
	 */
	public static void load(String[] args) {
		log.info("load - build environment based on args: " + Arrays.asList(args));
	
		int port = Arrays.stream(args)
				.filter(arg -> arg.startsWith("-port"))
				.mapToInt(arg -> getValue(arg)).findFirst()
				.orElseGet(() -> PORT);
		
		int timeout = Arrays.stream(args)
				.filter(arg -> arg.startsWith("-timeoutMinutes"))
				.mapToInt(arg -> getValue(arg)).findFirst()
				.orElseGet(() -> SESSION_TIMEOUT_MINUTES);
		
		int numOfThreads = Arrays.stream(args)
				.filter(arg -> arg.startsWith("-numOfThreads"))
				.mapToInt(arg -> getValue(arg)).findFirst()
				.orElseGet(() -> NUM_OF_THREADS);
		
		Environment.environment = new Environment(port, 1000 * 60 * timeout, numOfThreads);
	}
		
	public int getPort() {
		return port;
	}

	public int getTimeoutMilis() {
		return timeoutMilis;
	}

	public int getNumOfThreads() {
		return numOfThreads;
	}
	
	private static int getValue(String arg) {
		String[] parts = arg.split("=");
		if (parts.length != 2)
			throw new IllegalArgumentException("Command line argument " + arg + " is invalid!");
		
		try {
			return Integer.valueOf(parts[1]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Command line argument " + arg + " does not have a valid INT value!");
		}
	}

	@Override
	public String toString() {
		return "Environment [port=" + port + ", timeoutMilis=" + timeoutMilis + ", numOfThreads=" + numOfThreads + "]";
	}
}
