package com.centit.framework.system.service.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.SysDaoOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.QueryFilterConditionDao;
import com.centit.framework.system.po.QueryFilterCondition;
import com.centit.framework.system.service.QueryFilterConditionManager;

/**
 * QueryFilterCondition  Service.
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 * 系统内置查询方式null   
*/
@Service
public class QueryFilterConditionManagerImpl 
		extends BaseEntityManagerImpl<QueryFilterCondition,java.lang.Long,QueryFilterConditionDao>
	implements QueryFilterConditionManager{

	public static final Logger logger = LoggerFactory.getLogger(QueryFilterConditionManager.class);

	
	private QueryFilterConditionDao queryFilterConditionDao ;
	
	@Resource(name = "queryFilterConditionDao")
    @NotNull
	public void setQueryFilterConditionDao(QueryFilterConditionDao baseDao)
	{
		this.queryFilterConditionDao = baseDao;
		setBaseDao(this.queryFilterConditionDao);
	}
	
/*
 	@PostConstruct
    public void init() {
        
    }
 	
 */
	@Override
    @Transactional(propagation=Propagation.REQUIRED) 
	public JSONArray listQueryFilterConditionsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc){
			
		return SysDaoOptUtils.listObjectsAsJson(baseDao, fields, QueryFilterCondition.class,
    			filterMap, pageDesc);
	}
	
}

