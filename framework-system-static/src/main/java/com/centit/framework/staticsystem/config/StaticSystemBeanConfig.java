package com.centit.framework.staticsystem.config;

import com.centit.framework.listener.InitialWebRuntimeEnvironment;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.*;
import com.centit.framework.staticsystem.service.impl.JdbcPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;

@PropertySource("classpath:system.properties")
public class StaticSystemBeanConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    @Bean(initMethod = "initialEnvironment")
    @Lazy(value = false)
    public InitialWebRuntimeEnvironment initialEnvironment() {
        return new InitialWebRuntimeEnvironment();
    }

    @Bean
    public CentitPasswordEncoder passwordEncoder() {
        return  new CentitPasswordEncoderImpl();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
            @Autowired CentitPasswordEncoder passwordEncoder) {

        Boolean jdbcEnable = env.getProperty("centit.jdbcplatform.enable", Boolean.class);// = false
        if (jdbcEnable != null && jdbcEnable) {
            JdbcPlatformEnvironment jdbcPlatformEnvironment = new JdbcPlatformEnvironment();
            jdbcPlatformEnvironment.setDataBaseConnectInfo(
                 env.getProperty("centit.jdbcplatform.url"),
                    env.getProperty("centit.jdbcplatform.username"),
                    env.getProperty("centit.jdbcplatform.password")
            );
            jdbcPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            jdbcPlatformEnvironment.init();
            return jdbcPlatformEnvironment;
        } else{
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            jsonPlatformEnvironment.init();
            return jsonPlatformEnvironment;
        }
    }


    @Bean
    public CentitUserDetailsService userDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

    @Bean
    public CentitSessionRegistry centitSessionRegistry(){
        return new MemorySessionRegistryImpl();
    }


    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return //new LazyCsrfTokenRepository(
                new HttpSessionCsrfTokenRepository();
    }

}
