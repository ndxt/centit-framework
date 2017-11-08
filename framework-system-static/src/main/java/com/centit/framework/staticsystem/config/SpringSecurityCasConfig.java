package com.centit.framework.staticsystem.config;

import com.centit.framework.config.SecurityCasCondition;
import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityCasCondition.class)
public class SpringSecurityCasConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    public CentitSessionRegistry centitSessionRegistry;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置不拦截规则
        web.ignoring().antMatchers("/system/login","/service/exception/**","/system/login/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ServiceProperties casServiceProperties = createCasServiceProperties();
        CasAuthenticationEntryPoint casEntryPoint = createCasEntryPoint(casServiceProperties);

        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.csrf.enable"),false)) {
            http.csrf();
        } else {
            http.csrf().disable();
        }
        http.logout()
                .logoutSuccessUrl("/index.jsp")
                .and()
                .exceptionHandling().accessDeniedPage("/service/exception/accessDenied")
                .and()
                .httpBasic()
                .authenticationEntryPoint(casEntryPoint);

        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = createAjaxSuccessHandler();
        AjaxAuthenticationFailureHandler ajaxFailureHandler = createAjaxFailureHandler();
        CasAuthenticationProvider casAuthenticationProvider = createCasAuthenticationProvider(casServiceProperties);
        AuthenticationManager authenticationManager = creatAuthenticationManager(casAuthenticationProvider);
        CasAuthenticationFilter casFilter = createCasFilter(authenticationManager, ajaxSuccessHandler, ajaxFailureHandler);

        DaoAccessDecisionManager centitAccessDecisionManagerBean = createCentitAccessDecisionManagerBean();
        DaoInvocationSecurityMetadataSource centitSecurityMetadataSource = createCentitSecurityMetadataSource();
        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(authenticationManager,
                centitAccessDecisionManagerBean,centitSecurityMetadataSource);

        http.addFilterAt(casFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class)
                .addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleLogoutFilter(), CasAuthenticationFilter.class);
    }

    private CasAuthenticationEntryPoint createCasEntryPoint(ServiceProperties casServiceProperties) {
        CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
        casEntryPoint.setLoginUrl(env.getProperty("cas.home")+"/login");
        casEntryPoint.setServiceProperties(casServiceProperties);
        return casEntryPoint;
    }

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(env.getProperty("local.home")+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    private CasAuthenticationProvider createCasAuthenticationProvider(ServiceProperties casServiceProperties) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setUserDetailsService(userDetailsService);
        casAuthenticationProvider.setServiceProperties(casServiceProperties);
        casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(env.getProperty("cas.home")));
        casAuthenticationProvider.setKey(env.getProperty("app.key"));
        return casAuthenticationProvider;
    }

    private DaoAccessDecisionManager createCentitAccessDecisionManagerBean() {
        return new DaoAccessDecisionManager();
    }

    private DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        return new DaoInvocationSecurityMetadataSource();
    }

    private DaoFilterSecurityInterceptor createCentitPowerFilter(AuthenticationManager authenticationManager,
                                                                 DaoAccessDecisionManager centitAccessDecisionManagerBean,
                                                                 DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        DaoFilterSecurityInterceptor centitPowerFilter = new DaoFilterSecurityInterceptor();
        centitPowerFilter.setAuthenticationManager(authenticationManager);
        centitPowerFilter.setAccessDecisionManager(centitAccessDecisionManagerBean);
        centitPowerFilter.setSecurityMetadataSource(centitSecurityMetadataSource);
        centitPowerFilter.setSessionRegistry(centitSessionRegistry);
        return centitPowerFilter;
    }

    private AjaxAuthenticationFailureHandler createAjaxFailureHandler() {
        AjaxAuthenticationFailureHandler ajaxFailureHandler = new AjaxAuthenticationFailureHandler();
        ajaxFailureHandler.setDefaultFailureUrl("/system/mainframe/login/error");
        ajaxFailureHandler.setWriteLog(false);
        return ajaxFailureHandler;
    }

    private AjaxAuthenticationSuccessHandler createAjaxSuccessHandler() {
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = new AjaxAuthenticationSuccessHandler();
        ajaxSuccessHandler.setWriteLog(true);
        return ajaxSuccessHandler;
    }

    private CasAuthenticationFilter createCasFilter(AuthenticationManager authenticationManager,
                                                    AjaxAuthenticationSuccessHandler ajaxSuccessHandler,
                                                    AjaxAuthenticationFailureHandler ajaxFailureHandler) {

        CasAuthenticationFilter casFilter = new CasAuthenticationFilter();
        casFilter.setAuthenticationManager(authenticationManager);
        casFilter.setAuthenticationFailureHandler(ajaxFailureHandler);
        casFilter.setAuthenticationSuccessHandler(ajaxSuccessHandler);
        return casFilter;
    }

    private SingleSignOutFilter singleLogoutFilter() {
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        singleLogoutFilter.setCasServerUrlPrefix(env.getProperty("cas.home"));
        return singleLogoutFilter;
    }

    private LogoutFilter requestSingleLogoutFilter() {
        return new LogoutFilter(env.getProperty("cas.home")+"/logout", new SecurityContextLogoutHandler());
    }

    private AuthenticationManager creatAuthenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(casAuthenticationProvider);
        return new ProviderManager(providerList);
    }

}
