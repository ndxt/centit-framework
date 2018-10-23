package com.centit.framework.security;

import javax.servlet.http.HttpSession;

import com.centit.framework.common.ObjectException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.UuidOpt;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.context.ContextLoaderListener;

public class SecurityContextUtils {
    public static final String AJAX_CHECK_CAPTCHA_RESULT = "ajaxCheckCaptchaResult";
    public final static String SecurityContextUserdetail = "SECURITY_CONTEXT_USERDETAIL";
    public final static String SecurityContextTokenName = "accessToken";
    public final static String PUBLIC_ROLE_CODE = "public";
    public final static String ADMIN_ROLE_CODE = "sysadmin";
    public final static String ANONYMOUS_ROLE_CODE = "anonymous";
    public final static String FORBIDDEN_ROLE_CODE = "forbidden";
    public final static String DEPLOYER_ROLE_CODE = "deploy";

    public static SessionRegistry getSessionRegistry() {
        return ContextLoaderListener.getCurrentWebApplicationContext().
                getBean("sessionRegistry",  SessionRegistry.class);
    }

    public static String registerUserToken(CentitUserDetails ud){
        String tokenKey = UuidOpt.getUuidAsString();
        SessionRegistry registry = getSessionRegistry();
        if(registry==null)
            throw new ObjectException(ud,"获取bean：centitSessionRegistry出错，请检查配置文件。");
        registry.registerNewSession(tokenKey,ud);
        return tokenKey;
    }

    public static void setSecurityContext(CentitUserDetails ud){
        SecurityContextHolder.getContext().setAuthentication(ud);
    }

    public static void setSecurityContext(CentitUserDetails ud,HttpSession session){
        SecurityContextHolder.getContext().setAuthentication(ud);
        session.setAttribute(SecurityContextUserdetail, ud);
    }

    public static CentitUserDetails  getCurrentUserDetails(SessionRegistry sessionRegistry, String /**sessionId*/ accessToken){

        SessionInformation info = sessionRegistry.getSessionInformation(accessToken);
        if(info==null){
            return null;
        }
        return (CentitUserDetails)info.getPrincipal();
    }
}
