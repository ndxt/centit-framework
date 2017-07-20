package com.centit.framework.staticsystem.config;

import com.centit.framework.config.SecurityDaoCondition;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import com.centit.framework.security.model.CentitSessionRegistry;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
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
    private HttpSessionCsrfTokenRepository csrfTokenRepository;

    @Autowired
    private CentitPasswordEncoderImpl passwordEncoder;

//    @Autowired
//    private CentitUserDetailsService centitUserDetailsService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Autowired
    private CentitSessionRegistry centitSessionRegistry;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/system/mainframe/login","/system/exception").anonymous()
                .and()
                .exceptionHandling().accessDeniedPage("/system/exception/accessDenied")
                .and()
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint());
        http.headers().frameOptions().sameOrigin();

        CentitUserDetailsService centitUserDetailsService = centitUserDetailsService();

        AuthenticationProvider authenticationProvider = createAuthenticationProvider(centitUserDetailsService);

        AuthenticationManager authenticationManager = createAuthenticationManager(authenticationProvider);

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(authenticationManager,
                new DaoInvocationSecurityMetadataSource(),
                new DaoAccessDecisionManager(),
                centitSessionRegistry );

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

    private AuthenticationProvider createAuthenticationProvider(CentitUserDetailsService centitUserDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    private AuthenticationManager createAuthenticationManager(AuthenticationProvider authenticationProvider) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService() {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

}
