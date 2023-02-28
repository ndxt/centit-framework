package com.centit.framework.config;

import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.centit.framework.core.controller.MvcConfigUtil;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.staticsystem.service.impl.JdbcPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@EnableConfigurationProperties(FrameworkProperties.class)
@Configuration("frameworkBeanConfiguation")
public class FrameworkBeanConfiguation {

    @Autowired
    private FrameworkProperties frameworkProperties;

    @Autowired
    private CentitPasswordEncoder passwordEncoder;

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        return MvcConfigUtil.fastJsonHttpMessageConverter();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment() {

        if (frameworkProperties.getJdbcplatform().isEnable()) {
            JdbcPlatformEnvironment jdbcPlatformEnvironment = new JdbcPlatformEnvironment();
            jdbcPlatformEnvironment.setDataBaseConnectInfo(
                frameworkProperties.getJdbcplatform().getUrl(),
                frameworkProperties.getJdbcplatform().getUsername(),
                frameworkProperties.getJdbcplatform().getPassword());
            jdbcPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            return jdbcPlatformEnvironment;
        } else{
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setAppHome(frameworkProperties.getApp().getHome());
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            return jsonPlatformEnvironment;
        }
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }
}
