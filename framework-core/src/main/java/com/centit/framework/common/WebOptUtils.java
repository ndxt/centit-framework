package com.centit.framework.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 系统Web常用工具类可以和spring WebUtils配合使用
 *
 * @author codefan
 * date 2014年10月24日
 */
public class WebOptUtils {
    public static final String LOCAL_LANGUAGE_LABLE = "LOCAL_LANG";
    public static final String CURRENT_USER_CODE_TAG   = "cnt-current-user-code";
    public static final String CURRENT_TOP_UNIT_TAG   = "cnt-current-top-unit";
    public static final String CURRENT_UNIT_CODE_TAG   = "cnt-current-uint-code";
    public static final String CURRENT_STATION_ID_TAG   = "cnt-current-station-id";
    public static final String CORRELATION_ID = "cnt-correlation-id";
    public static final String AUTHORIZATION_TOKEN  = "Authorization";
    public static final String SESSION_ID_TOKEN     = "x-auth-token";

    //不使用http的状态码来标识错误状态
    public static boolean exceptionNotAsHttpError = false;

    public static void setExceptionNotAsHttpError(boolean exceptionNotAsHttpError) {
        WebOptUtils.exceptionNotAsHttpError = exceptionNotAsHttpError;
    }

    public static boolean requestInSpringCloud = false;

    public static void setRequestInSpringCloud(boolean requestInSpringCloud) {
        WebOptUtils.requestInSpringCloud = requestInSpringCloud;
    }

    public static boolean isTenant = false;

    public static void setIsTenant(boolean isTenant) {
        WebOptUtils.isTenant = isTenant;
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

    private static CentitUserDetails innerGetUserDetail(HttpSession session){
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

    private static IUserInfo innerGetLoginUserFromCloud(HttpServletRequest request){
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
     * @return IUserInfo or CentitUserDetails
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
        String sHostIp = request.getHeader("x-forwarded-for");
        if (StringUtils.isNotBlank(sHostIp) && !"unKnown".equalsIgnoreCase(sHostIp)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = sHostIp.indexOf(",");
            if (index != -1) {
                return sHostIp.substring(0, index);
            } else {
                return sHostIp;
            }
        }
        sHostIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(sHostIp) && !"unKnown".equalsIgnoreCase(sHostIp)) {
            return sHostIp;
        }
        if (StringUtils.isBlank(sHostIp)) {
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

    public static String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(WebOptUtils.CORRELATION_ID);
        if(StringUtils.isBlank(correlationId)){
            correlationId = request.getSession().getId();
            //request.hashCode()
        }
        return correlationId;
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
            if(langs.length>1)
                return StringUtils.lowerCase(langs[0])
                        +"_"+StringUtils.upperCase(langs[1]);
        }

        return "zh_CN";
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

    public static JSONObject getCurrentUserInfo(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            return (JSONObject)JSON.toJSON(innerGetLoginUserFromCloud(request));
        }
        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if(ud != null) {
            return ud.getUserInfo();
        }
        return null;
    }

    public static CentitUserDetails getCurrentUserDetails(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            String userCode = request.getHeader(WebOptUtils.CURRENT_USER_CODE_TAG);
            String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
            if(userCode!=null){
                IUserInfo userinfo = CodeRepositoryUtil.getUserInfoByCode(topUnit, userCode);
                JsonCentitUserDetails userDetails = new JsonCentitUserDetails();
                userDetails.setUserInfo((JSONObject) JSON.toJSON(userinfo));
                List<? extends IUserUnit> uulist = CodeRepositoryUtil.listUserUnits(topUnit, userCode);
                userDetails.setUserUnits((JSONArray) JSON.toJSON(uulist));
                String unitCode = request.getHeader(WebOptUtils.CURRENT_UNIT_CODE_TAG);
                if(uulist!=null && uulist.size()>0) {
                    for (IUserUnit uu : uulist) {
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
        if(request==null){
            return GlobalConstValue.NO_TENANT_TOP_UNIT;
        }
        if(WebOptUtils.requestInSpringCloud){
            String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
            if(StringUtils.isNotBlank(topUnit)){
                return topUnit;
            }
        }
        CentitUserDetails ud = innerGetUserDetail(request.getSession());
        if (ud == null || StringUtils.isBlank(ud.getTopUnitCode())) {
            return GlobalConstValue.NO_TENANT_TOP_UNIT;
        }
        return ud.getTopUnitCode();
    }

    public static String getCurrentUserCode(HttpServletRequest request) {
        if(request == null){
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
        JSONObject ud = getCurrentUserInfo(request);
        if (ud == null)
            return "";
        return ud.getString("userName");
    }

    public static String getCurrentUserLoginName(HttpServletRequest request) {
        JSONObject ud = getCurrentUserInfo(request);
        if (ud == null)
            return "";
        return ud.getString("loginName");
    }

    public static String getCurrentUnitCode(HttpServletRequest request) {
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

    public static boolean isTenantTopUnit(HttpServletRequest request) {
        if(WebOptUtils.requestInSpringCloud){
            String topUnit = request.getHeader(WebOptUtils.CURRENT_TOP_UNIT_TAG);
            if(StringUtils.isNotBlank(topUnit) && !GlobalConstValue.NO_TENANT_TOP_UNIT.equalsIgnoreCase(topUnit)){
                return true;
            }
        }
        String topUnit = getCurrentTopUnit(request);
        if (GlobalConstValue.NO_TENANT_TOP_UNIT.equalsIgnoreCase(topUnit) || StringUtils.isBlank(topUnit)) {
            return false;
        }
        if (!WebOptUtils.isTenant) {
            return false;
        }
        return true;
    }
}
