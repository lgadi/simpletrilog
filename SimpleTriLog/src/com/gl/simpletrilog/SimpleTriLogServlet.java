package com.gl.simpletrilog;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gl.routing.AbstractServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class SimpleTriLogServlet extends AbstractServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		 UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();

	        if (user != null) {
	            resp.setContentType("text/html");
	            resp.getWriter().println("Hello, " + user.getNickname());
	            resp.getWriter().println("<a href=\""+userService.createLogoutURL(req.getRequestURI())+"\">Logout</a>");
	        } else {
	            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
	        }
	}

	@Override
	public String getUrlPattern() {
		// TODO Auto-generated method stub
		return "/";
	}
}
