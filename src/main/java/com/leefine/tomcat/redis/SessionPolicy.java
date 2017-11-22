package com.leefine.tomcat.redis;

public enum SessionPolicy {
    DEFAULT, SAVE_ON_CHANGE, ALWAYS_SAVE_AFTER_REQUEST;

    static SessionPolicy fromName(String name) {
        for (SessionPolicy policy : SessionPolicy.values()) {
            if (policy.name().equalsIgnoreCase(name)) {
                return policy;
            }
        }
        throw new IllegalArgumentException("Invalid session policy [" + name + "]");
    }
}