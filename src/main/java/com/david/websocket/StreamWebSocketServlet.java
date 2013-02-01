package com.david.websocket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

@WebServlet(urlPatterns = "/tuit")
public class StreamWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 3695910334826723009L;

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest request) {
		return new StreamConnection(request);
	}

}