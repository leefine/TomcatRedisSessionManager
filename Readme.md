<div class="expandable unchanged js-expandable rich-diff-level-zero">
    <h1 class="unchanged rich-diff-level-one">Tomcat Redis Session Manager</h1>
    <p class="unchanged rich-diff-level-one">Redis session manager is pluggable one. It uses to store sessions into Redis for easy distribution of HTTP Requests across a cluster of Tomcat servers. Sessions are implemented as as non-sticky i.e, each request is forwarded to any server in round-robin manner.</p>
    <p class="unchanged rich-diff-level-one">The HTTP Requests session setAttribute(name, value) method stores the session into Redis (must be Serializable) immediately and the session getAttribute(name) method request directly from Redis. Also, the inactive sessions has been removed based on the session time-out configuration.</p>
    <p class="unchanged rich-diff-level-one">It supports, both single redis master and redis cluster based on the RedisDataCache.properties configuration.</p>
    <p class="unchanged rich-diff-level-one">Going forward, we no need to enable sticky session (JSESSIONID) in Load balancer.</p>    
    <p class="unchanged rich-diff-level-one">Download:https://github.com/leefine/TomcatRedisSessionManager/releases/download/1.0/TomcatRedisSessionManager-1.0.0.zip
    </p>
    <h2 class="unchanged rich-diff-level-one">Supports:</h2>
    <ul class="unchanged rich-diff-level-one">
        <li class="unchanged">Apache Tomcat 7</li>
        <li class="unchanged">Apache Tomcat 8</li>
        <li class="unchanged">Apache Tomcat 9</li>
    </ul>
    <h4 class="unchanged rich-diff-level-one">Dependency:</h4>
    <ol class="unchanged rich-diff-level-one">
        <li class="unchanged">jedis.jar</li>
        <li class="unchanged">commons-pool2.jar</li>
        <li class="unchanged">commons-logging.jar</li>
    </ol>
    <h4 class="unchanged rich-diff-level-one">Steps to be done,</h4>
    <ol class="unchanged rich-diff-level-one">
        <li class="unchanged">
            <p class="unchanged">Extract downloaded package (tomcat-redis-session-manager.zip) to configure Redis credentials in redis-server.properties file and move the file to tomcat/conf directory</p>
            <ul class="unchanged">
                <li class="unchanged"><strong>tomcat/conf/redis-server.properties</strong></li>
                <li class="unchanged"><strong>modify configuration in [redis-server.properties]</strong></li>
            </ul>
        </li>
        <li class="unchanged">
            <p class="unchanged">Move the downloaded jars to <b>tomcat/lib directory</b></p>
            <ul class="unchanged">
                <li class="unchanged"><strong>tomcat/lib/</strong></li>
            </ul>
        </li>
        <li class="unchanged">
            <p class="unchanged">Add the below two lines in  <b>tomcat/conf/context.xml</b></p>
            <ul class="unchanged">
                <li class="unchanged"><strong>&lt;Valve className="com.leefine.tomcat.redis.SessionHandlerValve" /&gt;</strong></li>
                <li class="unchanged"><strong>&lt;Manager className="com.leefine.tomcat.redis.SessionManager" /&gt;</strong></li>
            </ul>
        </li>
        <li class="unchanged">
            <p class="unchanged">Verify the session expiration time in <b>tomcat/conf/web.xml</b></p>
            <ul class="unchanged">
                <li class="unchanged"><strong>&lt;session-config&gt;</strong></li>
                <li class="unchanged"><strong>&lt;session-timeout&gt;60&lt;/session-timeout&gt;</strong></li>
                <li class="unchanged"><strong>&lt;/session-config&gt;</strong></li>
            </ul>
        </li>
    </ol>
    <h3 class="unchanged rich-diff-level-one">
      Note:</h3>
    <ul class="unchanged rich-diff-level-one">
        <li class="unchanged">This supports, both redis stand-alone and multiple node cluster based on the redis-data-cache.properties configuration.</li>
    </ul>
     <h3 class="unchanged rich-diff-level-one">
      Config Nginx:</h3>   
      
      
      #user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;

events {
    worker_connections  1024;
}

http {
    log_format main ‘$remote_addr – $remote_user [$time_local] “$request” ‘‘$status $body_bytes_sent “$http_referer” ‘‘”$http_user_agent” $http_x_forwarded_for’;
    include       mime.types;
    default_type  application/octet-stream;
	access_log    logs/access.log  main;
	sendfile      on;
	keepalive_timeout  65;
    upstream epower {
        least_conn;
        server 127.0.0.1:8090;
        server 127.0.0.1:8091;
    }
    server {
        listen 8080;
        location / {
            proxy_pass http://epower;
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
</div>
