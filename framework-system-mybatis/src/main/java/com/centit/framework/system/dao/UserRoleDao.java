package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.FVUserRoles;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.system.po.UserRoleId;

@Repository
public interface UserRoleDao{
	
	 void saveNewObject(UserRole dbUserRole);
	
	 List<UserRole> listObjects();
	
	 void mergeObject(UserRole dbUserRole);
	
	 void deleteObject(UserRole dbUserRole);
	
	 void deleteObjectById(UserRoleId id);
	
	 UserRole getObjectById(UserRoleId id);
	
	
     int  pageCount(Map<String, Object> filterDescMap);
     List<UserRole>  pageQuery(Map<String, Object> pageQureyMap);
	
	//DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.roleCode = ?", roid);
     void deleteByRoleId(String roid);
    
    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.userCode = ?", usid);
     void deleteByUserId(String usid);
    
    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.userCode = '"+userCode+"' and id.roleCode= '"+roleCode+"'");
    //参数 String roleCode,String userCode
     void deleteByRoleCodeAndUserCode(Map map);
    
    /**
     * List roleInfos = new ArrayList();
        //所有的用户 都要添加这个角色
        roleInfos.add(new RoleInfo("G-", "general ","G",
        		"G","T", "general "));        
        final String sSqlsen = "from FVUserRoles v where userCode = ?";
     * @param usid usid
     * @return List FVUserRoles
     */
     List<FVUserRoles> getSysRolesByUserId(String usid);
    
    
    
    
    
    
    
    /**
     *  "FROM UserRole ur where ur.id.userCode = ? and ur.id.roleCode like ?"
                + "and ur.id.obtainDate &lt;= ? and (ur.secedeDate is null or ur.secedeDate &gt; ?) "
                + "ORDER BY obtainDate,secedeDate";
     * @param map map
     * @return List UserRole
     * 参数String usid, String rolePrefix
     */
     List<UserRole> getUserRolesByUserId(Map map);

    /**
     * "FROM UserRole ur where ur.id.userCode=? and ur.id.roleCode like ? "
                + "ORDER BY obtainDate,secedeDate";
     * @param usid usid
     * @param rolePrefix rolePrefix
     * @return List UserRole
     */
     List<UserRole> getAllUserRolesByUserId(String usid, String rolePrefix);
    /**
     *  "FROM UserRole ur where ur.id.userCode=? and ur.id.roleCode = ? " +
             "ORDER BY obtainDate,secedeDate";

     * @param map map
     * @return UserRole
     * 参数 String userCode, String rolecode
     */
     UserRole getValidUserRole(Map map);
}
