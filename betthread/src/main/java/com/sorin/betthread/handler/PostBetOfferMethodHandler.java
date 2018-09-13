package com.sorin.betthread.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.sorin.betthread.BetThreadApp;
import com.sorin.betthread.Log;
import com.sorin.betthread.repository.BetRepository;
import com.sorin.betthread.session.InvalidSessionKeyException;
import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;

/**
 * Submit a new bet offer stake for a particular session;
 * 
 * If the session is valid - existing and not expired - we will identify the customer
 * associated with that session - and add in the {@link BetRepository} the Stake passed
 * inside the body of the POST request.
 * 
 * @author Sorin.Slavic
 *
 */
public class PostBetOfferMethodHandler {
	private static final Log log = new Log(PostBetOfferMethodHandler.class);

	private final SessionCache sessionCache;
	private final BetRepository betRepo;
	
	public PostBetOfferMethodHandler(SessionCache sessionCache,
			BetRepository betRepo) {
		this.sessionCache = sessionCache;
		this.betRepo = betRepo;
	}
	
	public void perform(String path, String query, InputStream requestBody) throws HttpStatusCodeException, IOException {		
		// handle paths of pattern: /<betofferid>/stake
		// query should be ?sessionkey=<sessionkey>
		int betOfferId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
		
		Session session = getSession(query);		
		int customerId = session.getCustomerId();

		log.debug("perform - add new stake for customerId: " + customerId + " on betOfferId: " + betOfferId);
		
		// read the content of the input stream
		// TODO - move to a dedicated reader implementation as to cleanup the method body here
		InputStreamReader reader = new InputStreamReader(requestBody, BetThreadApp.CHARSET);
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[10];
		int read;
		while ((read = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, read);
		}
		
		int stake = Integer.valueOf(sb.toString());
		betRepo.placeStake(stake, customerId, betOfferId);
	}

	/**
	 * Try to retrieve the session parameterized in the query.
	 * 
	 * Only a valid session will be returned - if the session does not exist or it is invalid
	 * a {@link HttpStatusCodeException} will be throws with error code 403 Forbidden.
	 * 
	 * Once the session is identified - if it is still valid - it's expiration period will be updated and 
	 * a new timeout will be set for it.
	 * 
	 * @param query - the URI format query part of the request - should be: sessionkey=value
	 * @return - the valid session identified from the sessionkey
	 * @throws HttpStatusCodeException
	 */
	private Session getSession(String query) throws HttpStatusCodeException {		
		String sessionKey = PathUtil.getParameterValue(query, "sessionkey");
		log.debug("getSession - verify session for key: " + sessionKey);
		
		Session session;
		try {
			session = sessionCache.getSession(sessionKey);
			if (session == null)
				throw new InvalidSessionKeyException("No valid session defined! Please generate a sessionkey first.");
			
			if (session.isValid()) {
				session.updateExpiration();
			} else {
				throw new InvalidSessionKeyException("Session expired! Please generate a new sessionkey and try again.");
			}
		} catch (InvalidSessionKeyException e) {
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_FORBIDDEN, e.getMessage());
		}
		
		return session;
	}
}
