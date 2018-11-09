package com.centit.framework.config;

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
@EnableJdbcHttpSession
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

    @Value("${session.jdbc.drive:}")
    private String drive;

    @Bean
    public EmbeddedDatabase h2SessiondataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .continueOnError(true)
                .setDataSourceFactory(new DataSourceFactory() {

                    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource(
                            BeanUtils.instantiateClass(org.h2.Driver.class),
                            "jdbc:h2:" + filePath + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "");

                    @Override
                    public ConnectionProperties getConnectionProperties() {
                        return new ConnectionProperties() {
                            @Override
                            public void setDriverClass(Class<? extends Driver> driverClass) {
//                                try {
//                                    dataSource.setDriverClass(Class.forName(drive));
//                                }catch (ClassNotFoundException e){
//                                    logger.error("找不到数据库驱动类", e);
//                                }
                            }

                            @Override
                            public void setUrl(String url) {
//                                dataSource.setUrl(url);
                            }

                            @Override
                            public void setUsername(String username) {
//                                dataSource.setUsername(username);
                            }

                            @Override
                            public void setPassword(String password) {
//                                dataSource.setPassword(password);
                            }
                        };
                    }

                    @Override
                    public DataSource getDataSource() {
                        return this.dataSource;
                    }
                })
                .addScript("org/springframework/session/jdbc/schema-h2.sql").build();
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
