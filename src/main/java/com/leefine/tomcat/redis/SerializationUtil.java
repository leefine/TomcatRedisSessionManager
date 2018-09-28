package com.leefine.tomcat.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.catalina.Context;
import java.io.*;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * <p>
 * Session serialization utility.
 */
public class SerializationUtil {

    //private ClassLoader loader;
    private static Log log = LogFactory.getLog(SerializationUtil.class);

    /**
     * To serialize session object
     *
     * @param session
     * @param metadata
     * @return
     * @throws IOException
     */
    public static byte[] serializeSessionData(Session session, SessionMetadata metadata) {
        byte[] serialized = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));) {
            oos.writeObject(metadata);
            session.writeObjectData(oos);
            oos.flush();
            serialized = bos.toByteArray();
        } catch (IOException e) {
            log.error(e);
        }
        return serialized;
    }

    /**
     * To de-serialize session object
     *
     * @param data
     * @param session
     * @param metadata
     */
    public static void deserializeSessionData(byte[] data, Session session, SessionMetadata metadata, Context context) {
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new BufferedInputStream(new ByteArrayInputStream(data));
            //ois = new CustomObjectInputStream(bis, this.loader);
            ois = new SessionObjectInputStream(bis,context);
            SessionMetadata serializedMetadata = (SessionMetadata) ois.readObject();
            metadata.copyFieldsFrom(serializedMetadata);
            session.readObjectData(ois);
        } catch (IOException e) {
            log.error(e);
            log.error(new String(data));
        } catch (ClassNotFoundException e) {
            log.error(e);
            log.error(new String(data));
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
