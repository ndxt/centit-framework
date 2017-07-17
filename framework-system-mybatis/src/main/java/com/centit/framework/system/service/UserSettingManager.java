package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;

public interface UserSettingManager {

    public List<UserSetting> getUserSettings(String userCode); 

    public List<UserSetting> getUserSettings(String userCode,String optID); 

    public UserSetting getUserSetting(String userCode,String paramCode);

    public void saveUserSetting(UserSetting userSetting);
    
    public void saveUserSetting(String userCode,String paramCode,String paramName,String paramValue,String optId); 

    public List<UserSetting> listObjects(Map<String,Object>searchColumn,PageDesc pageDesc);
    
    public List<UserSetting> listObjects(Map<String,Object>searchColumn);
    
    public UserSetting getObjectById(UserSettingId userSettingid);
    
    public void deleteObject(UserSetting userSetting);
}
