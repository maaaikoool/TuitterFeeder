package com.david.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.auth.AccessToken;
import twitter4j.internal.org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/**
 * @author david.sanchez
 * DAO de MongoDB
 */
public class MongoDao {

	private Mongo connection = null;
	private DB db = null;
	private static final String dbName = "tuiter";
	private static final String collectionTuit = "tuit";
	private static final String collectionUser = "users";
	private static final String tuitID = "id_str";
	private static final String tuitCreatedAt = "created_at";
	private static final String tuitFavorito = "favorito";
	private static final String tuitFilter = "filter";
	private static final String userScreenName = "screenName";
	private static final String userID = "userId";
	private static final String userToken = "token";
	private static final String userTokenSecret = "tokenSecret";
	

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	private static MongoDao mongoDao = null;

	private MongoDao() throws UnknownHostException {
		// Connection is created as below
		connection = new Mongo("localhost", 27017);
		db = connection.getDB(dbName);
	}

	// A singleton design pattern to get the connection for the outside world
	public static MongoDao getInstance() {
		if (mongoDao == null) {
			try {
				mongoDao = new MongoDao();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return mongoDao;
	}

	/*
	 * This is how you insert a record in the database Every record is a
	 * DBObject which is a map type where a key is the column name and value is
	 * column value
	 */
	private void saveToDB(String tableName, DBObject dbObject)
			throws DaoException {
		DBCollection dbCollection = db.getCollection(tableName);
		dbCollection.insert(dbObject);
	}

	public String addFavoritos(String user, String id) throws DaoException {
		DBCollection col = db.getCollection(collectionTuit);
		BasicDBObject query = new BasicDBObject(tuitID, id);
		DBCursor cursor = col.find(query);
		DBObject nuevo = null;
		if (cursor.hasNext()) {
			nuevo = cursor.next();
		} else {
			throw new DaoException("Tuit encontrado " + id);
		}
		nuevo.put(tuitFavorito, user);
		col.update(query, nuevo);
		return nuevo.toString();
	}

	public String getFavoritos(String user) {
		DBCollection col = db.getCollection(collectionTuit);
		BasicDBObject old = new BasicDBObject(tuitFavorito, user);
		DBCursor cursor = col.find(old)
				.sort(new BasicDBObject(tuitCreatedAt, 1));
		List<String> res = new ArrayList<>();
		while (cursor.hasNext()) {
			DBObject tuit = cursor.next();
			res.add(tuit.toString());
		}
		JSONArray jArray = new JSONArray(res);
		return jArray.toString();
	}

	public void addUser(AccessToken at) throws DaoException {
		BasicDBObject db = new BasicDBObject();
		db.put(userScreenName, at.getScreenName());
		db.put(userToken, at.getToken());
		db.put(userTokenSecret, at.getTokenSecret());
		db.put(userID, at.getUserId());
		saveToDB(collectionUser, db);
	}

	public AccessToken getUser(String screenName) throws DaoException {
		DBCollection col = db.getCollection(collectionUser);

		BasicDBObject db = new BasicDBObject(userScreenName, screenName);
		DBCursor cursor = col.find(db);
		if (cursor.hasNext()) {
			DBObject user = cursor.next();
			AccessToken at = new AccessToken(user.get(userToken).toString(), user
					.get(userTokenSecret).toString(), (long) user.get(userID));
			return at;
		} else
			return null;

	}
	
	public void addTuit(String tuit, String filter) throws DaoException
	{
		DBObject tuitDB = (DBObject) JSON.parse(tuit);
		tuitDB.put(tuitFilter, filter);
		saveTuitToDB(tuitDB);
	}

	private void saveTuitToDB(DBObject tuitDB) throws DaoException {
		saveToDB(collectionTuit, tuitDB);

	}

}