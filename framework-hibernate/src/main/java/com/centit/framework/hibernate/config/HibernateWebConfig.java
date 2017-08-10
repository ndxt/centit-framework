package com.centit.framework.hibernate.config;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

import javax.servlet.ServletContext;

/**
 * Created by zou_wy on 2017/6/30.
 */
@SuppressWarnings("unused")
public class HibernateWebConfig  {
    /**
     * 注册OpenSessionInViewFilter 过滤器
     * @param servletContext ServletContext
     */
    public static void registerOpenSessionInViewFilter(ServletContext servletContext) {
        javax.servlet.FilterRegistration.Dynamic openSessionInViewFilter
                = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
        openSessionInViewFilter.setAsyncSupported(true);
        openSessionInViewFilter.setInitParameter("flushMode", "AUTO");
        openSessionInViewFilter.setInitParameter("singleSession", "true");
        openSessionInViewFilter.addMappingForUrlPatterns(null, false, "*.jsp", "/system/*", "/service/*");
    }
}
