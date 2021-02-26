package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;

import java.util.List;

public interface PlatformEnvironment {

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
    List<? extends IUserRole> listUserRoles(String userCode);

    /**
     * 获取拥有该角色的所有用户
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    List<? extends IUserRole> listRoleUsers(String roleCode);

    /**
     * 获取用户所有角色
     * @param unitCode 机构代码
     * @return  List 用户所有菜单功能
     */
    List<? extends IUnitRole> listUnitRoles(String unitCode);

    /**
     * 获取拥有该角色的所有用户
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    List<? extends IUnitRole> listRoleUnits(String roleCode);

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
    List<? extends IUserInfo> listAllUsers(String topUnit);

    /**
     * 获取所有机构
     * @return List 所有机构
     */
    List<? extends IUnitInfo> listAllUnits(String topUnit);

    /*
     * 获取机构信息
     * @param unitCode 机构主键
     * @return 机构信息

     IUnitInfo getUnitrInfo(String unitCode);
    */
    /**
     * 获取所有用户和机构关联关系
     * @return List 所有用户和机构关联关系
     */
    List<? extends IUserUnit> listAllUserUnits(String topUnit);

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

    /**
     * 获取所有角色信息
     * @return List 操作方法信息
     */
    List<? extends IRoleInfo> listAllRoleInfo();

    /**
     * 获取所有角色和权限对应关系
     * @return List 操作方法信息
     */
    List<? extends IRolePower> listAllRolePower();


    /**
     * 获取业务操作信息
     * @return List 业务信息
     */
    List<? extends IOptInfo> listAllOptInfo();

    /**
     * 获取操作方法信息
     * @return List 操作方法信息
     */
    List<? extends IOptMethod> listAllOptMethod();

    /**
     * @return 所有的数据范围定义表达式
     */
    List<? extends IOptDataScope> listAllOptDataScope();
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

    /*
     * 获取用户信息
     * @param userCode 用户主键
     * @return 用户信息
     *
      IUserInfo getUserInfo(String userCode);
    */
    /**
     * 根据用户ID修改用户信息
     * @param userInfo 用户信息
     */
    void updateUserInfo(IUserInfo userInfo);

    /**
     * 获得用户设置参数
     * @param userCode 用户编码
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    IUserSetting getUserSetting(String userCode,String paramCode);

    /**
     * 获取全部个人设置
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    List<? extends IUserSetting> listUserSettings(String userCode);

    /**
     * 设置用户参数
     * @param userSetting 用户参数， paramValue = null 则为删除
     */
    void saveUserSetting(IUserSetting userSetting);

    /**
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    void insertOrUpdateMenu(List<? extends IOptInfo> optInfos,
                            List<? extends IOptMethod> optMethods);

    /**
     * 获取所有注册的业务系统
     * @return 所有注册的业务系统
     */
    List<? extends IOsInfo> listOsInfos();
}

