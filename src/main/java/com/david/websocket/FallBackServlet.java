package com.david.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.json.DataObjectFactory;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;

/**
 * @author david.sanchez Servlet que implementa el fallback de websockets con
 *         AJAX polling
 */
@WebServlet(urlPatterns = "/tuitfallback")
public class FallBackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private HashMap<String, ConcurrentLinkedQueue<String>> statusBuffer = new HashMap<String, ConcurrentLinkedQueue<String>>();
	private Queue<TwitterStream> twitterPool = new ConcurrentLinkedQueue<TwitterStream>();
	private final static String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private final static String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String user = req.getParameter("user");
		resp.getWriter().write(getTuits(user));

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			String user = req.getParameter("user");
			String filter = req.getParameter("filter");

			if (req.getParameter("stop") != null) {

				statusBuffer.remove(user);
				for (TwitterStream t : twitterPool) {
					if (user.equals(t.getScreenName()))
						t.shutdown();
				}

			} else {

				AccessToken at = MongoDao.getInstance().getUser(user);
				TwitterStream twitter = new TwitterStreamFactory(
						new ConfigurationBuilder().setJSONStoreEnabled(true)
								.setHttpStreamingReadTimeout(100000)
								.setHttpRetryCount(1).build()).getInstance();

				twitter.setOAuthConsumer(OAuthConsumerKey, OAuthConsumerSecret);
				twitter.setOAuthAccessToken(at);
				StatusAdapterFallback statusR = new StatusAdapterFallback(
						filter, user);
				twitter.addListener(statusR);
				FilterQuery query = new FilterQuery();
				query.track(new String[] { filter });
				twitter.filter(query);
				statusBuffer.put(user, new ConcurrentLinkedQueue<String>());
				twitterPool.add(twitter);
			}

		} catch (DaoException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private String getTuits(String user) {
		JSONArray res = new JSONArray();

		ConcurrentLinkedQueue<String> echoBufferFIFO = statusBuffer.get(user);
		for (String msg : echoBufferFIFO) {
			res.put(msg);
			echoBufferFIFO.remove(msg);
		}

		return res.toString();
	}

	private class StatusAdapterFallback extends StatusAdapter {
		private String filter;
		private String user;

		public StatusAdapterFallback(String filter, String user) {
			super();
			this.filter = filter;
			this.user = user;
		}

		@Override
		public void onStatus(Status status) {

			String tweet = DataObjectFactory.getRawJSON(status);
			ConcurrentLinkedQueue<String> echoBufferFIFO = statusBuffer.get(user);
			echoBufferFIFO.add(tweet);

			try {
				MongoDao.getInstance().addTuit(tweet, filter);
			} catch (DaoException e1) {
				e1.printStackTrace();
			}
		}

	}
}
