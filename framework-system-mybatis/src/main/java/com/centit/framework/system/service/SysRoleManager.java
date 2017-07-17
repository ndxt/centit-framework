package com.centit.framework.system.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.RolePower;
import com.centit.framework.system.po.VOptTree;

public interface SysRoleManager{
	
	public List<RoleInfo> listObjects(Map<String, Object> filterMap);

	public List<RoleInfo> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	
	public RoleInfo getObjectById(String roleCode);
	
    public List<RolePower> getRolePowers(String rolecode); // 角色操作权限

    public List<RolePower> getRolePowersByDefCode(String optCode);

    public List<VOptTree> getVOptTreeList();// 获取菜单TREE

    public void loadRoleSecurityMetadata();

    public Serializable saveNewRoleInfo(RoleInfo o);

    public void updateRoleInfo(RoleInfo o);
    
    public void deleteRoleInfo(String roleCode);
    
    public RoleInfo getRoleInfo(String roleCode);
    
    public List<RolePower> listAllRolePowers();

    public List<OptMethod> listAllOptMethods();

    public int countRoleUserSum(String roleCode);

}
