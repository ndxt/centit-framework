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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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


@Configuration
@EnableWebMvc
@SuppressWarnings("unused")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

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
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        /*FormattingConversionService conversionService2 =
                conversionServiceBean().getObject();
        //conversionService2.addConverter(new ObjectByteConverter());
        webBindingInitializer.setConversionService(conversionService2);*/
        return webBindingInitializer;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new FastJsonHttpMessageConverter());
    }



    /**
     * 注册RequestContextListener监听器 （增加request、session和global session作用域）
     * @param servletContext ServletContext
     */
    public static void registerRequestContextListener(ServletContext servletContext) {
        servletContext.addListener(RequestContextListener.class);
    }

    /**
     * 注册SingleSignOutHttpSessionListener监听器 （单点登录统一登出）
     * @param servletContext ServletContext
     */
    public static void registerSingleSignOutHttpSessionListener(ServletContext servletContext) {
        Properties properties = PropertiesReader.getClassPathProperties("/system.properties");
        if( StringRegularOpt.isTrue(
                properties.getProperty("cas.sso"))) {
            servletContext.addListener(SingleSignOutHttpSessionListener.class);
        }
    }

    /**
     * 注册ResponseCorsFilter过滤器 （允许跨站脚本访问过滤器）
     * @param servletContext ServletContext
     */
    public static void registerResponseCorsFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic corsFilter
                = servletContext.addFilter("corsFilter", ResponseCorsFilter.class);
        corsFilter.addMappingForUrlPatterns(null, false, "/service/*");
    }

    /**
     * 注册CharacterEncodingFilter过滤器 （设置字符集）
     * @param servletContext ServletContext
     */
    public static void registerCharacterEncodingFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic encodingFilter
                = servletContext.addFilter("encodingFilter", CharacterEncodingFilter.class);
        encodingFilter.addMappingForUrlPatterns(null, false, "*.jsp", "*.html", "/service/*", "/system/*");
        encodingFilter.setAsyncSupported(true);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
    }

    /**
     * 注册HttpPutFormContentFilter过滤器 （让put也可以想post一样接收form表单中的数据）
     * @param servletContext ServletContext
     */
    public static void registerHttpPutFormContentFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic httpPutFormContentFilter
                = servletContext.addFilter("httpPutFormContentFilter", HttpPutFormContentFilter.class);
        httpPutFormContentFilter.addMappingForUrlPatterns(null, false, "/service/*", "/system/*");
        httpPutFormContentFilter.setAsyncSupported(true);
    }

    /**
     * 注册HiddenHttpMethodFilter过滤器 （过滤请求方式）
     * @param servletContext ServletContext
     */
    public static void registerHiddenHttpMethodFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic hiddenHttpMethodFilter
                = servletContext.addFilter("hiddenHttpMethodFilter", HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(
                EnumSet.allOf(DispatcherType.class), false, "/service/*", "/system/*");
        hiddenHttpMethodFilter.setAsyncSupported(true);
    }

    /**
     * 注册RequestThreadLocalFilter过滤器
     *                  (将HttpServletRequest请求与本地线程绑定，方便在非Controller层获取HttpServletRequest实例)
     * @param servletContext ServletContext
     */
    public static void registerRequestThreadLocalFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic requestThreadLocalFilter
                = servletContext.addFilter("requestThreadLocalFilter", RequestThreadLocalFilter.class);
        requestThreadLocalFilter.addMappingForUrlPatterns(null, false, "/*");
        requestThreadLocalFilter.setAsyncSupported(true);
    }

    /**
     * 注册springSecurityFilterChain过滤器 （使用spring security 授权与验证）
     * @param servletContext ServletContext
     */
    public static void registerSpringSecurityFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic springSecurityFilterChain
                = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(
                null, false, "/login/*" ,"/logout/*", "/service/*", "/system/*");
        springSecurityFilterChain.setAsyncSupported(true);
    }

    /**
     * 访问 h2 console
     * @param servletContext ServletContext
     */
    private void initializeH2Console(ServletContext servletContext){
        ServletRegistration.Dynamic h2console  = servletContext.addServlet("h2console", WebServlet.class);
        h2console.setInitParameter("webAllowOthers", "");
        h2console.addMapping("/h2console/*");
        h2console.setLoadOnStartup(1);
        h2console.setAsyncSupported(true);
    }
}
