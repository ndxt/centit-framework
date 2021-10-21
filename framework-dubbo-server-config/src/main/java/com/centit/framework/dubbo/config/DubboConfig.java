package com.centit.framework.dubbo.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:system.properties")
//@ImportResource({"classpath:dubbo-server.xml"})
public class DubboConfig {

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

    @Value("${centit.dubbo.rmiprotocol.name:rmi}")
    private String rmiProtocolName;
    @Value("${centit.dubbo.rmiprotocol.server:servlet}")
    private String rmiProtocolServer;
    @Value("${centit.dubbo.rmiprotocol.port:21886}")
    private Integer rmiProtocolPort;


    @Value("${centit.dubbo.hessianprotocol.name:hessian}")
    private String hessianProtocolName;
    @Value("${centit.dubbo.hessianprotocol.server:servlet}")
    private String hessianProtocolServer;
    //该端口必须和tomcat端口一致   默认8080
    @Value("${centit.dubbo.hessianprotocol.port:8080}")
    private Integer hessianProtocolPort;
    @Value("${centit.dubbo.hessianprotocol.contextpath:}")
    private String contextpath;


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

    @Bean
    public ProtocolConfig rmiProtocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(rmiProtocolName);
        protocolConfig.setServer(rmiProtocolServer);
        protocolConfig.setPort(rmiProtocolPort);
        return protocolConfig;
    }

    @Bean
    public ProtocolConfig hessianProtocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(hessianProtocolName);
        protocolConfig.setServer(hessianProtocolServer);
        protocolConfig.setPort(hessianProtocolPort);
        protocolConfig.setContextpath(contextpath);
        return protocolConfig;
    }
}
