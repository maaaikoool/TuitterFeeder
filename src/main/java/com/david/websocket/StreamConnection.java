package com.david.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.david.tuit.StatusListenerWebSocket;

public class StreamConnection extends MessageInbound {

	private String filter;
	private static final String pFiltro = "filter";
	private TwitterStream twitter;
	private final String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private final String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

	public StreamConnection(HttpServletRequest request) {
		this.filter = request.getParameter(pFiltro);

		twitter = new TwitterStreamFactory(new ConfigurationBuilder()
				.setJSONStoreEnabled(true).build()).getInstance();

		twitter.setOAuthConsumer(OAuthConsumerKey, OAuthConsumerSecret);

		AccessToken accessToken = (AccessToken) request.getSession()
				.getAttribute("accessToken");
		twitter.setOAuthAccessToken(accessToken);
		try {
			System.out.println("AUTHED "+twitter.getScreenName());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onOpen(WsOutbound outbound) {

		StatusListenerWebSocket statusR = new StatusListenerWebSocket(outbound, filter);
		twitter.addListener(statusR);
		FilterQuery query = new FilterQuery();
		query.track(new String[] { filter });
		twitter.filter(query);
	}

	@Override
	protected void onClose(int status) {
		twitter.cleanUp();
//		twitter.shutdown();
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
	}

}
