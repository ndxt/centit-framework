package com.centit.framework.session.jdbc;

import com.centit.framework.session.CentitSessionRepo;
import com.centit.support.database.utils.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 7200)
public class JdbcSessionPersistenceConfig {

    private Logger logger = LoggerFactory.getLogger(JdbcSessionPersistenceConfig.class);

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
                driverClass = com.mysql.cj.jdbc.Driver.class;
                break;
            case Oracle:
                schema = String.format(schema, "oracle");
                driverClass = oracle.jdbc.driver.OracleDriver.class;
                break;
            default:
                schema = String.format(schema, "h2");
                driverClass = org.h2.Driver.class;
        }
        logger.info("已启用 jdbc [{}] session持久化", type.toString());
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
    public JdbcIndexedSessionRepository sessionRepository(
        @Qualifier(value = "jdbcSessionDataSource") EmbeddedDatabase dataSource) {
        return new JdbcIndexedSessionRepository(new JdbcTemplate(dataSource), new TransactionTemplate(new DataSourceTransactionManager(dataSource)));
    }

    @Bean
    public SessionRegistry sessionRegistry(
        @Autowired JdbcIndexedSessionRepository sessionRepository){
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }

    @Bean
    public CentitSessionRepo centitSessionRepo(@Autowired JdbcIndexedSessionRepository sessionRepository){
        return new CentitSessionJdbcRepo(sessionRepository);
    }
}
