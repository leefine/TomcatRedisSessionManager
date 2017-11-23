package com.leefine.tomcat.redis;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * API for Data cache.
 */
public interface DataCache {
	byte[] set(String key, byte[] value);
	Long setnx(String key, byte[] value);
	Long expire(String key, int seconds);
	byte[] get(String key);
	Long delete(String key);
}
