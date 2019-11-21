package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.common.WebOptUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private AuthenticationEntryPoint browse;
    private AuthenticationEntryPoint api;

    public AjaxAuthenticationEntryPoint(String loginFormUrl) {
        this.browse = new LoginUrlAuthenticationEntryPoint(loginFormUrl);
        this.api = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(WebOptUtils.isAjax(request)){
            if(WebOptUtils.exceptionNotAsHttpError) {
                ResponseSingleData responseData =
                    new ResponseSingleData(ResponseData.ERROR_UNAUTHORIZED,
                        "未登录！");
                JsonResultUtils.writeResponseDataAsJson(responseData, response);
            } else {
                api.commence(request, response, authException);
            }
        }else {
            browse.commence(request, response, authException);
        }
    }
}
