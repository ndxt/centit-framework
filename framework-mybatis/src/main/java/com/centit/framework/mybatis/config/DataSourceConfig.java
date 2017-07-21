package com.centit.framework.mybatis.config;

import com.centit.framework.config.FlywayDisableCondition;
import com.centit.framework.config.FlywayEnableCondition;
import com.centit.framework.mybatis.dao.BaseDaoSupport;
import com.centit.support.algorithm.ListOpt;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)//启用注解事物管理
@PropertySource("classpath:system.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Lazy
public class DataSourceConfig implements EnvironmentAware {

    @Autowired
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
        dataSource.setValidationQuery(env.getProperty("jdbc.validationQuery"));
        return dataSource;
    }

    @Bean(initMethod = "migrate")
    @Conditional(FlywayEnableCondition.class)
    @Lazy(value = false)
    public Flyway flyway(BasicDataSource dataSource) {
        Flyway flywayMigration = new Flyway();
        flywayMigration.setDataSource(dataSource);
        flywayMigration.setBaselineOnMigrate(true);
        flywayMigration.setLocations(env.getProperty("flyway.sql.dir"),
                "com.centit.framework.system.update");
        return flywayMigration;
    }

    @Bean(name = "flyway")
    @Conditional(FlywayDisableCondition.class)
    @Lazy(value = false)
    public Flyway flywayBean() {
        return null;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(BasicDataSource dataSource) throws IOException {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLazyLoadingEnabled(true);
        configuration.setSafeRowBoundsEnabled(false);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setAggressiveLazyLoading(false);

        Properties properties = new Properties();
        properties.setProperty("Oracle","oracle");
        properties.setProperty("DB2","db2");
        properties.setProperty("MySQL","mysql");
        properties.setProperty("SQL Server","sqlserver");

        /*PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
        propertiesFactory.setProperties(properties);*/

        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        databaseIdProvider.setProperties(properties);

        sessionFactory.setDataSource(dataSource);
        sessionFactory.setConfiguration(configuration);

        sessionFactory.setDatabaseIdProvider(databaseIdProvider);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//      sessionFactory.setConfigLocation(resolver.getResource("classpath:mybatis/mybatis-config.xml"));
        String fileMatch = env.getProperty("mybatis.map.xml.filematch");
        String[] fileMatchs =  fileMatch.split(",");
        ArrayList<Resource> fileMatchList = new ArrayList<>(256);
        for(String fm : fileMatchs){
            Resource [] resources = resolver.getResources(fm);
            if(resources!=null) {
                for (Resource obj : resources)
                    fileMatchList.add(obj);
            }
        }
        sessionFactory.setMapperLocations(ListOpt.listToArray(fileMatchList));
        return  sessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() throws IOException {
        MapperScannerConfigurer configuerer = new MapperScannerConfigurer();
        configuerer.setBasePackage("com.centit");
        configuerer.setAnnotationClass(Repository.class);
        configuerer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configuerer;
    }

    @Bean
    @DependsOn("flyway")
    public DataSourceTransactionManager transactionManager(BasicDataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
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

    @Bean
    public BaseDaoSupport baseDaoSupport (@Autowired SqlSessionFactory sqlSessionFactory){
        BaseDaoSupport baseDaoSupport = new BaseDaoSupport();
        baseDaoSupport.setSqlSessionFactory(sqlSessionFactory);
        return baseDaoSupport;
    }
}
