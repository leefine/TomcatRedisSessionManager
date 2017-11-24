package com.leefine.tomcat.redis;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * Session context uses to manage current session data.
 */
public class SessionContext {
	
	private String id;
	private Session session;
	private boolean persisted;
	private SessionMetadata metadata;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean isPersisted() {
		return persisted;
	}

	public void setPersisted(boolean persisted) {
		this.persisted = persisted;
	}

	public SessionMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(SessionMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "SessionContext [id=" + id + "]";
	}
	
}
