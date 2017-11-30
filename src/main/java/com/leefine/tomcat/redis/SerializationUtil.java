package com.leefine.tomcat.redis;

import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * Session serialization utility.
 */
public class SerializationUtil {

	private ClassLoader loader;
	private Log log = LogFactory.getLog(SerializationUtil.class);

	public SerializationUtil(ClassLoader loader) {
		this.loader = loader;
	}

	public byte[] getSessionAttributesHashCode(Session session) throws IOException {
		byte[] serialized = null;
		Map<String, Object> attributes = new HashMap<String, Object>();

		for (Enumeration<String> enumerator = session.getAttributeNames(); enumerator.hasMoreElements();) {
			String key = enumerator.nextElement();
			attributes.put(key, session.getAttribute(key));
		}

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));) {
			oos.writeUnshared(attributes);
			oos.flush();
			serialized = bos.toByteArray();
		}

		MessageDigest digester = null;
		try {
			digester = MessageDigest.getInstance("MD5");
		} catch (Exception ex) {
			log.error("Unable to get MessageDigest instance for MD5", ex);
		}
		return digester.digest(serialized);
	}

	public byte[] serializeSessionData(Session session, SessionMetadata metadata) throws IOException {
		byte[] serialized = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));) {
			oos.writeObject(metadata);
			session.writeObjectData(oos);
			oos.flush();
			serialized = bos.toByteArray();
		}
		return serialized;
	}

	public void deserializeSessionData(byte[] data, Session session, SessionMetadata metadata)
			throws IOException, ClassNotFoundException {
		try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
				ObjectInputStream ois = new CustomObjectInputStream(bis, this.loader);) {
			SessionMetadata serializedMetadata = (SessionMetadata) ois.readObject();
			metadata.copyFieldsFrom(serializedMetadata);
			session.readObjectData(ois);
		}
	}
}
