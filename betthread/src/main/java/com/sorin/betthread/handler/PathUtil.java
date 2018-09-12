package com.sorin.betthread.handler;

import java.net.HttpURLConnection;

public class PathUtil {

	/**
	 * Our "server" only support the following methods:
	 * GET /<customerId>/session
	 * GET /<betOfferId>/highstakes
	 * POST /<betOfferId>/stake?sessionkey=<sessionkey>
	 * 
	 * A pattern emerges where the only parameterized part is the first part of the path
	 * 
	 * @param path - the URI path string that must follow the pattern: /<firstPart>/<secondPart>[?optional]
	 * @return the first part of the URI
	 * @throws HttpStatusCodeException - if the path is not formated correctly
	 */
	public static String getFirstPartOfPath(String path) throws HttpStatusCodeException {
		// FIXME maybe path.split("/") is faster
		
		if (! path.startsWith("/"))
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Path: " + path + " does not start with slash.");
		
		path = path.substring(1);
		
		String[] pathParts = path.split("/");
		if (pathParts.length != 2)
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Path: " + path + "  does not follow known pattern.");
		
		return pathParts[0];
	}
	
	/**
	 * Parse the query URI string in search for the given query parameter. 
	 * 
	 * @param query - URI query that must include a parameter query; Separator from key to value is "="
	 * @param parameter - the case sensitive name of the parameter we search for
	 * @return the value of the parameter in the url
	 * @throws HttpStatusCodeException - if the query is not correct or of the parameter is missing
	 */
	public static String getParameterValue(String query, String parameter) throws HttpStatusCodeException {
		String[] queryParts = query.split("=");
		
		if (queryParts.length != 2)
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Path query: " + query + " is invalid.");
		
		if (! parameter.equals(queryParts[0]))
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_INTERNAL_ERROR, "Query parameter: " + parameter + " is not set on query: " + query);
		
		return queryParts[1];
	}
}
