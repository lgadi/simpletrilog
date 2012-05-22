package com.gl.routing;


import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class JspServlet extends AbstractServlet {
	
	
	private static final long serialVersionUID = 1L;
	private String jspName;
	
	public JspServlet(String jspName) {
		this.jspName = jspName;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			getServletContext().getRequestDispatcher(jspName).forward(req, resp);
		} catch (ServletException e) {
			Logger.getLogger("JspServlet for "+jspName).severe(e.getMessage());
		}
	}

}
