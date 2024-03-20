package com.centit.framework.filter;

import com.centit.framework.common.WebOptUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AssertUserLoginFilter  implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {
        if(request!=null && response!=null) {
            if(request instanceof HttpServletRequest) {
                WebOptUtils.assertUserLogin((HttpServletRequest) request);
            }
            if(chain!=null) {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }

}
