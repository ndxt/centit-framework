package com.centit.framework.security;

public abstract class SecurityContextUtils {
    public static String AJAX_CHECK_CAPTCHA_RESULT = "ajaxCheckCaptchaResult";
    public static String SecurityContextTokenName = "accessToken";
    public static String PUBLIC_ROLE_CODE = "public";
    public static String ADMIN_ROLE_CODE = "sysadmin";
    public static String ANONYMOUS_ROLE_CODE = "anonymous";
    public static String SPRING_ANONYMOUS_ROLE_CODE = "ROLE_ANONYMOUS";
    public static String FORBIDDEN_ROLE_CODE = "forbidden";
    public static String DEPLOYER_ROLE_CODE = "deploy";
}
