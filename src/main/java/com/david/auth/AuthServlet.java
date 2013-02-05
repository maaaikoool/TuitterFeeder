package com.david.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.RequestToken;

/**
 * @author david.sanchez
 *Crea un request token para la aplicación y se dirige a Twitter para la autorización del usuario
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

	private static final long serialVersionUID = 3895941826584737619L;
	private static final String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private static final String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		TwitterStream twitter = new TwitterStreamFactory().getInstance();
		twitter.setOAuthConsumer(OAuthConsumerKey, OAuthConsumerSecret);

		try {
			String callback = getURL(req) + "/back";
			RequestToken requestToken = twitter.getOAuthRequestToken(callback);
			String url = requestToken.getAuthenticationURL();
			req.getSession().setAttribute("requestToken", requestToken);

			req.getSession().setAttribute("twitter", twitter);

			resp.sendRedirect(url);

		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private String getURL(HttpServletRequest req) {

		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		return url.toString();
	}
}
