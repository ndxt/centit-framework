package com.centit.framework.security.model;

import java.util.*;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.centit.framework.model.basedata.IUserInfo;

public interface CentitUserDetails extends
        Authentication, UserDetails {
    /**
     * 获取用户基本信息，将用户信息 有继承 重构为组合
     * @return IUserInfo
     */
    IUserInfo getUserInfo();

    default String getUserCode(){
        return getUserInfo().getUserCode();
    }
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
     * 获得用户授权角色代码
     * @return 获得用户授权角色代码
     */
     List<String> getUserRoleCodes();
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

    /*
     * 判断用户是否有以下角色集合中的一个
     * @param sortedRoleAttribute 排序号的角色列表
     * @return 如果有返回 true 否则会 false
     */
     //boolean hasOneRoleOf(Collection<ConfigAttribute> sortedRoleAttribute);

    /*
     * 为了配合是由添加的额外的角色，用户登录时可以选择一个事由，配合这个事由有一个额外的角色
     * 并将这个状态记录在这个用户的信息中
     * @param roleCode 角色代码
     */
    //void setUserExtendRole(String roleCode);
}
