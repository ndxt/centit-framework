package com.centit.framework.system.service;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.UserUnit;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-28
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public interface SysUserUnitManager{
	UserUnit getObjectById(String userUnitId);
	
	void deleteObject(UserUnit userUnit);
	
	List<UserUnit> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
        
    List<UserUnit> listObjectByUserUnit(String userCode,String unitCode);
    
    UserUnit getPrimaryUnitByUserCode(String userCode);
    String saveNewUserUnit(UserUnit userUnit);
    void updateUserUnit(UserUnit userunit);
    boolean hasUserStation(String stationCode,String userCode);
}
