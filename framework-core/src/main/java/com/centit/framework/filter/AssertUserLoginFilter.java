package com.centit.framework.filter;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.basedata.UserInfo;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                HttpServletRequest httpRequest = (HttpServletRequest)request;
                String requestUrl = httpRequest.getRequestURI();
                if(!requestUrl.contains("no-auth")) {
                    UserInfo userInfo = WebOptUtils.getCurrentUserInfo(httpRequest);
                    if (userInfo == null) {
                        JsonResultUtils.writeHttpErrorMessage(ResponseData.ERROR_UNAUTHORIZED,
                            ResponseData.ERROR_NOT_LOGIN_MSG, (HttpServletResponse) response);
                        return;
                    }
                }
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
