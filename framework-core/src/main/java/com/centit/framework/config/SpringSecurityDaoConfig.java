package com.centit.framework.config;

import com.centit.framework.security.AjaxAuthenticationSuccessHandler;
import com.centit.framework.security.DaoFilterSecurityInterceptor;
import com.centit.framework.security.PretreatmentAuthenticationProcessingFilter;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityDaoCondition.class)
public class SpringSecurityDaoConfig extends SpringSecurityBaseConfig {

    @Autowired
    protected CentitPasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.csrf.enable"),false)) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
                .antMatchers("/system/mainframe/login","/system/exception").permitAll()
                .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
                .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
                .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint());

        http.headers().frameOptions().sameOrigin();

        AuthenticationProvider authenticationProvider = createAuthenticationProvider();
        AuthenticationManager authenticationManager = createAuthenticationManager(authenticationProvider);
        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(authenticationManager,
                createCentitAccessDecisionManager(),createCentitSecurityMetadataSource());

        AuthenticationFailureHandler ajaxFailureHandler = createAjaxFailureHandler();
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = createAjaxSuccessHandler(centitUserDetailsService);

        UsernamePasswordAuthenticationFilter pretreatmentAuthenticationProcessingFilter =
                createPretreatmentAuthenticationProcessingFilter(
                        authenticationManager, ajaxSuccessHandler, ajaxFailureHandler);

        http.addFilterAt(pretreatmentAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class)
                .addFilterAt(logoutFilter(), LogoutFilter.class);
    }

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
        pretreatmentAuthenticationProcessingFilter.setAuthenticationFailureHandler(ajaxFailureHandler);
        pretreatmentAuthenticationProcessingFilter.setAuthenticationSuccessHandler(ajaxSuccessHandler);
        return pretreatmentAuthenticationProcessingFilter;
    }

    private LogoutFilter logoutFilter() {
        return new LogoutFilter("/system/mainframe/login",
                new CsrfLogoutHandler(csrfTokenRepository),
                new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
                new SecurityContextLogoutHandler());
    }

    public AuthenticationProvider createAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    public  AuthenticationManager createAuthenticationManager(AuthenticationProvider authenticationProvider) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

}
