package com.centit.framework.security;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.UserSetting;
import com.centit.framework.model.security.CentitUserDetails;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public abstract class SecurityContextUtils {
    public static String AJAX_CHECK_CAPTCHA_RESULT = "ajaxCheckCaptchaResult";
    public static String SecurityContextTokenName = "accessToken";
    public static String SecurityContextUserInfo = "userInfo";
    public static String PUBLIC_ROLE_CODE = "public";
    public static String ADMIN_ROLE_CODE = "sysadmin";
    public static String ANONYMOUS_ROLE_CODE = "anonymous";
    public static String SPRING_ANONYMOUS_ROLE_CODE = "ROLE_ANONYMOUS";
    public static String FORBIDDEN_ROLE_CODE = "forbidden";
    public static String DEPLOYER_ROLE_CODE = "deploy";

    public static void fetchAndSetLocalParams(CentitUserDetails ud,
                                    HttpServletRequest request, PlatformEnvironment platformEnvironment){
        ud.setLoginIp(WebOptUtils.getRequestAddr(request));
        //设置Lang参数
        String lang = WebOptUtils.getLocalLangParameter(request);
        if(StringUtils.isNotBlank(lang)){
            //request.getSession().setAttribute("LOCAL_LANG", lang);
            WebOptUtils.setCurrentLang(request, lang);
            String userLang = ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE);
            if(! lang.equals(userLang)){
                ud.setUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE, userLang);
                if(platformEnvironment!=null){
                    platformEnvironment.saveUserSetting(new UserSetting(ud.getUserCode(),
                        WebOptUtils.LOCAL_LANGUAGE_LABLE, lang, "SYS", "用户默认区域语言"));
                }
            }
        }else{
            lang = ud.getUserSettingValue(WebOptUtils.LOCAL_LANGUAGE_LABLE);
            if(StringUtils.isNotBlank(lang)){
                WebOptUtils.setCurrentLang(request, lang);
                request.setAttribute(WebOptUtils.LOCAL_LANGUAGE_LABLE, lang);
            }
        }
    }

    public static ResponseData makeLoginSuccessResponse(CentitUserDetails ud,
                                                        HttpServletRequest request) {
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(SecurityContextUtils.SecurityContextTokenName,
            request.getSession().getId());
        resData.addResponseData(SecurityContextUtils.SecurityContextUserInfo, ud.toJsonWithoutSensitive());
        return resData;
    }
}
