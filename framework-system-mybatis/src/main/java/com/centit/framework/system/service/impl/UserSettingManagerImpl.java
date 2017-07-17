package com.centit.framework.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.dao.UserSettingDao;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;
import com.centit.framework.system.service.UserSettingManager;

@Service
public class UserSettingManagerImpl implements UserSettingManager {

    public static final Logger logger = LoggerFactory.getLogger(UserSettingManager.class);

    @Resource
    private UserSettingDao userSettingDao;
    
    @Override
    public List<UserSetting> getUserSettings(String userCode) {
        return userSettingDao.getUserSettingsByCode(userCode);
    }
    
    @Override
    public List<UserSetting> getUserSettings(String userCode,String optID) {
    	Map<String,String> map=new HashMap<String,String>();
		map.put("userCode", userCode);
		map.put("optID", optID);
        return userSettingDao.getUserSettings(map);
    }
 
    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
      
        return userSettingDao.getObjectById(new UserSettingId(userCode,paramCode));
    }
    
    @Override
    @Transactional
    public void saveUserSetting(UserSetting userSetting){
    	userSettingDao.mergeObject(userSetting);
    }
    @Override
    @Transactional
    public void saveUserSetting(String userCode,String paramCode,String paramName,String paramValue,String optId){
    	UserSetting userSetting = new UserSetting( userCode,  paramCode, paramValue,
    									optId,  paramName);
    	userSettingDao.mergeObject(userSetting);
    }

	@Override
	public List<UserSetting> listObjects(Map<String, Object> searchColumn, PageDesc pageDesc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserSetting> listObjects(Map<String, Object> searchColumn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserSetting getObjectById(UserSettingId userSettingid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(UserSetting userSetting) {
		// TODO Auto-generated method stub
		
	}

	

}
