package com.centit.framework.system.service;

import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;

public interface UserSettingManager extends 
    BaseEntityManager<UserSetting, UserSettingId> {

    public List<UserSetting> getUserSettings(String userCode); 

    public List<UserSetting> getUserSettings(String userCode,String optID); 

    public UserSetting getUserSetting(String userCode,String paramCode);

    public void saveUserSetting(UserSetting userSetting);
    
    public void saveUserSetting(String userCode,String paramCode,String paramName,String paramValue,String optId); 

}
