package com.centit.framework.system.service;

import java.io.Serializable;
import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.UserUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-28
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public interface SysUserUnitManager extends BaseEntityManager<UserUnit, String> {
     
    public List<UserUnit> listObjectByUserUnit(String userCode,String unitCode);
    
    public UserUnit getPrimaryUnitByUserCode(String userCode);
    public Serializable saveNewUserUnit(UserUnit userUnit);
    public void updateUserUnit(UserUnit userunit);
    public boolean hasUserStation(String stationCode,String userCode);
}
