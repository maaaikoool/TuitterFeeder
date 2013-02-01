package com.david.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;

@Path("/favoritos")
public class TuitService {

	@POST
	@Path("/add/{user}/{id}")
	public Response addFavorito(@PathParam("user") String user ,@PathParam("id") String id) {
		try {
			MongoDao.getInstance().addFavoritos(user,id);
			return Response.status(200).entity(id).build();
		} catch (DaoException e) {
			return Response.status(404).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/list/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFavoritos(@PathParam("user") String user) {
		return MongoDao.getInstance().getFavoritos(user);
	}
}
