package com.centit.framework.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrameworkHttpSessionConfiguration {

    @Value("${session.strategy.cookie.first:false}")
    private boolean cookieFist;
    @Value("${session.strategy.cookie.path:/}")
    private String cookiePath;

    @Bean
    public SmartHttpSessionResolver smartHttpSessionStrategy(){
        return new SmartHttpSessionResolver(cookieFist, cookiePath);
    }
}
