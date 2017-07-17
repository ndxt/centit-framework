package com.centit.framework.security;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 * 
 * 这个过滤器仅对请求的IP进行过滤，对符合条件的IP的所有请求放行
 * 
 * @author codefan
 * @create 2015年11月30日
 * @version
 */
//@Component("hostIpFilterDecisionManagerBean")
public class HostIpFilterDecisionManager implements AccessDecisionManager {
    protected static final Logger logger = LoggerFactory.getLogger(HostIpFilterDecisionManager.class);

    // In this method, need to compare authentication with configAttributes.
    // 1, A object is a URL, a filter was find permission configuration by this
    // URL, and pass to here.
    // 2, Check authentication has attribute in permission configuration
    // (configAttributes)
    // 3, If not match corresponding authentication, throw a
    // AccessDeniedException.
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        
        if(configAttributes!=null && configAttributes.size()>0)
            return;
         
        //没有权限，组织提示信息。
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getHttpRequest();
        String urlIp = request.getRemoteHost();
        logger.error(urlIp+" 是不允许访问这个服务的。");
        throw new AccessDeniedException(urlIp+" 是不允许访问这个服务的。");
    }

    public boolean supports(ConfigAttribute arg0) {
        return true;
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}
