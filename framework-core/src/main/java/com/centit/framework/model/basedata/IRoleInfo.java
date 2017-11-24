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
     * 角色的类别 F （Fixe）系统内置的，固有的 这些角色不能删除，也不能赋给任何人， G （global） 全局的
     *          P （public） 公用的，指 系统全局 和 部门之间公用的
     *          D （department）部门（机构）特有的角色
     *          I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     * @return 角色的类别 F/G/P/D/I/W
     */
    String getRoleType();

    /**
     * 获取角色的所有者， roleType = 'D' 时 生效， 其他的都返回 system 系统的
     * @return
     */
    String getRoleOwner();
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