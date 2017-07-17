package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.UserQueryFilter;



/**
 * UserQueryFilterDao  Repository.
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 * 用户自定义过滤条件表null   
*/

@Repository
public interface UserQueryFilterDao{
	
	public void deleteObject(UserQueryFilter userQueryFilter);
	
	public void mergeObject(UserQueryFilter userQueryFilter);
	
	public Long saveNewObject(UserQueryFilter userQueryFilter);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<UserQueryFilter>  pageQuery(Map<String, Object> pageQureyMap);
	
	public UserQueryFilter getObjectById(Long filterNo);
	//"From UserQueryFilter where userCode = ? and modleCode = ? "
			//+ "order by isDefault desc , createDate desc"
	// 参数 String userCode,String modelCode
	public List<UserQueryFilter> listUserQueryFilterByModle(Map map);
	
	//super.listObjects("From UserQueryFilter where userCode = ? and modleCode = ? "
		//+ "and isDefault = 'T' order by isDefault desc , createDate desc",
	//参数 String userCode,String modelCode
	public List<UserQueryFilter> listUserDefaultFilterByModle(Map map);
	
	//= super.listObjects("From UserQueryFilter where userCode = ? and modleCode = ? "
		//+ "and isDefault = 'T' order by isDefault desc , createDate desc",
		//new Object[]{userCode,modelCode});
	//public UserQueryFilter getUserDefaultFilterByModle(String userCode,String modelCode);
	
	// DatabaseOptUtils.getNextLongSequence(this, "S_FILTER_NO");
    public Long getNextKey();
}
