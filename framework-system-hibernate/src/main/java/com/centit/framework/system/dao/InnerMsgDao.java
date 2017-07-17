package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.InnerMsg;
@Repository
public class InnerMsgDao extends BaseDaoImpl<InnerMsg, String> {
    
    @Override
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("msgType", CodeBook.EQUAL_HQL_ID);
            filterField.put("msgTitle", CodeBook.LIKE_HQL_ID);
            filterField.put("msgContent", CodeBook.LIKE_HQL_ID);
            filterField.put("msgStateNot", "msgState != ?");
            filterField.put("sender", CodeBook.EQUAL_HQL_ID);
            filterField.put("receive", "msgCode in (select re.mInnerMsg.msgCode as msgCode from InnerMsgRecipient re Where re.receive = ?)");
            filterField.put(CodeBook.ORDER_BY_HQL_ID, "sendDate desc");
        }
        return filterField;
    }
    /*
     * 新建
     * 
     */
    @Override
    @Transactional
    public String saveNewObject(InnerMsg o) {
        String msgCode = DatabaseOptUtils.getNextKeyBySequence(this, "S_MSGCODE",16);
        o.setMsgCode(msgCode);
        super.saveNewObject(o);
        return msgCode;
    }
    
}
