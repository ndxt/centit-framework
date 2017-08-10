package com.centit.framework.hibernate.config;

import com.centit.framework.config.FlywayDisableCondition;
import com.centit.framework.config.FlywayEnableCondition;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

//@Configuration
@EnableTransactionManagement(proxyTargetClass = true)//启用注解事物管理
//@PropertySource("classpath:/system.properties")
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@Lazy
public class DataSourceConfig implements EnvironmentAware {

//    @Autowired
    Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    @Bean(destroyMethod = "close")
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driver"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        dataSource.setMaxTotal(env.getProperty("jdbc.maxActive", Integer.class));
        dataSource.setMaxIdle(env.getProperty("jdbc.maxIdle", Integer.class));
        dataSource.setMaxWaitMillis(env.getProperty("jdbc.maxWait", Integer.class));
        dataSource.setDefaultAutoCommit(env.getProperty("jdbc.defaultAutoCommit", Boolean.class));
        dataSource.setRemoveAbandonedTimeout(env.getProperty("jdbc.removeAbandonedTimeout", Integer.class));
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(BasicDataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", env.getProperty("jdbc.dialect"));
        hibernateProperties.put("hibernate.show_sql", true);
        hibernateProperties.put("hibernate.id.new_generator_mappings", true);
        sessionFactory.setHibernateProperties(hibernateProperties);
        String[] packagesToScan = new String[]{"com.centit.*"};
        sessionFactory.setPackagesToScan(packagesToScan);
        return  sessionFactory;
    }

    @Bean(initMethod = "migrate")
    @Conditional(FlywayEnableCondition.class)
    @Lazy(value = false)
    public Flyway flyway(BasicDataSource dataSource) {
        Flyway flywayMigration = new Flyway();
        flywayMigration.setDataSource(dataSource);
        flywayMigration.setBaselineOnMigrate(true);
        flywayMigration.setLocations(env.getProperty("flyway.sql.dir"), "com.centit.framework.system.update");
        return flywayMigration;
    }

    @Bean(name = "flyway")
    @Conditional(FlywayDisableCondition.class)
    @Lazy(value = false)
    public Flyway flywayBean() {
        return null;
    }

    @Bean
    @DependsOn("flyway")
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
        return new AutowiredAnnotationBeanPostProcessor();
    }

   /* @Bean
    public ReloadableResourceBundleMessageSource validationMessageSource(){
        ReloadableResourceBundleMessageSource validationMessageSource = new ReloadableResourceBundleMessageSource();
        validationMessageSource.setBasename("classpath:messagesource/validation/validation");
        validationMessageSource.setFallbackToSystemLocale(false);
        validationMessageSource.setUseCodeAsDefaultMessage(false);
        validationMessageSource.setDefaultEncoding("UTF-8");
        validationMessageSource.setCacheSeconds(120);
        return  validationMessageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
        localValidatorFactoryBean.setValidationMessageSource(validationMessageSource());
        return  localValidatorFactoryBean;
    }*/

}
