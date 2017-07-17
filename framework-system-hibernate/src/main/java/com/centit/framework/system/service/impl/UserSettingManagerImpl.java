package com.centit.framework.system.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.UserSettingDao;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;
import com.centit.framework.system.service.UserSettingManager;

@Service
public class UserSettingManagerImpl extends
        BaseEntityManagerImpl<UserSetting, UserSettingId, UserSettingDao>
implements UserSettingManager {

    public static final Logger logger = LoggerFactory.getLogger(UserSettingManager.class);

    @Resource(name = "userSettingDao")
    @NotNull
    public void setUserSettingDao(UserSettingDao baseDao) {
        super.baseDao = baseDao;
    }

    @Override
    public List<UserSetting> getUserSettings(String userCode) {
        return baseDao.getUserSettings(userCode);
    }
    
    @Override
    public List<UserSetting> getUserSettings(String userCode,String optID) {
        return baseDao.getUserSettings(userCode,optID);
    }
 


    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
      
        return baseDao.getObjectById(new UserSettingId(userCode,paramCode));
    }
    
    @Override
    @Transactional
    public void saveUserSetting(UserSetting userSetting){
    	baseDao.mergeObject(userSetting);
    }
    @Override
    @Transactional
    public void saveUserSetting(String userCode,String paramCode,String paramName,String paramValue,String optId){
    	UserSetting userSetting = new UserSetting( userCode,  paramCode, paramValue,
    									optId,  paramName);
    	baseDao.mergeObject(userSetting);
    }

}
