package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.UserUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-28
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public interface SysUserUnitManager{
	public UserUnit getObjectById(String userUnitId);
	
	public void deleteObject(UserUnit userUnit);
	
	public List<UserUnit> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
        
    public List<UserUnit> listObjectByUserUnit(String userCode,String unitCode);
    
    public UserUnit getPrimaryUnitByUserCode(String userCode);
    public String saveNewUserUnit(UserUnit userUnit);
    public void updateUserUnit(UserUnit userunit);
    public boolean hasUserStation(String stationCode,String userCode);
}
