package com.centit.framework.model.basedata;

/**
 * FRolepower entity.
 *
 * @author MyEclipse Persistence Tools
 */
public interface IRolePower{
    /**
     * 角色代码
     * @return 角色代码
     */
     String getRoleCode();
    /**
     * 业务操作代码（OptMethod）的主键
     * @return 业务操作代码（OptMethod）的主键
     */
     String getOptCode();
    /**
     * 业务操作范围代码 集合
     * @return 业务操作范围代码 集合
     */
     String[] getOptScopeCodeSet();

     String getTopUnit();
}
