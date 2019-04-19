package com.centit.framework.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@Configuration
@EnableSpringHttpSession
public class FrameworkHttpSessionConfiguration {

    @Value("${session.strategy.cookie.first:true}")
    private boolean cookieFist;
    @Value("${session.strategy.cookie.path:/}")
    private String cookiePath;

    @Bean
    public SmartHttpSessionResolver smartHttpSessionStrategy(){
        return new SmartHttpSessionResolver(cookieFist, cookiePath);
    }
}
