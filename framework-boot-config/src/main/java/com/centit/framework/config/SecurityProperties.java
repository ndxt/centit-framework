package com.centit.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(SecurityProperties.PREFIX)
public class SecurityProperties {
    public static final String PREFIX = "security";

    private Login login;
    private Logout logout;
    private Access access;
    private Http http;

    @Data
    public static class Login {

        private Cas cas;
        private Success success;
        private Failure failure;
        private Captcha captcha;
        private Retry retry;

        @Data
        public static class Cas {
            private String localHome;
            private String casHome;
        }

        @Data
        public static class Success {
            private boolean writeLog;
            private boolean registToken;
            private String targetUrl;
        }

        @Data
        public static class Failure {
            private boolean writeLog;
            private String targetUrl;
        }

        @Data
        public static class Captcha {
            private int checkTime;
            private int checkType;
        }

        @Data
        public static class Retry {
            private String checkType;
            private int maxTryTimes;
            private int lockMinites;
            private int checkTimeInterval;
        }
    }

    @Data
    public static class Logout {
        private String targetUrl;
    }

    @Data
    public static class Access {
        private boolean enableAnonymous;
        private boolean resourceMustAudited;
    }

    @Data
    public static class Http {
        private boolean csrfEnable;
        private boolean filterContinueAuthentication;
        private String xFrameOptionsMode;
    }
}
