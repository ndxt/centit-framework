package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.SysNotify;

@Repository
public class SysNotifyDao extends BaseDaoImpl<SysNotify, Long> {

    @Override
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

        }
        return filterField;
    }


    @Override
    @Transactional
    public SysNotify mergeObject(SysNotify o) {
        if (null == o.getNotifyId()) {
            o.setNotifyId(DatabaseOptUtils.getNextLongSequence(this, "S_MSGCODE"));
        }
        return super.mergeObject(o);
    }
}
