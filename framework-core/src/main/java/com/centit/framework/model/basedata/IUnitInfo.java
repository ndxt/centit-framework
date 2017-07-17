package com.centit.framework.model.basedata;

import java.util.List;

/**
 * FUnitinfo entity.
 * 机构信息
 * @author MyEclipse Persistence Tools
 */
public interface IUnitInfo{
	/**
	 * 机构代码 是机构的主键
	 * @return
	 */
	public String getUnitCode();
	
	
	/**
	 * 机构自编代码
	 * @return
	 */
	public String getDepNo();
	
	/**
	 * 机构名称
	 * @return
	 */
	public String getUnitName();
	/**
	 * 上级机构代码
	 * @return
	 */
	public String getParentUnit();
	/**
	 * 机构类别 
	 * @return
	 */
	public String getUnitType();
	/**
	 * 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
	 * @return
	 */
	public String getIsValid();
	/**
	 * 机构路径
	 * @return
	 */
	public String getUnitPath();
	/**
	 * 机构排序
	 * @return
	 */
	public Long getUnitOrder();
	
	/**
	 * 分管领导（机构管理员）
	 * @return
	 */
	public String getUnitManager();
	/**
	 * 获取下级机构
	 * @return
	 */
	public List<? extends IUnitInfo> getSubUnits();
	
	/**
	 * 获取机构用户下属用户关系
	 * @return
	 */
	public List<? extends IUserUnit> getUnitUsers();
}
