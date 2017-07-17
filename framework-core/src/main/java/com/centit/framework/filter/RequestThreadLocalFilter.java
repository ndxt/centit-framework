package com.centit.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 将HttpServletRequest请求与本地线程绑定，方便在非Controller层获取HttpServletRequest实例
 *
 * @author sx
 * @create 2014-10-14
 */
public class RequestThreadLocalFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        RequestThreadLocal.setHttpThreadWrapper(
                new HttpThreadWrapper((HttpServletRequest) request, (HttpServletResponse) response));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
