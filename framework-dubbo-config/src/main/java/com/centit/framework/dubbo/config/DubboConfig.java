package com.centit.framework.dubbo.config;

import org.apache.dubbo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class DubboConfig {
    Logger logger = LoggerFactory.getLogger(DubboConfig.class);

    @Value("${centit.dubbo.nacos.url:nacos://192.168.134.10:8848}")
    private String nacosUrl;
    @Value("${centit.dubbo.app.name:centit-provider}")
    private String appName;
    @Value("${centit.dubbo.provider.timeout:50000}")
    private Integer timeout;

    @Value("${centit.dubbo.dubboprotocol.name:dubbo}")
    private String dubboProtocolName;
    @Value("${centit.dubbo.dubboprotocol.server:}")
    private String dubboprotocolServer;
    @Value("${centit.dubbo.dubboprotocol.port:20885}")
    private Integer dubboprotocolPort;

    @Value("${centit.dubbo.consumer.check:false}")
    private Boolean check;

    @Value("${centit.dubbo.consumer.retries:0}")
    private Integer retries;

    /**
     * 应用名
     * @return ApplicationConfig
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(appName);
        return applicationConfig;
    }

    /**
     * 地址配置 <dubbo:registry address="zookeeper://127.0.0.1:2181" client="zkclient"/>
     * @return registryConfig
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(nacosUrl);
        return registryConfig;
    }

    /**
     * <dubbo:provider timeout="10000" />
     * @return providerConfig
     */
    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(timeout);
        return providerConfig;
    }

    /**
     * 协议配置，等同于 <dubbo:protocol name="dubbo" port="20880" />
     * @return ProtocolConfig
     */
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(dubboProtocolName);
        protocolConfig.setPort(dubboprotocolPort);
        protocolConfig.setServer(dubboprotocolServer);
        return protocolConfig;
    }

    @Bean
    public ConsumerConfig consumerConfig(){
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setCheck(check);
        consumerConfig.setRetries(retries);
        return consumerConfig;
    }
}
