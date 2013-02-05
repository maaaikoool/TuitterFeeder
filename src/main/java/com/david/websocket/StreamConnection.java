package com.david.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;

/**
 * @author david.sanchez
 * Implementación del WebSocket. Conecta al TwitterStream API y envia los tuits recibidos a través del socket
 */
public class StreamConnection extends MessageInbound {

	private String filter;
	private static final String pFiltro = "filter";
	private TwitterStream twitter;
	private final static String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private final static String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

	public StreamConnection(HttpServletRequest request) {
		this.filter = request.getParameter(pFiltro);

		twitter = new TwitterStreamFactory(new ConfigurationBuilder()
				.setJSONStoreEnabled(true).build()).getInstance();
		twitter.setOAuthConsumer(OAuthConsumerKey, OAuthConsumerSecret);
		AccessToken accessToken = (AccessToken) request.getSession()
				.getAttribute("accessToken");
		twitter.setOAuthAccessToken(accessToken);

	}

	@Override
	protected void onOpen(WsOutbound outbound) {

		StatusAdapterWebSocket statusR = new StatusAdapterWebSocket(outbound,
				filter);
		twitter.addListener(statusR);
		FilterQuery query = new FilterQuery();
		query.track(new String[] { filter });
		twitter.filter(query);
	}

	@Override
	protected void onClose(int status) {
		twitter.cleanUp();
		// twitter.shutdown();
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
	}

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
	}

	
	
	private class StatusAdapterWebSocket extends StatusAdapter {
		private WsOutbound out;
		private String filter;

		public StatusAdapterWebSocket(WsOutbound out, String filter) {
			super();
			this.out = out;
			this.filter = filter;
		}

		@Override
		public void onStatus(Status status) {

			String tweet = DataObjectFactory.getRawJSON(status);

			try {
				MongoDao.getInstance().addTuit(tweet, filter);
				out.writeTextMessage(CharBuffer.wrap(tweet.toCharArray()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DaoException e1) {
				e1.printStackTrace();
			}
		}
	}

}
