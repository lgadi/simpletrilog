package com.gl.routing;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gl.simpletrilog.ListAllActivities;
import com.gl.simpletrilog.LogSingleActivity;
import com.gl.simpletrilog.RawLogInput;

@SuppressWarnings("serial")
public class RoutingServlet extends HttpServlet {

	private static Method GET_METHOD = null;
	private static Method POST_METHOD = null;
	private static final String TOKEN_PLACEHOLDER = "TOKEN_PLACEHOLDER";
	private static final String REST_PLACEHOLDER = "REST_PLACEHOLDER";
	static {
		try {
			GET_METHOD = IHandler.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
			POST_METHOD = IHandler.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static final Logger log = Logger.getLogger(RoutingServlet.class.getName());
	private static final String BASE_PATH = "/simpletrilog";
	private Map<String, IHandler> handlers = new HashMap<String, IHandler>();
	private Map<String[], IHandler> complexHandlers = new HashMap<String[], IHandler>();

	@Override
	public void init() throws ServletException {
		super.init();
		log.warning("RoutingServlet init");
		initHandlers();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.warning("doPost(" + req.getRequestURI() + ")");
		processUri(req.getRequestURI(), req, resp, POST_METHOD);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.warning("doGet(" + req.getRequestURI() + ")");

		processUri(req.getRequestURI(), req, resp, GET_METHOD);
	}

	private String[] urlToTokens(String url, boolean replacePlaceHolders) {
		StringTokenizer strtok = new StringTokenizer(url, "/");

		List<String> tokensList = new ArrayList<String>();
		while (strtok.hasMoreTokens()) {
			String token = strtok.nextToken();
			if (replacePlaceHolders && token.startsWith("{")) {
				token = TOKEN_PLACEHOLDER;
			}
			if (replacePlaceHolders && !strtok.hasMoreTokens() && token.equals("*")) {
				token = REST_PLACEHOLDER;
			}
			tokensList.add(token);
		}
		return tokensList.toArray(new String[tokensList.size()]);
	}

	private String[] urlToTokens(String url) {
		return urlToTokens(url, true);
	}

	public void addHandler(IHandler handler) {
		String[] pattern = urlToTokens(BASE_PATH + handler.getUrlPattern());
		handlers.put(handler.getUrlPattern(), handler);
		complexHandlers.put(pattern, handler);

	}

	public IHandler getHandlerForUri(String uri) {
		IHandler handler = handlers.get(uri);
		if (handler == null) {
			String[] targetPattern = urlToTokens(uri);
			for (String[] pattern : complexHandlers.keySet()) {
				if (patternsMatch(pattern, targetPattern)) {
					handler = complexHandlers.get(pattern);
					break;
				}
			}
		}
		return handler;
	}

	public Map<String, String> getValuesForPlaceHolders(IHandler handler, String url) {
		String[] pattern = urlToTokens(BASE_PATH+handler.getUrlPattern());
		String[] originalPattern = urlToTokens(BASE_PATH+handler.getUrlPattern(), false);
		String[] targetPattern = urlToTokens(url, false);
		Map<String, String> result = new HashMap<String, String>();

		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i].equals(TOKEN_PLACEHOLDER)) {
				result.put(originalPattern[i].substring(1, originalPattern[i].length() - 1), targetPattern[i]);
			}
		}
		return result;
	}

	private boolean patternsMatch(String[] pattern, String[] targetPattern) {
		for (int i = 0; i < pattern.length; i++) {
			if (i >= targetPattern.length)
				return false;
			if (pattern[i].equals(REST_PLACEHOLDER))
				return true;
			if (!pattern[i].equals(TOKEN_PLACEHOLDER) && !pattern[i].equals(targetPattern[i])) {
				return false;
			}
		}
		if (pattern.length != targetPattern.length)
			return false; // should have been handled with the REST_PLACEHOLDER
		return true;
	}

	private void initHandlers() {

//		addHandler(new CreatePollServlet("/pollcreate.jsp"));
//		addHandler(new ListPollsServlet());
//		addHandler(new ImageGenerator());
//		addHandler(new ProcessPollServlet());
//		addHandler(new ViewPollResultsServlet());
//		addHandler(new ProcessPollServlet());
//		addHandler(new UploadImageServlet("/uploadimage.jsp"));
//		addHandler(new FileUpload());
		// IHandler handler = new UsersHandler();
		// handlers.put(handler.getUrlPattern(), handler);
		// handler = new ListHandler();
		// handlers.put(handler.getUrlPattern(), handler);
		addHandler(new LogSingleActivity());
		addHandler(new ListAllActivities());
		addHandler(new RawLogInput("/simpletrilog.jsp"));

	}

	private void processUri(String requestURI, HttpServletRequest req, HttpServletResponse resp, Method handlingMethod)
			throws IOException {
		//requestURI = requestURI.substring(BASE_PATH.length());
		req = new WriteableParametersHttpServletRequestWrapper(req);
		IHandler handler = getHandlerForUri(requestURI);
		if (handler == null) {
			throw new IOException("No handler found for "+requestURI);
		}
		for (Map.Entry<String, String> entry : getValuesForPlaceHolders(handler, requestURI).entrySet()) {
			req.getParameterMap().put(entry.getKey(), entry.getValue());
		}
		try {
			handler.setServletContext(getServletContext());
			log.warning("about to execute on: "+handler);
			handlingMethod.invoke(handler, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.setServletContext(null);
		}

	}
}
