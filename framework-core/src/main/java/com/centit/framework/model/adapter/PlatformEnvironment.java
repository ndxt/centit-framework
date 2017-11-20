package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;

import java.util.List;
import java.util.Map;

public interface PlatformEnvironment {

    /**
     * 刷新数据字典
     * @return 是否刷新
     */
    boolean reloadDictionary();
    /**
     * 刷新权限相关的元数据
     * @return 是否刷新
     */
    boolean reloadSecurityMetadata();
    /**
     * 获取系统配置参数
     * @param paramCode paramCode
     * @return  系统配置参数
     */
    String getSystemParameter(String paramCode);
    /**
     * 获得用户设置参数
     * @param userCode userCode
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    String getUserSetting(String userCode,String paramCode);

    /**
     * 获取用户所有菜单功能
     * @param userCode userCode
     * @param asAdmin 是否是作为管理员
     * @return  List 用户所有菜单功能
     */
    List<? extends IOptInfo> listUserMenuOptInfos(String userCode,boolean asAdmin);

    /**
     * 获取用户所有菜单功能
     * @param userCode userCode
     * @param superOptId superOptId
     * @param asAdmin 是否是作为管理员
     * @return List 用户所有菜单功能
     */
    List<? extends IOptInfo> listUserMenuOptInfosUnderSuperOptId(
            String userCode,String superOptId,boolean asAdmin);

    /**
     * 获取用户所有角色
     * @param userCode 用户代码
     * @return  List 用户所有菜单功能
     */
    List<? extends IRoleInfo> listUserRoles(String userCode);

    /**
     * 获取拥有改角色的所有用户
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    List<? extends IUserInfo> listRoleUsers(String roleCode);
    /**
     * 根据用户代码获取用户信息，
     * @param userCode userCode
     * @return 用户信息
     */
    IUserInfo getUserInfoByUserCode(String userCode);

    /**
     * 根据登录名获取用户信息，
     * @param  loginName loginName
     * @return 登录名获取用户信息
     */
    IUserInfo getUserInfoByLoginName(String loginName);

    /**
     * 根据用户代码获取用户信息，
     * @param unitCode unitCode
     * @return 用户信息
     */
    IUnitInfo getUnitInfoByUnitCode(String unitCode);
    /**
     * 修改用户密码
     * @param userCode userCode
     * @param userPassword userPassword
     */
    void changeUserPassword(String userCode,String userPassword);

    /**
     * 验证用户密码
     * @param userCode userCode
     * @param userPassword userPassword
     * @return 验证结果
     */
    boolean checkUserPassword(String userCode,String userPassword);
    /**
     * 获取所有用户，
     * @return List 所有用户
     */
    List<? extends IUserInfo> listAllUsers();

    /**
     * 获取所有机构
     * @return List 所有机构
     */
    List<? extends IUnitInfo> listAllUnits();

    /**
     * 获取所有用户和机构关联关系
     * @return List 所有用户和机构关联关系
     */
    List<? extends IUserUnit> listAllUserUnits();

    /**
     * 根据用户代码获得 用户所有的机构信息
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    List<? extends IUserUnit> listUserUnits(String userCode);

    /**
     * 根据机构代码获得 机构所有用户信息
     * @param unitCode unitCode
     * @return  List 机构所有用户信息
     */
    List<? extends IUserUnit> listUnitUsers(String unitCode);
    /*---------------------------------------------------------------
    下面一组 get*Repo的方法，在使用数据库持久化的项目中需要缓存
    --------------------------------------------------------------*/
    /**
     * 获取机构代码映射表
     * @return Map 机构代码映射表
     */
    Map<String, ? extends IUnitInfo> getUnitRepo();
    
    /**
     * 获取部门编码映射表
     * @return map 部门编码映射表
     */
    Map<String, ? extends IUserInfo> getUserRepo();
    
    /**
     * 获取用户登陆名映射表
     * @return Map 机构代码映射表
     */
    Map<String, ? extends IUserInfo> getLoginNameRepo();
    
    /**
     * 获取部门编码映射表
     * @return Map 部门编码映射表
     */
    Map<String, ? extends IUnitInfo> getDepNoRepo();
    /**
     * 获取所有角色信息
     * @return Map 所有角色信息
     */
     Map<String, ? extends IRoleInfo> getRoleRepo();


    /**
     * 获取业务信息
     * @return Map 业务信息
     */
    Map<String, ? extends IOptInfo> getOptInfoRepo();

    /**
     * 获取操作方法信息
     * @return Map 操作方法信息
     */
    Map<String, ? extends IOptMethod> getOptMethodRepo();

    /**
     * 获取所有数据字典类别信息
     * @return List 所有数据字典类别信息
     */
    List<? extends IDataCatalog> listAllDataCatalogs();


    /**
     * 获取所有数据字典类别信息
     * @param catalogCode catalogCode
     * @return List 所有数据字典类别信息
     */
    List<? extends IDataDictionary> listDataDictionaries(String catalogCode);


    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     * @param loginName loginName
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByLoginName(String loginName);
    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     * @param userCode userCode
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByUserCode(String userCode);
    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     * @param regEmail regEmail
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByRegEmail(String regEmail);
    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     * @param regCellPhone regCellPhone
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone);
}
