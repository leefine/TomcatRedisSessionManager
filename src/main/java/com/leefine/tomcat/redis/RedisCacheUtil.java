package com.leefine.tomcat.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * Redis stand-alone mode data-cache implementation.
 */
public class RedisCacheUtil implements DataCache {

    private JedisPool pool;
    private final int numRetries = 3;
    private Log log = LogFactory.getLog(RedisCacheUtil.class);

    public RedisCacheUtil(String host, int port, String password, int database, int timeout,JedisPoolConfig poolConfig) {
        pool = new JedisPool(poolConfig, host, port, timeout, password, database);
    }

    @Override
    public byte[] set(String key, byte[] value) {
        int tries = 0;
        boolean sucess = false;
        String retVal = null;
        do {
            tries++;
            try {
                Jedis jedis = pool.getResource();
                retVal = jedis.set(key.getBytes(), value);
                jedis.close();
                sucess = true;
            } catch (JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries)
                    throw ex;
            }
        } while (!sucess && tries <= numRetries);
        return (retVal != null) ? retVal.getBytes() : null;
    }

    @Override
    public Long setnx(String key, byte[] value) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                Jedis jedis = pool.getResource();
                retVal = jedis.setnx(key.getBytes(), value);
                jedis.close();
                sucess = true;
            } catch (JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries)
                    throw ex;
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    @Override
    public Long expire(String key, int seconds) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                Jedis jedis = pool.getResource();
                retVal = jedis.expire(key, seconds);
                jedis.close();
                sucess = true;
            } catch (JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries)
                    throw ex;
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    @Override
    public byte[] get(String key) {
        int tries = 0;
        boolean sucess = false;
        byte[] retVal = null;
        do {
            tries++;
            try {
                Jedis jedis = pool.getResource();
                retVal = jedis.get(key.getBytes());
                jedis.close();
                sucess = true;
            } catch (JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries)
                    throw ex;
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    @Override
    public Long delete(String key) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                Jedis jedis = pool.getResource();
                retVal = jedis.del(key);
                jedis.close();
                sucess = true;
            } catch (JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries)
                    throw ex;
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }
}
