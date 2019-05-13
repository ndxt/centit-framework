package com.centit.framework.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.framework.staticsystem.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@EnableConfigurationProperties(FrameworkProperties.class)
@Configuration("environmentConfiguation")
public class FrameworkBeanConfiguation {

    @Autowired
    private FrameworkProperties frameworkProperties;

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter =
                new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setFeatures(Feature.AllowArbitraryCommas,Feature.AllowUnQuotedFieldNames,
                Feature.DisableCircularReferenceDetect);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }

    @Bean({"passwordEncoder"})
    public CentitPasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    @Lazy(value = false)
    public PlatformEnvironment platformEnvironment(
            @Autowired CentitPasswordEncoder passwordEncoder) {


        JsonPlatformEnvironment jsonPlatformEnvironment = new JsonPlatformEnvironment();
        jsonPlatformEnvironment.setAppHome(frameworkProperties.getApp().getHome());
        jsonPlatformEnvironment.setPasswordEncoder(passwordEncoder);
        return jsonPlatformEnvironment;
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService(@Autowired PlatformEnvironment platformEnvironment) {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setPlatformEnvironment(platformEnvironment);
        return userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        @Autowired CentitUserDetailsService centitUserDetailsService,
        @Autowired StandardPasswordEncoderImpl passwordEncoder) {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

}
