package com.centit.framework.system.dao;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.InnerMsgRecipient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InnerMsgRecipientDao extends BaseDaoImpl<InnerMsgRecipient, String> {

    @Override
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("RECEIVE", "receive = :RECEIVE");
            filterField.put("sender", "msgCode in ( select  msgCode from InnerMsg where sender = :sender )");
            filterField.put("msgContent", "msgCode in ( select  msgCode from InnerMsg where msgContent LIKE :msgContent )");
            filterField.put("msgTitle", "msgCode in ( select  msgCode from InnerMsg where msgTitle LIKE :msgTitle )");
            filterField.put("mailType", "msgCode in ( select  msgCode from InnerMsg where mailType = :mailType )");
            filterField.put("mailTypeNot", "msgCode in ( select  msgCode from InnerMsg where mailType != :mailTypeNot )");
            filterField.put("msgStateNot", "msgState != :msgStateNot");
            filterField.put("innerMsgStateNot", "msgCode in ( select  msgCode from InnerMsg where msgState != :innerMsgStateNot )");
            filterField.put("isRecycled", CodeBook.EQUAL_HQL_ID);
            filterField.put("MSGSTATE", CodeBook.EQUAL_HQL_ID);
            filterField.put("msgType", "msgCode in ( select  msgCode from InnerMsg where msgType = :msgType )");
            filterField.put(CodeBook.ORDER_BY_HQL_ID, "msgCode desc");
        }
        return filterField;
    }
    
    
    /*
     * 新建
     * 
     */
    @Override
    @Transactional
    public String saveNewObject(InnerMsgRecipient recipient){
        String id=DatabaseOptUtils.getNextKeyBySequence(this, "S_RECIPIENT",16);
        recipient.setId(id);
        super.saveNewObject(recipient);
        return id;
    }
    
    /*
     * 两人间来往消息列表
     * 
     */
    @Transactional
    public  List<InnerMsgRecipient> getExchangeMsgs(String sender, String receiver) {
        
        String queryString ="From InnerMsgRecipient where( (msgCode in (Select msgCode from InnerMsg where sender= ? and (mailType='I' or mailType='O')) and Receive= ?) " +
        		"or (msgCode in(Select msgCode from InnerMsg where sender= ? and (mailType='I' or mailType='O')) and Receive=? )) order by msgCode desc";
        List<InnerMsgRecipient> l = listObjects(queryString, new Object[]{sender,receiver,receiver,sender});
        return l;
    }
   
    public long getUnreadMessageCount(String userCode){
        Object obj= DatabaseOptUtils.getSingleObjectByHql(this, "select count(1)"
                + " from InnerMsgRecipient re Where re.receive = ? and msgState ='U'",userCode);
        if (obj == null)
            return 0;
        if (obj instanceof Long)
            return ((Long) obj).longValue();
        if (obj instanceof String)
            return Long.valueOf(obj.toString()).longValue();
        if (obj instanceof BigDecimal)
            return ((BigDecimal) obj).longValue();
        return 0;
    }
    
    public List<InnerMsgRecipient> listUnreadMessage(String userCode){
        String queryString ="From InnerMsgRecipient re Where re.receive = ? and re.msgState ='U' "
                + "  order by re.id desc";
        return listObjects(queryString, userCode);
    }
}
 