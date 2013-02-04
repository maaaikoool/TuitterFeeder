package com.david.tuit;

import java.io.IOException;
import java.nio.CharBuffer;

import org.apache.catalina.websocket.WsOutbound;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.json.DataObjectFactory;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class StatusListenerWebSocket extends StatusAdapter {
	private WsOutbound out;
	private String filter;

	public StatusListenerWebSocket(WsOutbound out, String filter) {
		super();
		this.out = out;
	}

	
	@Override
	public void onStatus(Status status) {

		String tweet = DataObjectFactory.getRawJSON(status);
		DBObject tuitDB = (DBObject) JSON.parse(tweet);
		tuitDB.put("filter", filter);

		try {
			MongoDao.getInstance().saveTuitToDB(tuitDB); 
		} catch (DaoException e1) {
			e1.printStackTrace();
		}
		try {
			out.writeTextMessage(CharBuffer.wrap(tweet.toCharArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
