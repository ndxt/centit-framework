package com.centit.framework.security;

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

public class CentitAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private AuthenticationEntryPoint browse = new LoginUrlAuthenticationEntryPoint("/system/mainframe/login");
    private AuthenticationEntryPoint api = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);

    public CentitAuthenticationEntryPoint() {
    }

    public CentitAuthenticationEntryPoint(String loginFormUrl) {
        this.browse = new LoginUrlAuthenticationEntryPoint(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(WebOptUtils.isAjax(request)){
            api.commence(request, response, authException);
        }else {
            browse.commence(request, response, authException);
        }
    }
}
