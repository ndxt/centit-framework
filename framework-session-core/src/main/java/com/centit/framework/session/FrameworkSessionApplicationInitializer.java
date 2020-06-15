package com.centit.framework.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import javax.servlet.ServletContext;

public class FrameworkSessionApplicationInitializer extends AbstractHttpSessionApplicationInitializer {
    Logger logger = LoggerFactory.getLogger(FrameworkSessionApplicationInitializer.class);

    @Override
    protected void beforeSessionRepositoryFilter(ServletContext servletContext) {
        logger.debug("初始化spring session 过滤器");
    }

    @Override
    protected void afterSessionRepositoryFilter(ServletContext servletContext) {
        logger.debug("spring session 过滤器启动完成");
    }
}
