package com.centit.framework.web.demo.config;

import com.centit.framework.config.SystemSpringMvcConfig;
import com.centit.framework.config.WebConfig;
import com.centit.framework.filter.RequestThreadLocalFilter;
import com.centit.framework.filter.ResponseCorsFilter;

import com.centit.framework.staticsystem.config.StaticSystemBeanConfig;
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

        initializeSpringConfig(servletContext);

        initializeSpringMvcConfig(servletContext);

        WebConfig.registerSpringSessionRepositoryFilter(servletContext);

        WebConfig.registerRequestContextListener(servletContext);

        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);

        WebConfig.registerResponseCorsFilter(servletContext);

        WebConfig.registerCharacterEncodingFilter(servletContext);

        WebConfig.registerHttpPutFormContentFilter(servletContext);

        WebConfig.registerHiddenHttpMethodFilter(servletContext);

        WebConfig.registerRequestThreadLocalFilter(servletContext);

        WebConfig.registerSpringSecurityFilter(servletContext);
    }

    /**
     * 加载Spring 配置
     * @param servletContext ServletContext
     */
    private void initializeSpringConfig(ServletContext servletContext){
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(StaticSystemBeanConfig.class, ServiceConfig.class);
        servletContext.addListener(new ContextLoaderListener(springContext));
    }

    /**
     * 加载Servlet 配置
     * @param servletContext ServletContext
     */
    private void initializeSpringMvcConfig(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SystemSpringMvcConfig.class);
        Dynamic system  = servletContext.addServlet("system", new DispatcherServlet(context));
        system.addMapping("/system/*");
        system.setLoadOnStartup(1);
        system.setAsyncSupported(true);
    }

}
