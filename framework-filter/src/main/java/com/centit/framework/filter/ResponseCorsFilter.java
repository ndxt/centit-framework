package com.centit.framework.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 允许跨站脚本访问过滤器
 * spring mvc 4.2 以上版本可以通过添加  @CrossOrigin 来更优雅的实现跨站访问
 * for-example ： @CrossOrigin(origins = "*",allowCredentials="true",maxAge=86400)
 * @author codefan
 *
 */
public class ResponseCorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse alteredResponse = (HttpServletResponse) servletResponse;
            if((request.getMethod().equals("OPTION") || request.getMethod().equals("OPTIONS")) &&
                request.getServletPath().equals("/login")) {
                    //System.out.println("request path match");
                    alteredResponse.setStatus(HttpServletResponse.SC_OK);
                    addHeadersFor200Response(alteredResponse);
                    return;
            }
            addHeadersFor200Response(alteredResponse);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void addHeadersFor200Response(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");//允许cookies
        response.addHeader("Access-Control-Allow-Methods", "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
        response.addHeader("Access-Control-Allow-Headers", "useXDomain, withCredentials, Overwrite, Destination, Content-Type, Depth, User-Agent, Translate, Range, Content-Range, Timeout, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control, Location, Lock-Token, If");
        response.addHeader("Access-Control-Expose-Headers", "DAV, content-length, Allow");
        response.addHeader("Access-Control-Max-Age", "86400");
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}
