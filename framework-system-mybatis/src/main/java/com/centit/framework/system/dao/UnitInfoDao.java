package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

@Repository
public interface UnitInfoDao{
	
	 String saveNewObject(UnitInfo unitInfo);
	
	 void mergeObject(UnitInfo unitInfo);
	
	 void deleteObjectById(String unitCode);
	
	 List<UnitInfo> listObjects(Map<String, Object> filterMap);
	
	
     int  pageCount(Map<String, Object> filterDescMap);
     List<UnitInfo>  pageQuery(Map<String, Object> pageQureyMap);
    
    
	 int countChildrenSum(String unitCode);
	
	 List<UnitInfo> listObjects();
	
	 UnitInfo getObjectById(String unitCode);
	
	// DatabaseOptUtils.getNextKeyBySequence(this, "S_UNITCODE", 6);
     String getNextKey();

    //listObjectsAll("FROM UnitInfo where depNo=?", depno);
     String getUnitCode(String depno);

    /**
     * "select a.* " +
                "from f_Userinfo a join f_userunit b on(a.userCode=b.userCode) " +
                "where b.unitcode =?"
     * @param unitCode unitCode
     * @return List UserInfo
     */
     List<UserInfo> listUnitUsers(String unitCode);

    /**
     * "select * FROM F_Userinfo ui where ui.userCode in " +
                "(select userCode from f_userunit where unitcode='" + unitCode + "') or " +
                "ui.userCode in (select userCode from f_userrole where rolecode like ? "
     * @param unitCode unitCode
     * @return List UserInfo
     */
     List<UserInfo> listRelationUsers(String unitCode);

    // "select unitname from f_unitinfo where unitcode=?", unitcode ));
     String getUnitNameOfCode(String unitcode);
    

     List<UnitInfo> listUnitinfoByUnitcodes(List<String> unitcodes);

    /**
     * "from UnitInfo where unitName = ? or unitShortName = ?"
            			+ " order by unitOrder asc";
     * @param name name
     * @return UnitInfo
     */
     UnitInfo getUnitByName(String name);
    
    //return super.getObjectByProperty("unitTag", unitTag);
     UnitInfo getUnitByTag(String unitTag);
    
    //return super.getObjectByProperty("unitWord", unitWord);
     UnitInfo getUnitByWord(String unitWord);
    
    //return super.listObjectByProperty("parentUnit", unitCode);
     List<UnitInfo> listSubUnits(String unitCode);
    
    /**
     * @param parentunitcodes List
     * @return List UnitInfo
     */
     List<UnitInfo> listSubUnitinfoByParentUnitcodes(List<String> parentunitcodes);
    
    /**
     * 这个方法应该转移到ManagerImpl类中
     * @param primaryUnit primaryUnit
     * @return List UnitInfo
     */
     List<UnitInfo> listAllSubUnits(String primaryUnit);
    /* List<UnitInfo> listAllSubUnits(String unitCode){
    	
    	List<UnitInfo> subUnits = listSubUnits(unitCode);
    	List<UnitInfo> allSubUnits = new ArrayList<UnitInfo>();
    	UnitInfo currUnit = getObjectById(unitCode);
    	if(currUnit==null)
    		return allSubUnits;
    	allSubUnits.add(currUnit);
    	//listUnitinfoByUnitcodes
    	while(subUnits!=null && subUnits.size()>0){
    		allSubUnits.addAll(subUnits);
    		List<String> subUnitCodes = new ArrayList<String>();
    		for(UnitInfo ui:subUnits){
    			subUnitCodes.add(ui.getUnitCode());
    		}
    		subUnits =
    				listSubUnitinfoByParentUnitcodes(subUnitCodes);
    	}
    	return allSubUnits;    	
    }*/
    
    //String hql = "from UnitInfo where unitPath like ?";{unitPath+"/%"});
     List<UnitInfo> listSubUnitsByUnitPaht(String unitPath);

     List<String> getAllParentUnit();
}
