package com.centit.framework.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@Configuration
@EnableSpringHttpSession
public class FrameworkHttpSessionConfiguration {

    @Bean
    public SmartHttpSessionStrategy smartHttpSessionStrategy(){
        return new SmartHttpSessionStrategy();
    }

    @Bean
    public MapSessionRepository sessionRepository(){
        return new MapSessionRepository();
    }

}
