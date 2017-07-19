package com.centit.framework.staticsystem.service.impl;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;

import java.util.List;
import java.util.Map;

/**
 * Created by codefan on 16-12-16.
 */
public class PlatformEnvironmentProxy implements PlatformEnvironment
{
    private List<PlatformEnvironment> evrnMangers;
    public PlatformEnvironmentProxy(){

    }

    public void setEvrnMangers(List<PlatformEnvironment> evrnMangers) {
        this.evrnMangers = evrnMangers;
    }


    /**
     * 刷新数据字典
     *
     * @return boolean 刷新数据字典
     */
    @Override
    public boolean reloadDictionary() {
        boolean res=true;
        for(PlatformEnvironment evrnManger:evrnMangers){
            if(!evrnManger.reloadDictionary())
                res = false;
        }
        return res;
    }

    /**
     * 刷新权限相关的元数据
     *
     * @return boolean 刷新权限相关的元数据
     */
    @Override
    public boolean reloadSecurityMetadata() {
        boolean res=true;
        for(PlatformEnvironment evrnManger:evrnMangers){
            if(!evrnManger.reloadSecurityMetadata())
                res = false;
        }
        return res;
    }


    /**
     * 获取系统配置参数
     *
     * @param paramCode paramCode
     * @return 系统配置参数
     */
    @Override
    public String getSystemParameter(String paramCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            String svalue = evrnManger.getSystemParameter(paramCode);
            if(svalue!=null)
                return svalue;
        }
        return null;
    }

    /**
     * 获得用户设置参数
     *
     * @param userCode userCode
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    @Override
    public String getUserSetting(String userCode, String paramCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            String svalue = evrnManger.getUserSetting(userCode,paramCode);
            if(svalue!=null)
                return svalue;
        }
        return null;
    }

    /**
     * 获取用户所有菜单功能
     *
     * @param userCode userCode
     * @param asAdmin  是否是作为管理员
     * @return 用户所有菜单功能
     */
    @Override
    public List<? extends IOptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value = evrnManger.listUserMenuOptInfos(userCode,asAdmin);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取用户所有菜单功能
     *
     * @param userCode userCode
     * @param superOptId superOptId
     * @param asAdmin    是否是作为管理员
     * @return 用户所有菜单功能
     */
    @Override
    public List<? extends IOptInfo> listUserMenuOptInfosUnderSuperOptId(
            String userCode, String superOptId, boolean asAdmin) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IOptInfo> value =
                    evrnManger.listUserMenuOptInfosUnderSuperOptId(userCode,superOptId,asAdmin);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 根据用户代码获取用户信息，
     *
     * @param userCode userCode
     * @return 用户信息，
     */
    @Override
    public IUserInfo getUserInfoByUserCode(String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            IUserInfo value = evrnManger.getUserInfoByUserCode(userCode);
            if(value!=null)
                return value;
        }
        return null;
    }

    @Override
    public IUnitInfo getUnitInfoByUnitCode(String unitCode){
        for(PlatformEnvironment evrnManger:evrnMangers){
            IUnitInfo value = evrnManger.getUnitInfoByUnitCode(unitCode);
            if(value!=null)
                return value;
        }
        return null;
    }
    /**
     * 根据登录名获取用户信息，
     *
     * @param loginName loginName
     * @return 用户信息，
     */
    @Override
    public IUserInfo getUserInfoByLoginName(String loginName) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            IUserInfo value = evrnManger.getUserInfoByLoginName(loginName);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 修改用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            evrnManger.changeUserPassword(userCode,userPassword);
        }
    }

    /**
     * 验证用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     * @return boolean 验证用户密码
     */
    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            if(evrnManger.checkUserPassword(userCode,userPassword))
                return true;
        }
        return false;
    }

    /**
     * 获取所有用户，
     *
     * @return 所有用户，
     */
    @Override
    public List<? extends IUserInfo> listAllUsers() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserInfo> value = evrnManger.listAllUsers();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取所有机构
     *
     * @return 所有机构
     */
    @Override
    public List<? extends IUnitInfo> listAllUnits() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUnitInfo> value = evrnManger.listAllUnits();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取所有用户和机构关联关系
     *
     * @return 所有用户和机构关联关系
     */
    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listAllUserUnits();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param userCode userCode
     * @return 用户所有的机构信息
     */
    @Override
    public List<? extends IUserUnit> listUserUnits(String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listUserUnits(userCode);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return 机构所有用户信息
     */
    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IUserUnit> value = evrnManger.listUnitUsers(unitCode);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取机构代码映射表
     *
     * @return 机构代码映射表
     */
    @Override
    public Map<String, ? extends IUnitInfo> getUnitRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IUnitInfo> value = evrnManger.getUnitRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return 部门编码映射表
     */
    @Override
    public Map<String, ? extends IUserInfo> getUserRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IUserInfo> value = evrnManger.getUserRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取机构代码映射表
     *
     * @return 机构代码映射表
     */
    @Override
    public Map<String, ? extends IUserInfo> getLoginNameRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IUserInfo> value = evrnManger.getLoginNameRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return 部门编码映射表
     */
    @Override
    public Map<String, ? extends IUnitInfo> getDepNoRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IUnitInfo> value = evrnManger.getDepNoRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取所有角色信息
     *
     * @return 所有角色信息
     */
    @Override
    public Map<String, ? extends IRoleInfo> getRoleRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IRoleInfo> value = evrnManger.getRoleRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取业务信息
     *
     * @return 业务信息
     */
    @Override
    public Map<String, ? extends IOptInfo> getOptInfoRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IOptInfo> value = evrnManger.getOptInfoRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取操作方法信息
     *
     * @return 操作方法信息
     */
    @Override
    public Map<String, ? extends IOptMethod> getOptMethodRepo() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            Map<String, ? extends IOptMethod> value = evrnManger.getOptMethodRepo();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @return 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataCatalog> listAllDataCatalogs() {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IDataCatalog> value = evrnManger.listAllDataCatalogs();
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataDictionary> listDataDictionaries(String catalogCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            List<? extends IDataDictionary> value = evrnManger.listDataDictionaries(catalogCode);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param loginName loginName
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByLoginName(loginName);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param userCode userCode
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByUserCode(userCode);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regEmail regEmail
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByRegEmail(regEmail);
            if(value!=null)
                return value;
        }
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regCellPhone regCellPhone
     * @return 用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        for(PlatformEnvironment evrnManger:evrnMangers){
            CentitUserDetails value = evrnManger.loadUserDetailsByRegCellPhone(regCellPhone);
            if(value!=null)
                return value;
        }
        return null;
    }
}
