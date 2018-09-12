package com.sorin.betthread;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class Log {

	private static final String FORMAT = "%tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s\t - [%3$s] %4$s: %5$s"; 
	
	private final Class<?> clazz;
	
	public Log(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public void info(String message) {
		printLogMessage("INFO", message);
	}
	
	public void debug(String message) {
		printLogMessage("DEBUG", message);
	}
	
	public void error(String message, Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		// do we need to close ?

		printLogMessage("ERROR", message + System.lineSeparator() + sw.toString());
	}
	
	private void printLogMessage(String level, String message) {
		System.out.println(String.format(FORMAT, new Date(), level, Thread.currentThread().getName(), clazz.getName(), message));
	}
}
