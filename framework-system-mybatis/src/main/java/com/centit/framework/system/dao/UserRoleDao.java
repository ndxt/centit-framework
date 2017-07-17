package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.FVUserRoles;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.system.po.UserRoleId;

@Repository
public interface UserRoleDao{
	
	public void saveNewObject(UserRole dbUserRole);
	
	public List<UserRole> listObjects();
	
	public void mergeObject(UserRole dbUserRole);
	
	public void deleteObject(UserRole dbUserRole);
	
	public void deleteObjectById(UserRoleId id);
	
	public UserRole getObjectById(UserRoleId id);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<UserRole>  pageQuery(Map<String, Object> pageQureyMap);
	
	//DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.roleCode = ?", roid);
    public void deleteByRoleId(String roid);
    
    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.userCode = ?", usid);
    public void deleteByUserId(String usid);
    
    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM UserRole WHERE id.userCode = '"+userCode+"' and id.roleCode= '"+roleCode+"'");
    //参数 String roleCode,String userCode
    public void deleteByRoleCodeAndUserCode(Map map);
    
    /**
     * List<RoleInfo> roleInfos = new ArrayList<>();
        //所有的用户 都要添加这个角色
        roleInfos.add(new RoleInfo("G-public", "general public","G",
        		"G","T", "general public"));        
        final String sSqlsen = "from FVUserRoles v where userCode = ?";
     * @param usid
     * @return
     */
    public List<FVUserRoles> getSysRolesByUserId(String usid);
    
    
    
    
    
    
    
    /**
     *  "FROM UserRole ur where ur.id.userCode = ? and ur.id.roleCode like ?"
                + "and ur.id.obtainDate <= ? and (ur.secedeDate is null or ur.secedeDate > ?) "
                + "ORDER BY obtainDate,secedeDate";
     * @param usid
     * @param rolePrefix
     * @return
     * 参数String usid, String rolePrefix
     */
    public List<UserRole> getUserRolesByUserId(Map map);

    /**
     * "FROM UserRole ur where ur.id.userCode=? and ur.id.roleCode like ? "
                + "ORDER BY obtainDate,secedeDate";
     * @param usid
     * @param rolePrefix
     * @return
     */
    public List<UserRole> getAllUserRolesByUserId(String usid, String rolePrefix);
    /**
     *  "FROM UserRole ur where ur.id.userCode=? and ur.id.roleCode = ? " +
             "ORDER BY obtainDate,secedeDate";

     * @param userCode
     * @param rolecode
     * @return
     * 参数 String userCode, String rolecode
     */
    public UserRole getValidUserRole(Map map);
}
