package com.centit.framework.hibernate.config;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Created by zou_wy on 2017/6/30.
 */
public class HibernateWebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        javax.servlet.FilterRegistration.Dynamic openSessionInViewFilter
                = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
        openSessionInViewFilter.setAsyncSupported(true);
        openSessionInViewFilter.setInitParameter("flushMode", "AUTO");
        openSessionInViewFilter.setInitParameter("singleSession", "true");
        openSessionInViewFilter.addMappingForUrlPatterns(null, false, "*.jsp", "/system/*", "/service/*");
    }
}
