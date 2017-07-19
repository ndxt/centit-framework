package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;

public interface SysUserManager{

	 List<UserInfo> listObjects(Map<String, Object> filterMap);
	
	 List<UserInfo> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	
	 UserInfo getObjectById(String userCode);
	
	/**
     * getObjectByLoginName
     * @param userCode userCode
     * @return UserInfo
     */
     UserInfo loadUserByLoginname(String userCode);
    
	// return super.getObjectByProperty("regEmail", regEmail);
     UserInfo getUserByRegEmail(String regEmail);
    
    //return super.getObjectByProperty("regCellPhone", regCellPhone);
     UserInfo getUserByRegCellPhone(String regCellPhone);
    
    
    //  Collection<GrantedAuthority> loadUserAuthorities(String username);

     void resetPwd(String userCode);

     void resetPwd(String[] userCodes);

     void setNewPassword(String userID, String oldPassword, String newPassword);

     void forceSetPassword(String userCode, String newPassword);
     /**
     * 保存用户信息，包括用户机构、用户角色信息
     * @param userinfo userinfo
     */
     void saveNewUserInfo(UserInfo userinfo);
    
     void updateUserInfo(UserInfo userinfo);
    
     void updateUserProperities(UserInfo userinfo);
    
    
     void deleteUserInfo(String userCode);
      
 
     String getNextUserCode();

     List<RoleInfo> listUserValidRoles(String userCode);
   
    
     List<FVUserOptList> getAllOptMethodByUser(String userCode);
     boolean checkIfUserExists(UserInfo user);

     boolean checkUserPassword(String userCode, String oldPassword);

     boolean isLoginNameExist(String userCode, String loginName);
     boolean isCellPhoneExist(String userCode, String regPhone);
     boolean isEmailExist(String userCode, String regEmail);
     boolean isAnyOneExist(String userCode, String loginName,String regPhone,String regEmail);
}
