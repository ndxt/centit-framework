package com.centit.framework.security;

public interface SecurityContextUtils {
    String AJAX_CHECK_CAPTCHA_RESULT = "ajaxCheckCaptchaResult";
    String SecurityContextTokenName = "accessToken";
    String PUBLIC_ROLE_CODE = "public";
    String ADMIN_ROLE_CODE = "sysadmin";
    String ANONYMOUS_ROLE_CODE = "anonymous";
    String FORBIDDEN_ROLE_CODE = "forbidden";
    String DEPLOYER_ROLE_CODE = "deploy";
}
