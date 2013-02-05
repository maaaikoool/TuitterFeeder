package com.david.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.david.dao.DaoException;
import com.david.dao.MongoDao;

import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * @author david.sanchez
 * Callback de la autorización de Twitter. Generamos un AccesToken y lo guardamos en MongoDB
 */
@WebServlet("/back")
public class AuthServletCallback extends HttpServlet {

	private static final long serialVersionUID = -4966863466219546669L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		RequestToken requestToken = (RequestToken) req.getSession()
				.getAttribute("requestToken");
		TwitterStream twitter = (TwitterStream) req.getSession().getAttribute(
				"twitter");

		String verifier = req.getParameter("oauth_verifier");
		try {
			AccessToken at = twitter
					.getOAuthAccessToken(requestToken, verifier);

			if (MongoDao.getInstance().getUser(at.getScreenName()) == null) {
				MongoDao.getInstance().addUser(at);
			}

			req.getSession().setAttribute("accessToken", at);
			req.getSession().removeAttribute("twitter");
			req.getSession().removeAttribute("requestToken");
		} catch (TwitterException e) {
			throw new ServletException(e);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/");
	}
}
