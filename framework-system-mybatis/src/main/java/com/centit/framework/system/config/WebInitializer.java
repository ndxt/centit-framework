package com.centit.framework.system.config;

import com.centit.framework.config.SystemSpringMvcConfig;
import com.centit.framework.filter.RequestThreadLocalFilter;
import com.centit.framework.filter.ResponseCorsFilter;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.file.PropertiesReader;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import java.util.EnumSet;

/**
 * Created by zou_wy on 2017/3/29.
 */


public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        if(StringRegularOpt.isTrue(
                PropertiesReader.getClassPathProperties(
                        "/system.properties", "sys.runas.systemmybatis"))) {
            initializeSpringConfig(servletContext);
        }

        initializeSpringMvcConfig(servletContext);

        registerRequestContextListener(servletContext);

        registerSingleSignOutHttpSessionListener(servletContext);

        registerResponseCorsFilter(servletContext);

        registerCharacterEncodingFilter(servletContext);

        registerHttpPutFormContentFilter(servletContext);

        registerHiddenHttpMethodFilter(servletContext);

        registerRequestThreadLocalFilter(servletContext);

        registerSpringSecurityFilter(servletContext);
    }

    private void initializeSpringConfig(ServletContext servletContext){
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(SpringConfig.class);
        servletContext.addListener(new ContextLoaderListener(springContext));
    }

    private void initializeSpringMvcConfig(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SystemSpringMvcConfig.class);
        Dynamic system  = servletContext.addServlet("system", new DispatcherServlet(context));
        system.addMapping("/system/*");
        system.setLoadOnStartup(1);
        system.setAsyncSupported(true);
    }

    private void registerRequestContextListener(ServletContext servletContext) {
        servletContext.addListener(RequestContextListener.class);
    }

    private void registerSingleSignOutHttpSessionListener(ServletContext servletContext) {
        if( StringRegularOpt.isTrue(
                PropertiesReader.getClassPathProperties(
                        "/system.properties", "cas.sso"))) {
            servletContext.addListener(SingleSignOutHttpSessionListener.class);
        }
    }

    private void registerResponseCorsFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic corsFilter
                = servletContext.addFilter("corsFilter", ResponseCorsFilter.class);
        corsFilter.addMappingForUrlPatterns(null, false, "/service/*");
    }

    private void registerCharacterEncodingFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic encodingFilter
                = servletContext.addFilter("encodingFilter", CharacterEncodingFilter.class);
        encodingFilter.addMappingForUrlPatterns(null, false, "*.jsp", "*.html", "/service/*", "/system/*");
        encodingFilter.setAsyncSupported(true);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
    }

    private void registerHttpPutFormContentFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic httpPutFormContentFilter
                = servletContext.addFilter("httpPutFormContentFilter", HttpPutFormContentFilter.class);
        httpPutFormContentFilter.addMappingForUrlPatterns(null, false, "/service/*", "/system/*");
        httpPutFormContentFilter.setAsyncSupported(true);
    }

    private void registerHiddenHttpMethodFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic hiddenHttpMethodFilter
                = servletContext.addFilter("hiddenHttpMethodFilter", HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(
                EnumSet.allOf(DispatcherType.class), false, "/service/*", "/system/*");
        hiddenHttpMethodFilter.setAsyncSupported(true);
    }

    private void registerRequestThreadLocalFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic requestThreadLocalFilter
                = servletContext.addFilter("requestThreadLocalFilter", RequestThreadLocalFilter.class);
        requestThreadLocalFilter.addMappingForUrlPatterns(null, false, "/*");
        requestThreadLocalFilter.setAsyncSupported(true);
    }

    private void registerSpringSecurityFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic springSecurityFilterChain
                = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(
                null, false, "/login/*" ,"/logout/*", "/service/*", "/system/*");
        springSecurityFilterChain.setAsyncSupported(true);
    }
}
