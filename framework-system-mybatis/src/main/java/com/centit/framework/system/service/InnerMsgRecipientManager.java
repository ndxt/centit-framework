package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.InnerMsg;
import com.centit.framework.system.po.InnerMsgRecipient;

public interface InnerMsgRecipientManager{
	public List<InnerMsgRecipient> listObjects(Map<String, Object> filterMap);

	public List<InnerMsgRecipient> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	
	public InnerMsgRecipient getObjectById(String id);

    List<InnerMsgRecipient> getExchangeMsgs(String sender, String receiver);

    void mergeRecipient(InnerMsgRecipient recipientCopy,
            InnerMsgRecipient recipient);
    /*
     * msg为消息主题，recipient为接收人
     * 添加消息接受者,群发(receipient.receive为数组，但是保存到数据库是挨个保存)
     * 
     */
    void sendMsg(InnerMsgRecipient recipient,String sysUserCode);

    void noticeByUnitCode(String unitCode, InnerMsg msg) throws Exception;

    void deleteOneRecipientById(String id);
    
    long getUnreadMessageCount(String userCode);
    
    List<InnerMsgRecipient> listUnreadMessage(String userCode);
}
