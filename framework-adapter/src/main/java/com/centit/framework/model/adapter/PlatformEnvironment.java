package com.centit.framework.model.adapter;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;

import java.util.List;

public interface PlatformEnvironment {

    /*用户组织和机构相关接口*/

    /**
     * 修改用户密码
     *
     * @param userCode     userCode
     * @param userPassword userPassword
     */
    void changeUserPassword(String userCode, String userPassword);

    /**
     * 验证用户密码
     *
     * @param userCode     userCode
     * @param userPassword userPassword
     * @return 验证结果
     */
    boolean checkUserPassword(String userCode, String userPassword);

    /**
     * 获取当前租户的所有用户
     *
     * @return List 所有用户
     */
    List<? extends IUserInfo> listAllUsers(String topUnit);

    /**
     * 获取当前租户的所有机构
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
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
     * 获取租户下所有用户和机构关联关系
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
     * @return List 所有用户和机构关联关系
     */
    List<? extends IUserUnit> listAllUserUnits(String topUnit);


    /**
     * 根据用户代码获得 用户的所有租户，顶级机构
     *
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    List<? extends IUnitInfo> listUserTopUnits(String userCode);

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     *                 通过 unitpath来过滤
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    List<? extends IUserUnit> listUserUnits(String topUnit, String userCode);


    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return List 机构所有用户信息
     */
    List<? extends IUserUnit> listUnitUsers(/*String topUnit, */String unitCode);

    /*权限相关的*/


    /**
     * 获取当前租户下的所有应用
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
     * @return 所有注册的业务系统
     */
    List<? extends IOsInfo> listOsInfos(String topUnit);

    default IOsInfo getOsInfo(String osId) {
        return null;
    }

    default IOsInfo deleteOsInfo(String osId) {
        return null;
    }

    default IOsInfo updateOsInfo(IOsInfo osInfo) {
        return null;
    }

    default IOsInfo addOsInfo(IOsInfo osInfo) {
        return null;
    }

    /*
     * 获取用户所有菜单功能; 获取某个应用的菜单
     * @param userCode userCode
     * @param asAdmin 是否是作为管理员
     * @return  List 用户所有菜单功能
     * 这个接口可以废弃调，用 listUserMenuOptInfosUnderSuperOptId 代替
     */
    /*@Deprecated
    List<? extends IOptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin);
*/

    /**
     * 获取租户下用户所有菜单功能
     *
     * @param userCode   userCode
     * @param superOptId 应用id 对应osinfo的osid 或者 对应paas平台的applicationId，
     *                   对应菜单树中根菜单（顶层菜单）的optId
     *                   如果 superOptId = 'all'  返回所有的
     * @param asAdmin    是否是作为管理员
     * @return List 用户所有菜单功能
     */
    List<? extends IOptInfo> listUserMenuOptInfosUnderSuperOptId(
        String userCode, String superOptId, boolean asAdmin);

    default List<? extends IOptInfo> listMenuOptInfosUnderOsId(String osId) {
        return null;
    }

    default IOptInfo addOptInfo(IOptInfo optInfo) {
        return null;
    }
    default IOptInfo updateOptInfo(IOptInfo optInfo) {
        return null;
    }
    default IOptMethod addOptMethod(IOptMethod optMethod){
        return null;
    }

    /**
     * 获取租户下用户所有角色
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     * @param userCode 用户代码
     * @return List 用户所有菜单功能
     */
    List<? extends IUserRole> listUserRoles(String topUnit, String userCode);

    /**
     * 获取租户下拥有该角色的所有用户
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    List<? extends IUserRole> listRoleUsers(String topUnit, String roleCode);

    /**
     * 获取用户所有角色
     *
     * @param unitCode 机构代码
     * @return List 用户所有菜单功能
     */
    List<? extends IUnitRole> listUnitRoles(String unitCode);

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    List<? extends IUnitRole> listRoleUnits(String roleCode);


    /**
     * 获取租户下所有角色信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<? extends IRoleInfo> listAllRoleInfo(String topUnit);

    /**
     * 获取租户下所有角色和权限对应关系
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<? extends IRolePower> listAllRolePower(String topUnit);


    /**
     * 获取租户下业务操作信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 业务信息
     */
    List<? extends IOptInfo> listAllOptInfo(String topUnit);

    /**
     * 获取租户下操作方法信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<? extends IOptMethod> listAllOptMethod(String topUnit);

    default IOptMethod addOptMethod(JSONObject optMethod) {
        return null;
    }

    /**
     * 获取租户下所有的数据范围定义表达式
     *
     * @param superOptId
     * @return 所有的数据范围定义表达式
     */
    List<? extends IOptDataScope> listAllOptDataScope(String superOptId);

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param loginName loginName
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByLoginName(String loginName);

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param userCode userCode
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByUserCode(String userCode);

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regEmail regEmail
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    CentitUserDetails loadUserDetailsByRegEmail(String regEmail);

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
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
     *
     * @param userInfo 用户信息
     */
    void updateUserInfo(IUserInfo userInfo);

    /**
     * 获得用户设置参数
     *
     * @param userCode  用户编码
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    IUserSetting getUserSetting(String userCode, String paramCode);

    /**
     * 获取全部个人设置
     *
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    List<? extends IUserSetting> listUserSettings(String userCode);

    /**
     * 设置用户参数
     *
     * @param userSetting 用户参数， paramValue = null 则为删除
     */
    void saveUserSetting(IUserSetting userSetting);

    /*
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    /*
    void insertOrUpdateMenu(List<? extends IOptInfo> optInfos,
                            List<? extends IOptMethod> optMethods);*/

    /*数据字典相关接口*/

    /**
     * 获取租户下所有数据字典类别信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 所有数据字典类别信息
     */
    List<? extends IDataCatalog> listAllDataCatalogs(String topUnit);

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return List 所有数据字典类别信息
     */
    List<? extends IDataDictionary> listDataDictionaries(String catalogCode);


}

