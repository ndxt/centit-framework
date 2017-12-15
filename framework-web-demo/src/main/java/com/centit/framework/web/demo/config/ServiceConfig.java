package com.centit.framework.web.demo.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.H2SessionPersistenceConfig;
import com.centit.framework.config.RedisSessionPersistenceConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.staticsystem.config.StaticSystemBeanConfig;
import com.centit.framework.web.demo.listener.InstantiationServiceBeanPostProcessor;
import com.centit.msgpusher.msgpusher.websocket.SocketMsgPusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

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
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }

}
