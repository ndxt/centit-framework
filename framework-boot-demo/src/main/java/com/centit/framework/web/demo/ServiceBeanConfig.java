package com.centit.framework.web.demo;

import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.config.FrameworkProperties;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codefan on 17-7-18.
 */
@Configuration
@EnableConfigurationProperties(FrameworkProperties.class)
public class ServiceBeanConfig {


    @Autowired
    private FrameworkProperties frameworkProperties;

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
        operationLog.setOptLogHomePath(
            frameworkProperties.getApp().getHome()+"/logs");
        operationLog.init();
        return operationLog;
    }

    @Bean
    public InstantiationServiceBeanPostProcessor instantiationServiceBeanPostProcessor() {
        return new InstantiationServiceBeanPostProcessor();
    }




}
