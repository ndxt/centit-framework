package com.centit.framework.system.service.impl;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.mybatis.dao.DatabaseOptUtils;
import com.centit.framework.system.dao.InnerMsgDao;
import com.centit.framework.system.dao.InnerMsgRecipientDao;
import com.centit.framework.system.po.InnerMsg;
import com.centit.framework.system.po.InnerMsgRecipient;
import com.centit.framework.system.service.InnerMsgManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service("innerMessageManager")
@Transactional
public class InnerMsgManagerImpl implements InnerMsgManager, MessageSender{

    @Resource
    @NotNull
    protected InnerMsgDao innerMsgDao;

    @Resource(name="innerMsgRecipientDao")
    @NotNull
    private InnerMsgRecipientDao recipientDao;
    
    
//    @Resource
//    //@NotNull
//    public void setNotificationCenter(NotificationCenter notificationCenter){
//    	if(notificationCenter!=null){
//	    	MessageSender sender =
//	                ContextLoaderListener.getCurrentWebApplicationContext().
//	                getBean("innerMessageManager",  MessageSender.class);
//	        // 这个地方不能直接用 this， this不是spring管理的bean，必须从容器中获取托管的 bean
//	    	notificationCenter.registerMessageSender("innerMsg", sender);
//    	}
//    }
    
    /*
     * 更新消息
     * 
     */
    @Override
    public void mergeMInnerMsg(InnerMsg msgCopy, InnerMsg msg) {
       innerMsgDao.deleteObject(msgCopy);
       innerMsgDao.mergeObject(msg);
    }
    /**
     * 发送消息
     * 
     */
    @Override
    public String sendMessage(String sender, String receiver,
            String msgSubject, String msgContent, String optId,
            String optMethod, String optTag) {
        InnerMsg msg = new InnerMsg(sender,msgSubject,msgContent);
        msg.setSendDate(new Date());
        msg.setMsgType("P");
        msg.setMailType("O");
        msg.setMsgState("U");
        msg.setOptId(optId);
        msg.setOptMethod(optMethod);
        msg.setOptTag(optTag);
        InnerMsgRecipient recipient=new InnerMsgRecipient();
        recipient.setMInnerMsg(msg);
        recipient.setReplyMsgCode(0);
        recipient.setReceiveType("P");
        recipient.setMailType("T");
        recipient.setMsgState("U");
        recipientDao.saveNewObject(recipient);
            return "OK";
    }
    
    @Override
    public String sendMessage(String sender, String receiver,
        String msgSubject, String msgContent) {
       
        return sendMessage( sender,  receiver,
                 msgSubject,  msgContent,  "msg",
                 "sender", "");
    }

    @Override
    public void deleteMsgById(String msgCode) {
       InnerMsg msg= innerMsgDao.getObjectById(msgCode);
       msg.setMsgState("D");
       innerMsgDao.saveObject(msg);
    }

	@Override
	public List<InnerMsg> listObjects(Map<String, Object> filterMap) {
		return innerMsgDao.listObjects(filterMap);
	}

	@Override
	public List<InnerMsg> listObjects(Map<String, Object> filterMap, PageDesc pageDesc) {
		return innerMsgDao.pageQuery(DatabaseOptUtils.prepPageParmers(filterMap,pageDesc,innerMsgDao.pageCount(filterMap)));
	}

	@Override
	public InnerMsg getObjectById(String msgCode) {
		return innerMsgDao.getObjectById(msgCode);
	} 
}
