package com.centit.framework.config;

import com.centit.framework.security.AjaxAuthenticationSuccessHandler;
import com.centit.framework.security.DaoFilterSecurityInterceptor;
import com.centit.framework.security.PretreatmentAuthenticationProcessingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfLogoutHandler;

@Configuration
@EnableWebSecurity
//ConditionalOnMissingClass("org.jasig.cas.client.session.SingleSignOutFilter")
@ConditionalOnProperty(prefix = "security.login.dao", name = "enabled")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityDaoConfig extends WebSecurityBaseConfig {

    private LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/system/mainframe/login");
    }

    private UsernamePasswordAuthenticationFilter createPretreatmentAuthenticationProcessingFilter(
        AuthenticationManager authenticationManager,AjaxAuthenticationSuccessHandler ajaxSuccessHandler,
        AuthenticationFailureHandler ajaxFailureHandler) {

        PretreatmentAuthenticationProcessingFilter
            pretreatmentAuthenticationProcessingFilter = new PretreatmentAuthenticationProcessingFilter();
        pretreatmentAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaTime(
            securityProperties.getLogin().getCaptcha().getCheckTime());
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaType(
            securityProperties.getLogin().getCaptcha().getCheckType());
        pretreatmentAuthenticationProcessingFilter.setRetryCheckType(securityProperties.getLogin().getRetry().getCheckType());

        pretreatmentAuthenticationProcessingFilter.setRetryMaxTryTimes(
            securityProperties.getLogin().getRetry().getMaxTryTimes());
        pretreatmentAuthenticationProcessingFilter.setRetryLockMinites(
            securityProperties.getLogin().getRetry().getLockMinites());
        pretreatmentAuthenticationProcessingFilter.setRetryCheckTimeTnterval(
            securityProperties.getLogin().getRetry().getCheckTimeInterval());

        pretreatmentAuthenticationProcessingFilter.setContinueChainBeforeSuccessfulAuthentication(
            securityProperties.getHttp().isFilterContinueAuthentication());
        pretreatmentAuthenticationProcessingFilter.setAuthenticationFailureHandler(ajaxFailureHandler);
        pretreatmentAuthenticationProcessingFilter.setAuthenticationSuccessHandler(ajaxSuccessHandler);
        return pretreatmentAuthenticationProcessingFilter;
    }

    private LogoutFilter logoutFilter() {
        return new LogoutFilter(securityProperties.getLogout().getTargetUrl(),
            new CsrfLogoutHandler(csrfTokenRepository),
            new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
            new SecurityContextLogoutHandler());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(securityProperties.getHttp().isCsrfEnable()) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
            .antMatchers("/system/mainframe/login", "/system/exception", "/oauth/check_token").permitAll()
            .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
            .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
            .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint());

        http.headers().frameOptions().sameOrigin();

        //AuthenticationProvider authenticationProvider = createAuthenticationProvider();
        //AuthenticationManager authenticationManager = createAuthenticationManager(authenticationProvider);

        AuthenticationFailureHandler ajaxFailureHandler = createFailureHandler();
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = createSuccessHandler(centitUserDetailsService);

        UsernamePasswordAuthenticationFilter pretreatmentAuthenticationProcessingFilter =
            createPretreatmentAuthenticationProcessingFilter(
                authenticationManager, ajaxSuccessHandler, ajaxFailureHandler);

        http.addFilterAt(pretreatmentAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(logoutFilter(), LogoutFilter.class);

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(
            createCentitAccessDecisionManager(),
            createCentitSecurityMetadataSource());

        http.addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class);
    }

}
