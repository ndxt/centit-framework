package com.centit.framework.security;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.support.common.CachedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class CentitSecurityMetadata {

    public static final String ROLE_PREFIX = "R_";
    public static boolean isForbiddenWhenAssigned = false;
    private static SystemSecurityMetadata systemSecurityMetadata = new SystemSecurityMetadata();
    private static CachedMap<String, List<ConfigAttribute>> apiSecurityMetadata =
        new CachedMap<>((apiId)->CodeRepositoryCache.getPlatformEnvironment().getRolesWithApiId(apiId),
            CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);
    private static final String DDE_RUN = "/dde/run/";
    private static final String DDE_RUN_DRAFT = "/dde/run/draft";

    public static void evictCache(int apiOrSystem){
        if(apiOrSystem==0){
            systemSecurityMetadata.evictCache();
        }else /*if(apiOrSystem==1) */{
            apiSecurityMetadata.evictCache();
        }
    }
    /**
     * @param isForbiddenWhenAssigned 设置为true时，将url分配到菜单后 该url需要授权才能访问；
     *                                设置为false时，将url分配到菜单后不会对该url进行拦截，只有将该url分配给某个角色，其他角色才会被拦截
     */
    public static void setIsForbiddenWhenAssigned(boolean isForbiddenWhenAssigned) {
        CentitSecurityMetadata.isForbiddenWhenAssigned = isForbiddenWhenAssigned;
    }

    public static List<ConfigAttribute> matchUrlToRole(String sUrl, HttpServletRequest request) {
        String apiId = parseUrlToApi(sUrl);
        if (StringUtils.isBlank(apiId)) {
            return systemSecurityMetadata.matchUrlToRole(sUrl, request);
        }
        return apiSecurityMetadata.getCachedValue(apiId);
    }

    private static String parseUrlToApi(String sUrl) {
        String apiId = "";
        boolean isApi= StringUtils.contains(sUrl,DDE_RUN)&&!StringUtils.contains(sUrl,DDE_RUN_DRAFT);
        if (isApi){
            String sFunUrl ;
            int p = sUrl.indexOf('?');
            if(p<1) {
                sFunUrl = sUrl;
            }else {
                sFunUrl = sUrl.substring(0, p);
            }

            int nPos = sFunUrl.lastIndexOf('.');
            if(nPos > 0) {
                int nPos2 = sFunUrl.lastIndexOf('/');
                if (nPos > nPos2) {
                    sFunUrl = sFunUrl.substring(0, nPos);
                }
            }
            String[] sUrls = sFunUrl.split("/");
            int length=sUrls.length;
            apiId = sUrls[length-1];
        }
        return apiId;
    }


}