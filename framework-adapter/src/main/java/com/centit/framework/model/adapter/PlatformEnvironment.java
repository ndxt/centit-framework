package com.centit.framework.model.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.model.security.OptTreeNode;
import com.centit.support.database.utils.PageDesc;
import org.springframework.security.access.ConfigAttribute;

import java.util.List;
import java.util.Map;

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
    List<UserInfo> listAllUsers(String topUnit);

    //List<UserInfo> listUsersByProperties(Map<String, Object> filters, String topUnit);

    /**
     * 获取当前租户的所有机构
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
     * @return List 所有机构
     */
    List<UnitInfo> listAllUnits(String topUnit);

    //List<UnitInfo> listUnitsByProperties(Map<String, Object> filters, String topUnit);

    /**
     * 获取租户下所有用户和机构关联关系
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
     * @return List 所有用户和机构关联关系
     */
    List<UserUnit> listAllUserUnits(String topUnit);


    /**
     * 根据用户代码获得 用户的所有租户，顶级机构
     *
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    List<UnitInfo> listUserTopUnits(String userCode);

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     *                 通过 unitpath来过滤
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    List<UserUnit> listUserUnits(String topUnit, String userCode);


    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return List 机构所有用户信息
     */
    List<UserUnit> listUnitUsers(/*String topUnit, */String unitCode);

    /*权限相关的*/


    /**
     * 获取当前租户下的所有应用
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     *                通过 unitpath来过滤
     * @return 所有注册的业务系统
     */
    List<OsInfo> listOsInfos(String topUnit);

    /**
     * 获取应用信息
     * @param osId 应用ID， applicationId
     * @return 应用基本信息
     */
    OsInfo getOsInfo(String osId);

    OsInfo deleteOsInfo(String osId);

    OsInfo updateOsInfo(OsInfo osInfo);;

    OsInfo addOsInfo(OsInfo osInfo);

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
    List<OptInfo> listUserMenuOptInfosUnderSuperOptId(
        String userCode, String superOptId, boolean asAdmin);

    List<OptInfo> listMenuOptInfosUnderOsId(String osId);

    OptInfo addOptInfo(OptInfo optInfo);

    OptInfo updateOptInfo(OptInfo optInfo);


    /**
     * 获取租户下用户所有角色
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     * @param userCode 用户代码
     * @return List 用户所有菜单功能
     */
    List<UserRole> listUserRoles(String topUnit, String userCode);

    /**
     * 获取租户下拥有该角色的所有用户
     *
     * @param topUnit  租户代码，对应f_unitinfo表中最顶层的机构代码
     *                 如果 topUnit = 'all' 返回所有机构
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    List<UserRole> listRoleUsers(String topUnit, String roleCode);

    /**
     * 获取用户所有角色
     *
     * @param unitCode 机构代码
     * @return List 用户所有菜单功能
     */
    List<UnitRole> listUnitRoles(String unitCode);

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    List<UnitRole> listRoleUnits(String roleCode);


    /**
     * 获取租户下所有角色信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<RoleInfo> listAllRoleInfo(String topUnit);

    /**
     * 获取租户下所有角色和权限对应关系
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<RolePower> listAllRolePower(String topUnit);

    /**
     * 获取租户下业务操作信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 业务信息
     */
    List<OptInfo> listAllOptInfo(String topUnit);

    /**
     * 根据角色code获取操作信息
     *
     * @param roleCode
     * @return
     */
    List<OptInfo> listOptInfoByRole(String roleCode);
    /**
     * 获取租户下操作方法信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 操作方法信息
     */
    List<OptMethod> listAllOptMethod(String topUnit);

    OptTreeNode getSysOptTree();

    /**
     * 根据角色code获取操作方法信息信息
     *
     * @param roleCode
     * @return
     */
    List<OptMethod> listOptMethodByRoleCode(String roleCode);

    /**
     * 添加业务操作
     * @param optMethod 添加业务操作
     * @return 更改后的对象
     */
    OptMethod addOptMethod(OptMethod optMethod);

    /**
     * 修改业务操作
     * @param optMethod 业务操作
     * @return 更改后的对象
     */
    OptMethod mergeOptMethod(OptMethod optMethod);

    /**
     * 删除操作
     * 根据optcode 删除optdef表 和 f_rolepower表数据
     */
    void deleteOptMethod(String optCode);

    /**
     * 根据角色code获取操作方法信息信息
     *
     * @param apiId
     * @return
     */
    List<ConfigAttribute> getRolesWithApiId(String apiId);

    /**
     * 获取租户下所有的数据范围定义表达式
     *
     * @param superOptId
     * @return 所有的数据范围定义表达式
     */
    List<OptDataScope> listAllOptDataScope(String superOptId);

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

    /**
     * 从源头获取机构信息，不是从缓存中获取
     * @param unitCode 机构的代码
     * @return 机构信息
     */
    UnitInfo loadUnitInfo(String unitCode);
    /*
     * 获取用户信息
     * @param userCode 用户主键
     * @return 用户信息
     *
      UserInfo getUserInfo(String userCode);
    */

    /**
     * 根据用户ID修改用户信息
     *
     * @param userInfo 用户信息
     */
    void updateUserInfo(UserInfo userInfo);

    /**
     * 获得用户设置参数
     *
     * @param userCode  用户编码
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    UserSetting getUserSetting(String userCode, String paramCode);

    /**
     * 获取全部个人设置
     *
     * @param userCode 用户编码
     * @return 个人设置列表
     */
    List<UserSetting> listUserSettings(String userCode);

    /**
     * 设置用户参数
     *
     * @param userSetting 用户参数， paramValue = null 则为删除
     */
    void saveUserSetting(UserSetting userSetting);

    /*
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    /*
    void insertOrUpdateMenu(List<OptInfo> optInfos,
                            List<IOptMethod> optMethods);*/

    /*数据字典相关接口*/

    /**
     * 获取租户下所有数据字典类别信息
     *
     * @param topUnit 租户代码，对应f_unitinfo表中最顶层的机构代码
     *                如果 topUnit = 'all' 返回所有机构
     * @return List 所有数据字典类别信息
     */
    List<DataCatalog> listAllDataCatalogs(String topUnit);

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return List 所有数据字典类别信息
     */
    List<DataDictionary> listDataDictionaries(String catalogCode);

    /**
     * 根据catalogCode删除数据字典以及子项
     * @param catalogCode
     * @return
     */
    void deleteDataDictionary(String catalogCode);

    /**
     * 操作定义所属业务模块（页面删除菜单时需要先将设计好的数据更新到其它业务模块下）
     *
     * @param optId    新业务模块id
     * @param optCodes 需要更新数据的主键集合
     * @return
     */
    int[] updateOptIdByOptCodes(String optId, List<String> optCodes);

    /**
     * 根据optId删除业务模块信息
     *
     * @param optId
     * @return
     */
    boolean deleteOptInfoByOptId(String optId);

    List<WorkGroup> listWorkGroup(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 批量保存用户组； 这个名字怎么起的，sign
     * @param workGroups 用户组列表
     */
    void batchWorkGroup(List<WorkGroup> workGroups);

    boolean loginUserIsExistWorkGroup(String osId, String userCode);

    /**
     * 根据topUnit获取租户基本信息，
     * TODO 需要添加 现有的资源占有量，用于平台判断是否可以增加资源
     * @param topUnit 租户code
     * @return 获取TenantInfo
     */
    JSONObject getTenantInfoByTopUnit(String topUnit);

    /**
     * 获取用户相关的租户和工作组信息
     * @param userCode 用户code
     */
    JSONObject fetchUserTenantGroupInfo(String userCode, String topUnit);
}

