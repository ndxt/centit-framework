package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;

import java.util.List;
import java.util.Map;

public interface PlatformEnvironment {

	/**
	 * 刷新数据字典
	 * @return
	 */
	boolean reloadDictionary();
	/**
	 * 刷新权限相关的元数据 
	 * @return
	 */
	boolean reloadSecurityMetadata();	
	/**
	 * 获取系统配置参数
	 * @param paramCode
	 * @return
	 */
	String getSystemParameter(String paramCode);
	/**
	 * 获得用户设置参数
	 * @param userCode
	 * @param paramCode
	 * @return
	 */
	String getUserSetting(String userCode,String paramCode);
	
	/**
	 * 获取用户所有菜单功能
	 * @param userCode
	 * @param asAdmin 是否是作为管理员
	 * @return
	 */
	List<? extends IOptInfo> listUserMenuOptInfos(String userCode,boolean asAdmin);
	
	/**
	 * 获取用户所有菜单功能
	 * @param userCode
	 * @param asAdmin 是否是作为管理员
	 * @return
	 */
	List<? extends IOptInfo> listUserMenuOptInfosUnderSuperOptId(
			String userCode,String superOptId,boolean asAdmin);
	
	/**
	 * 根据用户代码获取用户信息，
	 * @return
	 */
	IUserInfo getUserInfoByUserCode(String userCode);
	
	/**
	 * 根据登录名获取用户信息，
	 * @return
	 */
	IUserInfo getUserInfoByLoginName(String loginName);

	/**
	 * 根据用户代码获取用户信息，
	 * @return
	 */
	IUnitInfo getUnitInfoByUnitCode(String unitCode);
	/**
	 * 修改用户密码
	 * @param userCode
	 * @param userPassword
	 */
	void changeUserPassword(String userCode,String userPassword);
	
	/**
	 * 验证用户密码
	 * @param userCode
	 * @param userPassword
	 */
	boolean checkUserPassword(String userCode,String userPassword);
	/**
	 * 获取所有用户，
	 * @return
	 */
	List<? extends IUserInfo> listAllUsers();
	
	/**
	 * 获取所有机构 
	 * @return
	 */
	List<? extends IUnitInfo> listAllUnits();
	
	/**
	 * 获取所有用户和机构关联关系
	 * @return
	 */
	List<? extends IUserUnit> listAllUserUnits();
	
	/**
	 * 根据用户代码获得 用户所有的机构信息
	 * @param userCode
	 * @return
	 */
	List<? extends IUserUnit> listUserUnits(String userCode);
	
    /**
     * 根据机构代码获得 机构所有用户信息
     * @param unitCode
     * @return
     */
    List<? extends IUserUnit> listUnitUsers(String unitCode);
    /*---------------------------------------------------------------
	下面一组 get*Repo的方法，在使用数据库持久化的项目中需要缓存
	--------------------------------------------------------------*/
    /**
     * 获取机构代码映射表
     * @return
     */
    Map<String, ? extends IUnitInfo> getUnitRepo();
    
    /**
     * 获取部门编码映射表
     * @return
     */
    Map<String, ? extends IUserInfo> getUserRepo();
    
    /**
     * 获取机构代码映射表
     * @return
     */
    Map<String, ? extends IUserInfo> getLoginNameRepo();
    
    /**
     * 获取部门编码映射表
     * @return
     */
    Map<String, ? extends IUnitInfo> getDepNoRepo();
	/**
	 * 获取所有角色信息
	 * @return
	 */
	 Map<String, ? extends IRoleInfo> getRoleRepo();
	

	/**
	 * 获取业务信息
	 * @return
	 */
	Map<String, ? extends IOptInfo> getOptInfoRepo();
	
	/**
	 * 获取操作方法信息
	 * @return
	 */
	Map<String, ? extends IOptMethod> getOptMethodRepo();
	
	/**
	 * 获取所有数据字典类别信息
	 * @return
	 */
	List<? extends IDataCatalog> listAllDataCatalogs();
	
	
	/**
	 * 获取所有数据字典类别信息
	 * @return
	 */
	List<? extends IDataDictionary> listDataDictionaries(String catalogCode);
	
	
	/**
	 * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
	 * @param loginName
	 * @return
	 */
	CentitUserDetails loadUserDetailsByLoginName(String loginName);
	/**
	 * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
	 * @param userCode
	 * @return
	 */
	CentitUserDetails loadUserDetailsByUserCode(String userCode);
	/**
	 * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
	 * @param regEmail
	 * @return
	 */
	CentitUserDetails loadUserDetailsByRegEmail(String regEmail);
	/**
	 * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
	 * @param regCellPhone
	 * @return
	 */
	CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone);
}
