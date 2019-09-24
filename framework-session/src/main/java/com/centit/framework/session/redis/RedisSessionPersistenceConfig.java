package com.centit.framework.session.redis;

import com.centit.framework.session.FrameworkHttpSessionConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Conditional(RedisSessionPersistenceCondition.class)
@EnableRedisHttpSession
public class RedisSessionPersistenceConfig extends FrameworkHttpSessionConfiguration {
    //private Logger logger = LoggerFactory.getLogger(RedisSessionPersistenceConfig.class);

    @Value("${session.redis.host:}")
    private String host;

    @Value("${session.redis.port:6379}")
    private Integer port;

    @Value("${session.redis.password:}")
    private String password;

    @Value("${session.redis.database:0}")
    private Integer database;
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
            new RedisStandaloneConfiguration(host,port);
        configuration.setDatabase(database);
        if(StringUtils.isNotBlank(password)){
            configuration.setPassword(RedisPassword.of(password));
        }
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public SessionRegistry sessionRegistry(
        @Autowired FindByIndexNameSessionRepository sessionRepository){
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }
}
