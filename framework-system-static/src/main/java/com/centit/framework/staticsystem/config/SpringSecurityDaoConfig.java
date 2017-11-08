package com.centit.framework.staticsystem.config;

import com.centit.framework.config.SecurityDaoCondition;
import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.BooleanBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zou_wy on 2017/3/29.
 */
@EnableWebSecurity
@Conditional(SecurityDaoCondition.class)
public class SpringSecurityDaoConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    private CentitPasswordEncoderImpl passwordEncoder;

    @Autowired
    private CentitUserDetailsService userDetailsService;


    @Autowired
    private CentitSessionRegistry centitSessionRegistry;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.csrf.enable"),false)) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
                .antMatchers("/system/mainframe/login","/system/exception").permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/system/exception/accessDenied")
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint());
        http.headers().frameOptions().sameOrigin();



        AuthenticationProvider authenticationProvider = authenticationProvider();

        AuthenticationManager authenticationManager = authenticationManager(authenticationProvider);

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(authenticationManager,
                new DaoInvocationSecurityMetadataSource(),
                new DaoAccessDecisionManager(),
                centitSessionRegistry );

        AuthenticationFailureHandler ajaxFailureHandler = createAjaxFailureHandler();

        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = createAjaxSuccessHandler(userDetailsService);

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



    private DaoFilterSecurityInterceptor createCentitPowerFilter(AuthenticationManager authenticationManager,
                                                                 FilterInvocationSecurityMetadataSource centitSecurityMetadataSource,
                                                                 AccessDecisionManager centitAccessDecisionManagerBean,
                                                                 CentitSessionRegistry centitSessionRegistry) {

        DaoFilterSecurityInterceptor centitPowerFilter = new DaoFilterSecurityInterceptor();
        centitPowerFilter.setAuthenticationManager(authenticationManager);
        centitPowerFilter.setAccessDecisionManager(centitAccessDecisionManagerBean);
        centitPowerFilter.setSecurityMetadataSource(centitSecurityMetadataSource);
        centitPowerFilter.setSessionRegistry(centitSessionRegistry);
        return centitPowerFilter;
    }


    private AuthenticationFailureHandler createAjaxFailureHandler() {
        AjaxAuthenticationFailureHandler ajaxFailureHandler = new AjaxAuthenticationFailureHandler();
        ajaxFailureHandler.setDefaultFailureUrl("/system/mainframe/login/error");
        ajaxFailureHandler.setWriteLog(false);
        return ajaxFailureHandler;
    }

    private AjaxAuthenticationSuccessHandler createAjaxSuccessHandler(CentitUserDetailsService centitUserDetailsService) {
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = new AjaxAuthenticationSuccessHandler();
        ajaxSuccessHandler.setWriteLog(true);
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    private UsernamePasswordAuthenticationFilter createPretreatmentAuthenticationProcessingFilter(
            AuthenticationManager authenticationManager,AjaxAuthenticationSuccessHandler ajaxSuccessHandler,
            AuthenticationFailureHandler ajaxFailureHandler) {

        PretreatmentAuthenticationProcessingFilter
                pretreatmentAuthenticationProcessingFilter = new PretreatmentAuthenticationProcessingFilter();
        pretreatmentAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        pretreatmentAuthenticationProcessingFilter.setCheckCaptcha(false);
        pretreatmentAuthenticationProcessingFilter.setMaxTryTimes(0);
        pretreatmentAuthenticationProcessingFilter.setCheckTimeTnterval(3);
        pretreatmentAuthenticationProcessingFilter.setCheckType("loginName");
        pretreatmentAuthenticationProcessingFilter.setLockMinites(3);
        pretreatmentAuthenticationProcessingFilter.setContinueChainBeforeSuccessfulAuthentication(false);
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

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public  AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }


}
