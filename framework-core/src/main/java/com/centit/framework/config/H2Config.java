package com.centit.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Configuration
@Conditional(H2Condition.class)
@EnableJdbcHttpSession
public class H2Config {

    @Bean
    public DataSource h2SessiondataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setDataSourceFactory(new H2DataSourceFactory())
                .addScript("org/springframework/session/jdbc/schema-derby.sql").build();
    }

//    @Bean
//    DataSource datasource(){
//
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        resolver.getResource("classpath:mybatis/mybatis-config.xml");
//
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUsername("embedded");
//        dataSource.setPassword("embedded");
//        dataSource.setUrl("jdbc:h2:D:/EmbeddedDb");
////        dataSource.setUrl("jdbc:h2:D:/EmbeddedDb;INIT=create schema if not exists Queue\\; runscript from 'D:/sql.sql'");
//
//        return dataSource;
//    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource h2SessiondataSource) {
        return new DataSourceTransactionManager(h2SessiondataSource);
    }
}
