package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.UserUnit;
import com.centit.support.algorithm.DatetimeOpt;

@Repository
public class UserUnitDao extends BaseDaoImpl<UserUnit, String> {
 
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<>();
            filterField.put("USERCODE_ISVALID", "userCode in (select userCode from UserInfo where isValid =?)");
            filterField.put("unitCode", "unitCode = ?");
            filterField.put("userStation", "userStation = ?");
            filterField.put("unitRank", "userRank = ?");
            filterField.put("userCode", "userCode = ?");
            filterField.put("isPrimary", CodeBook.EQUAL_HQL_ID);
            filterField.put("unitName", CodeBook.LIKE_HQL_ID);

            filterField.put(CodeBook.ORDER_BY_HQL_ID, "userOrder asc");

        }
        return filterField;
    }

    @Transactional
    public List<UserUnit> listUserUnitsByUserCode(String userId) {
        List<UserUnit> ls = listObjects(
                "FROM UserUnit where userCode=?",
                userId);
        /*
         * for (FUserunit usun : ls) {
         * usun.setUnitname(CodeRepositoryUtil.getValue
         * ("unitCode",usun.getId().getUnitcode() )); }
         */
        return ls;
    }
    
    @Transactional
    public List<UserUnit> listObjectByUserUnit(String userCode,String unitCode){
    	List<UserUnit> ls = listObjects(
                "FROM UserUnit where userCode=? and unitCode=?",
                new Object[]{userCode,unitCode});
        /*
         * for (FUserunit usun : ls) {
         * usun.setUnitname(CodeRepositoryUtil.getValue
         * ("unitCode",usun.getId().getUnitcode() )); }
         */
        return ls;
    }
    @Transactional
    public String getNextKey() {
        return "s"+ DatabaseOptUtils.getNextKeyBySequence(this, "S_USER_UNIT_ID", 9);
/*        
        return DatabaseOptUtils.getNextKeyByHqlStrOfMax(this, CodeRepositoryUtil.USERCODE,
                "UserInfo WHERE userCode !='U0000000'", 7);*/
    }
    
    @Transactional
    public void deleteOtherPrimaryUnit(UserUnit object) {
        DatabaseOptUtils
                .doExecuteHql(
                        this,
                        "update UserUnit set isPrimary='F',lastModifyDate= ?  where userCode = ? and (unitCode <> ? or userStation <> ? or userRank <> ?) and isPrimary='T'",
                        new Object[]{ DatetimeOpt.currentUtilDate(),object.getUserCode(), object.getUnitCode(), object.getUserStation(),
                                object.getUserRank()});

    }
    
    @Transactional
    public void deleteUserUnitByUser(String userCode) {
        DatabaseOptUtils
                .doExecuteHql(
                        this,
                        "delete UserUnit  where userCode = ? ",
                        userCode);

    }
    
    @Transactional
    public void deleteUserUnitByUnit(String unitCode) {
        DatabaseOptUtils
                .doExecuteHql(
                        this,
                        "delete UserUnit  where unitCode = ? ",
                        unitCode);
    }
    
    @Transactional
    public UserUnit getPrimaryUnitByUserId(String userId) {
        List<UserUnit> list = listObjects(
                "FROM UserUnit where userCode=? and isPrimary='T'",
                userId);
        if (list != null && list.size()>0) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    @Transactional
    public List<UserUnit> listUnitUsersByUnitCode(String unitCode) {
        List<UserUnit> ls =listObjects(
                "FROM UserUnit where unitCode=?",
                unitCode);
        return ls;
    }

    /**
     * unitcode不为null就是某个处室的某个角色，为NULL就是所有处室的某个角色
     *
     * @param roleType roleType
     * @param roleCode roleCode
     * @param unitCode unitCode
     * @return List
     */

    @Transactional
    public List<UserUnit> listUserUnitsByRoleAndUnit(String roleType,
                                                   String roleCode, String unitCode) {
        List<UserUnit> ls = null;
        if (unitCode != null && !"".equals(unitCode)) {
            if ("gw".equals(roleType))
                ls =listObjects("FROM UserUnit where unitCode=? and userStation=? ",
                               new Object[]{ unitCode, roleCode});
            else if ("xz".equals(roleType))
                ls = listObjects("FROM UserUnit where unitCode=? and userRank=? ",
                        new Object[]{ unitCode, roleCode});
        } else {
            if ("gw".equals(roleType))
                ls = listObjects("FROM UserUnit where userStation=? ",
                                roleCode);
            else if ("xz".equals(roleType))
                ls = listObjects("FROM UserUnit where userRank=? ",
                                roleCode);
        }
        return ls;
    }



    @Transactional
    public List<UserUnit> listUnitUsersByUnitCodeAndFilter(String unitCode, PageDesc pageDesc,
            Map<String, Object> filterMap) {

        StringBuffer hql = new StringBuffer("FROM UserUnit where unitCode=? ");

        if (null != filterMap && null != filterMap.get("ORDER_BY")) {
            hql.append("order by " + filterMap.get("ORDER_BY"));
        }
        return super.listObjects(hql.toString(), unitCode, pageDesc);

    }



    /**
     * 批量添加或更新
     *
     * @param userunits List
     */
    @Transactional
    public void batchSave(List<UserUnit> userunits) {
        for (int i = 0; i < userunits.size(); i++) {
            super.saveObject(userunits.get(i));

            if (0 == i % 20) {
                DatabaseOptUtils.flush(this.getCurrentSession());
            }
        }
    }


    @Transactional
    public void batchMerge(List<UserUnit> userunits) {
        for (int i = 0; i < userunits.size(); i++) {
            mergeObject(userunits.get(i));

            if (19 == i % 20) {
                DatabaseOptUtils.flush(this.getCurrentSession());
            }
        }
    }
}
