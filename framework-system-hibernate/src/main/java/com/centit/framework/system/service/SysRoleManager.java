package com.centit.framework.system.service;

import java.io.Serializable;
import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.RolePower;
import com.centit.framework.system.po.VOptTree;

public interface SysRoleManager extends BaseEntityManager<RoleInfo, String> {

     List<RolePower> getRolePowers(String rolecode); // 角色操作权限

    List<RolePower> getRolePowersByDefCode(String optCode);

     List<VOptTree> getVOptTreeList();// 获取菜单TREE

     void loadRoleSecurityMetadata();

     Serializable saveNewRoleInfo(RoleInfo o);

     void updateRoleInfo(RoleInfo o);
    
     void deleteRoleInfo(String roleCode);
    
     RoleInfo getRoleInfo(String roleCode);
    
     List<RolePower> listAllRolePowers();

     List<OptMethod> listAllOptMethods();
    /**
     * 对角色信息进行模糊搜索，适用于带搜索条件的下拉框。
     *
     * @param key      搜索条件
     * @param field    需要搜索的字段，如为空，默认，roleCode,roleName
     * @return   List RoleInfo
     */
     List<RoleInfo> search(String key, String[] field);

     int countRoleUserSum(String roleCode);
}
