package com.centit.framework.components.impl;

import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemUserUnitFilterCalcContext
    extends AbstractUserUnitFilterCalcContext {

    public SystemUserUnitFilterCalcContext(String topUnit){
        super(topUnit);
    }

    @Override
    public List<UserInfo> listAllUserInfo(){
        return CodeRepositoryUtil.listAllUsers(this.getTopUnit());
    }

    @Override
    public List<UnitInfo> listAllUnitInfo() {
        return CodeRepositoryUtil.listAllUnits(this.getTopUnit());
    }

    @Override
    public List<UnitInfo> listSubUnit(String unitCode) {
        return CodeRepositoryUtil.getSubUnits(this.getTopUnit(), unitCode);
    }

    @Override
    public UnitInfo getUnitInfoByCode(String unitCode) {
        return CodeRepositoryUtil.getUnitInfoByCode(this.getTopUnit(), unitCode);
    }

    @Override
    public List<UserUnit> listAllUserUnits() {
        return CodeRepositoryUtil.listAllUserUnits(this.getTopUnit());
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryUtil.listUnitUsers(unitCode);
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode){
        return CodeRepositoryUtil.listUserUnits(this.getTopUnit(), userCode);
    }

    @Override
    public List<UserRole> listUserRoles(String userCode) {
        return CodeRepositoryUtil.listUserRoles(this.getTopUnit(), userCode);
    }

    @Override
    public List<UserRole> listRoleUsers(String roleCode) {
        return CodeRepositoryUtil.listRoleUsers(this.getTopUnit(), roleCode);
    }

    @Override
    public Map<String, String> listAllSystemRole() {
        List<RoleInfo> roles = CodeRepositoryUtil.listAllRole(this.getTopUnit());
        Map<String, String> roleMap = new HashMap<>();
        if(roles!=null){
            for(RoleInfo r: roles){
                roleMap.put(r.getRoleCode(), r.getRoleName());
            }
        }
        return roleMap;
    }

    @Override
    public Map<String, String> listAllStation() {
        //TODO 适配 多租户
        return CodeRepositoryUtil.getLabelValueMap(GlobalConstValue.DATA_CATALOG_STATION);
    }

    @Override
    public Map<String, String> listAllRank() {
        return CodeRepositoryUtil.getLabelValueMap(GlobalConstValue.DATA_CATALOG_RANK);
    }

    @Override
    public UserInfo getUserInfoByCode(String userCode) {
        return CodeRepositoryUtil.getUserInfoByCode(this.getTopUnit(), userCode);
    }

}
