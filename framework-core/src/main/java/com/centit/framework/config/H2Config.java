package com.centit.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Conditional(H2Condition.class)
@EnableJdbcHttpSession
public class H2Config {

    @Value("${session.h2.file}")
    private String filePath;

    @Bean
    public DataSource h2SessiondataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
//                .setDataSourceFactory(new DataSourceFactory () {
//
//                    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
//                            BeanUtils.instantiateClass(org.h2.Driver.class),
//                            "jdbc:h2:" + filePath + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "");
//
//                    @Override
//                    public ConnectionProperties getConnectionProperties() {
//                        return new ConnectionProperties() {
//                            @Override
//                            public void setDriverClass(Class<? extends Driver> driverClass) {
//                                dataSource.setDriverClass(driverClass);
//                            }
//
//                            @Override
//                            public void setUrl(String url) {
//                                dataSource.setUrl(url);
//                            }
//
//                            @Override
//                            public void setUsername(String username) {
//                                dataSource.setUsername(username);
//                            }
//
//                            @Override
//                            public void setPassword(String password) {
//                                dataSource.setPassword(password);
//                            }
//                        };
//                    }
//
//                    @Override
//                    public DataSource getDataSource() {
//                        return this.dataSource;
//                    }
//                })
                .addScript("org/springframework/session/jdbc/schema-h2.sql").build();
    }



    @Bean
    public PlatformTransactionManager transactionManager(DataSource h2SessiondataSource) {
        return new DataSourceTransactionManager(h2SessiondataSource);
    }
}
