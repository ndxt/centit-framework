package com.centit.framework.system.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserUnit;
import com.centit.framework.system.service.SysUserUnitManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-28
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
@Service("sysUserUnitManager")
@Transactional
public class SysUserUnitManagerImpl extends BaseEntityManagerImpl<UserUnit, String, UserUnitDao>
    implements SysUserUnitManager {
    @Resource(name = "userUnitDao")
    @NotNull
    @Override
    protected void setBaseDao(UserUnitDao baseDao) {
        super.baseDao = baseDao;
    }

 
    @Resource(name = "userInfoDao")
    @NotNull
    private UserInfoDao userInfoDao;
     
    @Override
    @Transactional(readOnly = true)
    public List<UserUnit> listObjectByUserUnit(String userCode,String unitCode){
    	List<UserUnit> userUnits = baseDao.listObjectByUserUnit(userCode,unitCode);
        if(userUnits!=null){
            for (UserUnit uu : userUnits) {
                if (null == uu) {
                    continue;
                }
                // 设置行政角色等级
                IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", uu.getUserRank());
                if (dd != null && dd.getExtraCode() != null && StringRegularOpt.isNumber(dd.getExtraCode())) {
                    try {
                        uu.setXzRank(Integer.valueOf(dd.getExtraCode()));
                    } catch (Exception e) {
                        uu.setXzRank(CodeRepositoryUtil.MAXXZRANK);
                    }
                 }
            }
        }
        return userUnits;
    }

    private static boolean isMultiToMulti() {
    	IDataDictionary agencyMode = CodeRepositoryUtil.getDataPiece("SYSPARAM","userUnitMode");

        if (agencyMode!=null) {
            return ("M".equalsIgnoreCase(agencyMode.getDataValue()));
        }
        return true;
    }
    
    @Override
    @CacheEvict(value ={"UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public Serializable saveNewUserUnit(UserUnit userunit) {
        // 一对多模式, 删除主机构    多对多，将当前主机构设置为非主机构
        if (! isMultiToMulti()) {
            UserUnit pUserUnit = baseDao.getPrimaryUnitByUserId(userunit.getUserCode());
            if (null != pUserUnit) {
                baseDao.deleteObjectById(pUserUnit.getUserUnitId());
            }
        }
        
        if(StringBaseOpt.isNvl(userunit.getUserUnitId())){
        	userunit.setUserUnitId(baseDao.getNextKey());
        } 

        if ("T".equals(userunit.getIsPrimary())) {
        	UserUnit origPrimUnit=baseDao.getPrimaryUnitByUserId(userunit.getUserCode());
        	if(origPrimUnit!=null){
        		origPrimUnit.setIsPrimary("F");
        		userunit.setIsPrimary("T");
        		baseDao.saveNewObject(origPrimUnit);
        	}
            UserInfo user=userInfoDao.getObjectById(userunit.getUserCode());
            user.setPrimaryUnit(userunit.getUnitCode());            
            userInfoDao.saveObject(user);
        }
        // userunit.setIsprimary("T");//modify by hx bug：会默认都是主机构        
        return baseDao.saveNewObject(userunit);
    }    
  
    @Override
    @CacheEvict(value ={"UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public void deleteObjectById(String id) {
        baseDao.deleteObjectById(id);
    }

    
    @Override
    public UserUnit getPrimaryUnitByUserCode(String userCode) {
        UserUnit uu=baseDao.getPrimaryUnitByUserId(userCode);
        return uu;
    }

    @Override
    public boolean hasUserStation(String stationCode,String userCode) {
        HashMap <String ,Object>filterDesc=new HashMap<String ,Object>();
        filterDesc.put("userStation", stationCode);
        filterDesc.put("userCode", userCode);
        List<UserUnit> list=baseDao.listObjects(filterDesc);
        if(null!=list&& list.size()!=0)
            return true;
        else
            return false;
    }

	@Override
	@CacheEvict(value ={"UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
	public void updateUserUnit(UserUnit userunit) {
		baseDao.updateObject(userunit);
	}
}
