package com.gl.routing;


import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHandler {
	String getUrlPattern();
	void setServletContext(ServletContext servletContext);
	ServletContext getServletContext();
	void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException;
	void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException;
	void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
