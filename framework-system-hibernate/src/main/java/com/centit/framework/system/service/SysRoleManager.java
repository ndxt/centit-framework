package com.centit.framework.system.service;

import java.io.Serializable;
import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.RolePower;
import com.centit.framework.system.po.VOptTree;

public interface SysRoleManager extends BaseEntityManager<RoleInfo, String> {

    public List<RolePower> getRolePowers(String rolecode); // 角色操作权限

    List<RolePower> getRolePowersByDefCode(String optCode);

    public List<VOptTree> getVOptTreeList();// 获取菜单TREE

    public void loadRoleSecurityMetadata();

    public Serializable saveNewRoleInfo(RoleInfo o);

    public void updateRoleInfo(RoleInfo o);
    
    public void deleteRoleInfo(String roleCode);
    
    public RoleInfo getRoleInfo(String roleCode);
    
    public List<RolePower> listAllRolePowers();

    public List<OptMethod> listAllOptMethods();
    /**
     * 对角色信息进行模糊搜索，适用于带搜索条件的下拉框。
     *
     * @param key      搜索条件
     * @param field    需要搜索的字段，如为空，默认，roleCode,roleName
     */
    public List<RoleInfo> search(String key, String[] field);

    public int countRoleUserSum(String roleCode);
}
