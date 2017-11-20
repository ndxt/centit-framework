package com.centit.framework.security.model;

import com.centit.framework.model.basedata.IRoleInfo;
import com.centit.framework.model.basedata.IUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
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
    IUserInfo getUserInfo();

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
     List<? extends IRoleInfo> getUserRoles();

    /**
     * 设置用户登录机器IP
     * @param loginHost loginHost
     */
     void setLoginIp(String loginHost);
    /**
     * 设置最新登录时间
     * @param loginTime loginTime
     */
     void setActiveTime(Date loginTime);


}
