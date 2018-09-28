package com.leefine.tomcat.redis;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * <p>
 * Valve that implements per-request session persistence. It is intended to be
 * used with non-sticky load-balancers.
 */
public class SessionHandlerValve extends ValveBase {
    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(Request request, Response response)  {
        try {
            getNext().invoke(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
