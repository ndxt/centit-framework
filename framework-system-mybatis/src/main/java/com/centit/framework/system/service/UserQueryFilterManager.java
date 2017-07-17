package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.UserQueryFilter;

/**
 * UserQueryFilter  Service.
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 * 用户自定义过滤条件表null   
*/

public interface UserQueryFilterManager
{
	
	public void mergeObject(UserQueryFilter userQueryFilter);
	
	public Long saveNewObject(UserQueryFilter userQueryFilter);
	
	public List<UserQueryFilter>listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	 
	public JSONArray listUserQueryFiltersAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc);
	
	public List<UserQueryFilter> listUserQueryFilterByModle(String userCode,String modelCode);
	
	public Long getNextFilterKey();
	
	public Long saveUserDefaultFilter(UserQueryFilter userQueryFilter);
	
	public UserQueryFilter getUserDefaultFilter(String userCode,String modelCode);
	
	public UserQueryFilter getUserQueryFilter(Long filterNo);
	
	public boolean deleteUserQueryFilter(Long filterNo);
	
}
