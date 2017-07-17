package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.QueryFilterCondition;



/**
 * QueryFilterConditionDao  Repository.
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 * 系统内置查询方式null   
*/

@Repository
public interface QueryFilterConditionDao{

	//DatabaseOptUtils.getNextLongSequence(this, "S_FILTER_NO");
    public Long getNextKey();
    
    
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<QueryFilterCondition>  pageQuery(Map<String, Object> pageQureyMap);
    
	
	public QueryFilterCondition getObjectById(Long filterNo);
	
	public void mergeObject(QueryFilterCondition userQueryFilter);
	
	public void deleteObjectById(Long filterNo);
			
	public Long saveNewObject(QueryFilterCondition userQueryFilter);
}
