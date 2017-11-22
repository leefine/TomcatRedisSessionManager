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


<table class="highlight tab-size js-file-line-container" data-tab-size="8">
      <tbody><tr>
        <td id="L1" class="blob-num js-line-number" data-line-number="1"></td>
        <td id="LC1" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L2" class="blob-num js-line-number" data-line-number="2"></td>
        <td id="LC2" class="blob-code blob-code-inner js-file-line"><span class="pl-c">#user  nobody;</span></td>
      </tr>
      <tr>
        <td id="L3" class="blob-num js-line-number" data-line-number="3"></td>
        <td id="LC3" class="blob-code blob-code-inner js-file-line"><span class="pl-k">worker_processes</span>  <span class="pl-s">1</span>;</td>
      </tr>
      <tr>
        <td id="L4" class="blob-num js-line-number" data-line-number="4"></td>
        <td id="LC4" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L5" class="blob-num js-line-number" data-line-number="5"></td>
        <td id="LC5" class="blob-code blob-code-inner js-file-line"><span class="pl-c">#error_log  logs/error.log;</span></td>
      </tr>
      <tr>
        <td id="L6" class="blob-num js-line-number" data-line-number="6"></td>
        <td id="LC6" class="blob-code blob-code-inner js-file-line"><span class="pl-c">#error_log  logs/error.log  notice;</span></td>
      </tr>
      <tr>
        <td id="L7" class="blob-num js-line-number" data-line-number="7"></td>
        <td id="LC7" class="blob-code blob-code-inner js-file-line"><span class="pl-c">#error_log  logs/error.log  info;</span></td>
      </tr>
      <tr>
        <td id="L8" class="blob-num js-line-number" data-line-number="8"></td>
        <td id="LC8" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L9" class="blob-num js-line-number" data-line-number="9"></td>
        <td id="LC9" class="blob-code blob-code-inner js-file-line"><span class="pl-c">#pid        logs/nginx.pid;</span></td>
      </tr>
      <tr>
        <td id="L10" class="blob-num js-line-number" data-line-number="10"></td>
        <td id="LC10" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L11" class="blob-num js-line-number" data-line-number="11"></td>
        <td id="LC11" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L12" class="blob-num js-line-number" data-line-number="12"></td>
        <td id="LC12" class="blob-code blob-code-inner js-file-line"><span class="pl-k">events</span> {</td>
      </tr>
      <tr>
        <td id="L13" class="blob-num js-line-number" data-line-number="13"></td>
        <td id="LC13" class="blob-code blob-code-inner js-file-line">    <span class="pl-k">worker_connections</span>  <span class="pl-s">1024</span>;</td>
      </tr>
      <tr>
        <td id="L14" class="blob-num js-line-number" data-line-number="14"></td>
        <td id="LC14" class="blob-code blob-code-inner js-file-line">}</td>
      </tr>
      <tr>
        <td id="L15" class="blob-num js-line-number" data-line-number="15"></td>
        <td id="LC15" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L16" class="blob-num js-line-number" data-line-number="16"></td>
        <td id="LC16" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L17" class="blob-num js-line-number" data-line-number="17"></td>
        <td id="LC17" class="blob-code blob-code-inner js-file-line"><span class="pl-k">http</span> {</td>
      </tr>
      <tr>
        <td id="L18" class="blob-num js-line-number" data-line-number="18"></td>
        <td id="LC18" class="blob-code blob-code-inner js-file-line"><span class="pl-k">log_format</span><span class="pl-c1"> main</span> ‘<span class="pl-smi">$remote_addr</span> – <span class="pl-smi">$remote_user</span> [<span class="pl-smi">$time_local</span>] “<span class="pl-smi">$request</span>” ‘‘<span class="pl-smi">$status</span> <span class="pl-smi">$body_bytes_sent</span> “<span class="pl-smi">$http_referer</span>” ‘‘”<span class="pl-smi">$http_user_agent</span>” <span class="pl-smi">$http_x_forwarded_for</span>’;</td>
      </tr>
      <tr>
        <td id="L19" class="blob-num js-line-number" data-line-number="19"></td>
        <td id="LC19" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L20" class="blob-num js-line-number" data-line-number="20"></td>
        <td id="LC20" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L21" class="blob-num js-line-number" data-line-number="21"></td>
        <td id="LC21" class="blob-code blob-code-inner js-file-line">    <span class="pl-k">include</span>       mime.types;</td>
      </tr>
      <tr>
        <td id="L22" class="blob-num js-line-number" data-line-number="22"></td>
        <td id="LC22" class="blob-code blob-code-inner js-file-line">    <span class="pl-k">default_type</span>  application/octet-stream;</td>
      </tr>
      <tr>
        <td id="L23" class="blob-num js-line-number" data-line-number="23"></td>
        <td id="LC23" class="blob-code blob-code-inner js-file-line">	<span class="pl-k">access_log</span>    logs/access.log <span class="pl-c1"> main</span>;</td>
      </tr>
      <tr>
        <td id="L24" class="blob-num js-line-number" data-line-number="24"></td>
        <td id="LC24" class="blob-code blob-code-inner js-file-line">	<span class="pl-k">sendfile</span>     <span class="pl-c1"> on</span>;</td>
      </tr>
      <tr>
        <td id="L25" class="blob-num js-line-number" data-line-number="25"></td>
        <td id="LC25" class="blob-code blob-code-inner js-file-line">	<span class="pl-k">keepalive_timeout</span>  <span class="pl-s">65</span>;</td>
      </tr>
      <tr>
        <td id="L26" class="blob-num js-line-number" data-line-number="26"></td>
        <td id="LC26" class="blob-code blob-code-inner js-file-line">	</td>
      </tr>
      <tr>
        <td id="L27" class="blob-num js-line-number" data-line-number="27"></td>
        <td id="LC27" class="blob-code blob-code-inner js-file-line">    <span class="pl-k">upstream</span> <span class="pl-en">epower </span>{</td>
      </tr>
      <tr>
        <td id="L28" class="blob-num js-line-number" data-line-number="28"></td>
        <td id="LC28" class="blob-code blob-code-inner js-file-line">	least_conn;</td>
      </tr>
      <tr>
        <td id="L29" class="blob-num js-line-number" data-line-number="29"></td>
        <td id="LC29" class="blob-code blob-code-inner js-file-line">        <span class="pl-k">server</span> 127.0.0.1:8090;</td>
      </tr>
      <tr>
        <td id="L30" class="blob-num js-line-number" data-line-number="30"></td>
        <td id="LC30" class="blob-code blob-code-inner js-file-line">        <span class="pl-k">server</span> 127.0.0.1:8091;</td>
      </tr>
      <tr>
        <td id="L31" class="blob-num js-line-number" data-line-number="31"></td>
        <td id="LC31" class="blob-code blob-code-inner js-file-line">    }</td>
      </tr>
      <tr>
        <td id="L32" class="blob-num js-line-number" data-line-number="32"></td>
        <td id="LC32" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L33" class="blob-num js-line-number" data-line-number="33"></td>
        <td id="LC33" class="blob-code blob-code-inner js-file-line">    <span class="pl-k">server</span> {</td>
      </tr>
      <tr>
        <td id="L34" class="blob-num js-line-number" data-line-number="34"></td>
        <td id="LC34" class="blob-code blob-code-inner js-file-line">        <span class="pl-k">listen</span> <span class="pl-s">8080</span>;</td>
      </tr>
      <tr>
        <td id="L35" class="blob-num js-line-number" data-line-number="35"></td>
        <td id="LC35" class="blob-code blob-code-inner js-file-line">
</td>
      </tr>
      <tr>
        <td id="L36" class="blob-num js-line-number" data-line-number="36"></td>
        <td id="LC36" class="blob-code blob-code-inner js-file-line">        <span class="pl-k">location</span> <span class="pl-en">/ </span>{</td>
      </tr>
      <tr>
        <td id="L37" class="blob-num js-line-number" data-line-number="37"></td>
        <td id="LC37" class="blob-code blob-code-inner js-file-line">            <span class="pl-k">proxy_pass</span> http://epower;</td>
      </tr>
      <tr>
        <td id="L38" class="blob-num js-line-number" data-line-number="38"></td>
        <td id="LC38" class="blob-code blob-code-inner js-file-line">        }</td>
      </tr>
      <tr>
        <td id="L39" class="blob-num js-line-number" data-line-number="39"></td>
        <td id="LC39" class="blob-code blob-code-inner js-file-line">    }</td>
      </tr>
      <tr>
        <td id="L40" class="blob-num js-line-number" data-line-number="40"></td>
        <td id="LC40" class="blob-code blob-code-inner js-file-line">}</td>
      </tr>
</tbody></table>
}
</div>
