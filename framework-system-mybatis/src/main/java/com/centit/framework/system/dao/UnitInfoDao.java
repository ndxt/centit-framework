package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

@Repository
public interface UnitInfoDao{
	
	public String saveNewObject(UnitInfo unitInfo);
	
	public void mergeObject(UnitInfo unitInfo);
	
	public void deleteObjectById(String unitCode);
	
	public List<UnitInfo> listObjects(Map<String, Object> filterMap);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<UnitInfo>  pageQuery(Map<String, Object> pageQureyMap);
    
    
	public int countChildrenSum(String unitCode);
	
	public List<UnitInfo> listObjects();
	
	public UnitInfo getObjectById(String unitCode);
	
	// DatabaseOptUtils.getNextKeyBySequence(this, "S_UNITCODE", 6);
    public String getNextKey();

    //listObjectsAll("FROM UnitInfo where depNo=?", depno);
    public String getUnitCode(String depno);

    /**
     * "select a.* " +
                "from f_Userinfo a join f_userunit b on(a.userCode=b.userCode) " +
                "where b.unitcode =?"
     * @param unitCode
     * @return
     */
    public List<UserInfo> listUnitUsers(String unitCode);

    /**
     * "select * FROM F_Userinfo ui where ui.userCode in " +
                "(select userCode from f_userunit where unitcode='" + unitCode + "') or " +
                "ui.userCode in (select userCode from f_userrole where rolecode like ? "
     * @param unitCode
     * @return
     */
    public List<UserInfo> listRelationUsers(String unitCode);

    // "select unitname from f_unitinfo where unitcode=?", unitcode ));
    public String getUnitNameOfCode(String unitcode);
    

    public List<UnitInfo> listUnitinfoByUnitcodes(List<String> unitcodes);
    /**
     * 批量添加或更新
     *
     * @param unitinfos
     */

    /**
     * "from UnitInfo where unitName = ? or unitShortName = ?"
            			+ " order by unitOrder asc";
     * @param name
     * @return
     */
    public UnitInfo getUnitByName(String name);
    
    //return super.getObjectByProperty("unitTag", unitTag);
    public UnitInfo getUnitByTag(String unitTag);
    
    //return super.getObjectByProperty("unitWord", unitWord);
    public UnitInfo getUnitByWord(String unitWord);
    
    //return super.listObjectByProperty("parentUnit", unitCode);
    public List<UnitInfo> listSubUnits(String unitCode);
    
    /**
     * @param parentunitcodes
     * @return
     */
    public List<UnitInfo> listSubUnitinfoByParentUnitcodes(List<String> parentunitcodes);
    
    /**
     * 这个方法应该转移到ManagerImpl类中
     * @param primaryUnit
     * @return
     */
    public List<UnitInfo> listAllSubUnits(String primaryUnit);
    /*public List<UnitInfo> listAllSubUnits(String unitCode){
    	
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
    public List<UnitInfo> listSubUnitsByUnitPaht(String unitPath);

    public List<String> getAllParentUnit();
}
