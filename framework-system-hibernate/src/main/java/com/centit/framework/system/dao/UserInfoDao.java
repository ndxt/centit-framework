package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.common.ObjectException;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.UserInfo;
import com.centit.support.database.QueryUtils;

@Repository
public class UserInfoDao extends BaseDaoImpl<UserInfo, String> {
    @Transactional
    public boolean checkIfUserExists(UserInfo user) {
        long hasExist = 0l;
        String hql;

        if (StringUtils.isNotBlank(user.getUserCode())) {
            hql = "SELECT COUNT(*) FROM UserInfo WHERE userCode = " + QueryUtils.buildStringForQuery(user.getUserCode());
            hasExist = DatabaseOptUtils.getSingleIntByHql(this, hql);
        }

        hql = "SELECT COUNT(*) FROM UserInfo WHERE loginName = " + QueryUtils.buildStringForQuery(user.getLoginName());

        if (StringUtils.isNotBlank(user.getUserCode())) {
            hql += " AND userCode <> " + QueryUtils.buildStringForQuery(user.getUserCode());
        }
        long size = DatabaseOptUtils.getSingleIntByHql(this, hql);
        if (size >= 1) {
            throw new ObjectException("登录名：" + user.getLoginName() + " 已存在!!!");
        }

        return hasExist > 0L;
    }

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put(CodeRepositoryUtil.USER_CODE, CodeBook.LIKE_HQL_ID);
            filterField.put("USERCODE_EQ", CodeBook.EQUAL_HQL_ID);
            filterField.put("USERNAME", CodeBook.LIKE_HQL_ID);
            filterField.put("ISVALID", CodeBook.EQUAL_HQL_ID);
            filterField.put("LOGINNAME", CodeBook.LIKE_HQL_ID);
            filterField.put("USERSTATE", CodeBook.EQUAL_HQL_ID);
            filterField.put("USERORDER", CodeBook.EQUAL_HQL_ID);
            filterField.put("USERTAG", CodeBook.EQUAL_HQL_ID);
            filterField.put("USERWORD", CodeBook.EQUAL_HQL_ID);

            filterField.put("byUnderUnit", "userCode in ( select  id.userCode from UserUnit where id.unitCode = ? ) ");

            filterField.put("queryByUnit", "userCode in ( select  id.userCode from UserUnit where id.unitCode = ? ) ");
            filterField.put("queryByGW", "userCode in ( select  id.userCode from UserUnit where id.userStation = ? )");
            filterField.put("queryByXZ", "userCode in ( select  id.userCode from UserUnit where id.userRank = ? )");
            filterField
                    .put("queryByRole",
                            "userCode in (select r.id.userCode from UserRole r, RoleInfo i where r.id.roleCode = ? and r.id.roleCode = i.roleCode and i.isValid = 'T')");

            filterField.put(CodeBook.ORDER_BY_HQL_ID, "userOrder asc");
        }
        return filterField;
    }

    @Transactional
    public String getNextKey() {
        return "U"+ DatabaseOptUtils.getNextKeyBySequence(this, "S_USERCODE", 7);
/*        
        return DatabaseOptUtils.getNextKeyByHqlStrOfMax(this, CodeRepositoryUtil.USERCODE,
                "UserInfo WHERE userCode !='U0000000'", 7);*/
    }

    @Override
    @Transactional
    public void saveObject(UserInfo o) {
        if (!StringUtils.isNotBlank(o.getUserCode())) {
            // o.setUsercode("u" + this.getNextKey());
            o.setUserCode(this.getNextKey());
        }

        // 无密码，初始化密码
        if (!StringUtils.isNotBlank(o.getUserPin())) {
            o.setUserPin(new Md5PasswordEncoder().encodePassword("000000", o.getUserCode()));
        }

        super.saveObject(o);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public List<FVUserOptList> getAllOptMethodByUser(String userCode) {
        String[] params = null;
        String hql = "FROM FVUserOptList urv where urv.id.userCode=?";

        params = new String[]{userCode};
        List<FVUserOptList> ls = (List<FVUserOptList>) DatabaseOptUtils.findObjectsByHql
                (this,hql, (Object[]) params);
        return ls;
    }
	/*
     * public FUserinfo loginUser(String userName, String password) { return
	 * (FUserinfo) getHibernateTemplate().find(
	 * "FROM FUserinfo WHERE username = ? AND userpin = ? ", new Object[] {
	 * userName, password }).get(0); }
	 */
   
    @Transactional
    public List<UserInfo> listUnderUnit(Map<String, Object> filterMap) {
        return this.listObjects(filterMap);
       /* String shql = "from f_userinfo where 1=1 ";

        HqlAndNamedParams hql = builderHqlAndNamedParams(shql, filterMap);
        String hql1 = "select *  " + hql.getHql();
        System.out.println(1);
        List<UserInfo> l = null;
        try {
            l = (List<UserInfo>)getHibernateTemplate().executeFind(
                    new SQLWithNamedParamsCallBack(hql1, hql.getParams(), UserInfo.class));
        } catch (Exception e) {
            log.error(e.getMessage());
            // return null;
        }

        return l;*/
    }

    @Transactional
    public List<UserInfo> listUnderUnit(Map<String, Object> filterMap, PageDesc pageDesc) {
        return this.listObjects(filterMap, pageDesc);
    }

    @Transactional
    public UserInfo getUserByCode(String userCode) {
        return getObjectById(userCode);
    }
    
    @Transactional
    public UserInfo getUserByLoginName(String loginName) {
        return super.getObjectByProperty("loginName",loginName.toLowerCase());
    }
    
    @Transactional
    public UserInfo getUserByRegEmail(String regEmail) {
    	return super.getObjectByProperty("regEmail", regEmail);
    }
    
    @Transactional
    public UserInfo getUserByRegCellPhone(String regCellPhone) {
    	return super.getObjectByProperty("regCellPhone", regCellPhone);
    }
    
    @Transactional
    public UserInfo getUserByTag(String userTag) {
    	return super.getObjectByProperty("userTag", userTag);
    }
    
    @Transactional
    public UserInfo getUserByWord(String userWord) {
    	return super.getObjectByProperty("userWord", userWord);
    }
    
    /**
     * 批量添加或更新
     *
     * @param userinfos List
     */
    @Transactional
    public void batchSave(List<UserInfo> userinfos) {
        for (int i = 0; i < userinfos.size(); i++) {
            this.saveObject(userinfos.get(i));

            if (19 == i % 20) {
                DatabaseOptUtils.flush(this.getCurrentSession());
            }
        }
    }
    @Transactional
    public void batchMerge(List<UserInfo> userinfos) {
        for (int i = 0; i < userinfos.size(); i++) {
            mergeObject(userinfos.get(i));

            if (19 == i % 20) {
                DatabaseOptUtils.flush(this.getCurrentSession());
            }
        }
    }

    @Transactional
    public List<UserInfo> search(String key, String[] field) {
        StringBuilder hql = new StringBuilder("from UserInfo u where ");
        String params[] = new String[field.length];
        String sMatch = QueryUtils.getMatchString(key);
        for (int i = 0; i < field.length; i++) {
            hql.append("u." + field[i] + " like ? ");//'%" +  key + "%' ");
            if (i != field.length - 1) {
                hql.append(" or ");
            }
            params[i] = sMatch; 
        }
        
        return listObjects( hql.toString(),params);
    }
}
