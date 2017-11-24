package com.leefine.tomcat.redis;

import org.apache.catalina.*;
import org.apache.catalina.connector.Request;
import org.apache.catalina.session.ManagerBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * Manager that implements per-request session persistence. It is intended to be used with non-sticky load-balancers.
 */
public class SessionManager extends ManagerBase implements Lifecycle {

	private DataCache dataCache;
	protected SerializationUtil serializer;
	protected ThreadLocal<SessionContext> sessionContext = new ThreadLocal<>();
	protected SessionHandlerValve handlerValve;
	protected Set<SessionPolicy> sessionPolicy = EnumSet.of(SessionPolicy.DEFAULT);
	private Log log = LogFactory.getLog(SessionManager.class);

	public String getSessionPersistPolicies() {
		String policyStr = null;
		for (SessionPolicy policy : this.sessionPolicy) {
			policyStr = (policyStr == null) ? policy.name() : policyStr.concat(",").concat(policy.name());
		}
		return policyStr;
	}

	public void setSessionPersistPolicies(String policyStr) {
		Set<SessionPolicy> policySet = EnumSet.of(SessionPolicy.DEFAULT);
		String[] policyArray = policyStr.split(",");

		for (String policy : policyArray) {
			policySet.add(SessionPolicy.fromName(policy));
		}
		this.sessionPolicy = policySet;
	}

	public boolean getSaveOnChange() {
		return this.sessionPolicy.contains(SessionPolicy.SAVE_ON_CHANGE);
	}

	public boolean getAlwaysSaveAfterRequest() {
		return this.sessionPolicy.contains(SessionPolicy.ALWAYS_SAVE_AFTER_REQUEST);
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
	protected synchronized void startInternal() throws LifecycleException {
		super.startInternal();
		super.setState(LifecycleState.STARTING);

		boolean initializedValve = false;
		Context context = getContextIns();
		Valve[] vals = context.getPipeline().getValves();
		for (Valve valve : vals) {
		//for (Valve valve : context.getPipeline().getValves()) {
			if (valve instanceof SessionHandlerValve) {
				this.handlerValve = (SessionHandlerValve) valve;
				this.handlerValve.setSessionManager(this);
				initializedValve = true;
				break;
			}
		}

		if (!initializedValve)
			throw new LifecycleException("Session handling valve is not initialized..");

		try {
			this.dataCache = new RedisDataCache();
			this.serializer = new SerializationUtil();
			ClassLoader loader = (context != null && context.getLoader() != null) ? context.getLoader().getClassLoader() : null;
			this.serializer.setClassLoader(loader);
      		  } catch (Exception ex) {
			log.error("Error occured while initializing the session manager..", ex);
			throw ex;
		 }

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
		if (sessionId != null) {
			sessionId = (this.dataCache.setnx(sessionId, Constants.NULL_SESSION) == 0L) ? null : sessionId;
		} else {
			do {
				sessionId = generateSessionId();
			} while (this.dataCache.setnx(sessionId, Constants.NULL_SESSION) == 0L);
		}

		Session session = (sessionId != null) ? (Session) createEmptySession() : null;
		if (session != null) {
			session.setId(sessionId);
			session.setNew(true);
			session.setValid(true);
			session.setCreationTime(System.currentTimeMillis());
			session.setMaxInactiveInterval(getSessionTimeout());
			session.tellNew();
		}
		setValues(sessionId, session, false, new SessionMetadata());

		if (session != null) {
			try {
				save(session, true);
			} catch (Exception ex) {
				log.error("Error occured while creating session..", ex);
				setValues(null, null);
				session = null;
			}
		}
		return session;
	}

	@Override
	public Session createEmptySession() {
		return new Session(this);
	}

	@Override
	public void add(org.apache.catalina.Session session) {
		save(session, false);
	}

	@Override
	public Session findSession(String sessionId) throws IOException {
		Session session = null;
		if (sessionId != null && this.sessionContext.get() != null && sessionId.equals(this.sessionContext.get().getId())) {
			session = this.sessionContext.get().getSession();
		} else {
			byte[] data = this.dataCache.get(sessionId);

			boolean isPersisted = false;
			SessionMetadata metadata = null;
			if (data == null) {
				session = null;
				metadata = null;
				sessionId = null;
				isPersisted = false;
			} else {
				if (Arrays.equals(Constants.NULL_SESSION, data)) {
					throw new IOException("NULL session data");
				}
				try {
					metadata = new SessionMetadata();
					Session newSession = (Session) createEmptySession();
					this.serializer.deserializeSessionData(data, newSession, metadata);

					newSession.setId(sessionId);
					newSession.access();
					newSession.setNew(false);
					newSession.setValid(true);
					newSession.resetDirtyTracking();
					newSession.setMaxInactiveInterval(getSessionTimeout());

					session = newSession;
					isPersisted = true;
				} catch (Exception ex) {
					log.error("Error occured while de-serializing the session object..", ex);
				}
			}
			setValues(sessionId, session, isPersisted, metadata);
		}
		return session;
	}

	@Override
	public void remove(org.apache.catalina.Session session) {
		remove(session, false);
	}

	@Override
	public void remove(org.apache.catalina.Session session, boolean update) {
		this.dataCache.expire(session.getId(), 10);
	}

	@Override
	public void load() throws ClassNotFoundException, IOException {
		// Auto-generated method stub
	}

	@Override
	public void unload() throws IOException {
		// Auto-generated method stub
	}

	/*private void initialize() {
		try {
			this.dataCache = new RedisDataCache();
			this.serializer = new SerializationUtil();
			Context context = getContextIns();
			ClassLoader loader = (context != null && context.getLoader() != null) ? context.getLoader().getClassLoader() : null;
			this.serializer.setClassLoader(loader);
		} catch (Exception ex) {
			log.error("Error occured while initializing the session manager..", ex);
			throw ex;
		}
	}*/

	public void save(org.apache.catalina.Session session, boolean forceSave) {
		try {
			Boolean isPersisted;
			Session newSession = (Session) session;
			byte[] hash = (this.sessionContext.get() != null && this.sessionContext.get().getMetadata() != null)
					? this.sessionContext.get().getMetadata().getAttributesHash() : null;
			byte[] currentHash = serializer.getSessionAttributesHashCode(newSession);

			if (forceSave || newSession.isDirty()
					|| (isPersisted = (this.sessionContext.get() != null) ? this.sessionContext.get().isPersisted() : null) == null
					|| !isPersisted || !Arrays.equals(hash, currentHash)) {

				SessionMetadata metadata = new SessionMetadata();
				metadata.setAttributesHash(currentHash);

				this.dataCache.set(newSession.getId(), serializer.serializeSessionData(newSession, metadata));
				newSession.resetDirtyTracking();
				setValues(true, metadata);
			}

			int timeout = getSessionTimeout();
			this.dataCache.expire(newSession.getId(), timeout);
			log.trace("Session [" + newSession.getId() + "] expire in [" + timeout + "] seconds.");

		} catch (IOException ex) {
			log.error("Error occured while saving the session object in data cache..", ex);
		}
	}

	public void afterRequest(Request request) {
		Session session = null;
		try {
			session = (this.sessionContext.get() != null) ? this.sessionContext.get().getSession() : null;
			if (session != null) {
				if (session.isValid())
					save(session, getAlwaysSaveAfterRequest());
				else
					remove(session);
				log.trace("Session object " + (session.isValid() ? "saved: " : "removed: ") + session.getId());
			}
		} catch (Exception ex) {
			log.error("Error occured while processing post request process..", ex);
		} finally {
			this.sessionContext.remove();
			log.trace("Session removed from ThreadLocal:" + ((session != null) ? session.getIdInternal() : ""));
		}
	}

	private int getSessionTimeout() {
		int timeout = getContextIns().getSessionTimeout() * 60;
		return timeout;
		//return (timeout < 1800) ? 1800 : timeout;
	}

	private void setValues(String sessionId, Session session) {
		if (this.sessionContext.get() == null) {
			this.sessionContext.set(new SessionContext());
		}
		this.sessionContext.get().setId(sessionId);
		this.sessionContext.get().setSession(session);
	}

	private void setValues(boolean isPersisted, SessionMetadata metadata) {
		if (this.sessionContext.get() == null) {
			this.sessionContext.set(new SessionContext());
		}
		this.sessionContext.get().setMetadata(metadata);
		this.sessionContext.get().setPersisted(isPersisted);
	}

	private void setValues(String sessionId, Session session, boolean isPersisted, SessionMetadata metadata) {
		setValues(sessionId, session);
		setValues(isPersisted, metadata);
	}

	private Context getContextIns() {
		try {
			Method method = this.getClass().getSuperclass().getDeclaredMethod("getContext");
			return (Context) method.invoke(this);
		} catch (Exception ex) {
			try {
				Method method = this.getClass().getSuperclass().getDeclaredMethod("getContainer");
				return (Context) method.invoke(this);
			} catch (Exception ex2) {
				log.error("Error in getContext", ex2);
			}
		}
		return null;
	}
}
