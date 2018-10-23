package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.components.OperationLogCenter;
import com.centit.support.algorithm.DatetimeOpt;
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
 
    public AjaxAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if(writeLog){
            String loginName = request.getParameter("username");
            String loginHost = request.getRemoteHost()+":"+request.getRemotePort();
            OperationLogCenter.log(loginName,"login", "loginError",
                    "用户 ："+loginName+"于"+DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate())
                    + "从主机"+loginHost+"尝试登录,失败原因:"+exception.getMessage()+"。");
        }
        int tryTimes = CheckFailLogs.getHasTriedTimes(request);
//        String ajax = request.getParameter("ajax");
//        if(ajax==null || "".equals(ajax) || "null".equals(ajax)  || "false".equals(ajax)) {
        if(!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            request.setAttribute("hasTriedTimes",tryTimes);
            super.onAuthenticationFailure(request, response, exception);
        }else {
            ResponseMapData resData = new ResponseMapData(ResponseData.ERROR_USER_LOGIN_ERROR,
                    exception.getMessage() + "!");
            resData.addResponseData("hasTriedTimes",tryTimes);
            JsonResultUtils.writeResponseDataAsJson(resData, response);
        }
    }
}
