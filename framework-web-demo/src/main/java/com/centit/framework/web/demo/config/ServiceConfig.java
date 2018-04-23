package com.centit.framework.web.demo.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.staticsystem.config.StaticSystemBeanConfig;
import com.centit.msgpusher.msgpusher.websocket.SocketMsgPusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@ComponentScan(basePackages = {"com.centit","com.otherpackage"},
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@Import({RedisSessionPersistenceConfig.class,
        H2SessionPersistenceConfig.class,
        SpringSecurityDaoConfig.class,
        SpringSecurityCasConfig.class,
        StaticSystemBeanConfig.class})
public class ServiceConfig {

    @Autowired
    protected SocketMsgPusher socketMsgPusher;

    @Value("${app.home}")
    private String appHome;

    @Bean(initMethod = "initialEnvironment")
    @Lazy(value = false)
    public InitialWebRuntimeEnvironment initialEnvironment() {
        return new InitialWebRuntimeEnvironment();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        if(socketMsgPusher!=null){
            notificationCenter.setSocketMsgPusher(socketMsgPusher);
        }

        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl  operationLog =  new TextOperationLogWriterImpl();
        operationLog.setOptLogHomePath(appHome+"/logs");
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
