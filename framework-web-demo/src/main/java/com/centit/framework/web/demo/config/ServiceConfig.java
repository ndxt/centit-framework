package com.centit.framework.web.demo.config;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.InitialWebRuntimeEnvironment;
import com.centit.framework.config.SpringSecurityCasConfig;
import com.centit.framework.config.SpringSecurityDaoConfig;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.security.model.MemorySessionRegistryImpl;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.service.impl.EmailMessageSenderImpl;
import com.centit.framework.staticsystem.config.StaticSystemBeanConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.core.session.SessionRegistry;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@ComponentScan(basePackages = {"com.centit","com.otherpackage"},
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
@Import({
        SpringSecurityDaoConfig.class,
        SpringSecurityCasConfig.class,
        StaticSystemBeanConfig.class})
public class ServiceConfig {

    @Value("${app.home:./}")
    private String appHome;

    @Bean(initMethod = "initialEnvironment")
    @Lazy(value = false)
    public InitialWebRuntimeEnvironment initialEnvironment() {
        return new InitialWebRuntimeEnvironment();
    }

    /**
     * 这个bean必须要有
     * @return CentitPasswordEncoder 密码加密算法
     */
    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return  new StandardPasswordEncoderImpl();
    }
    //这个bean必须要有 可以配置不同策略的session保存方案

    //@Value("${message.sender.email.hostName:}")
    //@Value("${message.sender.email.smtpPort:25}")
    //@Value("${message.sender.email.userName:}")
    //@Value("${message.sender.email.userPassword:}")
    //@Value("${message.sender.email.serverEmail:}")

    @Bean
    public EmailMessageSenderImpl emailMessageManager(){
        EmailMessageSenderImpl messageManager = new EmailMessageSenderImpl();
        messageManager.setHostName("mail.centit.com");
        messageManager.setSmtpPort(25);
        messageManager.setUserName("accounts");
        messageManager.setUserPassword("yhs@yhs1");
        messageManager.setServerEmail("noreplay@centit.com");
        return messageManager;
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        //这个不是必须的,只是为了在没有真正的发送类时不报错
        notificationCenter.initDummyMsgSenders();
        //打开消息推送服务日志
        notificationCenter.setWriteNoticeLog(true);
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
