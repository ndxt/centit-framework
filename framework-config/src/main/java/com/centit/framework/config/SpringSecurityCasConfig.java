package com.centit.framework.config;

import com.centit.framework.security.AjaxAuthenticationFailureHandler;
import com.centit.framework.security.AjaxAuthenticationSuccessHandler;
import com.centit.framework.security.DaoFilterSecurityInterceptor;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpMethod;
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
public class SpringSecurityCasConfig extends SpringSecurityBaseConfig {

    @Override
    public void configure(WebSecurity web) throws Exception {
        String ignoreUrl = StringUtils.deleteWhitespace(env.getProperty("security.ignore.url"));
        if(StringUtils.isNotBlank(ignoreUrl)){
            String[] ignoreUrls = ignoreUrl.split(",");
            for(int i = 0; i < ignoreUrls.length; i++){
                web.ignoring().antMatchers(HttpMethod.GET, ignoreUrls[i]);
            }
        }

        web.httpFirewall(httpFirewall());
        // 设置不拦截规则
//        web.ignoring().antMatchers(HttpMethod.GET, "/**/login","/**/exception/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.anonymous.disable"),false)) {
            http.anonymous().disable();
        }

        http.authorizeRequests().antMatchers("/system/exception").permitAll();
        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("access.resource.notallowed.anonymous"),false)) {
            http.authorizeRequests().antMatchers("/**").authenticated();
        } else {
            http.authorizeRequests().antMatchers("/system/mainframe/logincas").authenticated();
        }

        ServiceProperties casServiceProperties = createCasServiceProperties();
        CasAuthenticationEntryPoint casEntryPoint = createCasEntryPoint(casServiceProperties);

        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.csrf.enable"),false)) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        http.logout()
                .logoutSuccessUrl(StringBaseOpt.emptyValue(defaultTargetUrl,"/"))
                .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
//                .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
                .and().httpBasic().authenticationEntryPoint(casEntryPoint);

        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = createAjaxSuccessHandler(centitUserDetailsService);
        AjaxAuthenticationFailureHandler ajaxFailureHandler = createAjaxFailureHandler();
        CasAuthenticationProvider casAuthenticationProvider = createCasAuthenticationProvider(casServiceProperties);
        AuthenticationManager authenticationManager = creatAuthenticationManager(casAuthenticationProvider);
        CasAuthenticationFilter casFilter = createCasFilter(authenticationManager,
                ajaxSuccessHandler, ajaxFailureHandler);

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(authenticationManager,
                createCentitAccessDecisionManager(),createCentitSecurityMetadataSource());

        http.addFilterAt(casFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class)
//                .addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleLogoutFilter(), CasAuthenticationFilter.class);
    }

    private CasAuthenticationEntryPoint createCasEntryPoint(ServiceProperties casServiceProperties) {
        CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
        casEntryPoint.setLoginUrl(env.getProperty("login.cas.casHome")+"/login");
        casEntryPoint.setServiceProperties(casServiceProperties);
        return casEntryPoint;
    }

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(env.getProperty("login.cas.localHome")+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    private CasAuthenticationProvider createCasAuthenticationProvider(ServiceProperties casServiceProperties) {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setUserDetailsService(centitUserDetailsService);
        casAuthenticationProvider.setServiceProperties(casServiceProperties);
        casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(env.getProperty("login.cas.casHome")));
        casAuthenticationProvider.setKey(env.getProperty("app.key"));
        return casAuthenticationProvider;
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

    @Bean
    public SingleSignOutFilter singleLogoutFilter() {
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        singleLogoutFilter.setCasServerUrlPrefix(env.getProperty("login.cas.casHome"));
        return singleLogoutFilter;
    }

    private LogoutFilter requestSingleLogoutFilter() {
        return new LogoutFilter(env.getProperty("login.cas.casHome")+"/logout", new SecurityContextLogoutHandler());
    }

    private AuthenticationManager creatAuthenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(casAuthenticationProvider);
        return new ProviderManager(providerList);
    }

}
