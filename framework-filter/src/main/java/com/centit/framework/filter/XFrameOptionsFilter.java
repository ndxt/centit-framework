package com.centit.framework.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XFrameOptionsFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            //必须
            //HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpRponse = (HttpServletResponse) response;
            //实际设置
            httpRponse.setHeader("x-frame-options", "SAMEORIGIN");
        }
        //调用下一个过滤器（这是过滤器工作原理，不用动）
        chain.doFilter(request, response);

    }


    @Override
    public void destroy() {

    }
}
