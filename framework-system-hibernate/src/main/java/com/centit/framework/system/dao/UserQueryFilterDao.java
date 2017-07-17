package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.UserQueryFilter;



/**
 * UserQueryFilterDao  Repository.
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 * 用户自定义过滤条件表null   
*/

@Repository
public class UserQueryFilterDao extends BaseDaoImpl<UserQueryFilter,java.lang.Long>
	{
	public static final Logger logger = LoggerFactory.getLogger(UserQueryFilterDao.class);
	
	@Override
	public Map<String, String> getFilterField() {
		if( filterField == null){
			filterField = new HashMap<String, String>();

			filterField.put("filterNo" , CodeBook.EQUAL_HQL_ID);

			filterField.put("userCode" , CodeBook.EQUAL_HQL_ID);
			filterField.put("modleCode" , CodeBook.EQUAL_HQL_ID);

			filterField.put("filterName" , CodeBook.EQUAL_HQL_ID);

			filterField.put("filterValue" , CodeBook.EQUAL_HQL_ID);
			
		}
		return filterField;
	}
	
	@Transactional
	public List<UserQueryFilter> listUserQueryFilterByModle(String userCode,String modelCode){
		return super.listObjects("From UserQueryFilter where userCode = ? and modleCode = ? "
				+ "order by isDefault desc , createDate desc",
				new Object[]{userCode,modelCode});
	}
	
	@Transactional
	public List<UserQueryFilter> listUserDefaultFilterByModle(String userCode,String modelCode){
		return super.listObjects("From UserQueryFilter where userCode = ? and modleCode = ? "
				+ "and isDefault = 'T' order by isDefault desc , createDate desc",
				new Object[]{userCode,modelCode});
	}
	
	@Transactional
	public UserQueryFilter getUserDefaultFilterByModle(String userCode,String modelCode){
		List<UserQueryFilter> uqfs = super.listObjects("From UserQueryFilter where userCode = ? " +
						"and modleCode = ? and isDefault = 'T' order by isDefault desc , createDate desc",
				new Object[]{userCode,modelCode});
		if(uqfs==null || uqfs.size()==0)
			return null;
		return uqfs.get(0);
	}
	
	@Transactional
    public Long getNextKey() {
        return DatabaseOptUtils.getNextLongSequence(this, "S_FILTER_NO");
    }
}
