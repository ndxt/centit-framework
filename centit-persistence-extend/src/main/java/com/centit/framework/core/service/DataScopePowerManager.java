package com.centit.framework.core.service;

import com.centit.framework.core.dao.DataPowerFilter;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.security.CentitUserDetails;

import java.util.List;

/**
 * 将项目Service层需要用到的通用服务放在这个，供其他业务服务调用。
 * 使用方法：用下面的注解
 *             (name = "generalService")
 *            protected GeneralService  generalService;
 * @author codefan
 */
public interface DataScopePowerManager {

    /**
     * 获得用户摸个功能方法的数据范围权限，返回null或者size==0表示拥有所有权限
     * @param topUnit 用户租户代码
     * @param sUserCode sUserCode
     * @param sOptid sOptid
     * @param sOptMethod sOptMethod
     * @return 用户摸个功能方法的数据范围权限
     */
    List<String> listUserDataFiltersByOptIdAndMethod
            (String topUnit, String sUserCode, String sOptid, String sOptMethod);

    /**
     * 创建用户数据范围过滤器，和上面的方法结合使用
     * @param userInfo JSONObject 用户信息
     * @param topUnit 用户租户代码
     * @param currentUnit 用户当前机构
     * @return DataPowerFilter
     */
    DataPowerFilter createUserDataPowerFilter(UserInfo userInfo, String topUnit, String currentUnit);

    /**
     * 创建用户数据范围过滤器，和上面的方法结合使用
     * @param userDetails CentitUserDetails
     * @return DataPowerFilter
     */
    DataPowerFilter createUserDataPowerFilter(CentitUserDetails userDetails);
}
