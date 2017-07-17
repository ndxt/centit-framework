package com.centit.framework.config;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by zou_wy on 2017/6/21.
 */
public class H2DataSourceFactory implements DataSourceFactory {

    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource(BeanUtils.instantiateClass(org.h2.Driver.class),"jdbc:h2:D:/h2Database;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false","sa","");

    @Override
    public ConnectionProperties getConnectionProperties() {
        return new ConnectionProperties() {
            @Override
            public void setDriverClass(Class<? extends Driver> driverClass) {
                dataSource.setDriverClass(driverClass);
            }

            @Override
            public void setUrl(String url) {
                dataSource.setUrl(url);
            }

            @Override
            public void setUsername(String username) {
                dataSource.setUsername(username);
            }

            @Override
            public void setPassword(String password) {
                dataSource.setPassword(password);
            }
        };
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
