package com.sorin.betthread.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.sorin.betthread.BetThreadApp;
import com.sorin.betthread.repository.BetRepository;
import com.sorin.betthread.session.InvalidSessionKeyException;
import com.sorin.betthread.session.Session;
import com.sorin.betthread.session.SessionCache;

public class PostBetOfferMethodHandler {

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
		
		if (! path.endsWith("stake")) {
			throw new HttpStatusCodeException(HttpURLConnection.HTTP_NOT_FOUND, "Path: " + path + " is not a known endpoint.");
		}
		int betOfferId = Integer.valueOf(PathUtil.getFirstPartOfPath(path));
		
		Session session = getSession(query);		
		int customerId = session.getCustomerId();
		
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

	private Session getSession(String query) throws HttpStatusCodeException{
		String sessionKey = PathUtil.getParameterValue(query, "sessionkey");
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
