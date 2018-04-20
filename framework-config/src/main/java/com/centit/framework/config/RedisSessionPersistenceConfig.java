package com.centit.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Conditional(RedisSessionPersistenceCondition.class)
@EnableRedisHttpSession
public class RedisSessionPersistenceConfig {

    @Value("${session.redis.host}")
    private String host;

    @Value("${session.redis.port:6379}")
    private Integer port;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(host,port);
    }
}
