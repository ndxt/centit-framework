package com.centit.framework.core.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 重写404
 *
 * @author sx
 * 2014-10-22
 */
public class SimpleDispatcherServlet extends DispatcherServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(SimpleDispatcherServlet.class);

    private static UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestUri = urlPathHelper.getRequestUri(request);
        if (logger.isWarnEnabled()) {
            logger.warn("No mapping found for HTTP request with URI [" + requestUri
                    + "] in DispatcherServlet with name '" + getServletName() + "'");
        }

        String header = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(header)) {
            JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_NOT_FOUND, "未找到此资源（"+requestUri+"）", response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/system/exception/error/404");
    }

}
