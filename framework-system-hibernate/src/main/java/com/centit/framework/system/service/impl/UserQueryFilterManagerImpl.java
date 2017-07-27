package com.centit.framework.system.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import com.centit.framework.core.dao.DictionaryMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.UserQueryFilterDao;
import com.centit.framework.system.po.UserQueryFilter;
import com.centit.framework.system.service.UserQueryFilterManager;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;

/**
 * UserQueryFilter  Service.
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 * 用户自定义过滤条件表null   
*/
@Service
public class UserQueryFilterManagerImpl 
		extends BaseEntityManagerImpl<UserQueryFilter,java.lang.Long,UserQueryFilterDao>
	implements UserQueryFilterManager{

	public static final Logger logger = LoggerFactory.getLogger(UserQueryFilterManager.class);

	
	private UserQueryFilterDao userQueryFilterDao ;
	
	@Resource(name = "userQueryFilterDao")
    @NotNull
	public void setUserQueryFilterDao(UserQueryFilterDao baseDao)
	{
		this.userQueryFilterDao = baseDao;
		setBaseDao(this.userQueryFilterDao);
	}
	
/*
 	@PostConstruct
    public void init() {
        
    }
 	
 */
	@Override
    @Transactional(propagation=Propagation.REQUIRED) 
	public JSONArray listUserQueryFiltersAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc){
			
		return DictionaryMapUtils.objectsToJSONArray(baseDao.listObjects(filterMap, pageDesc),
				fields);
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public List<UserQueryFilter> listUserQueryFilterByModle(String userCode,String modelCode){
		return baseDao.listUserQueryFilterByModle(userCode,modelCode);
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public Long getNextFilterKey(){
		return baseDao.getNextKey();
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public Long saveUserDefaultFilter(UserQueryFilter userQueryFilter){
		if(StringBaseOpt.isNvl(userQueryFilter.getUserCode())||
				StringBaseOpt.isNvl(userQueryFilter.getModleCode()))
			return null;
		List<UserQueryFilter> filters = baseDao.listUserDefaultFilterByModle(
				userQueryFilter.getUserCode(),userQueryFilter.getModleCode());
		if(filters==null || filters.size()<1){
			userQueryFilter.setFilterNo(getNextFilterKey());
			userQueryFilter.setIsDefault("T");
			userQueryFilter.setCreateDate(DatetimeOpt.currentUtilDate());
			baseDao.saveNewObject(userQueryFilter);
			return userQueryFilter.getFilterNo();
		}else{
			UserQueryFilter dbFilter;
			for(int i=1;i<filters.size();i++){
				 dbFilter = filters.get(i);
				 dbFilter.setIsDefault("F");
				 baseDao.mergeObject(dbFilter);
			}
			dbFilter = filters.get(0);
			dbFilter.setFilterName( userQueryFilter.getFilterName());
			dbFilter.setFilterValue(userQueryFilter.getFilterValue());
			userQueryFilter.setCreateDate(DatetimeOpt.currentUtilDate());
			baseDao.mergeObject(dbFilter);
			return dbFilter.getFilterNo();
		}
		
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public UserQueryFilter getUserDefaultFilter(String userCode,String modelCode){
		if(StringBaseOpt.isNvl(userCode)||
				StringBaseOpt.isNvl(modelCode))
			return null;
		
		List<UserQueryFilter> filters = baseDao.listUserDefaultFilterByModle(userCode,modelCode);
		if(filters==null || filters.size()<1)
			return null;
		return filters.get(0);
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public UserQueryFilter getUserQueryFilter(Long filterNo){
		return baseDao.getObjectById(filterNo);
	}
	
	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public boolean deleteUserQueryFilter(Long filterNo){
		UserQueryFilter uqf = baseDao.getObjectById(filterNo);
		if(uqf==null)
			return false;
		if("T".equals(uqf.getIsDefault()))
			return false;
		baseDao.deleteObject(uqf);
		return true;
	}
}

