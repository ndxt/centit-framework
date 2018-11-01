package com.centit.framework.security;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DaoFilterSecurityInterceptor extends AbstractSecurityInterceptor
        implements Filter {

    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    private SessionRegistry sessionRegistry;

    public void setSessionRegistry(SessionRegistry sessionManger) {
        this.sessionRegistry = sessionManger;
    }
    // ~ Methods
    // ========================================================================================================

    private boolean allResourceMustBeAudited = false;

    public void setAllResourceMustBeAudited(boolean allResourceMustBeAudited) {
        this.allResourceMustBeAudited = allResourceMustBeAudited;
    }
    /**
     * Method that is actually called by the filter chain. Simply delegates to
     * the {@link #invoke(FilterInvocation)} method.
     *
     * @param request  the servlet request
     * @param response the servlet response
     * @param chain    the filter chain
     * @throws IOException      if the filter chain fails
     * @throws ServletException if the filter chain fails
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);

        // throw new AccessDeniedException("");
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public Class<? extends Object> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    public void invoke(FilterInvocation fi) throws IOException,
            ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean alwaysReauthenticate = false;

        if("XMLHttpRequest".equals(fi.getRequest().getHeader("X-Requested-With"))) {
            //从token中获取用户信息
            if(authentication==null || "anonymousUser".equals(authentication.getName())){
                HttpServletRequest request = fi.getHttpRequest();
                String accessToken = request.getParameter(SecurityContextUtils.SecurityContextTokenName);

                if(StringUtils.isBlank(accessToken)) {
                    accessToken = request.getHeader("Authorization");
                }

                if(StringUtils.isBlank(accessToken)) {
                    accessToken = request.getSession().getId();
                }

                CentitUserDetails ud = SecurityContextUtils.getCurrentUserDetails(sessionRegistry, accessToken);
                if(ud!=null){
                    alwaysReauthenticate = this.isAlwaysReauthenticate();
                    if(alwaysReauthenticate) {
                        this.setAlwaysReauthenticate(false);
                    }
                    SecurityContextHolder.getContext().setAuthentication(ud);
                    //设置用户默认语言
                    WebOptUtils.setCurrentLang(fi.getHttpRequest(),
                        ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE));
                }
            }
            if (allResourceMustBeAudited && (authentication == null || "anonymousUser".equals(authentication.getName()))) {
                fi.getResponse().setStatus(401);
                return;
            }
        }
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }

        if(alwaysReauthenticate) {
            this.setAlwaysReauthenticate(true);
        }
    }

    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(
            FilterInvocationSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

}

