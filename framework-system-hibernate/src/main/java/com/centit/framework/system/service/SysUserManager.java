package com.centit.framework.system.service;

import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;

public interface SysUserManager extends BaseEntityManager<UserInfo, String> {

    //  Collection<GrantedAuthority> loadUserAuthorities(String username);

     void resetPwd(String userCode);

     void resetPwd(String[] userCodes);

     void forceSetPassword(String userCode, String newPassword);

     void setNewPassword(String userCode, String oldPassword, String newPassword);

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
   
    /**
     * 模糊搜索
     * @param key 搜索关键字
     * @param field 在以下字段中搜索
     * @return   List  UserInfo
     */
     List<UserInfo> search(String key, String[] field);
    
     UserInfo loadUserByLoginname(String userCode);
     List<FVUserOptList> getAllOptMethodByUser(String userCode);

     boolean checkUserPassword(String userCode, String oldPassword);

     boolean isLoginNameExist(String userCode, String loginName);
     boolean isCellPhoneExist(String userCode, String regPhone);
     boolean isEmailExist(String userCode, String regEmail);
     boolean isAnyOneExist(String userCode, String loginName,String regPhone,String regEmail);
}
