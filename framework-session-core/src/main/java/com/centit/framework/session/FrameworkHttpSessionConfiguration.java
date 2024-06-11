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
    @Value("${session.strategy.addAccessToken:true}")
    private boolean addAccessToken;

    @Bean
    public SmartHttpSessionResolver httpSessionIdResolver(){
        return new SmartHttpSessionResolver(cookieFist, cookiePath, addAccessToken);
    }
}
