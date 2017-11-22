package com.leefine.tomcat.redis;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * 
 * Valve that implements per-request session persistence. It is intended to be
 * used with non-sticky load-balancers.

 */
public class SessionHandlerValve extends ValveBase {

	private SessionManager manager;

	/**
	 * To set session manager
	 * 
	 * @param manager
	 */
	public void setSessionManager(SessionManager manager) {
		this.manager = manager;
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try {
			getNext().invoke(request, response);
		} finally {
			manager.afterRequest(request);
		}
	}
}