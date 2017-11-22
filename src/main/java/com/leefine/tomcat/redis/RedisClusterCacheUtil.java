package com.leefine.tomcat.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisClusterMaxRedirectionsException;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * <p>
 * Redis multiple node cluster data-cache implementation.

 */
public class RedisClusterCacheUtil implements DataCache {

    private JedisCluster cluster;

    private final int numRetries = 30;

    private Log log = LogFactory.getLog(RedisClusterCacheUtil.class);

    public RedisClusterCacheUtil(Set<HostAndPort> nodes, int timeout, JedisPoolConfig poolConfig) {
        cluster = new JedisCluster(nodes, timeout, poolConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] set(String key, byte[] value) {
        int tries = 0;
        boolean sucess = false;
        String retVal = null;
        do {
            tries++;
            try {
                retVal = cluster.set(key.getBytes(), value);
                sucess = true;
            } catch (JedisClusterMaxRedirectionsException | JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries) {
                    throw ex;
                }
                waitforFailover();
            }
        } while (!sucess && tries <= numRetries);
        return (retVal != null) ? retVal.getBytes() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long setnx(String key, byte[] value) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                retVal = cluster.setnx(key.getBytes(), value);
                sucess = true;
            } catch (JedisClusterMaxRedirectionsException | JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries) {
                    throw ex;
                }
                waitforFailover();
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long expire(String key, int seconds) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                retVal = cluster.expire(key, seconds);
                sucess = true;
            } catch (JedisClusterMaxRedirectionsException | JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries) {
                    throw ex;
                }
                waitforFailover();
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] get(String key) {
        int tries = 0;
        boolean sucess = false;
        byte[] retVal = null;
        do {
            tries++;
            try {
                retVal = cluster.get(key.getBytes());
                sucess = true;
            } catch (JedisClusterMaxRedirectionsException | JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries) {
                    throw ex;
                }
                waitforFailover();
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long delete(String key) {
        int tries = 0;
        boolean sucess = false;
        Long retVal = null;
        do {
            tries++;
            try {
                retVal = cluster.del(key);
                sucess = true;
            } catch (JedisClusterMaxRedirectionsException | JedisConnectionException ex) {
                log.error(Constants.CONN_FAILED_RETRY_MSG + tries);
                if (tries == numRetries) {
                    throw ex;
                }
                waitforFailover();
            }
        } while (!sucess && tries <= numRetries);
        return retVal;
    }

    /**
     * To wait for handling redis fail-over
     */
    private void waitforFailover() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}