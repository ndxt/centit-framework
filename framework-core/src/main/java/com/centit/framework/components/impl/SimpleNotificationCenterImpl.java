package com.centit.framework.components.impl;

import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通知中心实现，所有的消息通过此类进行发送，消息中心会通过接收用户设置的消息接收方式自行决定使用哪种消息发送方式
 */
public class SimpleNotificationCenterImpl implements NotificationCenter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleNotificationCenterImpl.class);

    private MessageSender realMessageSender;


    /**
     * 这个通过spring注入
     */
    public SimpleNotificationCenterImpl() {
        realMessageSender = DummyMessageSenderImpl.instance;
    }


    public void setRealMessageSender(MessageSender realMsgSender) {
        realMessageSender = realMsgSender;
    }
    /**
     * 根据用户设定的方式发送消息
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主题
     * @return 结果
     */
    @Override
    public String sendMessage(String sender, String receiver, NoticeMessage message) {
        return realMessageSender.sendMessage(sender, receiver, message);
    }


    /**
     * 注册新的发送消息通知
     *
     * @param sendType String 发送类型
     * @param sender   MessageSender
     * @return NotificationCenter 系统的内置的通知中心
     */
    @Override
    public NotificationCenter registerMessageSender(String sendType, MessageSender sender) {
        this.realMessageSender = sender;
        return this;
    }

    /**
     * 设置默认的发送通知内部
     *
     * @param sendType 发送类型
     * @return 默认的消息发送器
     */
    @Override
    public MessageSender appointDefaultSendType(String sendType) {
        return this.realMessageSender;
    }

    /**
     * 发送指定类别的消息
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主题
     * @param noticeType   指定发送类别
     * @return 结果
     */
    @Override
    public String sendMessageAppointedType(String noticeType, String sender, String receiver, NoticeMessage message) {
        return realMessageSender.sendMessage(sender, receiver, message);
    }

}
