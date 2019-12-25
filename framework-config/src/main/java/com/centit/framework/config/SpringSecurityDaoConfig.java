package com.centit.framework.config;

import com.centit.framework.security.AjaxAuthenticationEntryPoint;
import com.centit.framework.security.PretreatmentAuthenticationProcessingFilter;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityDaoCondition.class)
public class SpringSecurityDaoConfig extends SpringSecurityBaseConfig {

    @Autowired
    @Qualifier("passwordEncoder")
    protected PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private MessageSource messageSource;

    @Override
    protected String[] getAuthenticatedUrl() {
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("access.resource.notallowed.anonymous"),false)) {
            return new String[]{"/**"};
        }
        return null;
    }

    @Override
    protected String[] getPermitAllUrl() {
        return new String[]{"/**/login", "/**/logout","/**/csrf","/**/doc.html","/system/exception"};
    }

    @Override
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return new AjaxAuthenticationEntryPoint("/system/mainframe/login");
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
        String requiresAuthenticationUrl = env.getProperty("login.authentication.url");
        if(StringUtils.isNotBlank(requiresAuthenticationUrl)) {
            pretreatmentAuthenticationProcessingFilter.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher(requiresAuthenticationUrl, "POST"));
        }
        if(sessionRegistry != null) {
            ConcurrentSessionControlAuthenticationStrategy strategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
            strategy.setMaximumSessions(NumberBaseOpt.parseInteger(env.getProperty("session.concurrent.maximum"), -1));
            pretreatmentAuthenticationProcessingFilter.setSessionAuthenticationStrategy(strategy);
        }
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(BooleanBaseOpt.castObjectToBoolean(
            env.getProperty("session.always.rememberme"), false));
        pretreatmentAuthenticationProcessingFilter.setRememberMeServices(rememberMeServices);

        return pretreatmentAuthenticationProcessingFilter;
    }

    @Override
    protected AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        /*if( passwordEncoder instanceof org.springframework.security.authentication.encoding.PasswordEncoder) {
            ReflectionSaltSource saltSource = new ReflectionSaltSource();
            //UserInfo.salt 盐值数据字段
            String propertyToUse = env.getProperty("login.dao.passwordEncoder.salt");
            if(StringUtils.isBlank(propertyToUse)){
                propertyToUse = "userCode";
            }
            saltSource.setUserPropertyToUse(propertyToUse);
            authenticationProvider.setSaltSource(saltSource);
        }*/
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        if(messageSource != null && !(messageSource instanceof DelegatingMessageSource)) {
            authenticationProvider.setMessageSource(messageSource);
        }
        return authenticationProvider;
    }

    @Override
    protected LogoutFilter logoutFilter() {
        String defaultTargetUrl = env.getProperty("logout.success.targetUrl");
        defaultTargetUrl = StringBaseOpt.emptyValue(defaultTargetUrl,"/system/mainframe/login");

        return new LogoutFilter(defaultTargetUrl,
            new CsrfLogoutHandler(csrfTokenRepository),
            new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
            new SecurityContextLogoutHandler());
    }

}
