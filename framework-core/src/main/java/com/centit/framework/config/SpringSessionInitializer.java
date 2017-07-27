package com.centit.framework.config;

import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.file.PropertiesReader;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.Properties;

/**
 * Created by zou_wy on 2017/6/16.
 */
public class SpringSessionInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if(isEnablePersistSession()){
            javax.servlet.FilterRegistration.Dynamic springSessionRepositoryFilter
                    = servletContext.addFilter("springSessionRepositoryFilter", DelegatingFilterProxy.class);
            springSessionRepositoryFilter.addMappingForUrlPatterns(
                    EnumSet.allOf(DispatcherType.class), false, "/*");
        }
    }

    protected boolean isEnablePersistSession(){
        Properties properties = PropertiesReader.getClassPathProperties("/system.properties");
        return StringRegularOpt.isTrue(
                properties.getProperty("session.persistence.enable"));
    }
}
