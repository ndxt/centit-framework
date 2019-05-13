package com.centit.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(FrameworkProperties.PREFIX)
@Data
public class FrameworkProperties {
    public static final String PREFIX = "framework";

    private AppConfig app;

    @Data
    public static class AppConfig{
        private String home;
    }


}
