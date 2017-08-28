package com.centit.framework.security;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseMapData;
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
            	userDetailsService.saveUserSetting(ud.getUserCode(),
            			WebOptUtils.LOCAL_LANGUAGE_LABLE, lang, "SYS", "用户默认区域语言");
            }
		}else{
            lang = ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE);
            if(StringUtils.isNotBlank(lang)){
            	WebOptUtils.setCurrentLang(request, lang);
	            //request.getSession().setAttribute("LOCAL_LANG", lang);
	            request.setAttribute(WebOptUtils.LOCAL_LANGUAGE_LABLE,lang);
            }
		}
		ud.setLoginIp(request.getRemoteHost()+":"+request.getRemotePort());
		ud.setActiveTime(DatetimeOpt.currentUtilDate());
		request.getSession().setAttribute(
				SecurityContextUtils.SecurityContextUserdetail,ud);
		//ud.setAuthenticated(true);
		String tokenKey =request.getSession().getId();
		
		if(registToken){
			//tokenKey = UuidOpt.getUuidAsString();
			sessionRegistry.registerNewSession(tokenKey,ud);
			request.getSession().setAttribute(SecurityContextUtils.SecurityContextTokenName, tokenKey);	
		}
		
		if(writeLog){
            OperationLogCenter.log(ud.getUserCode(),"login", "login",
                    "用户 ："+ud.getUserCode()+"于"+DatetimeOpt.convertDatetimeToString(DatetimeOpt.currentUtilDate())
                    + "从主机"+request.getRemoteHost()+":"+request.getRemotePort()+"登录。");
        }
		
    	String ajax = request.getParameter("ajax");
    	if(ajax==null || "".equals(ajax) || "null".equals(ajax)  || "false".equals(ajax)){
    		super.onAuthenticationSuccess(request,response,authentication);
    	}else{
			ResponseMapData resData = new ResponseMapData();
    		if(registToken)
    			resData.addResponseData(SecurityContextUtils.SecurityContextTokenName, tokenKey);
    		resData.addResponseData("userInfo", ud);
    		JsonResultUtils.writeResponseDataAsJson(resData, response);
    		//request.getSession().setAttribute("SPRING_SECURITY_AUTHENTICATION", authentication);
    		//JsonResultUtils.writeSingleErrorDataJson(0,authentication.getName() + " login ok！",request.getSession().getId(), response);
    	}
    }
}