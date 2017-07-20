package com.centit.framework.system.dao;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.OptLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class OptLogDao extends BaseDaoImpl<OptLog, Long> {

    public static final Logger logger = LoggerFactory.getLogger(OptLogDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<>();

            filterField.put("logId", CodeBook.EQUAL_HQL_ID);

            filterField.put("logLevel", CodeBook.LIKE_HQL_ID);

            filterField.put(CodeRepositoryUtil.USER_CODE, CodeBook.EQUAL_HQL_ID);

            filterField.put("(date)optTimeBegin", "opttime >= :optTimeBegin ");

            filterField.put("(date)optTimeEnd", "opttime <= :optTimeEnd");

            filterField.put("optId", CodeBook.LIKE_HQL_ID);

            filterField.put("optCode", CodeBook.LIKE_HQL_ID);

            filterField.put("optContent", CodeBook.LIKE_HQL_ID);

            filterField.put("oldValue", CodeBook.LIKE_HQL_ID);
            
            filterField.put("optMethod", CodeBook.EQUAL_HQL_ID);

            filterField.put("optId", CodeBook.EQUAL_HQL_ID);
            
            filterField.put(CodeBook.ORDER_BY_HQL_ID, " opttime desc");

        }
        return filterField;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<String> listOptIds() {
        final String hql = "select DISTINCT f.optId from OptLog f";

        return (List<String>) DatabaseOptUtils.findObjectsByHql(this, hql);
    }

    @Override
    @Transactional
    public OptLog mergeObject(OptLog o) {
        if (null == o.getLogId()) {
            o.setLogId(DatabaseOptUtils.getNextLongSequence(this, "S_SYS_LOG"));
        }
        return super.mergeObject(o);
    }

    @Transactional
    public void delete(Date begin, Date end) {
        String hql = "delete from OptLog o where 1=1 ";
        List<Object> objects = new ArrayList<>();
        if (null != begin) {
            hql += "and o.optTime > ?";
            objects.add(begin);
        }
        if (null != end) {
            hql += "and o.optTime < ?";
            objects.add(end);
        }

        DatabaseOptUtils.doExecuteHql(this, hql, objects.toArray(new Object[objects.size()]));
        
    }

}
