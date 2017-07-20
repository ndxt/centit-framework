package com.centit.framework.system.dao;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserSettingDao extends BaseDaoImpl<UserSetting, UserSettingId> {

    public static final Logger logger = LoggerFactory.getLogger(UserSettingDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

            filterField.put(CodeRepositoryUtil.USER_CODE, "cid.userCode = :userCode");

            filterField.put("paramCode", "cid.paramCode = :paramCode");

            filterField.put("paramValue", CodeBook.LIKE_HQL_ID);

            filterField.put("paramClass", CodeBook.LIKE_HQL_ID);

            filterField.put("paramName", CodeBook.LIKE_HQL_ID);

            filterField.put("createDate", CodeBook.LIKE_HQL_ID);

        }
        return filterField;
    }
    
    @Transactional
    public List<UserSetting> getUserSettings(String userCode) {
        return listObjects("From UserSetting where cid.userCode=?",userCode);
    }
    
    @Transactional
    public List<UserSetting> getUserSettings(String userCode,String optID) {
        return listObjects("From UserSetting where cid.userCode=? and optId= ?",
                new Object[]{userCode,optID});
    }
    
    @Transactional
    public void saveUserSetting(String userCode, String paramCode,String paramValue,
			String paramClass, String paramName){
    	UserSetting us = new UserSetting(userCode,  paramCode, paramValue,
			 paramClass,  paramName);
    	super.mergeObject(us);
    }
}
