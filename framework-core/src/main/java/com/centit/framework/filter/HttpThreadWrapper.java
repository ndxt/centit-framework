package com.centit.framework.filter;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在ThreadLocal中封装请求响应
 *
 * @author sx
 * @create 2014-10-20
 */
public class HttpThreadWrapper implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3434170518095254917L;

    private HttpServletRequest request;

    private HttpServletResponse response;

    public HttpThreadWrapper(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
