package com.centit.framework.config;

import com.centit.support.database.utils.DBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.session.jdbc.JdbcOperationsSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by zou_wy on 2017/6/14.
 */
@Conditional(H2SessionPersistenceCondition.class)
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = -1)
public class H2SessionPersistenceConfig {

    private Logger logger = LoggerFactory.getLogger("session持久化");

    @Value("${session.h2.file:}")
    private String filePath;

    @Value("${session.jdbc.url:}")
    private String url;

    @Value("${session.jdbc.username:}")
    private String username;

    @Value("${session.jdbc.password:}")
    private String password;

    @Bean
    public EmbeddedDatabase h2SessiondataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .continueOnError(true)
                .setDataSourceFactory(new DataSourceFactory() {

                    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

                    @Override
                    public ConnectionProperties getConnectionProperties() {
                        return new ConnectionProperties() {
                            @Override
                            public void setDriverClass(Class<? extends Driver> driverClass) {
                                DBType type = DBType.mapDBType(url);
                                switch (type){
                                    case H2:
                                        dataSource.setDriverClass(org.h2.Driver.class);
                                        break;
                                    case MySql:
                                        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
                                        break;
                                    case Oracle:
                                        dataSource.setDriverClass(oracle.jdbc.driver.OracleDriver.class);
                                        break;
                                    default:
                                }
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
                .addScript("org/springframework/session/jdbc/schema-mysql.sql").build();
    }

    @Bean(name = "springSessionJdbcOperations")
	public JdbcTemplate springSessionJdbcOperations(@Qualifier(value = "h2SessiondataSource") EmbeddedDatabase dataSource) {
		return new JdbcTemplate(dataSource);
	}

    @Bean
    public PlatformTransactionManager sessionTransactionManager(EmbeddedDatabase dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcOperationsSessionRepository sessionRepository(
        @Qualifier("springSessionJdbcOperations") JdbcOperations jdbcOperations,
        @Qualifier("sessionTransactionManager")PlatformTransactionManager transactionManager) {
        JdbcOperationsSessionRepository sessionRepository =
            new JdbcOperationsSessionRepository(jdbcOperations, transactionManager);
        sessionRepository.setDefaultMaxInactiveInterval(-1);
//        String tableName = getTableName();
//        if (StringUtils.hasText(tableName)) {
//            sessionRepository.setTableName(tableName);
//        }
//        sessionRepository
//            .setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
//        if (this.lobHandler != null) {
//            sessionRepository.setLobHandler(this.lobHandler);
//        }
//        if (this.springSessionConversionService != null) {
//            sessionRepository.setConversionService(this.springSessionConversionService);
//        }
//        else if (this.conversionService != null) {
//            sessionRepository.setConversionService(this.conversionService);
//        }
//        else if (deserializingConverterSupportsCustomClassLoader()) {
//            GenericConversionService conversionService = createConversionServiceWithBeanClassLoader();
//            sessionRepository.setConversionService(conversionService);
//        }
        return sessionRepository;
    }
}
