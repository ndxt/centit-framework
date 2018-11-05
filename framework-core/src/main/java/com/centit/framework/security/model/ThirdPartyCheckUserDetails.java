package com.centit.framework.security.model;

/**
 * 用户和第三方验证对接
 */
public interface ThirdPartyCheckUserDetails {
    boolean check(CentitUserDetails userDetails, Object token);
}
