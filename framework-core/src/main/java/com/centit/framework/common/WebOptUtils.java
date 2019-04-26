package com.centit.framework.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IUserInfo;
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
    public static final String CURRENT_USER_CODE_TAG   = "cnt-current-user-code";
    public static final String CURRENT_UNIT_CODE_TAG   = "cnt-current-uint-code";
    public static final String CURRENT_STATION_ID_TAG   = "cnt-current-station-id";
    //不使用http的状态码来标识错误状态
    public static boolean exceptionNotAsHttpError = false;

    public static void setExceptionNotAsHttpError(boolean exceptionNotAsHttpError) {
        WebOptUtils.exceptionNotAsHttpError = exceptionNotAsHttpError;
    }

    public static boolean requestInSpringCloud = false;

    public static void setRequestInSpringCloud(boolean requestInSpringCloud) {
        WebOptUtils.requestInSpringCloud = requestInSpringCloud;
    }

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
            request.getHeader("accept").contains("application/json");
    }

    /**
     *  获取当前用户 不建议用这个函数，这个可能获取不到用户信息，推荐使用
     *  getLoginUser(HttpServletRequest request)
     * @return CentitUserDetails
     */
    private static CentitUserDetails getLoginUser() {
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
        if(attr==null){
            return null;
        }
        if(attr instanceof CentitUserDetails){
            return (CentitUserDetails)attr;
        }
        CentitUserDetails ud = null;
        if(attr instanceof SecurityContext) {
            Authentication auth = ((SecurityContext) attr).getAuthentication();
            if (auth == null)
                return null;
            Object o = auth.getPrincipal();
            if (o instanceof CentitUserDetails) {
                ud = (CentitUserDetails) o;// auth.getPrincipal();
            }
        }
        return ud;
    }

    @Deprecated
    public static CentitUserDetails getLoginUser(HttpSession session) {
        CentitUserDetails ud = getLoginUser();
        if(ud!=null){
             return ud;
        }
        return innerGetLoginUser(session);
    }

    /**
     * 返回IUserInfo or CentitUserDetails
     * @param request HttpServletRequest
     * @return IUserInfo or CentitUserDetails
     */
    public static Object getLoginUser(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
            if(userCode!=null){
                return CodeRepositoryUtil.getUserInfoByCode(userCode);
            }
        }
        CentitUserDetails ud = getLoginUser();
        if(ud != null) {
            return ud;
        }
        //根据token获取用户信息
        //在session中手动获得用户信息
        return innerGetLoginUser(request.getSession());
    }

    public static String getRequestAddr(HttpServletRequest request) {
        String sHostIp = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(sHostIp)){
            sHostIp = request.getRemoteAddr();
        }
        return sHostIp;
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

    public static JSONObject getCurrentUserInfo(HttpServletRequest request) {
        Object ud = getLoginUser(request);
        if(ud instanceof CentitUserDetails) {
            return ((CentitUserDetails)ud).getUserInfo();
        }

        if(ud instanceof IUserInfo) {
            return (JSONObject)JSON.toJSON(ud);
        }
        return null;
    }

    public static String getCurrentUserCode(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
            if(StringUtils.isNotBlank(userCode)){
                return userCode;
            }
        }

        Object ud = getLoginUser(request);
        if (ud == null)
            return "";
        if(ud instanceof CentitUserDetails) {
            return ((CentitUserDetails)ud).getUserCode();
        }

        return "";
    }

    public static String getCurrentUserName(HttpServletRequest request) {
        Object ud = getLoginUser(request);
        if (ud == null)
            return "";
        if(ud instanceof CentitUserDetails) {
            return ((CentitUserDetails)ud).getUserInfo().getString("userName");
        }

        if(ud instanceof IUserInfo) {
            return ((IUserInfo)ud).getUserName();
        }
        return "";
    }

    public static String getCurrentUserLoginName(HttpServletRequest request) {
        Object ud = getLoginUser(request);
        if (ud == null)
            return "";
        if(ud instanceof CentitUserDetails) {
            return ((CentitUserDetails)ud).getUsername();
        }

        if(ud instanceof IUserInfo) {
            return ((IUserInfo)ud).getLoginName();
        }
        return "";
    }

    public static String getCurrentUnitCode(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            String unitCode = request.getHeader(WebOptUtils.CURRENT_UNIT_CODE_TAG);
            if(StringUtils.isNotBlank(unitCode)){
                return unitCode;
            }
        }

        Object ud = getLoginUser(request);
        if (ud == null)
            return "";
        if(ud instanceof CentitUserDetails) {
            return ((CentitUserDetails)ud).getCurrentUnitCode();
        }
        return "";
    }
}
