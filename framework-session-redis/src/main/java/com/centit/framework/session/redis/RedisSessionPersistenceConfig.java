package com.centit.framework.session.redis;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import com.centit.framework.session.CentitSessionRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)
public class RedisSessionPersistenceConfig{
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
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public SessionRegistry sessionRegistry(
        @Autowired RedisIndexedSessionRepository sessionRepository){
        // sessionRepository.setDefaultSerializer(new FastJsonRedisSerializer<>(Object.class));
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }

    @Bean
    public CentitSessionRepo centitSessionRepo(@Autowired RedisIndexedSessionRepository sessionRepository){
        return new CentitSessionRedisRepo(sessionRepository);
    }

    /**
     * @param redisConnectionFactory 这个是 framework-session-redis中的bean耦合
     * @return RedisTemplate bean
     */
    @Bean
    public RedisTemplate<String, JSONObject> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, JSONObject> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        GenericFastJsonRedisSerializer serializer = new GenericFastJsonRedisSerializer();
        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
