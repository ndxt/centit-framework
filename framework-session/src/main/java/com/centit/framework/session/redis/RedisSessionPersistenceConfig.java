package com.centit.framework.session.redis;

import com.centit.framework.session.FrameworkHttpSessionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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

    @Value("${session.redis.host:}")
    private String host;

    @Value("${session.redis.port:6379}")
    private Integer port;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(host,port);
    }

    @Bean
    public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository sessionRepository){
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }
}
