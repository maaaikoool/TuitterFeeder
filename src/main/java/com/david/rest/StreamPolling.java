package com.david.rest;

import javax.ws.rs.Path;

@Path("/poll")
public class StreamPolling {

	private final String OAuthConsumerKey = "391LQjJbx84o2Je2Zx9YA";
	private final String OAuthConsumerSecret = "BYf3UdA9EwpV8K4qmjEyZHAaZVIJZlH5qciNaMg";

//	@POST
//	@Path("/start/{user}/{filter}")
//	public Response startStream(@PathParam("user") String user,
//			@PathParam("filter") String filter) {
//
//		try {
//			AccessToken at = MongoDao.getInstance().getUser(user);
//			TwitterStream twitter = new TwitterStreamFactory(
//					new ConfigurationBuilder().setJSONStoreEnabled(true)
//							.build()).getInstance();
//
//			twitter.setOAuthConsumer(OAuthConsumerKey, OAuthConsumerSecret);
//			twitter.setOAuthAccessToken(at);
//			StatusListener statusR = new StatusListener(filter, user);
//			twitter.addListener(statusR);
//			FilterQuery query = new FilterQuery();
//			query.track(new String[] { filter });
//			twitter.filter(query);
//
//		} catch (DaoException e) {
//			e.printStackTrace();
//		}
//		return Response.ok().build();
//	}
//
//	@POST
//	@Path("/stop/{user}")
//	public void stopStream(@PathParam("user") String user) {
//
//		try {
//			AccessToken at = MongoDao.getInstance().getUser(user);
//			ConfigurationBuilder cb = new ConfigurationBuilder();
//			cb.setOAuthConsumerKey(OAuthConsumerKey);
//			cb.setOAuthConsumerSecret(OAuthConsumerSecret);
//			TwitterStream twitter = new TwitterStreamFactory(cb.build())
//					.getInstance(at);
//			twitter.setOAuthAccessToken(at);
//			System.out.println("onClose");
//			twitter.cleanUp();
//			twitter.shutdown();
//		} catch (DaoException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@GET
//	@Path("/last/{user}/{timeStamp}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getStream(
//			@PathParam("user") String user,
//			@DefaultValue("Fri Feb 01 12:48:36 +0000 2013") @PathParam("timeStamp") String timeStamp) {
//		return MongoDao.getInstance().getLastTtuit(user, "Fri Feb 01 12:48:36 +0000 2013");
//	}
}
