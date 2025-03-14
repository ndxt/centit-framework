package com.centit.framework.common;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.basedata.UserUnit;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileIOOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 系统Web常用工具类可以和spring WebUtils配合使用
 *
 * @author codefan
 * date 2014年10月24日
 */
public class WebOptUtils {
    public static final String LOCAL_LANGUAGE_LABLE = "LOCAL_LANG";
    public static final String CURRENT_USER_CODE_TAG   = "currentUserCode";
    public static final String CURRENT_TOP_UNIT_TAG   = "currentTopUnit";
    public static final String CURRENT_UNIT_CODE_TAG   = "currentUintCode";
    public static final String CURRENT_STATION_ID_TAG   = "currentStationId";
    public static final String CORRELATION_ID = "correlationId";
    public static final String AUTHORIZATION_TOKEN  = "Authorization";
    public static final String SESSION_ID_TOKEN     = "X-Auth-Token";
    public static final String REQUEST_ACCESS_TOKEN     = "accessToken";

    //不使用http的状态码来标识错误状态
    public static boolean exceptionNotAsHttpError = true;

    public static void setExceptionNotAsHttpError(boolean exceptionNotAsHttpError) {
        WebOptUtils.exceptionNotAsHttpError = exceptionNotAsHttpError;
    }

    public static boolean requestInSpringCloud = false;

    public static void setRequestInSpringCloud(boolean requestInSpringCloud) {
        WebOptUtils.requestInSpringCloud = requestInSpringCloud;
    }

    public static boolean isAjax(HttpServletRequest request) {
        return StringUtils.equals("XMLHttpRequest", request.getHeader("X-Requested-With"))
            || StringUtils.contains(request.getHeader("content-type"),"application/json")
            || StringUtils.contains(request.getHeader("accept"),"application/json")
            || BooleanBaseOpt.castObjectToBoolean(
                request.getParameter("ajax"), false);
    }

    public static boolean isFromMobile(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        if(agent ==null)
            return false;
        agent = agent.toLowerCase();
        return StringUtils.containsAny(agent,
            "android", "iphone", "ipad", "windows phone",
            "mqqbrowser", "opera mini", "mobi");
    }
    /**
     *  获取当前用户 不建议用这个函数，这个可能获取不到用户信息，推荐使用
     *  getLoginUser(HttpServletRequest request)
     * @return CentitUserDetails
     */
    private static CentitUserDetails innerGetUserDetailFromSpringContext() {
        SecurityContext sch = SecurityContextHolder.getContext();
        if (sch == null)
            return null;
        Authentication auth = sch.getAuthentication();
        if (auth == null)
            return null;

        Object o = auth.getPrincipal();
        if (o instanceof CentitUserDetails) {
            return (CentitUserDetails) o;// auth.getPrincipal();
        }
        return null;
    }

    private static CentitUserDetails innerGetUserDetailFromSession(HttpSession session) {
        if(session==null){
           return null;
        }
        Object attr = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if(attr==null){
            return null;
        }
        if(attr instanceof CentitUserDetails){
            return (CentitUserDetails) attr;
        }

        if(attr instanceof SecurityContext) {
            Authentication auth = ((SecurityContext) attr).getAuthentication();
            if (auth == null)
                return null;
            Object o = auth.getPrincipal();
            if (o instanceof CentitUserDetails) {
                return (CentitUserDetails) o;// auth.getPrincipal();
            }
        }
        return null;
    }

    public static CentitUserDetails innerGetUserDetail(HttpSession session){
        CentitUserDetails ud = innerGetUserDetailFromSpringContext();
        if(ud!=null){
            return ud;
        }
        return innerGetUserDetailFromSession(session);
    }

    @Deprecated
    public static CentitUserDetails getLoginUser(HttpSession session) {
        return innerGetUserDetail(session);
    }

    private static UserInfo innerGetLoginUserFromCloud(HttpServletRequest request){
        String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
        String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
        if(userCode!=null){
            return CodeRepositoryUtil.getUserInfoByCode(topUnit, userCode);
        }
        return null;
    }
    /**
     * 返回IUserInfo or CentitUserDetails
     * @param request HttpServletRequest
     * @return UserInfo or CentitUserDetails
     */
    public static Object getLoginUser(HttpServletRequest request) {
        if(request==null){
            return null;
        }
        if(WebOptUtils.requestInSpringCloud){
            return innerGetLoginUserFromCloud(request);
        }
        return innerGetUserDetail(request.getSession());
    }

    public static String getRequestAddr(HttpServletRequest request) {
        String sHostIp = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(sHostIp) && !"unKnown".equalsIgnoreCase(sHostIp)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = sHostIp.indexOf(",");
            if (index != -1) {
                return sHostIp.substring(0, index);
            } else {
                return sHostIp;
            }
        }
        sHostIp = request.getHeader("X-Real-Ip");
        if (StringUtils.isNotBlank(sHostIp) && !"unKnown".equalsIgnoreCase(sHostIp)) {
            return sHostIp;
        }
        return request.getRemoteAddr();
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

    public static String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(WebOptUtils.CORRELATION_ID);
        if(StringUtils.isBlank(correlationId)){
            correlationId = request.getSession().getId();
            //request.hashCode()
        }
        return correlationId;
    }

    private static Locale fetchLocaleFromRequest(HttpServletRequest request) {
        Locale local = request.getLocale();
        if(local == null) {
            Object localeName = request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            if (localeName instanceof Locale) {
                local = (Locale) localeName;
            } else if (null != localeName) {
                local = new Locale(localeName.toString());
            }
        }
        return local;
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

        Locale local = fetchLocaleFromRequest(request);

        if(local!=null){
            return local.getLanguage() +"_"+ local.getCountry();
        }
        /**
         * Accept-Language: zh-cn
         */
        String localLang = request.getHeader("Accept-Language");
        if(StringUtils.isNotBlank(localLang)){
            if(localLang.length()>4)
                return localLang.substring(0, 2) +"_"+ localLang.substring(3, 5).toUpperCase();
        }

        return "zh_CN";
    }

    public static Locale getCurrentLocale(HttpServletRequest request) {
        if(request==null)
            return Locale.SIMPLIFIED_CHINESE; // createConstant("zh", "CN");
        Object obj = request.getParameter(LOCAL_LANGUAGE_LABLE);
        if(obj!=null) {
            String localLang =  String.valueOf(obj);
            if(localLang.length()>5) {
                return new Locale(localLang.substring(0, 2), localLang.substring(3, 5));
            }
        }

        Locale local = fetchLocaleFromRequest(request);

        if(local!=null){
            return local;
        }
        /**
         * Accept-Language: zh-CN
         */
        String localLang = request.getHeader("Accept-Language");
        if(StringUtils.isNotBlank(localLang)){
            if(localLang.length()>4)
                return new Locale(localLang.substring(0, 2), localLang.substring(3, 5));
        }

        return Locale.SIMPLIFIED_CHINESE;
    }

    public static void setCurrentLang(HttpSession session ,String localLang){
        if(StringUtils.isBlank(localLang))
            return;

        String [] langs = localLang.split("_");
        if(langs.length<1)
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

    public static UserInfo getCurrentUserInfo(HttpServletRequest request) {
        if(request == null){
            CentitUserDetails centitUserDetails = getUserInfoByHttpContext();
            if (null != centitUserDetails) {
                return centitUserDetails.getUserInfo();
            }
            return null;
        }
        if(WebOptUtils.requestInSpringCloud){
            return innerGetLoginUserFromCloud(request);
        }
        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if(ud != null) {
            return ud.getUserInfo();
        }
        return null;
    }

    public static UserInfo assertUserLogin(HttpServletRequest request) {
        UserInfo userInfo =  getCurrentUserInfo(request);
        if(userInfo == null){ // anonymous
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                ResponseData.ERROR_NOT_LOGIN_MSG);
        }
        return userInfo;
    }

    public static CentitUserDetails assertUserDetails(HttpServletRequest request) {
        CentitUserDetails userInfo =  getCurrentUserDetails(request);
        if(userInfo == null){ // anonymous
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                ResponseData.ERROR_NOT_LOGIN_MSG);
        }
        return userInfo;
    }

    public static CentitUserDetails getCurrentUserDetails(HttpServletRequest request) {
        if(request == null){
            CentitUserDetails centitUserDetails = getUserInfoByHttpContext();
            if (null != centitUserDetails) {
                return centitUserDetails;
            }
            return null;
        }
        if(WebOptUtils.requestInSpringCloud){
            String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
            String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
            if(userCode!=null){
                UserInfo userinfo = CodeRepositoryUtil.getUserInfoByCode(topUnit, userCode);
                CentitUserDetails userDetails = new CentitUserDetails();
                userDetails.setUserInfo(userinfo);
                List<UserUnit> uulist = CodeRepositoryUtil.listUserUnits(topUnit, userCode);
                userDetails.setUserUnits(uulist);
                String unitCode = request.getHeader(WebOptUtils.CURRENT_UNIT_CODE_TAG);
                if(uulist!=null && uulist.size()>0) {
                    for (UserUnit uu : uulist) {
                        if(StringUtils.equals(unitCode, uu.getUnitCode())){
                            userDetails.setCurrentStationId(uu.getUserUnitId());
                            break;
                        }
                    }
                }
                return userDetails;
            }
        }
        return innerGetUserDetail(request.getSession());
    }

    public static String getCurrentTopUnit(HttpServletRequest request) {
        if(request==null || request.getSession()==null){
            CentitUserDetails centitUserDetails = getUserInfoByHttpContext();
            if (null != centitUserDetails) {
                return centitUserDetails.getTopUnitCode();
            }
            return GlobalConstValue.NO_TENANT_TOP_UNIT;
        }
        if(WebOptUtils.requestInSpringCloud){
            String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
            if(StringUtils.isNotBlank(topUnit)){
                return topUnit;
            }
        }
        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if(ud==null || ud.getTopUnitCode()==null){
            return "";
        }
        return ud.getTopUnitCode();
    }

    private static CentitUserDetails getUserInfoByHttpContext() {
        CentitUserDetails centitUserDetails = HttpContextUtils.getCurrentUserInfo();
        if (null != centitUserDetails) {
            return centitUserDetails;
        }
        return null;
    }

    public static String getCurrentUserCode(HttpServletRequest request) {
        if(request == null){
            CentitUserDetails centitUserDetails = getUserInfoByHttpContext();
            if (null != centitUserDetails) {
                return centitUserDetails.getUserCode();
            }
            return "";
        }
        if(WebOptUtils.requestInSpringCloud){
            String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
            if(StringUtils.isNotBlank(userCode)){
                return userCode;
            }
        }

        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if (ud == null) {
            return "";
        }
        return ud.getUserCode();
    }

    public static String getCurrentUserName(HttpServletRequest request) {
        UserInfo ud = getCurrentUserInfo(request);
        if (ud == null)
            return "";
        return ud.getUserName();//.getString("userName");
    }

    public static String getCurrentUserLoginName(HttpServletRequest request) {
        UserInfo ud = getCurrentUserInfo(request);
        if (ud == null)
            return "";
        return ud.getLoginName();//("loginName");
    }

    public static String getCurrentUnitCode(HttpServletRequest request) {
        if (request == null) {
            CentitUserDetails centitUserDetails = getUserInfoByHttpContext();
            if (null != centitUserDetails) {
                return centitUserDetails.getCurrentUnitCode();
            }
            return "";
        }
        if(WebOptUtils.requestInSpringCloud){
            String unitCode = request.getHeader(WebOptUtils.CURRENT_UNIT_CODE_TAG);
            if(StringUtils.isNotBlank(unitCode)){
                return unitCode;
            }
        }
        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if (ud == null) {
            return "";
        }
        return ud.getCurrentUnitCode();
    }

    public static <T> T getWebAppContextBean(String beanName, Class<T> clazz ) {
        WebApplicationContext ctx = ContextLoaderListener.getCurrentWebApplicationContext();
        if(ctx == null) {
            return null;
        }
        return ctx.getBean(beanName, clazz);
    }

    public static String getRequestBody(HttpServletRequest request) {
        try {
            return FileIOOpt.readStringFromInputStream(request.getInputStream());
        } catch (IOException e) {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION, e);
        }
    }

    public static String getRequestFirstOneParameter(HttpServletRequest request, String... params) {
        for (String p : params) {
            String value = request.getParameter(p);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    public static String getRequestFirstOneHeader(HttpServletRequest request, String... params) {
        for (String p : params) {
            String value = request.getHeader(p);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    public static String getTraceId() {
        return HttpContextUtils.getTraceId();
    }

    public static Map<String, String> fetchRequestCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0)
            return null;
        Map<String, String> cookiesMap = new HashMap<>();
        for (Cookie cookie : cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        return cookiesMap;
    }

    public static Map<String, String> fetchRequestHeaders(HttpServletRequest request) {
        Enumeration<String>  names = request.getHeaderNames();
        if(names == null)
            return null;
        Map<String, String> headersMap = new HashMap<>();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            //cookie
            if(!"Cookie".equalsIgnoreCase(name)) {
                headersMap.put(name, request.getHeader(name));
            }
        }
        if(request.getSession()!=null) {
            if(StringUtils.isBlank(headersMap.get(SESSION_ID_TOKEN)))
                headersMap.put(SESSION_ID_TOKEN, request.getSession().getId());
        }
        return headersMap;
    }
}
