package com.centit.framework.model.basedata;

/**
 * FRolepower entity.
 *
 * @author MyEclipse Persistence Tools
 */
public interface IRolePower{
	/**
	 * 角色代码
	 * @return
	 */
	public String getRoleCode();
	/**
	 * 业务操作代码（OptMethod）的主键
	 * @return
	 */
	public String getOptCode();
	/**
	 * 业务操作范围代码 集合
	 * @return
	 */
	public String[] getOptScopeCodeSet();
}