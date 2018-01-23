package com.centit.framework.config;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * Created by zou_wy on 2017/3/29.
 */

public abstract class SpringSecurityBaseConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected Environment env;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;

    @Autowired
    protected CentitSessionRegistry centitSessionRegistry;

    @Autowired
    protected CentitUserDetailsService centitUserDetailsService;


    protected DaoFilterSecurityInterceptor createCentitPowerFilter(AuthenticationManager authenticationManager,
                                                                 DaoAccessDecisionManager centitAccessDecisionManagerBean,
                                                                 DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        DaoFilterSecurityInterceptor centitPowerFilter = new DaoFilterSecurityInterceptor();
        centitPowerFilter.setAuthenticationManager(authenticationManager);
        centitPowerFilter.setAccessDecisionManager(centitAccessDecisionManagerBean);
        centitPowerFilter.setSecurityMetadataSource(centitSecurityMetadataSource);
        centitPowerFilter.setSessionRegistry(centitSessionRegistry);
        return centitPowerFilter;
    }

    protected AjaxAuthenticationFailureHandler createAjaxFailureHandler() {
        AjaxAuthenticationFailureHandler ajaxFailureHandler = new AjaxAuthenticationFailureHandler();
        String defaultTargetUrl = env.getProperty("login.failure.targetUrl");
        ajaxFailureHandler.setDefaultFailureUrl(
                StringBaseOpt.emptyValue(defaultTargetUrl,
                        "/system/mainframe/login/error"));
        ajaxFailureHandler.setWriteLog(
                BooleanBaseOpt.castObjectToBoolean(
                        env.getProperty("login.failure.writeLog"),false));
        return ajaxFailureHandler;
    }

    protected AjaxAuthenticationSuccessHandler createAjaxSuccessHandler(CentitUserDetailsService centitUserDetailsService) {
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = new AjaxAuthenticationSuccessHandler();
        String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        ajaxSuccessHandler.setDefaultTargetUrl(StringBaseOpt.emptyValue(defaultTargetUrl,"/"));

        ajaxSuccessHandler.setWriteLog(BooleanBaseOpt.castObjectToBoolean(
                env.getProperty("login.success.writeLog"),true));
        ajaxSuccessHandler.setRegistToken(BooleanBaseOpt.castObjectToBoolean(
                env.getProperty("login.success.registToken"),false));
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    protected DaoAccessDecisionManager createCentitAccessDecisionManager() {
        DaoAccessDecisionManager accessDecisionManager = new DaoAccessDecisionManager();
        accessDecisionManager.setAllResourceMustBeAudited(
                BooleanBaseOpt.castObjectToBoolean(
                        env.getProperty("access.resource.must.be.audited"),false));
        return accessDecisionManager;
    }

    protected DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        return new DaoInvocationSecurityMetadataSource();
    }

}
