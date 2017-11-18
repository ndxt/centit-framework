package com.centit.framework.model.basedata;

/**
 * F_Unit_role entity.
 *  机构角色 这个所拥有的角色 为这个机构所有的成员 均有的角色
 *  需要重构 "F_V_USERROLES" 这个视图
 * @author MyEclipse Persistence Tools
 */
public interface IUnitRole {
    /**
     * 机构代码
     * @return 机构代码
     */
     String getUnitCode();
    /**
     * 角色代码
     * @return 角色代码
     */
     String getRoleCode();
}
