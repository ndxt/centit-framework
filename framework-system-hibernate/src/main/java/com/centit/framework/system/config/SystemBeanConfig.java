package com.centit.framework.system.config;

import com.centit.framework.config.BaseBeanConfig;
import com.centit.framework.config.H2Config;
import com.centit.framework.config.RedisConfig;
import com.centit.framework.hibernate.config.DataSourceConfig;
import com.centit.framework.listener.InitialWebRuntimeEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.MemorySessionRegistryImpl;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration

@ComponentScan(basePackages = "com.centit.framework",
               excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@PropertySource("classpath:system.properties")
@Import({BaseBeanConfig.class,
        DataSourceConfig.class,
        RedisConfig.class, H2Config.class,
        SpringSecurityDaoConfig.class,
        SpringSecurityCasConfig.class}
        )
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class SystemBeanConfig implements EnvironmentAware {

    @Autowired
    protected Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    /**
     * 配置事务异常封装
     * @return PersistenceExceptionTranslationPostProcessor
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

    @Bean
    public OpenSessionInViewInterceptor openSessionInViewInterceptor(SessionFactory sessionFactory) {
        OpenSessionInViewInterceptor openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(sessionFactory);
        return openSessionInViewInterceptor;
    }

    @Bean(initMethod = "initialEnvironment")
    @Lazy(value = false)
    public InitialWebRuntimeEnvironment initialEnvironment() {
        return new InitialWebRuntimeEnvironment();
    }

    @Bean
    public CentitPasswordEncoderImpl passwordEncoder() {
        return  new CentitPasswordEncoderImpl();
    }

    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public CentitSessionRegistry centitSessionRegistry(){
        return new MemorySessionRegistryImpl();
    }



}
