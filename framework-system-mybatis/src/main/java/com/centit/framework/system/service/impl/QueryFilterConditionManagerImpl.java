package com.centit.framework.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.dao.QueryParameterPrepare;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.system.dao.QueryFilterConditionDao;
import com.centit.framework.system.po.QueryFilterCondition;
import com.centit.framework.system.service.QueryFilterConditionManager;
import com.centit.support.common.KeyValuePair;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * QueryFilterCondition  Service.
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 * 系统内置查询方式null   
*/
@Service
public class QueryFilterConditionManagerImpl implements QueryFilterConditionManager{

	public static final Logger logger = LoggerFactory.getLogger(QueryFilterConditionManager.class);

	@Resource
    @NotNull
	private QueryFilterConditionDao queryFilterConditionDao ;
	
	
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
		//TODO 获取SQL SESSION	
		SqlSession sqlSession = null;
		return DictionaryMapUtils.listObjectsBySqlAsJson(sqlSession,"sql",filterMap, fields,
    			(Map<String,KeyValuePair<String,String>> )null, pageDesc);
	}	

	
	
	@Override
	@Transactional
	public List<QueryFilterCondition> listObjects(Map<String, Object> filterMap, PageDesc pageDesc) {
		return queryFilterConditionDao.pageQuery(QueryParameterPrepare.prepPageParmers(filterMap,pageDesc,queryFilterConditionDao.pageCount(filterMap)));
	}
	
	
	@Override
	@Transactional
	public QueryFilterCondition getObjectById(Long filterNo) {
		return queryFilterConditionDao.getObjectById(filterNo);
	}
	
	
	@Override
	@Transactional
	public void mergeObject(QueryFilterCondition userQueryFilter) {
		queryFilterConditionDao.mergeObject(userQueryFilter);
	}
	
	
	@Override
	@Transactional
	public void deleteObjectById(Long filterNo) {
		queryFilterConditionDao.deleteObjectById(filterNo);
	}
	
	
	@Override
	public Long saveNewObject(QueryFilterCondition userQueryFilter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

