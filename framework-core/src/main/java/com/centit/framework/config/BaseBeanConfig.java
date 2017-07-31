package com.centit.framework.config;

import com.centit.framework.filter.RequestThreadLocalFilter;
import com.centit.framework.filter.ResponseCorsFilter;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.file.PropertiesReader;
import org.h2.server.web.WebServlet;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;
import java.util.Properties;

/**
 * Created by zou_wy on 2017/3/29.
 */


@SuppressWarnings("unused")
@Configuration
public class BaseBeanConfig {


/*
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
*/

    @Bean
    public static PropertyPlaceholderConfigurer propertyConfigurer() {
        PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
        propertyConfigurer.setLocation(new ClassPathResource("system.properties"));
//        propertyConfigurer.setIgnoreUnresolvablePlaceholders(true);
        return propertyConfigurer;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messagesource/base/messages");
        Properties properties = new Properties();
        properties.put("fileEncodings", "utf-8");
        messageSource.setFileEncodings(properties);
        messageSource.setCacheSeconds(120);
        return messageSource;
    }

    @Bean
    public FormattingConversionServiceFactoryBean conversionServiceBean() {
        return new FormattingConversionServiceFactoryBean();
    }


    @Bean
    public ConfigurableWebBindingInitializer webBindingInitializer() {
        ConfigurableWebBindingInitializer webBindingInitializer =
                new ConfigurableWebBindingInitializer();
        /*FormattingConversionService conversionService2 =
                conversionServiceBean().getObject();
        //conversionService2.addConverter(new ObjectByteConverter());
        webBindingInitializer.setConversionService(conversionService2);*/
        return webBindingInitializer;
    }


}
