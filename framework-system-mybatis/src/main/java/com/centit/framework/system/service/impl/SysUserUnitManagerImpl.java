package com.centit.framework.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.mybatis.dao.DatabaseOptUtils;
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
public class SysUserUnitManagerImpl 
    implements SysUserUnitManager {
	
    @Resource
    @NotNull
    protected UserUnitDao userUnitDao;

 
    @Resource(name = "userInfoDao")
    @NotNull
    private UserInfoDao userInfoDao;
     
    @Override
    @Transactional(readOnly = true)
    public List<UserUnit> listObjectByUserUnit(String userCode,String unitCode){
    	Map<String,String>map=new HashMap<String,String>();
    	map.put("userCode", userCode);
    	map.put("unitCode", unitCode);
    	
    	List<UserUnit> userUnits = userUnitDao.listObjectByUserUnit(map);
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
    public String saveNewUserUnit(UserUnit userunit) {
        // 一对多模式, 删除主机构    多对多，将当前主机构设置为非主机构
        if (! isMultiToMulti()) {
            UserUnit pUserUnit = userUnitDao.getPrimaryUnitByUserId(userunit.getUserCode());
            if (null != pUserUnit) {
                userUnitDao.deleteObjectById(pUserUnit.getUserUnitId());
            }
        }
        
        if(StringBaseOpt.isNvl(userunit.getUserUnitId())){
        	userunit.setUserUnitId(String.valueOf(userUnitDao.getNextKey()));
        } 

        if ("T".equals(userunit.getIsPrimary())) {
        	UserUnit origPrimUnit=userUnitDao.getPrimaryUnitByUserId(userunit.getUserCode());
        	if(origPrimUnit!=null){
        		origPrimUnit.setIsPrimary("F");
        		userunit.setIsPrimary("T");
        		userUnitDao.mergeObject(origPrimUnit);
        	}
            UserInfo user=userInfoDao.getObjectById(userunit.getUserCode());
            user.setPrimaryUnit(userunit.getUnitCode());            
            userInfoDao.mergeObject(user);
        }
        // userunit.setIsprimary("T");//modify by hx bug：会默认都是主机构        
         userUnitDao.saveNewObject(userunit);
         return userunit.getUserUnitId();
    }    
  

    
    @Override
    public UserUnit getPrimaryUnitByUserCode(String userCode) {
        UserUnit uu=userUnitDao.getPrimaryUnitByUserId(userCode);
        return uu;
    }

    @Override
    public boolean hasUserStation(String stationCode,String userCode) {
        HashMap <String ,Object>filterDesc=new HashMap<String ,Object>();
        filterDesc.put("userStation", stationCode);
        filterDesc.put("userCode", userCode);
        List<UserUnit> list=userUnitDao.listObjects(filterDesc);
        if(null!=list&& list.size()!=0)
            return true;
        else
            return false;
    }

	@Override
	@CacheEvict(value ={"UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
	public void updateUserUnit(UserUnit userunit) {
		userUnitDao.updateObject(userunit);
	}

	@Override
	public UserUnit getObjectById(String userUnitId) {
		return userUnitDao.getObjectById(userUnitId);
	}

	@Override
	public void deleteObject(UserUnit userUnit) {
		userUnitDao.deleteObject(userUnit);
	}

	@Override
	public List<UserUnit> listObjects(Map<String, Object> filterMap, PageDesc pageDesc) {
		return userUnitDao.pageQuery(DatabaseOptUtils.prepPageParmers(filterMap,pageDesc,userUnitDao.pageCount(filterMap)));
	}
}
