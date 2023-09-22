package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.algorithm.DatetimeOpt;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private boolean writeLog = false;

    public void setWriteLog(boolean writeLog) {
        this.writeLog = writeLog;
    }

    private PlatformEnvironment platformEnvironment;

    public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        this.platformEnvironment = platformEnvironment;
    }

    public AjaxAuthenticationSuccessHandler() {
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CentitUserDetails ud = (CentitUserDetails) authentication.getPrincipal();
        SecurityContextUtils.fetchAndSetLocalParams(ud, request, platformEnvironment);

        if(writeLog){
            String remoteHost = request.getRemoteHost();
            String loginIp = ud.getLoginIp();
            if(!loginIp.startsWith(remoteHost)){
                loginIp = remoteHost + ":" + loginIp;
            }
            OperationLogCenter.log(
                OperationLog.create().user(ud.getUserCode()).operation("mainframe")
                        .unit(ud.getCurrentUnitCode()).method("login")
                        .content("用户 ："+ud.getUserInfo().getUserName() + " 于"
                            +DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate()) + "从主机"+loginIp+"登录。")
                    .loginIp(loginIp).topUnit(ud.getTopUnitCode()));
        }
        Cookie cookie = new Cookie(WebOptUtils.SESSION_ID_TOKEN,
            request.getSession().getId());
        cookie.setPath("/");
        response.addCookie(cookie);
        boolean isAjaxQuery = WebOptUtils.isAjax(request);
        if(isAjaxQuery){
            ResponseData resData = SecurityContextUtils.makeLoginSuccessResponse(ud, request);
            JsonResultUtils.writeResponseDataAsJson(resData, response);
            //request.getSession().setAttribute("SPRING_SECURITY_AUTHENTICATION", authentication);
            //JsonResultUtils.writeSingleErrorDataJson(0,authentication.getName() + " login ok！",request.getSession().getId(), response);
        }else{
            response.setHeader(WebOptUtils.SESSION_ID_TOKEN,
                request.getSession().getId());
            response.setHeader(SecurityContextUtils.SecurityContextTokenName,
                request.getSession().getId());
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
