package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.security.SecurityOptUtils;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private boolean writeLog = false;

    public void setWriteLog(boolean writeLog) {
        this.writeLog = writeLog;
    }

    public AjaxAuthenticationFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
         CredentialsExpiredException cex = null;
        if(writeLog){
            String loginName = SecurityOptUtils.decodeSecurityString(request.getParameter("username"));
            String loginHost = WebOptUtils.getRequestAddr(request);//request.getRemoteHost()+":"+request.getRemotePort();
            OperationLogCenter.log(
                OperationLog.create().user(loginName).method("loginError").application("mainframe")
                    .operation("login").content(
                        "用户 ："+loginName+"于"+DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate())
                    + "从主机"+loginHost+"尝试登录,失败原因:"+exception.getMessage()).loginIp(loginHost));
        }
        int tryTimes = CheckFailLogs.getHasTriedTimes(request);
        boolean isAjaxQuery = WebOptUtils.isAjax(request);
        if(isAjaxQuery){
            ResponseMapData resData = new ResponseMapData(
                exception instanceof CredentialsExpiredException ? ResponseData.ERROR_USER_PASSWORD_EXPIRED
                    : ResponseData.ERROR_USER_LOGIN_ERROR ,
                exception.getMessage());
            resData.addResponseData("hasTriedTimes", tryTimes);
            JsonResultUtils.writeResponseDataAsJson(resData, response);
        }else {
            request.setAttribute("hasTriedTimes",tryTimes);
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
