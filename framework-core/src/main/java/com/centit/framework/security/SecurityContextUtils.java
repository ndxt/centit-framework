package com.centit.framework.security;

import javax.servlet.http.HttpSession;

import com.centit.framework.common.ObjectException;
import com.centit.framework.security.model.CentitSessionRegistry;
import org.springframework.security.core.context.SecurityContextHolder;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.UuidOpt;
import org.springframework.web.context.ContextLoaderListener;

public class SecurityContextUtils {
	
	public final static String SecurityContextUserdetail = "SECURITY_CONTEXT_USERDETAIL";
	public final static String SecurityContextTokenName = "accessToken";

	public static CentitSessionRegistry getCentitSessionRegistry() {
		return ContextLoaderListener.getCurrentWebApplicationContext().
				getBean("centitSessionRegistry",  CentitSessionRegistry.class);
	}
	
	public static String registerUserToken(CentitUserDetails ud){
		String tokenKey = UuidOpt.getUuidAsString();
		CentitSessionRegistry registry = getCentitSessionRegistry();
		if(registry==null)
			throw new ObjectException(ud,"获取bean：centitSessionRegistry出错，请检查配置文件。");
		registry.registerNewSession(tokenKey,ud);
		return tokenKey;
	}
	
	public static void setSecurityContext(CentitUserDetails ud){
		SecurityContextHolder.getContext().setAuthentication(ud);
	}
	
	public static void setSecurityContext(CentitUserDetails ud,HttpSession session){
		SecurityContextHolder.getContext().setAuthentication(ud);
		session.setAttribute(SecurityContextUserdetail, ud);
	}

}
