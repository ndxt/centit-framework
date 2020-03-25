package com.centit.framework.config;

import com.centit.framework.security.DaoFilterSecurityInterceptor;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
//ConditionalOnClass(name="org.jasig.cas.client.session.SingleSignOutFilter")
@ConditionalOnProperty(prefix = "security.login.cas", name = "enabled")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityCasConfig extends WebSecurityBaseConfig {

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(securityProperties.getLogin().getCas().getLocalHome()+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    protected AuthenticationProvider getAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setUserDetailsService(centitUserDetailsService);
        casAuthenticationProvider.setServiceProperties(createCasServiceProperties());
        casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(
            securityProperties.getLogin().getCas().getCasHome()));
        /*"centit-demo"*/
        casAuthenticationProvider.setKey(securityProperties.getLogin().getCas().getAppKey());
        return casAuthenticationProvider;
    }

    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        ServiceProperties serviceProperties = createCasServiceProperties();
        CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
        casEntryPoint.setLoginUrl(securityProperties.getLogin().getCas().getCasHome());
        casEntryPoint.setServiceProperties(serviceProperties);
        return casEntryPoint;
    }

    protected AuthenticationManager createAuthenticationManager() {
        AuthenticationProvider authenticationProvider = getAuthenticationProvider();
        Assert.notNull(authenticationProvider, "authenticationProvider不能为空");
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

    private SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        singleLogoutFilter.setCasServerUrlPrefix(
            securityProperties.getLogin().getCas().getCasHome());
        return singleLogoutFilter;
    }

    protected AbstractAuthenticationProcessingFilter getAuthenticationFilter() {
        CasAuthenticationFilter casFilter = new CasAuthenticationFilter();
        casFilter.setAuthenticationManager(createAuthenticationManager());
        casFilter.setAuthenticationFailureHandler(createFailureHandler());
        casFilter.setAuthenticationSuccessHandler(createSuccessHandler(centitUserDetailsService));
        /*SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);
        casFilter.setRememberMeServices(rememberMeServices);*/
        return casFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        if(securityProperties.getHttp().isCsrfEnable()) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
            .antMatchers("/system/mainframe/login", "/system/exception", "/oauth/check_token").permitAll()
            .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
            .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
            .and().httpBasic().authenticationEntryPoint(getAuthenticationEntryPoint());

        http.headers().frameOptions().sameOrigin();

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(
            createCentitAccessDecisionManager(),
            createCentitSecurityMetadataSource());
        http
            .addFilterAt(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class)
            .addFilterBefore( singleSignOutFilter(), CasAuthenticationFilter.class);
    }
}
