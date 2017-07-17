package com.centit.framework.model.basedata;

import java.util.List;

/**
 * 角色信息
 * @author MyEclipse Persistence Tools
 */
public interface IRoleInfo{
	/**
	 * 角色代码
	 * @return
	 */
	public String getRoleCode();
	/**
	 * 角色名称
	 * @return
	 */
	public String getRoleName();
	
	/**
	 * 是否有效 T有效 F无效
	 * @return
	 */
	public String getIsValid();
	/**
	 * 角色所有操作权限
	 * @return
	 */
	public List<? extends IRolePower> getRolePowers();
}