package com.centit.framework.model.basedata;

import java.util.List;

/**
 * 角色信息
 * @author MyEclipse Persistence Tools
 */
public interface IRoleInfo{
    /**
     * 角色代码
     * @return 角色代码
     */
     String getRoleCode();
    /**
     * 角色名称
     * @return 角色名称
     */
     String getRoleName();

    /**
     * 是否有效 T有效 F无效
     * @return 是否有效 T有效 F无效
     */
     String getIsValid();
    /**
     * 角色所有操作权限
     * @return 角色所有操作权限
     */
     List<? extends IRolePower> getRolePowers();
}