package com.centit.framework.security.model;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface CentitUserDetails extends
        Authentication, UserDetails {
    /**
     * 获取用户代码
     * @return 用户代码
     * 设置为 default getUserInfo().getUserCode()
     */
    String getUserCode();

    /**
     * 获取用户基本信息，将用户信息 有继承 重构为组合
     * @return IUserInfo
     */
    JSONObject getUserInfo();

    /**
     * 用户的参数，是个Map对，有系统参数也有业务系统自定义的参数
     * @return 用户的参数，是个Map对，有系统参数也有业务系统自定义的参数
     */
    Map<String, String> getUserSettings();

    /**
     * 设置到缓存中，并不是保存到数据库中，如果需要保存到数据库中请调用Dao持久化接口
     * @param paramCode paramCode
     * @param paramValue paramValue
     */
    void setUserSettingValue(String paramCode,String paramValue);

    /**
     * 用户某个具体的参数值
     * @param paramCode    参数代码
     * @return 用户某个具体的参数值
     */
    String getUserSettingValue(String paramCode);

    /**
     * 用户所有的可以执行操作方法，用于权限控制
     * @return 用户所有的可以执行操作方法，用于权限控制
     */
    Map<String, String> getUserOptList();

    /**
     * 判断用户是否有某个具体的操作方法权限
     * @param optId    业务ID
     * @param optMethod    方法名称
     * @return 判断用户是否有某个具体的操作方法权限
     */
    boolean checkOptPower(String optId, String optMethod);

    /**
     * 获得用户授权角色
     * @return 获得用户授权角色代码
     */
    JSONArray getUserRoles();

    /**
     * 设置用户登录机器IP
     * @param loginHost loginHost
     */
    void setLoginIp(String loginHost);

    /**
     * 设置最新登录时间
     * @return 用户登录时的IP
     */
    String getLoginIp();

    /**
     * 获取用户当前身份，用户有多个岗位时需要在首页上确定当前身份，默认为主机构
     * @return 当前生
     */
    JSONObject getCurrentStation();

    /**
     * 获取当前用户，当前机构的顶级机构，用于处理帐套
     * @return 最上层机构代码，根据用户的当前结构设置可能有变化
     */
    String getTopUnitCode();
    /**
     * 设置当前用户机构
     * @param userUnitId 当前机构（单位）代码
     */
    void setCurrentStationId(String userUnitId);
    /**
     * 获取当前机构
     * @return 当前机构（单位）代码
     */
    String getCurrentUnitCode();
    /*{
        IUserUnit cs = getCurrentStation();
        return cs != null? cs.getUnitCode() : getUserInfo().getPrimaryUnit();
    }*/
    /**
     * 获取用户归属机构关系
     * @return 获取用户归属机构关系
     */
    JSONArray getUserUnits();
}
