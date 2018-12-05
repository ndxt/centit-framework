package com.centit.framework.session.jdbc;

import com.centit.framework.session.FrameworkHttpSessionConfiguration;
import com.centit.support.database.utils.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Conditional(JdbcSessionPersistenceCondition.class)
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = -1)
public class JdbcSessionPersistenceConfig extends FrameworkHttpSessionConfiguration {

    private Logger logger = LoggerFactory.getLogger("session持久化");

    @Value("${session.jdbc.url:jdbc:h2:mem:framework_session;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false}")
    private String url;

    @Value("${session.jdbc.username:sa}")
    private String username;

    @Value("${session.jdbc.password:}")
    private String password;

    @Bean
    public EmbeddedDatabase jdbcSessionDataSource() {
        String schema = "org/springframework/session/jdbc/schema-%s.sql";
        Class<? extends Driver> driverClass;
        DBType type = DBType.mapDBType(url);
        switch (type){
            case MySql:
                schema = String.format(schema, "mysql");
                driverClass = com.mysql.jdbc.Driver.class;
                break;
            case Oracle:
                schema = String.format(schema, "oracle");
                driverClass = oracle.jdbc.driver.OracleDriver.class;
                break;
            default:
                schema = String.format(schema, "h2");
                driverClass = org.h2.Driver.class;
        }

        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .continueOnError(true)
                .setDataSourceFactory(new DataSourceFactory() {

                    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

                    @Override
                    public ConnectionProperties getConnectionProperties() {
                        return new ConnectionProperties() {
                            @Override
                            public void setDriverClass(Class<? extends Driver> d) {
                                dataSource.setDriverClass(driverClass);
                            }

                            @Override
                            public void setUrl(String u) {
                                dataSource.setUrl(url);
                            }

                            @Override
                            public void setUsername(String u) {
                                dataSource.setUsername(username);
                            }

                            @Override
                            public void setPassword(String p) {
                                dataSource.setPassword(password);
                            }
                        };
                    }
                    @Override
                    public DataSource getDataSource() {
                        return this.dataSource;
                    }
                })
                .addScript(schema).build();
    }

    @Bean
    public FindByIndexNameSessionRepository sessionRepository(
        @Qualifier(value = "jdbcSessionDataSource") EmbeddedDatabase dataSource) {
        JdbcOperationsSessionRepository sessionRepository =
            new JdbcOperationsSessionRepository(new JdbcTemplate(dataSource), new DataSourceTransactionManager(dataSource));
        return sessionRepository;
    }

    @Bean
    public SessionRegistry sessionRegistry(
        @Autowired FindByIndexNameSessionRepository sessionRepository){
        return new SpringSessionBackedSessionRegistry( sessionRepository);
    }
}
