package com.gl.simpletrilog;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gl.routing.AbstractServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LogSingleActivity extends AbstractServlet {
	final String RAW_LOG = "rawlog";
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		 UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();
	        String username = "anonymous";
	        if (user != null) {
	            username = user.getNickname();
	        }
	        resp.setContentType("text/html");
            resp.getWriter().println("Hello, " + username);
            Key rawlogKey = KeyFactory.createKey("rawlog_entry", RAW_LOG);
            Entity rawEntry = new Entity("RawEntry", rawlogKey);
            rawEntry.setProperty("raw_data", req.getParameter("freetext"));
            DatastoreService datastore =
                    DatastoreServiceFactory.getDatastoreService();
            datastore.put(rawEntry);
            resp.getWriter().println("Logged activity: " + req.getParameter("freetext")+"<br/>");
	}
	@Override
	public String getUrlPattern() {
		// TODO Auto-generated method stub
		return "/logsingle";
	}
}
