package com.centit.framework.common;

import com.centit.framework.security.model.CentitUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * 系统Web常用工具类可以和spring WebUtils配合使用
 *
 * @author codefan
 * date 2014年10月24日
 */
public class WebOptUtils {
    public static final String LOCAL_LANGUAGE_LABLE="LOCAL_LANG";
    //不使用http的状态码来标识错误状态
    public static boolean exceptionNotAsHttpError = false;

    public static void setExceptionNotAsHttpError(boolean exceptionNotAsHttpError) {
        WebOptUtils.exceptionNotAsHttpError = exceptionNotAsHttpError;
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
            request.getHeader("accept").contains("application/json");
    }

    public static CentitUserDetails getLoginUser() {
        SecurityContext sch = SecurityContextHolder.getContext();
        if (sch == null)
            return null;
        Authentication auth = sch.getAuthentication();
        if (auth == null)
            return null;

        CentitUserDetails ud = null;
        Object o = auth.getPrincipal();
        if (o instanceof CentitUserDetails) {
            ud = (CentitUserDetails) o;// auth.getPrincipal();
        }
        return ud;
    }

    private static CentitUserDetails innerGetLoginUser(HttpSession session) {
        Object attr = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if(attr!=null && attr instanceof CentitUserDetails){
            return (CentitUserDetails)attr;
        }

        SecurityContext scontext = (SecurityContext) session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if(scontext==null)
            return null;
        Authentication auth = scontext.getAuthentication();
        if (auth == null)
            return null;

        CentitUserDetails ud = null;
        Object o = auth.getPrincipal();
        if (o instanceof CentitUserDetails) {
            ud = (CentitUserDetails) o;// auth.getPrincipal();
        }
        return ud;
    }

    public static CentitUserDetails getLoginUser(HttpSession session) {
        CentitUserDetails ud = getLoginUser();
        if(ud==null){
             ud = innerGetLoginUser(session);
        }
        return ud;
    }

    public static String getRequestAddr(HttpServletRequest request) {
        String sHostIp = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(sHostIp)){
            sHostIp = request.getRemoteAddr();
        }
        return sHostIp;
    }

    public static CentitUserDetails getLoginUser(HttpServletRequest request) {
        CentitUserDetails ud = getLoginUser();
        if(request == null || ud != null) {
            return ud;
        }
        //根据token获取用户信息
        //在session中手动获得用户信息
        return innerGetLoginUser(request.getSession());

    }

    public static String getLocalLangParameter(HttpServletRequest request) {
        if(request==null)
            return null;
        String lang = request.getParameter(LOCAL_LANGUAGE_LABLE);
        if(StringUtils.isNotBlank(lang)){
            if("en".equals(lang))
                lang="en_US";
            else if("zh".equals(lang))
                lang="zh_CN";
            return lang;
        }
        return null;
    }

    /**
     * 获取请求端希望的语言，策略是
     *     1，首先看请求中有没有通过 LOCAL_LANGUAGE_LABLE="LOCAL_LANG" 参数指定语言
     *  2，看spring有没有在session中保存语言类别
     *  3，看请求的head中有没有浏览器指定的语言
     *  4，默认返回zh_CN
     *
     * @param request 请求request
     * @return 范围语言类型
     */
    public static String getCurrentLang(HttpServletRequest request) {
        if(request==null)
            return "zh_CN";
        Object obj = request.getParameter(LOCAL_LANGUAGE_LABLE);
        if(obj!=null)
            return String.valueOf(obj);

        Locale local = (Locale)request.getSession().getAttribute(
                SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);

        if(local!=null){
            return local.getLanguage() +"_"+local.getCountry();
        }
        /**
         * Accept-Language: zh-cn
         */
        String localLang = request.getHeader("Accept-Language");
        if(StringUtils.isNotBlank(localLang)){
            String [] langs = localLang.split("-");
            if(langs!=null && langs.length>1)
                return StringUtils.lowerCase(langs[0])
                        +"_"+StringUtils.upperCase(langs[1]);
        }

        return "zh_CN";
    }

    public static void setCurrentLang(HttpSession session ,String localLang){
        if(StringUtils.isBlank(localLang))
            return;

        String [] langs = localLang.split("_");
        if(langs==null || langs.length<1)
            return;
        if(langs.length>1){
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
                    new Locale(langs[0],langs[1]));
        }else{
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
                    new Locale(langs[0]));
        }
    }

    public static void setCurrentLang(HttpServletRequest request,String localLang){
        setCurrentLang(request.getSession(),localLang);
    }

    public static String getLoginUserName(HttpServletRequest request) {
        UserDetails ud = getLoginUser(request);
        if (ud == null)
            return "";
        return ud.getUsername();
    }

}
