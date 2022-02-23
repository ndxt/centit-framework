package com.centit.framework.components.impl;

import com.centit.framework.common.GlobalConstValue;
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
    public List<? extends IUserInfo> listAllUserInfo(){
        return CodeRepositoryUtil.listAllUsers(this.getTopUnit());
    }

    @Override
    public List<? extends IUnitInfo> listAllUnitInfo() {
        return CodeRepositoryUtil.listAllUnits(this.getTopUnit());
    }

    @Override
    public List<? extends IUnitInfo> listSubUnit(String unitCode) {
        return CodeRepositoryUtil.getSubUnits(this.getTopUnit(), unitCode);
    }

    @Override
    public IUnitInfo getUnitInfoByCode(String unitCode) {
        return CodeRepositoryUtil.getUnitInfoByCode(this.getTopUnit(), unitCode);
    }

    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        return CodeRepositoryUtil.listAllUserUnits(this.getTopUnit());
    }

    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryUtil.listUnitUsers(unitCode);
    }

    @Override
    public List<? extends IUserUnit> listUserUnits(String userCode){
        return CodeRepositoryUtil.listUserUnits(this.getTopUnit(), userCode);
    }

    @Override
    public List<? extends IUserRole> listUserRoles(String userCode) {
        return CodeRepositoryUtil.listUserRoles(this.getTopUnit(), userCode);
    }

    @Override
    public List<? extends IUserRole> listRoleUsers(String roleCode) {
        return CodeRepositoryUtil.listRoleUsers(this.getTopUnit(), roleCode);
    }

    @Override
    public Map<String, String> listAllSystemRole() {
        List<? extends IRoleInfo> roles = CodeRepositoryUtil.listAllRole(this.getTopUnit());
        Map<String, String> roleMap = new HashMap<>();
        if(roles!=null){
            for(IRoleInfo r: roles){
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
    public IUserInfo getUserInfoByCode(String userCode) {
        return CodeRepositoryUtil.getUserInfoByCode(this.getTopUnit(), userCode);
    }

    /**
     * 从数据字典中获取 Rank 的等级
     *
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        IDataDictionary dd = CodeRepositoryUtil.getDataPiece(GlobalConstValue.DATA_CATALOG_RANK, rankCode,null);
        if(dd!=null)
            return Integer.valueOf(dd.getExtraCode());
        return IUserUnit.MAX_XZ_RANK;
    }
}
