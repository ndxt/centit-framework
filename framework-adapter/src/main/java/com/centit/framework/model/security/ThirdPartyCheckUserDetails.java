package com.centit.framework.model.security;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.adapter.PlatformEnvironment;

/**
 * 用户和第三方验证对接
 */
public interface ThirdPartyCheckUserDetails {
    /**
     * 验证成功返回用户对象,失败返回null,或者抛ObjectException
     * @param platformEnvironment 环境
     * @param token 验证参数
     * @return 用户对象
     */
    CentitUserDetails check(PlatformEnvironment platformEnvironment, JSONObject token);
}
