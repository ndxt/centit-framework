package com.centit.framework.system.service.impl;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.mybatis.dao.DatabaseOptUtils;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.service.SysUnitManager;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("sysUnitManager")
@Transactional
public class SysUnitManagerImpl implements SysUnitManager {

	public static Logger logger = LoggerFactory.getLogger(SysUnitManagerImpl.class);
    @Resource
    @NotNull
    private UserUnitDao userUnitDao;

    @Resource
    @NotNull
    protected UnitInfoDao unitInfoDao;
    
    @Override
    public List<UnitInfo> listObjectsAsSort(Map<String, Object> searchColumn) {
        List<UnitInfo> listObjects = unitInfoDao.listObjects(searchColumn);
        Iterator<UnitInfo> unitInfos = listObjects.iterator();
       
        while (unitInfos.hasNext()) {
            UnitInfo unitInfo = unitInfos.next();
            if (StringBaseOpt.isNvl(unitInfo.getParentUnit()) || "0".equals(unitInfo.getParentUnit())) {
                continue;
            }
            for (UnitInfo opt : listObjects) {
                if (opt.getUnitCode().equals(unitInfo.getParentUnit())) {
                    opt.getSubUnits().add(unitInfo);
                    break;
                }
            }
        }
        // 获取顶级的父级菜单
        List<UnitInfo> parentUnit = new ArrayList<>();
        for (UnitInfo unitInfo : listObjects) {
            if (StringBaseOpt.isNvl(unitInfo.getParentUnit()) || "0".equals(unitInfo.getParentUnit())) {
                parentUnit.add(unitInfo);
            }
        }
        return parentUnit;
    }

    /**
     * 查找对象，如果没有新建一个空对象，并附一个默认的编码
     * @param  object UnitInfo
     * @return UnitInfo
     */
    public UnitInfo getObject(UnitInfo object) {
        UnitInfo newObj = unitInfoDao.getObjectById(object.getUnitCode());
        if (newObj == null) {
            newObj = object;
            newObj.setUnitCode(unitInfoDao.getNextKey());
            newObj.setIsValid("T");
        }
        return newObj;
    }
    

  

    @Override
    public List<UserInfo> getUnitUsers(String unitCode) {
        return unitInfoDao.listUnitUsers(unitCode);
    } 
    
    @Override
    public List<UserInfo> getRelationUsers(String unitCode) {
        return unitInfoDao.listRelationUsers(unitCode);
    }

    @Override
    public String getUnitCode(String depno) {
        return unitInfoDao.getUnitCode(depno);
    }

    @Override
    public String getNextKey() {
        return unitInfoDao.getNextKey();
    }   
   
    @Override
    public UnitInfo getUnitByName(String name) {
        return unitInfoDao.getUnitByName(name);
    }

    @Override
    @CacheEvict(value = "UnitInfo",allEntries = true)
    @Transactional
    public void changeStatus(String unitCode, String isValid) {
        List<UnitInfo> allSubUnits = unitInfoDao.listAllSubUnits(unitCode);
        for (UnitInfo subUnit : allSubUnits) {
            subUnit.setIsValid(isValid);
            unitInfoDao.mergeObject(subUnit);
        }
    }
    
        
    @Override
    @CacheEvict(value = {"UnitInfo","UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    @Transactional
    public void deleteUnitInfo(UnitInfo unitinfo){
    	String oldUnitPath = unitinfo.getUnitPath();
 		List<UnitInfo> subUnits = unitInfoDao.listSubUnitsByUnitPaht(oldUnitPath);
		int noupl = oldUnitPath.length();
		for(UnitInfo ui : subUnits){
			if(unitinfo.getUnitCode().equals(ui.getParentUnit()))
				ui.setParentUnit("0");
			ui.setParentUnit(ui.getUnitPath().substring(noupl));
			unitInfoDao.mergeObject(ui);
		}
    
        userUnitDao.deleteUserUnitByUnit(unitinfo.getUnitCode());        
        unitInfoDao.deleteObjectById(unitinfo.getUnitCode());
    }
    
    @Override
    @CacheEvict(value = "UnitInfo",allEntries = true)
    @Transactional
    public Serializable saveNewUnitInfo(UnitInfo unitinfo){
    	UnitInfo parentUnit = unitInfoDao.getObjectById(unitinfo.getParentUnit());
    	if(parentUnit==null)
    		unitinfo.setUnitPath("/"+unitinfo.getUnitCode());
    	else
    		unitinfo.setUnitPath(parentUnit.getUnitPath()+"/"+unitinfo.getUnitCode());
    	
    	 unitInfoDao.saveNewObject(unitinfo);
        return unitinfo.getUnitCode();
    }
    
    @Override
    @CacheEvict(value = "UnitInfo",allEntries = true)
    @Transactional
    public void updateUnitInfo(UnitInfo unitinfo){
    	UnitInfo dbUnitInfo = unitInfoDao.getObjectById(unitinfo.getUnitCode());
    	String oldParentUnit = dbUnitInfo.getParentUnit();
    	String oldUnitPath = dbUnitInfo.getUnitPath();
    	BeanUtils.copyProperties(unitinfo, dbUnitInfo, new String[]{"unitCode"});
    	if(!StringUtils.equals(oldParentUnit, unitinfo.getParentUnit())){
    		UnitInfo parentUnit = unitInfoDao.getObjectById(unitinfo.getParentUnit());
    		if(parentUnit==null)
        		unitinfo.setUnitPath("/"+unitinfo.getUnitCode());
        	else
        		unitinfo.setUnitPath(parentUnit.getUnitPath()+"/"+unitinfo.getUnitCode());
    		List<UnitInfo> subUnits = unitInfoDao.listSubUnitsByUnitPaht(oldUnitPath);
    		int noupl = oldUnitPath.length();
    		for(UnitInfo ui : subUnits){
    			ui.setParentUnit(unitinfo.getUnitPath()+ ui.getUnitPath().substring(noupl));
    			unitInfoDao.mergeObject(ui);
    		}
    	}    	
        unitInfoDao.mergeObject(dbUnitInfo);
    }

    @Override
    @Transactional
	public List<UnitInfo> listAllSubObjects(String primaryUnit) {
        if(StringUtils.isBlank(primaryUnit))
            return null;
		return unitInfoDao.listAllSubUnits(primaryUnit);
    }
    
	@Override
	@Transactional
	public List<UnitInfo> listAllSubObjectsAsSort(String primaryUnit) {
		List<UnitInfo> listObjects = unitInfoDao.listAllSubUnits(primaryUnit);
        Iterator<UnitInfo> unitInfos = listObjects.iterator();        
        while (unitInfos.hasNext()) {
            UnitInfo unitInfo = unitInfos.next();
            if (StringBaseOpt.isNvl(unitInfo.getParentUnit()) || "0".equals(unitInfo.getParentUnit())) {
                continue;
            }
            for (UnitInfo opt : listObjects) {
                if (opt.getUnitCode().equals(unitInfo.getParentUnit())) {
                    opt.getSubUnits().add(unitInfo);

                    break;
                }
            }
        }
        List<UnitInfo> parentUnit = new ArrayList<>();
        // 获取顶级的父级菜单
        for (UnitInfo unitInfo : listObjects) {
            if (StringBaseOpt.isNvl(unitInfo.getParentUnit()) ||primaryUnit.equals(unitInfo.getUnitCode())) {
                parentUnit.add(unitInfo);
            }
        }
        return parentUnit;
	}

	@Override
	@Transactional
	public boolean hasChildren(String unitCode) {
		return unitInfoDao.countChildrenSum(unitCode)>0;
	}

	@Override
	@Transactional
	public List<UnitInfo> listObjects(Map<String, Object> filterMap) {
		return unitInfoDao.listObjects(filterMap);
	}

	@Override
	@Transactional
	public List<UnitInfo> listObjects(Map<String, Object> filterMap, PageDesc pageDesc) {
		return unitInfoDao.pageQuery(DatabaseOptUtils.prepPageParmers(filterMap,pageDesc,unitInfoDao.pageCount(filterMap)));
	}

	@Override
	@Transactional
	public UnitInfo getObjectById(String unitCode) {
		return unitInfoDao.getObjectById(unitCode);
	}

    @Override
    @Transactional
    public void checkState(List<UnitInfo> listObjects) {
       // List<String> objs = (List<String>) DatabaseOptUtils.findObjectsBySql(baseDao, "select distinct t.parentunit from f_unitinfo t ");
        List<String> objs = unitInfoDao.getAllParentUnit();
        if(objs!=null && objs.size()>0){
            for (UnitInfo u : listObjects){
                if(objs.contains(u.getUnitCode()))
                    u.setState("closed");
                else
                    u.setState("opend");
            }
        }else{
            for (UnitInfo u : listObjects){
                u.setState("opend");
            }
        }
    }


    
}
