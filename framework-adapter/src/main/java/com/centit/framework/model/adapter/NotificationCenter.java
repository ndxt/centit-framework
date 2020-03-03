package com.centit.framework.model.adapter;

import com.centit.framework.common.ResponseData;
import com.centit.framework.model.basedata.NoticeMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统的内置的通知中心bean名称为  notificationCenter
 * 在使用的地方 只要添加如下代码
        /@ Resource
        /@ NotNull
        private NotificationCenter notificationCenter;
         就可以使用，注意：去掉@后的空格
         通知中心内置了两个发送消息的方法
         sendType "I"
        /@ Resource(name = "InnerMessageManager")
        /@ NotNull
        private MessageSender innerMessageSender;

        sendType "I"
        /@ Resource(name = "emailMessageSender")
        /@ NotNull
        private MessageSender emailMessageSender;
 * @author codefan
 */
public interface NotificationCenter extends MessageSender {
    /**
     * 注册新的发送消息通知
     * @param sendType String 发送类型
     * @param sender MessageSender
     * @return NotificationCenter 系统的内置的通知中心
     */
    NotificationCenter registerMessageSender(String sendType, MessageSender sender);

    /**
     * 设置默认的发送通知内部
     * @param sendType 发送类型
     * @return 默认的消息发送器
     */
    MessageSender appointDefaultSendType(String sendType);

    /**
     * 发送内部系统通知，通过自定的方式 发送
     * @param noticeType 通知方式
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
    ResponseData sendMessageAppointedType(String noticeType, String sender, String receiver, NoticeMessage message);
    /**
     * 推送内部系统消息，比如通过socket
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
    ResponseData pushMessage(String sender, String receiver, NoticeMessage message);

    /**
     * 推送送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receivers   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
    default ResponseData pushMessage(String sender, Collection<String> receivers, NoticeMessage message){
        int error = 0; int success = 0;
        Map<String, Object> result = new HashMap<>();
        for (String receiver : receivers){
            ResponseData response = pushMessage(sender, receiver, message);
            if(response.getCode() !=0){
                error ++;
                result.put(receiver, response.getMessage());
            } else {
                success ++;
            }
        }
        String msgStr = "一共推送了" + (error+success) + "条消息，成功"+success+"条，失败"+error+"条。";
        int resCode = error == 0? 0:(success==0?2:3);
        return ResponseData.makeErrorMessageWithData(result, resCode, msgStr);
    }

}
