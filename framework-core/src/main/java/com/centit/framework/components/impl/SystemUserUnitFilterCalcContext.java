package com.centit.framework.components.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.basedata.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext
    implements UserUnitFilterCalcContext {

    public SystemUserUnitFilterCalcContext(){
        super();
    }

    @Override
    public List<? extends IUserInfo> listAllUserInfo(String topUnit){
        return CodeRepositoryUtil.listAllUsers(topUnit);
    }

    @Override
    public List<? extends IUnitInfo> listAllUnitInfo(String topUnit) {
        return CodeRepositoryUtil.listAllUnits(topUnit);
    }

    @Override
    public List<? extends IUnitInfo> listSubUnit(String unitCode) {
        return CodeRepositoryUtil.getSubUnits(unitCode);
    }

    @Override
    public IUnitInfo getUnitInfoByCode(String unitCode) {
        return CodeRepositoryUtil.getUnitInfoByCode(unitCode);
    }

    @Override
    public List<? extends IUserUnit> listAllUserUnits(String topUnit) {
        //return CodeRepositoryUtil.listAllUserUnits(topUnit);
        return null;
    }

    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryUtil.listUnitUsers(unitCode);
    }

    @Override
    public List<? extends IUserUnit> listUserUnits(String userCode){
        return CodeRepositoryUtil.listUserUnits(userCode);
    }

    @Override
    public List<? extends IUserRole> listUserRoles(String userCode) {
        return CodeRepositoryUtil.listUserRoles(userCode);
    }

    @Override
    public List<? extends IUserRole> listRoleUsers(String roleCode) {
        return CodeRepositoryUtil.listRoleUsers(roleCode);
    }

    @Override
    public Map<String, String> listAllSystemRole() {
        List<? extends IRoleInfo> roles = CodeRepositoryUtil.listAllRole();
        Map<String, String> roleMap = new HashMap<>();
        if(roles!=null){
            for(IRoleInfo r: roles){
                roleMap.put(r.getRoleCode(), r.getRoleName());
            }
        }
        return roleMap;
    }

    @Override
    public Map<String, String> listAllProjectRole() {
        return CodeRepositoryUtil.getLabelValueMap("FlowUserRole");
    }

    @Override
    public Map<String, String> listAllStation() {
        return CodeRepositoryUtil.getLabelValueMap("StationType");
    }

    @Override
    public Map<String, String> listAllRank() {
        return CodeRepositoryUtil.getLabelValueMap("RankType");
    }

    @Override
    public IUserInfo getUserInfoByCode(String userCode) {
        return CodeRepositoryUtil.getUserInfoByCode(userCode);
    }

    /**
     * 从数据字典中获取 Rank 的等级
     *
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", rankCode);
        if(dd!=null)
            return Integer.valueOf(dd.getExtraCode());
        return IUserUnit.MAX_XZ_RANK;
    }
}
