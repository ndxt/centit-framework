package com.centit.framework.config;

import com.centit.framework.security.PretreatmentAuthenticationProcessingFilter;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfLogoutHandler;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityDaoCondition.class)
public class SpringSecurityDaoConfig extends SpringSecurityBaseConfig {

    @Autowired
    @Qualifier("passwordEncoder")
    protected Object passwordEncoder;

    @Override
    protected String[] getAuthenticatedUrl() {
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("access.resource.notallowed.anonymous"),false)) {
            return new String[]{"/**"};
        }
        return null;
    }

    @Override
    protected String[] getPermitAllUrl() {
        return new String[]{"/**/login", "/system/exception"};
    }

    @Override
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/system/mainframe/login");
    }

    @Override
    protected AbstractAuthenticationProcessingFilter getAuthenticationFilter() {
        PretreatmentAuthenticationProcessingFilter
            pretreatmentAuthenticationProcessingFilter = new PretreatmentAuthenticationProcessingFilter();
        pretreatmentAuthenticationProcessingFilter.setAuthenticationManager(createAuthenticationManager());
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaTime(
            NumberBaseOpt.castObjectToInteger(env.getProperty("login.captcha.checkTime"),0));
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaType(
            NumberBaseOpt.castObjectToInteger(env.getProperty("login.captcha.checkType"),0));
        pretreatmentAuthenticationProcessingFilter.setRetryCheckType(
            StringBaseOpt.emptyValue( env.getProperty("login.retry.checkType"),"H"));

        pretreatmentAuthenticationProcessingFilter.setRetryMaxTryTimes(
            NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.maxTryTimes"),0));

        pretreatmentAuthenticationProcessingFilter.setRetryLockMinites(
            NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.lockMinites"),10));

        pretreatmentAuthenticationProcessingFilter.setRetryCheckTimeTnterval(
            NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.checkTimeTnterval"),3));

        pretreatmentAuthenticationProcessingFilter.setContinueChainBeforeSuccessfulAuthentication(
            BooleanBaseOpt.castObjectToBoolean(
                env.getProperty("http.filter.chain.continueBeforeSuccessfulAuthentication"),false));
        pretreatmentAuthenticationProcessingFilter.setAuthenticationFailureHandler(createAjaxFailureHandler());
        pretreatmentAuthenticationProcessingFilter.setAuthenticationSuccessHandler(createAjaxSuccessHandler());
        return pretreatmentAuthenticationProcessingFilter;
    }

    @Override
    protected AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Override
    protected LogoutFilter logoutFilter() {
        return new LogoutFilter("/system/mainframe/login",
            new CsrfLogoutHandler(csrfTokenRepository),
            new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
            new SecurityContextLogoutHandler());
    }
}
