package com.centit.framework.config;

import com.centit.support.algorithm.BooleanBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import javax.servlet.Filter;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityCasCondition.class)
public class SpringSecurityCasConfig extends SpringSecurityBaseConfig {

    @Override
    protected String[] getAuthenticatedUrl() {
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("access.resource.notallowed.anonymous"),false)) {
            return new String[]{"/**"};
        }
        return new String[]{"/system/mainframe/logincas"};
    }

    @Override
    protected String[] getPermitAllUrl() {
        return new String[]{"/**/csrf","/**/doc.html","/system/exception"};
    }

    @Override
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        ServiceProperties serviceProperties = createCasServiceProperties();
        CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
        String loginUrl = env.getProperty("login.cas.entryPoint");
        if(StringUtils.isBlank(loginUrl)){
            loginUrl = env.getProperty("login.cas.casHome")+"/login";
        }
        casEntryPoint.setLoginUrl(loginUrl);
        casEntryPoint.setServiceProperties(serviceProperties);
        return casEntryPoint;
    }

    @Override
    protected AbstractAuthenticationProcessingFilter getAuthenticationFilter() {
        CasAuthenticationFilter casFilter = new CasAuthenticationFilter();
        casFilter.setAuthenticationManager(createAuthenticationManager());
        casFilter.setAuthenticationFailureHandler(createAjaxFailureHandler());
        casFilter.setAuthenticationSuccessHandler(createAjaxSuccessHandler());

        if(sessionRegistry != null) {
            casFilter.setSessionAuthenticationStrategy(
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry));
        }
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(BooleanBaseOpt.castObjectToBoolean(
            env.getProperty("session.always.rememberme"), false));

        casFilter.setRememberMeServices(rememberMeServices);
        return casFilter;
    }

    @Override
    protected Filter logoutFilter() {
        return new LogoutFilter(env.getProperty("login.cas.casHome")+"/logout", new SecurityContextLogoutHandler());
    }

    @Override
    protected AuthenticationProvider getAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setUserDetailsService(centitUserDetailsService);
        casAuthenticationProvider.setServiceProperties(createCasServiceProperties());
        casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(env.getProperty("login.cas.casHome")));
        casAuthenticationProvider.setKey(env.getProperty("app.key"));
        return casAuthenticationProvider;
    }

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(env.getProperty("login.cas.localHome")+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // 添加单点登出过滤器
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        //singleLogoutFilter.setCasServerUrlPrefix(env.getProperty("login.cas.casHome"));
        http.addFilterBefore(singleLogoutFilter, CasAuthenticationFilter.class);
    }
}
