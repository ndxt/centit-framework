package com.centit.framework.dubbo.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:system.properties")
//@ImportResource({"classpath:dubbo-server.xml"})
public class DubboServiceConfig {
    Logger logger = LoggerFactory.getLogger(DubboServiceConfig.class);

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

    /**
     * 应用名
     * @return
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(appName);
        return applicationConfig;
    }

    /**
     * 地址配置 <dubbo:registry address="zookeeper://127.0.0.1:2181" client="zkclient"/>
     *
     * @return
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(nacosUrl);
        return registryConfig;
    }

    /**
     * <dubbo:provider timeout="10000" />
     *
     * @return
     */
    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(timeout);
        return providerConfig;
    }

    /**
     * 协议配置，等同于 <dubbo:protocol name="dubbo" port="20880" />
     *
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
}
