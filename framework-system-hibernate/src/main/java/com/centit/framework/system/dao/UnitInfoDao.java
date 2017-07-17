package com.centit.framework.system.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.UnitInfo;
import com.centit.framework.system.po.UserInfo;

@Repository
public class UnitInfoDao extends BaseDaoImpl<UnitInfo, String> {
    public static final Logger logger = LoggerFactory.getLogger(UnitInfoDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("UNITCODE", CodeBook.EQUAL_HQL_ID);
            filterField.put("UNITNAME", CodeBook.LIKE_HQL_ID);
            filterField.put("ISVALID", CodeBook.EQUAL_HQL_ID);
            filterField.put("UNITTAG", CodeBook.EQUAL_HQL_ID);
            filterField.put("UNITWORD", CodeBook.EQUAL_HQL_ID);
            filterField.put("PARENTUNIT", CodeBook.EQUAL_HQL_ID);
            filterField.put("NP_TOPUnit", "(parentUnit is null or parentUnit='0')");
            filterField.put(CodeBook.ORDER_BY_HQL_ID, " unitOrder, unitCode ");
        }
        return filterField;
    }

    @Transactional
    public String getNextKey() {
	/*	return getNextKeyByHqlStrOfMax("unitCode",
						"FUnitinfo WHERE unitCode !='99999999'",6);*/
        return DatabaseOptUtils.getNextKeyBySequence(this, "S_UNITCODE", 6);
    }

    @Transactional
    public String getUnitCode(String depno) {
        List<UnitInfo> ls = listObjects("FROM UnitInfo where depNo=?", depno);
        if (ls != null) {
            return ls.get(0).getUnitCode();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.MANDATORY) 
    public List<UserInfo> listUnitUsers(String unitCode) {
        String sSqlsen = "select a.* " +
                "from F_USERINFO a join F_USERUNIT b on(a.USERCODE=b.USERCODE) " +
                "where b.UNITCODE =?";

        return (List<UserInfo>) DatabaseOptUtils.findObjectsBySql(this, sSqlsen, new Object[]{unitCode} ,UserInfo.class);
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.MANDATORY) 
    public List<UserInfo> listRelationUsers(String unitCode) {
        String sSqlsen = "select * FROM F_USERINFO ui where ui.USERCODE in " +
                "(select USERCODE from F_USERUNIT where UNITCODE= ? ) or " +
                "ui.USERCODE in (select USERCODE from F_USERROLE where ROLECODE like ? ";

        return (List<UserInfo>) DatabaseOptUtils.findObjectsBySql(
                this, sSqlsen,new Object[]{unitCode,unitCode+ "-%"}, UserInfo.class);
    }

    @Transactional
    public String getUnitNameOfCode(String unitcode) {
       return String.valueOf( DatabaseOptUtils.getSingleObjectBySql(this,
                "select UNITNAME from F_UNITINFO where UNITCODE=?", unitcode ));
    }

    /**
     * 批量添加或更新
     *
     * @param unitinfos
     */
    @Transactional
    public void batchSave(List<UnitInfo> unitinfos) {
        for (int i = 0; i < unitinfos.size(); i++) {
            saveObject(unitinfos.get(i));
        }
    }
    @Transactional
    public void batchMerge(List<UnitInfo> unitinfos) {
        for (int i = 0; i < unitinfos.size(); i++) {
            this.mergeObject(unitinfos.get(i));

            if (19 == i % 20) {
                DatabaseOptUtils.flush(this.getCurrentSession());
            }
        }
    }
    @Transactional
    public UnitInfo getUnitByName(String name) {
        if (StringUtils.isNotBlank(name)) {
            String hql = "from UnitInfo where unitName = ? or unitShortName = ?"
            			+ " order by unitOrder asc";
            List<UnitInfo> list = listObjects(hql,
            		new Object[]{name,name});
            if (list !=null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }
    
    @Transactional
    public UnitInfo getUnitByTag(String unitTag) {
    	return super.getObjectByProperty("unitTag", unitTag);
    }
    
    @Transactional
    public UnitInfo getUnitByWord(String unitWord) {
    	return super.getObjectByProperty("unitWord", unitWord);
    }
    
    @Transactional
    public List<UnitInfo> listSubUnits(String unitCode){
    	return super.listObjectByProperty("parentUnit", unitCode);
    	/*String hql = "from UnitInfo where parentUnit = ?";
    	return listObjects(hql,
    		new Object[]{unitCode,unitCode});*/
    }

    @Transactional(propagation=Propagation.MANDATORY)
    public List<UnitInfo> listAllSubUnits(String unitCode){
        UnitInfo unitInfo = this.getObjectById(unitCode);
        return listSubUnitsByUnitPaht(unitInfo.getUnitPath());
    }
    
    @Transactional(propagation=Propagation.MANDATORY) 
    public List<UnitInfo> listSubUnitsByUnitPaht(String unitPath){
    	String hql = "from UnitInfo where unitPath like ?";
    	return listObjects(hql,
    		new Object[]{unitPath+"/%"});
    }
}
