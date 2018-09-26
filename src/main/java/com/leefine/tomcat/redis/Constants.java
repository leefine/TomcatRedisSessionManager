package com.leefine.tomcat.redis;

/**
 * Tomcat clustering with Redis data-cache implementation.
 * Redis data-cache constants.
 */
public class Constants {
    //Open sessionDestroyed for HttpSessionListener,if you don't need this,pls make this false
    public static boolean IS_MONITOR_SESSION_DESTROYED = false;

    public final static byte[] NULL_SESSION = "null".getBytes();
    public final static String CATALINA_BASE = "catalina.base";
    public final static String CONF = "conf";

    // redis properties file name
    public final static String PROPERTIES_FILE = "redis-server.properties";

    // redis properties
    public final static String HOSTS = "redis.hosts";
    public final static String CLUSTER_ENABLED = "redis.cluster.enabled";

    public final static String MAX_ACTIVE = "redis.max.active";
    public final static String TEST_ONBORROW = "redis.test.onBorrow";
    public final static String TEST_ONRETURN = "redis.test.onReturn";
    public final static String MAX_IDLE = "redis.max.idle";
    public final static String MIN_IDLE = "redis.min.idle";
    public final static String TEST_WHILEIDLE = "redis.test.whileIdle";
    public final static String TEST_NUMPEREVICTION = "redis.test.numPerEviction";
    public final static String TIME_BETWEENEVICTION = "redis.time.betweenEviction";

    public final static String PASSWORD = "redis.password";
    public final static String DATABASE = "redis.database";
    public final static String TIMEOUT = "redis.timeout";

    // redis property default values
    public final static String DEFAULT_MAX_ACTIVE_VALUE = "10";
    public final static String DEFAULT_TEST_ONBORROW_VALUE = "true";
    public final static String DEFAULT_TEST_ONRETURN_VALUE = "true";
    public final static String DEFAULT_MAX_IDLE_VALUE = "5";
    public final static String DEFAULT_MIN_IDLE_VALUE = "1";
    public final static String DEFAULT_TEST_WHILEIDLE_VALUE = "true";
    public final static String DEFAULT_TEST_NUMPEREVICTION_VALUE = "10";
    public final static String DEFAULT_TIME_BETWEENEVICTION_VALUE = "60000";
    public final static String DEFAULT_CLUSTER_ENABLED = "false";
    public final static String CONN_FAILED_RETRY_MSG = "Jedis connection failed, retrying...";
}
