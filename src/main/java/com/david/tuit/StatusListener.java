package com.david.tuit;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.json.DataObjectFactory;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


public class StatusListener extends StatusAdapter {
	private String filter;
	private String user;

	
	public StatusListener(String filter, String user) {
		super();
		this.filter = filter;
		this.user = user;
	}

	@Override
	public void onStatus(Status status) {

		String tweet = DataObjectFactory.getRawJSON(status);
		DBObject tuitDB = (DBObject) JSON.parse(tweet);
		tuitDB.put("filter", filter);
		tuitDB.put("filterBy", user);

		try {
			MongoDao.getInstance().saveTuitToDB(tuitDB); 
		} catch (DaoException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();

	}

}
