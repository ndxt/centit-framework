package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.model.security.CentitUserDetailsService;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
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

    /*private boolean registToken = false;

    public void setRegistToken(boolean registToken) {
        this.registToken = registToken;
    }*/

    private CentitUserDetailsService userDetailsService;

    public void setUserDetailsService(CentitUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public AjaxAuthenticationSuccessHandler() {
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CentitUserDetails ud = (CentitUserDetails) authentication.getPrincipal();
        String lang = WebOptUtils.getLocalLangParameter(request);
        if(StringUtils.isNotBlank(lang)){
            //request.getSession().setAttribute("LOCAL_LANG", lang);
            WebOptUtils.setCurrentLang(request, lang);
            String userLang = ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE);
            if(! lang.equals(userLang)){
                ud.setUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE, userLang);
                if(userDetailsService!=null){
                    userDetailsService.saveUserSetting(ud.getUserCode(),
                            WebOptUtils.LOCAL_LANGUAGE_LABLE, lang, "SYS", "用户默认区域语言");
                }
            }
        }else{
            lang = ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE);
            if(StringUtils.isNotBlank(lang)){
                WebOptUtils.setCurrentLang(request, lang);
                //request.getSession().setAttribute("LOCAL_LANG", lang);
                request.setAttribute(WebOptUtils.LOCAL_LANGUAGE_LABLE,lang);
            }
        }

        //tokenKey = UuidOpt.getUuidAsString();
        // 这个代码应该迁移到 AuthenticationProcessingFilter 的 successfulAuthentication 方法中
        ud.setLoginIp(WebOptUtils.getRequestAddr(request));

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
            ResponseMapData resData = new ResponseMapData();
            resData.addResponseData(SecurityContextUtils.SecurityContextTokenName,
                request.getSession().getId());
            resData.addResponseData("userInfo", ud.toJsonWithoutSensitive());
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
