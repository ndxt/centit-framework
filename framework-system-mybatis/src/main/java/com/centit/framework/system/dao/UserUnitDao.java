package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.UserUnit;

@Repository
public interface UserUnitDao{
 
	 List<UserUnit> listObjects(Map<String, Object> filterMap);
	
	
     int  pageCount(Map<String, Object> filterDescMap);
     List<UserUnit>  pageQuery(Map<String, Object> pageQureyMap);
	
	
	 List<UserUnit> listObjectsAll();
	
	 UserUnit getObjectById(String userUnitId);
	
	 void saveNewObject(UserUnit object);
	
	 void updateObject(UserUnit object);
	
	 void deleteObjectById(String userUnitId);
	
	 void deleteObject(UserUnit object);
	
	//"FROM UserUnit where userCode=?", userId
     List<UserUnit> listUserUnitsByUserCode(String userId);
    
    //"FROM UserUnit where userCode=? and unitCode=?",new Object[]{userCode,unitCode});
    //参数 String userCode,String unitCode
     List<UserUnit> listObjectByUserUnit(Map map);
    
    // return "s"+ DatabaseOptUtils.getNextKeyBySequence(this, "S_USER_UNIT_ID", 9);
     Long getNextKey();
    
    //"update UserUnit set isPrimary='F',lastModifyDate= ?  where userCode = ? and (unitCode <> ? or userStation <> ? or userRank <> ?) and isPrimary='T'",
     void deleteOtherPrimaryUnit(UserUnit object);
    
    // "delete UserUnit  where userCode = ? ",userCode
     void deleteUserUnitByUser(String userCode);

    // "delete UserUnit  where unitCode = ? ",unitCode
     void deleteUserUnitByUnit(String unitCode);
    
    //"FROM UserUnit where userCode=? and isPrimary='T'", userId
     UserUnit getPrimaryUnitByUserId(String userId);
    
    //"FROM UserUnit where unitCode=?", unitCode
     List<UserUnit> listUnitUsersByUnitCode(String unitCode);

    /**
     * unitcode不为null就是某个处室的某个角色，为NULL就是所有处室的某个角色
     *if (unitCode != null &amp;&amp; !"".equals(unitCode)) {
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
     * @param roleType String
     * @param roleCode String
     * @param unitCode String
     * @return List UserUnit
     * 分页
     */
     List<UserUnit> listUserUnitsByRoleAndUnitFilterPagination(String roleType,
                                                   String roleCode, String unitCode);
    //"FROM UserUnit where unitCode=? "  hql.append("order by " + filterMap.get("ORDER_BY"));
    //分页
//     List<UserUnit> listUnitUsersByUnitCodeAndFilterPagination(String unitCode, PageDesc pageDesc,
//            Map<String, Object> filterMap);



    /**
     * 批量添加或更新
     * super.saveObject(userunits.get(i));
     * @param userunits UserUnit
     */
     void mergeObject(UserUnit userunits); 
}
