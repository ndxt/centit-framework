package com.centit.framework.security.model;

import com.centit.support.common.CachedMap;
import com.centit.support.common.ICachedObject;

public class CentitSecurityMetadata {

    public static final String ROLE_PREFIX = "R_";
    public static boolean isForbiddenWhenAssigned = false;

    /**
     * @param isForbiddenWhenAssigned 设置为true时，将url分配到菜单后 该url需要授权才能访问；
     * 设置为false时，将url分配到菜单后不会对该url进行拦截，只有将该url分配给某个角色，其他角色才会被拦截
     */
    public static void setIsForbiddenWhenAssigned(boolean isForbiddenWhenAssigned){
        CentitSecurityMetadata.isForbiddenWhenAssigned = isForbiddenWhenAssigned;
    }

    public static CachedMap<String, TopUnitSecurityMetadata> securityMetadata=
        new CachedMap<>((topUnit)-> new TopUnitSecurityMetadata(topUnit),
            ICachedObject.DEFAULT_REFRESH_PERIOD);

}
