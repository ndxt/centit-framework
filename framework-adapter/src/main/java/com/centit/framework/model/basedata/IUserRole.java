package com.centit.framework.model.basedata;

/**
 * FUserrole entity.
 *  用户角色
 * @author MyEclipse Persistence Tools
 */
public interface IUserRole{
    /**
     * 用户代码
     * @return 用户代码
     */
    String getUserCode();
    /**
     * 角色代码
     * @return 角色代码
     */
    String getRoleCode();

    /**
     * 这个是新版本的一个新的性所有 添加了这个 默认实现
     * 用户获得这个角色的方式，
     * @return "D" 直接活的 ， "I" 从机构继承， "M" 从机构层级继承，至少夸一级，这个默认不打开
     */
    String getObtainType();

    /**
     * 从何处继承
     * @return 继承
     */
    String getInheritedFrom();
}
