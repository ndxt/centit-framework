package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.NoticeMessage;

/**
 * 系统消息发送接口
 * 系统的内置的两个发送消息的实现,可以通过以下代码引入
        Resource(name = "innerMessageManager")
        NotNull
        private MessageSender innerMessageSender;

        Resource(name = "emailMessageSender")
        NotNull
        private MessageSender emailMessageSender;
         注意：去掉@后的空格
 * @author ljy
 */
public interface MessageSender {

    /**
     /**
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
    String sendMessage( String sender, String receiver, NoticeMessage message);

    /**
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @return "OK" 表示成功，其他的为错误信息
     */
    default String sendMessage(String sender, String receiver, String msgSubject, String msgContent){
        NoticeMessage message = new NoticeMessage();
        message.setMsgSubject(msgSubject);
        message.setMsgContent(msgContent);
        return sendMessage(sender, receiver, message);
    }



    /**
     *
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @return "OK" 表示成功，其他的为错误信息
     */
    default String sendMessage(String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag){
        NoticeMessage message = new NoticeMessage();
        message.setMsgSubject(msgSubject);
        message.setMsgContent(msgContent);
        message.setOptId(optId);
        message.setOptMethod(optMethod);
        message.setOptTag(optTag);
        return sendMessage(sender, receiver, message);
    }

    /**
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgType    消息类别
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @return "OK" 表示成功，其他的为错误信息
     */
    default String sendMessage(String sender, String receiver, String msgType, String msgSubject, String msgContent){
        NoticeMessage message = new NoticeMessage();
        message.setMsgType(msgType);
        message.setMsgSubject(msgSubject);
        message.setMsgContent(msgContent);
        return sendMessage(sender, receiver, message);
    }



    /**
     *
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgType    消息类别
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @return "OK" 表示成功，其他的为错误信息
     */
    default String sendMessage(String sender, String receiver, String msgType, String msgSubject, String msgContent,
                               String optId, String optMethod, String optTag){
        NoticeMessage message = new NoticeMessage();
        message.setMsgType(msgType);
        message.setMsgSubject(msgSubject);
        message.setMsgContent(msgContent);
        message.setOptId(optId);
        message.setOptMethod(optMethod);
        message.setOptTag(optTag);
        return sendMessage(sender, receiver, message);
    }


}
