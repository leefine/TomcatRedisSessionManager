package com.leefine.tomcat.redis;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.Enumeration;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * <p>
 * This class is uses to store and retrieve the HTTP request session objects.
 */
public class Session extends StandardSession {

    private Log log = LogFactory.getLog(SessionManager.class);

    public Session(Manager manager) {
        super(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(String id) {
        if (Constants.IS_MONITOR_SESSION_DESTROYED)
            if (this.id != null && this.manager != null) {
                this.manager.remove(this);
            }

        this.id = id;

        if (Constants.IS_MONITOR_SESSION_DESTROYED)
            if (this.manager != null) {
                this.manager.add(this);
            }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttribute(String key, Object value) {
        super.setAttribute(key, value);
        ((SessionManager) this.manager).save(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(String name) {
        return super.getAttribute(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        if (this.isValid)
            return super.getAttributeNames();
        else return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        ((SessionManager) this.manager).save(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrincipal(Principal principal) {
        super.setPrincipal(principal);
        //this.dirty = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObjectData(ObjectOutputStream out) throws IOException {
        super.writeObjectData(out);
        out.writeLong(this.getCreationTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readObjectData(ObjectInputStream in) {
        try {
            super.readObjectData(in);
            this.setCreationTime(in.readLong());
        } catch (IOException e) {
            log.error(e);
            log.error(in);
        } catch (ClassNotFoundException e) {
            log.error(e);
            log.error(in);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() {
        super.invalidate();
    }
}
