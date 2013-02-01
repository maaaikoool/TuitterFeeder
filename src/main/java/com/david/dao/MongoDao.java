package com.david.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import twitter4j.auth.AccessToken;
import twitter4j.internal.org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDao {

	private Mongo connection = null;
	private DB db = null;
	private static final String dbName = "tuiter";
	private static final String collectionTuit = "tuit";
	private static final String collectionUser = "users";

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

	// This is how you create a table in the mongoDB
	public void createTable(String tableName) throws DaoException {
		// If tableName doesn’t exist create it
		Set tableNames = db.getCollectionNames();
		if (!tableNames.contains(tableName)) {
			DBObject dbobject = new BasicDBObject();
			db.createCollection(tableName, dbobject);
		}
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

	// This is how you can retrieve all the table names
	public Set getTableNames() throws DaoException {
		return db.getCollectionNames();
	}

	// This is how you can retrieve all the rows in the table
	public void showDB(String tableName) throws DaoException {
		DBCollection dbCollection = db.getCollection(tableName);
		DBCursor cur = dbCollection.find();
		while (cur.hasNext()) {
			System.out.println(cur.next());
		}
	}

	public DBCursor getAllRows(String tableName) throws DaoException {
		DBCollection dbCollection = db.getCollection(tableName);
		DBCursor cur = dbCollection.find();
		return cur;
	}

	// Below code shows how you could get the row count
	public int getRowCount(String tableName) throws DaoException {
		DBCollection dbCollection = db.getCollection(tableName);
		DBCursor cur = dbCollection.find();
		return cur.count();
	}

	// Below code shows how to filter the data using where clause. A where
	// clause is a BasicDBObject type with key as the filter column name and the
	// value is the filter value.
	public DBCursor findByColumn(String tableName, DBObject whereClause)
			throws DaoException {
		DBCursor result = null;
		DBCollection dbCollection = db.getCollection(tableName);
		result = dbCollection.find(whereClause);
		return result;
	}

	public void createIndex(String tableName, String columnName)
			throws DaoException {
		DBCollection dbCollection = db.getCollection(tableName);
		DBObject indexData = new BasicDBObject(columnName, 1);
		dbCollection.createIndex(indexData);
	}

	public void dropTable(String collectionName) {
		db.getCollection(collectionName).drop();
	}

	public String addFavoritos(String user, String id) throws DaoException {
		DBCollection col = db.getCollection(collectionTuit);
		BasicDBObject query = new BasicDBObject("id_str", id);
		DBCursor cursor = col.find(query);
		DBObject nuevo = null;
		if (cursor.hasNext()) {
			nuevo = cursor.next();
			System.out.println(nuevo);

		} else {
			throw new DaoException("no encontrado " + id);
		}
		nuevo.put("favorito", user);
		col.update(query, nuevo);
		return nuevo.toString();
	}

	public String getFavoritos(String user) {
		DBCollection col = db.getCollection(collectionTuit);
		BasicDBObject old = new BasicDBObject("favorito", user);
		DBCursor cursor = col.find(old)
				.sort(new BasicDBObject("created_at", 1));
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
		db.put("screenName", at.getScreenName());
		db.put("token", at.getToken());
		db.put("tokenSecret", at.getTokenSecret());
		db.put("userId", at.getUserId());
		saveToDB(collectionUser, db);
	}

	public AccessToken getUser(String screenName) throws DaoException {
		DBCollection col = db.getCollection(collectionUser);

		BasicDBObject db = new BasicDBObject("screenName", screenName);
		DBCursor cursor = col.find(db);
		if (cursor.hasNext()) {
			DBObject user = cursor.next();
			AccessToken at = new AccessToken(user.get("token").toString(), user
					.get("tokenSecret").toString(), (long) user.get("userId"));
			return at;
		} else
			return null;

	}

	public void saveTuitToDB(DBObject tuitDB) throws DaoException {
		saveToDB(collectionTuit, tuitDB);

	}

	public String getLastTtuit(String user, String timeStamp) {
		DBCollection col = db.getCollection(collectionTuit);
		BasicDBObject query = new BasicDBObject("filterBy", user);
		query.put("created_at", new BasicDBObject("$gt", timeStamp));

		System.out.println(query);
		DBCursor cursor = col.find(query);
		List<String> res = new ArrayList<>();
		while (cursor.hasNext()) {
			DBObject tuit = cursor.next();
			res.add(tuit.toString());
		}
		JSONArray jArray = new JSONArray(res);
		return jArray.toString();
	}

}