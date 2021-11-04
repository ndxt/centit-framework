package com.centit.framework.dubbo.config;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:system.properties")
//@ImportResource({"classpath:dubbo-client.xml"})
public class DubboClientConfig {
    @Value("${centit.dubbo.consumer.nacos.url:nacos://192.168.134.10:8848}")
    private String nacosUrl;

    @Value("${centit.dubbo.consumer.app.name:centit-consumer}")
    private String appName;

    @Value("${centit.dubbo.consumer.check:false}")
    private Boolean check;
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

    @Bean
    public ConsumerConfig consumerConfig(){
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setCheck(check);
        return consumerConfig;
    }
}
