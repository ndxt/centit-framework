package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.UserUnit;

@Repository
public interface UserUnitDao{
 
	public List<UserUnit> listObjects(Map<String, Object> filterMap);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<UserUnit>  pageQuery(Map<String, Object> pageQureyMap);
	
	
	public List<UserUnit> listObjectsAll();
	
	public UserUnit getObjectById(String userUnitId);
	
	public void saveNewObject(UserUnit object);
	
	public void updateObject(UserUnit object);
	
	public void deleteObjectById(String userUnitId);
	
	public void deleteObject(UserUnit object);
	
	//"FROM UserUnit where userCode=?", userId
    public List<UserUnit> listUserUnitsByUserCode(String userId);
    
    //"FROM UserUnit where userCode=? and unitCode=?",new Object[]{userCode,unitCode});
    //参数 String userCode,String unitCode
    public List<UserUnit> listObjectByUserUnit(Map map);
    
    // return "s"+ DatabaseOptUtils.getNextKeyBySequence(this, "S_USER_UNIT_ID", 9);
    public Long getNextKey();
    
    //"update UserUnit set isPrimary='F',lastModifyDate= ?  where userCode = ? and (unitCode <> ? or userStation <> ? or userRank <> ?) and isPrimary='T'",
    public void deleteOtherPrimaryUnit(UserUnit object);
    
    // "delete UserUnit  where userCode = ? ",userCode
    public void deleteUserUnitByUser(String userCode);

    // "delete UserUnit  where unitCode = ? ",unitCode
    public void deleteUserUnitByUnit(String unitCode);
    
    //"FROM UserUnit where userCode=? and isPrimary='T'", userId
    public UserUnit getPrimaryUnitByUserId(String userId);
    
    //"FROM UserUnit where unitCode=?", unitCode
    public List<UserUnit> listUnitUsersByUnitCode(String unitCode);

    /**
     * unitcode不为null就是某个处室的某个角色，为NULL就是所有处室的某个角色
     *if (unitCode != null && !"".equals(unitCode)) {
            if ("gw".equals(roleType))
                ls =listObjectsAll("FROM UserUnit where unitCode=? and userStation=? ",
                               new Object[]{ unitCode, roleCode});
            else if ("xz".equals(roleType))
                ls = listObjectsAll("FROM UserUnit where unitCode=? and userRank=? ",
                        new Object[]{ unitCode, roleCode});
        } else {
            if ("gw".equals(roleType))
                ls = listObjectsAll("FROM UserUnit where userStation=? ",
                                roleCode);
            else if ("xz".equals(roleType))
                ls = listObjectsAll("FROM UserUnit where userRank=? ",
                                roleCode);
        }
     * @param roleType
     * @param roleCode
     * @param unitCode
     * @return
     * 分页
     */
    public List<UserUnit> listUserUnitsByRoleAndUnitFilterPagination(String roleType,
                                                   String roleCode, String unitCode);
    //"FROM UserUnit where unitCode=? "  hql.append("order by " + filterMap.get("ORDER_BY"));
    //分页
//    public List<UserUnit> listUnitUsersByUnitCodeAndFilterPagination(String unitCode, PageDesc pageDesc,
//            Map<String, Object> filterMap);



    /**
     * 批量添加或更新
     * super.saveObject(userunits.get(i));
     * @param userunits
     */
    public void mergeObject(UserUnit userunits); 
}
