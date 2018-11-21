package com.centit.framework.web.demo.config;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.service.impl.EmailMessageSenderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by codefan on 17-7-6.
 */
public class InstantiationServiceBeanPostProcessor
    implements ApplicationListener<ContextRefreshedEvent>{

    @Autowired
    protected NotificationCenter notificationCenter;

    @Autowired
    private OperationLogWriter operationLogWriter;

    @Autowired(required = false)
    private EmailMessageSenderImpl emailMessageManager;

    @Autowired(required = false)
    protected PlatformEnvironment platformEnvironment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event){
        CodeRepositoryCache.setPlatformEnvironment(platformEnvironment);
        CodeRepositoryCache.setAllCacheFreshPeriod(CodeRepositoryCache.CACHE_KEEP_FRESH);

        if(emailMessageManager!=null) {
            notificationCenter.registerMessageSender("email", emailMessageManager);
            notificationCenter.appointDefaultSendType("email");
        }
        if(operationLogWriter!=null) {
            OperationLogCenter.registerOperationLogWriter(operationLogWriter);
        }
    }

}
