package com.centit.framework.system.service;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.QueryFilterCondition;

/**
 * QueryFilterCondition  Service.
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 * 系统内置查询方式null   
*/

public interface QueryFilterConditionManager extends BaseEntityManager<QueryFilterCondition,java.lang.Long> 
{
	
	public JSONArray listQueryFilterConditionsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc);
}
