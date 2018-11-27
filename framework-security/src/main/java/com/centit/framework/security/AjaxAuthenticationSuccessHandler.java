package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private boolean writeLog = false;

    public void setWriteLog(boolean writeLog) {
        this.writeLog = writeLog;
    }

    private boolean registToken = false;

    public void setRegistToken(boolean registToken) {
        this.registToken = registToken;
    }


    private SessionRegistry sessionRegistry;
    private CentitUserDetailsService userDetailsService;

    public void setSessionRegistry(SessionRegistry sessionManger) {
        this.sessionRegistry = sessionManger;
    }

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
                    userDetailsService.saveUserSetting(ud.getUserInfo().getUserCode(),
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
        // TODO 登录成功 spring session 应该可以自动写入，这个代码应该去掉
        ud.setLoginIp(WebOptUtils.getRequestAddr(request));
        sessionRegistry.registerNewSession(request.getSession().getId() ,ud);

        if(writeLog){
            OperationLogCenter.log(ud.getUserInfo().getUserCode(),"login", "login",
                    "用户 ："+ud.getUserInfo().getUserName()+"于"+DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate())
                    + "从主机"+request.getRemoteHost()+":"+WebOptUtils.getRequestAddr(request)+"登录。");
        }

        String ajax = request.getParameter("ajax");
        boolean isAjaxQuery = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if(isAjaxQuery || "true".equalsIgnoreCase(ajax)){
            ResponseMapData resData = new ResponseMapData();
            if(registToken) {
                resData.addResponseData(SecurityContextUtils.SecurityContextTokenName,
                    request.getSession().getId());
            }
            resData.addResponseData("userInfo", ud);
            JsonResultUtils.writeResponseDataAsJson(resData, response);
            //request.getSession().setAttribute("SPRING_SECURITY_AUTHENTICATION", authentication);
            //JsonResultUtils.writeSingleErrorDataJson(0,authentication.getName() + " login ok！",request.getSession().getId(), response);
        }else{
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
