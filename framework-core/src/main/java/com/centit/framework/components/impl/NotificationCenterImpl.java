package com.centit.framework.components.impl;

import java.util.HashMap;
import java.util.Map;

import com.centit.framework.model.basedata.IUserSetting;
import com.centit.msgpusher.msgpusher.po.SimplePushMessage;
import com.centit.msgpusher.msgpusher.po.SimplePushMsgPoint;
import com.centit.msgpusher.msgpusher.websocket.SocketMsgPusher;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.PlatformEnvironment;

/**
 * 通知中心实现，所有的消息通过此类进行发送，消息中心会通过接收用户设置的消息接收方式自行决定使用哪种消息发送方式
 */
public class NotificationCenterImpl implements NotificationCenter {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCenterImpl.class);
    private static Map<String, MessageSender> msgSenders = new HashMap<>();

    protected SocketMsgPusher socketMsgPusher;
    /**
     * 用户设置
     */
    private PlatformEnvironment platformEnvironment;
    //注入接口MessageSender实现类，通过setMsgSenders方法进行配置
    
    public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        this.platformEnvironment = platformEnvironment;
    }

    private MessageSender defautlMsgSender;
    /**
     * 这个通过spring注入
     */
    public void initMsgSenders() {
        msgSenders.put("dummy", DummyMessageSenderImpl.instance);
        defautlMsgSender = DummyMessageSenderImpl.instance;
        //目前支持内部消息、短信
        //msgSenders中的键与UserSetting表中receiveways中值一一对应
        /*msgSenders.put("I", innerMessageSender);
        */
    }

    /**
     * 如果 MessageSender 是spring托管类请 
     * MessageSender msgManager = 
                ContextLoaderListener.getCurrentWebApplicationContext().
                getBean("optLogManager",  OperationLogWriter.class);
        // 这个地方不能直接用 this， this不是spring管理的bean，必须从容器中获取托管的 bean
        notificationCenter.registerMessageSender("type",msgManager); 
     */
    public NotificationCenter registerMessageSender(String sendType,MessageSender sender){
        msgSenders.put(sendType, sender);
        return this;
    }
    
    public MessageSender setDefaultSendType(String sendType){
        MessageSender ms = msgSenders.get(sendType);
        if(ms!=null)
            defautlMsgSender = ms;
        return defautlMsgSender;
    }

    /**
     * 根据用户设定的方式发送消息
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @return
     */
    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag) {

        /**
         *  从用户设置中获得用户希望的接收消息的方式，可能是多个，比如用户希望同时接收到Email和短信，这样就要发送两天
         *  并在数据库中记录发送信息，在发送方式中用逗号把多个方式拼接在一起保存在对应的字段中
         */
        String returnText = "OK";
        IUserSetting userReceiveWays = platformEnvironment.getUserSetting(receiver, "receiveways");
        String receiveways = userReceiveWays==null?null:userReceiveWays.getParamValue();
        StringBuilder errorObjects = new StringBuilder();

        String noticeType ="" ;//default;
        int sendTypeCount = 0;
        int sendErrorCount = 0;
        if (receiveways!= null && StringUtils.isNotBlank(receiveways)) {
            String[] vals = receiveways.split(",");
            
            if (ArrayUtils.isNotEmpty(vals)) {
                
                noticeType = receiveways; 
                for (String val : vals) {
                    if (StringUtils.isNotBlank(val)) {
                        sendTypeCount++;
                        String errorText = realSendMessage(msgSenders.get(val.trim()), sender, receiver, msgSubject, msgContent,
                                optId,  optMethod,  optTag);
                        if (StringUtils.isNotBlank(errorText)) {
                            sendErrorCount ++;
                            errorObjects.append(errorText).append("\r\n");
                        }
                    }
                }
            }
        }
        if(sendTypeCount==0 || sendErrorCount==sendTypeCount){
            String infoText = "用户 " + CodeRepositoryUtil.getUserInfoByCode(receiver).getLoginName() + " " +
                    "未选择任何通知接收方式，默认通过内部消息发送通知";
            logger.info(infoText);
            noticeType = StringUtils.isBlank(noticeType)?"D":noticeType+",D";
            sendTypeCount++;
            String errorText = realSendMessage(defautlMsgSender, sender, receiver, msgSubject, msgContent,
                    optId,  optMethod,  optTag);
            if (StringUtils.isNotBlank(errorText)) {
                sendErrorCount ++;
                errorObjects.append(errorText).append("\r\n");
            }
        }        

        if(socketMsgPusher!=null){
            try {
                socketMsgPusher.pushMessage(
                        new SimplePushMessage(sender,msgSubject, msgContent),
                        new SimplePushMsgPoint(receiver));
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        String notifyState =sendErrorCount==0?"0":(sendErrorCount==sendTypeCount?"1":"2");
        
        if (sendErrorCount>0) {//返回异常信息
            returnText = errorObjects.toString();
         }
        wirteNotifyLog(sender, receiver, msgSubject, msgContent, noticeType,
                optId,  optMethod,  optTag,
                returnText, notifyState);

        return returnText;
    }
       

    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent) {
        
        return sendMessage( sender,  receiver,  msgSubject,  msgContent,
                 "",  "",  "");
    }

    /**
     * 发送指定类别的消息
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @param noticeType   指定发送类别
     * @return
     */
    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag, String noticeType) {
        String returnText = "OK";
        String errorText = realSendMessage(msgSenders.get(noticeType), sender, receiver, msgSubject, msgContent,
                optId,  optMethod,  optTag);

        if(socketMsgPusher!=null){
            try {
                socketMsgPusher.pushMessage(
                        new SimplePushMessage(sender,msgSubject, msgContent),
                        new SimplePushMsgPoint(receiver));
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        //发送成功
        String notifyState = "0";
        if (StringUtils.isNotBlank(errorText)) {
            notifyState = "1";
            returnText = errorText;
        }
        wirteNotifyLog(sender, receiver, msgSubject, msgContent, noticeType, 
                optId,  optMethod,  optTag,
                errorText, notifyState);
        return returnText;
    }
    
    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent, String noticeType) {
        
        return sendMessage( sender,  receiver,  msgSubject,  msgContent,
                 "",  "",  "",  noticeType);
    }

     /**
     * 保存系统通知中心数据
     *
     * @param sender
     * @param receiver
     * @param msgSubject
     * @param msgContent
     * @param noticeType
     * @param errorText
     * @param notifyState
     */
    private void wirteNotifyLog(String sender, String receiver,
                               String msgSubject, String msgContent, String noticeType, 
                               String optId, String optMethod, String optTag,
                               String errorText, String notifyState ) {
        Map<String,String> sysNotify = new HashMap<String,String>();
        sysNotify.put("sender", sender);
        sysNotify.put("receiver", receiver);
        sysNotify.put("msgSubject", msgSubject);
        sysNotify.put("msgContent", msgContent);
        sysNotify.put("noticeType", noticeType);
        sysNotify.put("optId", optId);
        sysNotify.put("optMethod", optMethod);
        sysNotify.put("optTag", optTag);
        sysNotify.put("notifyState", notifyState);
        sysNotify.put("errorText", errorText);
        
        OperationLogCenter.log(sender, "Notify","notify", JSON.toJSONString(sysNotify));
  
    }

    /**
     * 发送通知中心消息
     * @param messageSender
     * @param sender
     * @param receiver
     * @param msgSubject
     * @param msgContent
     * @return
     */
    private static String realSendMessage(MessageSender messageSender, String sender, String receiver, String msgSubject,
                                          String msgContent , String optId, String optMethod, String optTag) {
        if (null == messageSender) {
            String errorText = "找不到消息发送器，请检查Spring中的配置和数据字典 WFNotice中的配置是否一致";
            logger.error(errorText);
            return errorText;
        }
        try {
            messageSender.sendMessage(sender, receiver, msgSubject, msgContent,optId,  optMethod,  optTag);

        } catch (Exception e) {
            String errorText = messageSender.getClass().getName() + "发送通知失败，异常信息 " + e.getMessage();
            logger.error(errorText,e);
            return errorText;
        }

        return null;
    }


    public void setSocketMsgPusher(SocketMsgPusher socketMsgPusher) {
        this.socketMsgPusher = socketMsgPusher;
    }
}
