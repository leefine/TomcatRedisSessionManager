package com.leefine.tomcat.redis;

import org.apache.catalina.*;
import org.apache.catalina.session.ManagerBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * <p>
 * Manager that implements per-request session persistence. It is intended to be
 * used with non-sticky load-balancers.
 */
public class SessionManager extends ManagerBase implements Lifecycle {

    private DataCache dataCache;
    protected SessionHandlerValve handlerValve;
    private Log log = LogFactory.getLog(SessionManager.class);

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        super.setState(LifecycleState.STARTING);

        boolean initializedValve = false;
        Context context = this.getContext();

        for (Valve valve : context.getPipeline().getValves()) {
            if (valve instanceof SessionHandlerValve) {
                this.handlerValve = (SessionHandlerValve) valve;
                initializedValve = true;
                break;
            }
        }

        if (!initializedValve)
            throw new LifecycleException("Session handling valve is not initialized..");

        this.dataCache = new RedisDataCache();

        log.info("The sessions will expire after " + (getSessionTimeout()) + " seconds.");
        context.setDistributable(true);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        super.setState(LifecycleState.STOPPING);
        super.stopInternal();
    }

    @Override
    public Session createSession(String sessionId) {
        Session session = createEmptySession();
        session.setNew(true);
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(getSessionTimeout());
        if (sessionId == null) sessionId = this.generateSessionId();
        session.setId(sessionId);
        session.tellNew();
        save(session);
        return session;
    }

    @Override
    public Session createEmptySession() {
        return new Session(this);
    }

    @Override
    public Session findSession(String sessionId) {
        Session session = null;
        byte[] data = this.dataCache.get(sessionId);
        if (data != null) {
            if (Arrays.equals(Constants.NULL_SESSION, data)) return null;
            SessionMetadata metadata = new SessionMetadata();
            Session newSession = createEmptySession();
            SerializationUtil.deserializeSessionData(data, newSession, metadata, this.getContext());
            newSession.setId(sessionId);
            newSession.access();
            newSession.setNew(false);
            newSession.setValid(true);
            newSession.setMaxInactiveInterval(getSessionTimeout());
            newSession.endAccess();
            session = newSession;
        }

        return session;
    }

    @Override
    public void add(org.apache.catalina.Session session) {
        //add this line for HttpSessionListener sessionDestroyed
        if (Constants.IS_MONITOR_SESSION_DESTROYED) {
            this.sessions.put(session.getIdInternal(), session);
        }
        save(session);
    }

    @Override
    public void remove(org.apache.catalina.Session session) {
        remove(session, false);
    }

    @Override
    public void remove(org.apache.catalina.Session session, boolean update) {
        if (update) {
            long timeNow = System.currentTimeMillis();
            int timeAlive =
                    (int) (timeNow - session.getCreationTimeInternal()) / 1000;
            updateSessionMaxAliveTime(timeAlive);
            expiredSessions.incrementAndGet();
            SessionTiming timing = new SessionTiming(timeNow, timeAlive);
            synchronized (sessionExpirationTiming) {
                sessionExpirationTiming.add(timing);
                sessionExpirationTiming.poll();
            }
        }

        if (session.getIdInternal() != null) {
            this.dataCache.expire(session.getId(), 10);
            if (Constants.IS_MONITOR_SESSION_DESTROYED)
                this.sessions.remove(session.getIdInternal());
        }
    }

    public void save(org.apache.catalina.Session session) {
        Session newSession = (Session) session;
        SessionMetadata metadata = new SessionMetadata();
        this.dataCache.set(newSession.getId(), SerializationUtil.serializeSessionData(newSession, metadata));
        this.dataCache.expire(newSession.getId(), getSessionTimeout());
    }

    private static int sessionTimeout = 0;

    private int getSessionTimeout() {
        if (sessionTimeout == 0)
            sessionTimeout = this.getContext().getSessionTimeout() * 60;
        return sessionTimeout;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        super.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return super.findLifecycleListeners();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        super.removeLifecycleListener(listener);
    }

    @Override
    public void load() throws ClassNotFoundException, IOException {
    }

    @Override
    public void unload() throws IOException {
    }
}
