package com.gl.simpletrilog;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gl.routing.AbstractServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class ListAllActivities extends AbstractServlet {
	final String RAW_LOG = "rawlog";
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		 UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();
	        String username = "anonymous";
	        if (user != null) {
	            username = user.getNickname();
	        }
	        resp.setContentType("text/html");
            resp.getWriter().println("Hello, " + username);
            Key rawlogKey = KeyFactory.createKey("rawlog_entry", RAW_LOG);
            DatastoreService datastore =
                    DatastoreServiceFactory.getDatastoreService();
            Query query = new Query("RawEntry", rawlogKey);
            List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
            resp.getWriter().println("---------------");
            for (Entity entity:greetings) {
            	resp.getWriter().println(entity.getProperty("raw_data")+"<br/>");
            }
	}
	@Override
	public String getUrlPattern() {
		return "/list";
	}
}
