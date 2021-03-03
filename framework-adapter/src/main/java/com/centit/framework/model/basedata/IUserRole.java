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
     * 角色的类别 F (Fixed)系统内置的，固有的 这些角色不能删除，也不能赋给任何人，
     *              public，forbidden，anonymous
     *          G (global) 全局的
     *          P (Public) 公用的，指 系统全局 和 部门之间公用的
     *          D (Department)部门(机构)特有的角色, 租户角色
     *          S (Sub System) 属于某个子系统
     *          I (Item )为项目角色
     *          W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     *          H (HIDE)系统内置的不要显示的，是部门可以自己支配的操作权限集合
     * 角色的类别 F/G/P/D/S/I/W/H
     */
    String getRoleType(); // 角色类别
    /**
     * 对租户的 topUnit
     * @return 对租户的 topUnit ，角色属于某个租户
     */
    String getUnitCode();
    /**
     * 从何处继承
     * @return 继承
     */
    String getInheritedFrom();

    /**
     * 这个是新版本的一个新的性所有 添加了这个 默认实现
     * 用户获得这个角色的方式，
     * @return "D" 直接活的 ， "I" 从机构继承， "M" 从机构层级继承，至少夸一级，这个默认不打开
     */
    String getObtainType();
}
