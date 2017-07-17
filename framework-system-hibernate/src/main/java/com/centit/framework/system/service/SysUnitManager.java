package com.centit.framework.system.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

public interface SysUnitManager extends BaseEntityManager<UnitInfo, String> {

    public List<UserInfo> getUnitUsers(String unitCode);

    public List<UserInfo> getRelationUsers(String unitCode);
 
    public String getNextKey();

    public String getUnitCode(String depno);

    public UnitInfo getUnitByName(String name);

    public List<UnitInfo> listObjectsAsSort(Map<String, Object> searchColumn);

    /**
     * 更新机构及子机构的状态
     * @param unitCode 机构代码
     * @param isValid 状态码
     */
    public void changeStatus(String unitCode, String isValid);
    public void deleteUnitInfo(UnitInfo unitinfo);
    public Serializable saveNewUnitInfo(UnitInfo unitinfo);
    public void updateUnitInfo(UnitInfo unitinfo);
    public List<UnitInfo> listAllSubObjects(String primaryUnit);
	public List<UnitInfo> listAllSubObjectsAsSort(String primaryUnit);

	public boolean hasChildren(String unitCode);

	public void checkState(List<UnitInfo> listObjects);
}
