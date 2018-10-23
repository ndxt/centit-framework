package com.centit.framework.staticsystem.config;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.MemorySessionRegistryImpl;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.annotation.Resource;

@PropertySource("classpath:system.properties")
public class StaticSystemBeanConfig implements EnvironmentAware {

    private Environment env;

    @Resource
    @Override
    public void setEnvironment(Environment environment) {
        if(environment!=null) {
            this.env = environment;
        }
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    /* 这bean从框架中移除，由开发人员自行定义
     * 这个bean必须要有
     * @return CentitPasswordEncoder 密码加密算法
     */
    /*@Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return  new StandardPasswordEncoderImpl();
    }*/

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
            return jdbcPlatformEnvironment;
        } else{
            JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
            jsonPlatformEnvironment.setAppHome(env.getProperty("app.home","./"));
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

    @Bean
    public SessionRegistry sessionRegistry(){
        return new MemorySessionRegistryImpl();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return //new LazyCsrfTokenRepository(
                new HttpSessionCsrfTokenRepository();
    }

}
