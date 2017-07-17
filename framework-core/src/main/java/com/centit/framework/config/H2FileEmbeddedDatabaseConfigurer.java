package com.centit.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

/**
 * Created by zou_wy on 2017/6/21.
 */
public class H2FileEmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static H2FileEmbeddedDatabaseConfigurer instance;

    private final Class<? extends Driver> driverClass;


    /**
     * Get the singleton {@code H2EmbeddedDatabaseConfigurer} instance.
     * @return the configurer
     * @throws ClassNotFoundException if H2 is not on the classpath
     */
    @SuppressWarnings("unchecked")
    public static synchronized H2FileEmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new H2FileEmbeddedDatabaseConfigurer( (Class<? extends Driver>)
                    ClassUtils.forName("org.h2.Driver", H2FileEmbeddedDatabaseConfigurer.class.getClassLoader()));
        }
        return instance;
    }


    private H2FileEmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        properties.setDriverClass(this.driverClass);
        properties.setUrl(String.format("jdbc:h2:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", databaseName));
        properties.setUsername("sa");
        properties.setPassword("");
    }

    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.createStatement().execute("SHUTDOWN");
        }
        catch (SQLException ex) {
            logger.warn("Could not shut down embedded database", ex);
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (Throwable ex) {
                    logger.debug("Could not close JDBC Connection on shutdown", ex);
                }
            }
        }
    }
}
