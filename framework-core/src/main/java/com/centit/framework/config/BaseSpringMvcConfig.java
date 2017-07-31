package com.centit.framework.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.filter.RequestThreadLocalFilter;
import com.centit.framework.filter.ResponseCorsFilter;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.file.PropertiesReader;
import org.h2.server.web.WebServlet;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

/**
 * Created by zou_wy on 2017/3/29.
 */


@SuppressWarnings("unused")
public class BaseSpringMvcConfig extends WebMvcConfigurerAdapter {


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");

        registry.viewResolver(resolver );
    }



    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new FastJsonHttpMessageConverter());
    }


}
