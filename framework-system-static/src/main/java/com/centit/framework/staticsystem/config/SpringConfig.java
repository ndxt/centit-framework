package com.centit.framework.staticsystem.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.H2Config;
import com.centit.framework.config.RedisConfig;
import com.centit.framework.listener.InitialWebRuntimeEnvironment;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.MemorySessionRegistryImpl;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;
import com.centit.framework.staticsystem.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource("classpath:system.properties")
@Import({RedisConfig.class, H2Config.class, SpringSecurityDaoConfig.class, SpringSecurityCasConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Lazy
public class SpringConfig implements EnvironmentAware {

    @Autowired
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
    public CentitPasswordEncoderImpl passwordEncoder() {
        return  new CentitPasswordEncoderImpl();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
            CentitPasswordEncoder passwordEncoder) {

        Boolean ipEnable = env.getProperty("centit.ip.enable",Boolean.class);// = false
        PlatformEnvironment staticPlatformEnvironment=null;

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
            if(ipEnable==null || !ipEnable) {
                return jdbcPlatformEnvironment;
            }else {
                staticPlatformEnvironment = jdbcPlatformEnvironment;
            }
        } else{
            StaticPlatformEnvironment jsonPlatformEnvironment = new StaticPlatformEnvironment();
            jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
            jsonPlatformEnvironment.init();
            if(ipEnable==null || !ipEnable) {
                return jsonPlatformEnvironment;
            }else {
                staticPlatformEnvironment = jsonPlatformEnvironment;
            }
        }

        IPClientPlatformEnvironment ipPlatformEnvironment = new IPClientPlatformEnvironment();
        ipPlatformEnvironment.setTopOptId(env.getProperty("centit.ip.topoptid"));
        ipPlatformEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));
        ipPlatformEnvironment.init();

        List<PlatformEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipPlatformEnvironment);
        evrnMangers.add(staticPlatformEnvironment);

        PlatformEnvironmentProxy platformEnvironment = new PlatformEnvironmentProxy();
        platformEnvironment.setEvrnMangers(evrnMangers);

        return platformEnvironment;
    }

    @Bean
    @Lazy(value = false)
    public IntegrationEnvironment integrationEnvironment() {

        Boolean ipEnable = env.getProperty("centit.ip.enable",Boolean.class);// = false
        StaticIntegrationEnvironment jsonIntegrationEnvironment = new StaticIntegrationEnvironment();
        jsonIntegrationEnvironment.init();
        if(ipEnable==null || !ipEnable)
            return jsonIntegrationEnvironment;

        IPClientIntegrationEnvironment ipIntegrationEnvironment = new IPClientIntegrationEnvironment();
        ipIntegrationEnvironment.setPlatServerUrl(env.getProperty("centit.ip.home"));
        //ipPlatformEnvironment.init();

        List<IntegrationEnvironment> evrnMangers = new ArrayList<>();
        evrnMangers.add(ipIntegrationEnvironment);
        evrnMangers.add(jsonIntegrationEnvironment);

        IntegrationEnvironmentProxy integrationEnvironment = new IntegrationEnvironmentProxy();
        integrationEnvironment.setEvrnMangers(evrnMangers);

        return integrationEnvironment;
    }

    @Bean
    public CentitSessionRegistry centitSessionRegistry(){
        return new MemorySessionRegistryImpl();
    }


    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

}
