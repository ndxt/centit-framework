package com.centit.framework.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将HttpServletRequest请求与本地线程绑定，方便在非Controller层获取HttpServletRequest实例
 *
 * @author sx
 * 2014-10-14
 */
public class RequestThreadLocalFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if(request!=null && response!=null) {
            RequestThreadLocal.setHttpThreadWrapper(
                new HttpThreadWrapper((HttpServletRequest) request, (HttpServletResponse) response));
            if(chain!=null) {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }

}
