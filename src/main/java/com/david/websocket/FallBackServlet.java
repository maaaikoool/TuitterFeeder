package com.david.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
 * @author david.sanchez
 * Servlet que implementa el fallback de websockets con AJAX polling
 */
@WebServlet(urlPatterns = "/tuitfallback")
public class FallBackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private HashMap<String, ConcurrentLinkedQueue<StatusPOJO>> usrBuffer = new HashMap<String, ConcurrentLinkedQueue<StatusPOJO>>();
	private Queue<TwitterStream> twitterPool = new ConcurrentLinkedQueue<TwitterStream>();
	private final static String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private final static String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String user = req.getParameter("user");
		long previousRequest = getRequestTimestamp(req, "previousRequest");
		long currentRequest = getRequestTimestamp(req, "currentRequest");

		resp.getWriter().write(getTuits(previousRequest, currentRequest, user));

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			String user = req.getParameter("user");
			String filter = req.getParameter("filter");

			if (req.getParameter("stop") != null) {

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
				StatusAdapterFallback statusR = new StatusAdapterFallback(filter, user);
				twitter.addListener(statusR);
				FilterQuery query = new FilterQuery();
				query.track(new String[] { filter });
				twitter.filter(query);
				usrBuffer.put(user, new ConcurrentLinkedQueue<StatusPOJO>());
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

	private class StatusPOJO {
		public long timestamp;
		public String status;

		public StatusPOJO(String s) {
			this.status = s;
			this.timestamp = System.currentTimeMillis();
		}
	}

	private String getTuits(long minTimestamp, long maxTimestamp, String user) {
		JSONArray res = new JSONArray();

		ConcurrentLinkedQueue<StatusPOJO> echoBufferFIFO = usrBuffer.get(user);
		for (StatusPOJO msg : echoBufferFIFO) {

			// System.out.println("for msg fifo min"+minTimestamp +"  nuestro"+
			// msg.timestamp+ " max"+maxTimestamp);
			// System.out.println(msg.timestamp > minTimestamp);
			// System.out.println(msg.timestamp <= maxTimestamp);
			// if (minTimestamp > -1 && msg.timestamp > minTimestamp
			// && msg.timestamp <= maxTimestamp) {
			res.put(msg.status);
			echoBufferFIFO.remove(msg);
			// }
		}

		return res.toString();
	}

	private long getRequestTimestamp(HttpServletRequest req,
			String parameterName) {
		String parameter = getQueryMap(req.getQueryString()).get(parameterName);

		long timestamp = -1;
		try {
			timestamp = Long.parseLong(parameter);
		} catch (NumberFormatException e) {
		}
		;

		return timestamp;
	}

	private Map<String, String> getQueryMap(String queryString) {
		Map<String, String> map = new HashMap<String, String>();

		if (queryString == null)
			return map;

		String[] params = queryString.split("&");
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
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
			ConcurrentLinkedQueue<StatusPOJO> echoBufferFIFO = usrBuffer
					.get(user);
			echoBufferFIFO.add(new StatusPOJO(tweet));

			try {
				MongoDao.getInstance().addTuit(tweet, filter);
			} catch (DaoException e1) {
				e1.printStackTrace();
			}
		}

	}
}
