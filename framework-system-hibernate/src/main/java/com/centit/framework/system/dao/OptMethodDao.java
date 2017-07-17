package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.OptMethod;

@Repository
public class OptMethodDao extends BaseDaoImpl<OptMethod, String> {

    @Transactional
    public List<OptMethod> listOptMethodByOptID(String sOptID) {
        return listObjects("FROM OptMethod WHERE optId =?", sOptID);
    }

    @Transactional
    public List<OptMethod> listOptMethodByRoleCode(String roleCode) {
        return listObjects("FROM OptMethod WHERE optCode in "
        		+ "(select id.optCode from RolePower where id.roleCode = ?)"
        		+ " order by optId", roleCode);
    }
    
    @Transactional
    public int getOptMethodSumByOptID(String sOptID) {
        return Integer.valueOf(String.valueOf(DatabaseOptUtils.getSingleObjectByHql(this,
                "SELECT count(optcode) FROM OptMethod WHERE optId = ?", sOptID)));
    }

    @Transactional
    public void deleteOptMethodsByOptID(String sOptID) {
        DatabaseOptUtils.doExecuteHql(this, "DELETE FROM OptMethod WHERE optId = ?", sOptID);
    }

  
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("OPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("PREOPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("ISINTOOLBAR", CodeBook.EQUAL_HQL_ID);
            filterField.put("TOPOPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTTYPE", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTNAME", CodeBook.LIKE_HQL_ID);
        }
        return filterField;
    }

    @Transactional
    public String getNextOptCode() {
    	return DatabaseOptUtils.getNextValueOfSequence(this, "S_OPTDEFCODE");
    }

}
