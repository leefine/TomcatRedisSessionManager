package com.leefine.tomcat.redis;

import org.apache.catalina.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;

public class SessionObjectInputStream extends ObjectInputStream {

    private Context context;

    public SessionObjectInputStream(InputStream in, Context context) throws IOException {
        super(in);
        this.context = context;
    }

    private static final HashMap<String, Class<?>> primClasses
            = new HashMap<>(8, 1.0F);

    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

    private static Log log = LogFactory.getLog(SessionObjectInputStream.class);

    private static ClassLoader classLoader = null;

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            ClassLoader loader = null;
            if (classLoader != null)
                loader = classLoader;
            else
                loader = Thread.currentThread().getContextClassLoader();

            Class<?> cls = Class.forName(name, false, loader);
            classLoader = loader;
            return cls;
        } catch (ClassNotFoundException ex) {
            Class cl = (Class) primClasses.get(name);
            if (cl != null) {
                return cl;
            } else {
                classLoader = null;
                throw ex;
            }
        }
    }
}
