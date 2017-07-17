package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;

public interface SysUserManager{	

	public List<UserInfo> listObjects(Map<String, Object> filterMap);
	
	public List<UserInfo> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	
	public UserInfo getObjectById(String userCode);
	
	/**
     * getObjectByLoginName
     * @param userCode
     * @return
     */
    public UserInfo loadUserByLoginname(String userCode);
    
	// return super.getObjectByProperty("regEmail", regEmail);
    public UserInfo getUserByRegEmail(String regEmail);
    
    //return super.getObjectByProperty("regCellPhone", regCellPhone);
    public UserInfo getUserByRegCellPhone(String regCellPhone);
    
    
    // public Collection<GrantedAuthority> loadUserAuthorities(String username);

    public void resetPwd(String userCode);

    public void resetPwd(String[] userCodes);

    public void setNewPassword(String userID, String oldPassword, String newPassword);

    public void forceSetPassword(String userCode, String newPassword);
     /**
     * 保存用户信息，包括用户机构、用户角色信息
     * @param userinfo
     */
    public void saveNewUserInfo(UserInfo userinfo);
    
    public void updateUserInfo(UserInfo userinfo);
    
    public void updateUserProperities(UserInfo userinfo);
    
    
    public void deleteUserInfo(String userCode);
      
 
    public String getNextUserCode();

    public List<RoleInfo> listUserValidRoles(String userCode);
   
    
    public List<FVUserOptList> getAllOptMethodByUser(String userCode);
    public boolean checkIfUserExists(UserInfo user);

    public boolean checkUserPassword(String userCode, String oldPassword);

    public boolean isLoginNameExist(String userCode, String loginName);
    public boolean isCellPhoneExist(String userCode, String regPhone);
    public boolean isEmailExist(String userCode, String regEmail);
    public boolean isAnyOneExist(String userCode, String loginName,String regPhone,String regEmail);
}
