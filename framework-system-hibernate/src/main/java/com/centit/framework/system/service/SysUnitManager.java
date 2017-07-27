package com.centit.framework.system.service;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SysUnitManager extends BaseEntityManager<UnitInfo, String> {

    List<UserInfo> getUnitUsers(String unitCode);

    List<UserInfo> getRelationUsers(String unitCode);
 
    String getNextKey();

    String getUnitCode(String depno);

    UnitInfo getUnitByName(String name);

    List<UnitInfo> listObjectsAsSort(Map<String, Object> searchColumn);

    /**
     * 更新机构及子机构的状态
     * @param unitCode 机构代码
     * @param isValid 状态码
     */
    void changeStatus(String unitCode, String isValid);
    void deleteUnitInfo(UnitInfo unitinfo);
    Serializable saveNewUnitInfo(UnitInfo unitinfo);
    void updateUnitInfo(UnitInfo unitinfo);
    List<UnitInfo> listAllSubObjects(String primaryUnit);
	List<UnitInfo> listAllSubObjectsAsSort(String primaryUnit);

	boolean hasChildren(String unitCode);

	void checkState(List<UnitInfo> listObjects);
}
