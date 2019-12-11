package com.centit.framework.components.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserSetting;
import com.centit.framework.model.basedata.NoticeMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知中心实现，所有的消息通过此类进行发送，消息中心会通过接收用户设置的消息接收方式自行决定使用哪种消息发送方式
 */
public class NotificationCenterImpl implements NotificationCenter {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCenterImpl.class);

    protected Map<String, MessageSender> msgSenders = new HashMap<>();
    protected boolean writeNoticeLog;
    protected MessageSender defautlMsgSender;

    /**
     * 用户设置
     */
    protected PlatformEnvironment platformEnvironment;
    //注入接口MessageSender实现类，通过setMsgSenders方法进行配置

    public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        this.platformEnvironment = platformEnvironment;
    }

    public PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment == null) {
            platformEnvironment = WebOptUtils.getWebAppContextBean("platformEnvironment", PlatformEnvironment.class);
        }
        return platformEnvironment;
    }

    public void setWriteNoticeLog(boolean writeNoticeLog) {
        this.writeNoticeLog = writeNoticeLog;
    }



    public NotificationCenterImpl() {
        writeNoticeLog = false;
    }

    /**
     * 这个通过spring注入
     */
    public void initDummyMsgSenders() {
        msgSenders.put("dummy", DummyMessageSenderImpl.instance);
        defautlMsgSender = DummyMessageSenderImpl.instance;
    }

    /**
     * 如果 MessageSender 是spring托管类请
     * MessageSender msgManager =
                ContextLoaderListener.getCurrentWebApplicationContext().
                getBean("optLogManager",  OperationLogWriter.class);
        // 这个地方不能直接用 this， this不是spring管理的bean，必须从容器中获取托管的 bean
        notificationCenter.registerMessageSender("type",msgManager);
     */
    @Override
    public NotificationCenter registerMessageSender(String sendType,MessageSender sender){
        msgSenders.put(sendType, sender);
        return this;
    }

    @Override
    public MessageSender appointDefaultSendType(String sendType){
        MessageSender ms = msgSenders.get(sendType);
        if(ms!=null)
            defautlMsgSender = ms;
        return defautlMsgSender;
    }


    /**
     * 根据用户设定的方式发送消息
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主题
     * @return 结果
     */
    @Override
    public ResponseData sendMessage(String sender, String receiver, NoticeMessage message) {
        /*
         *  从用户设置中获得用户希望的接收消息的方式，可能是多个，比如用户希望同时接收到Email和短信，这样就要发送两天
         *  并在数据库中记录发送信息，在发送方式中用逗号把多个方式拼接在一起保存在对应的字段中
         */
        IUserSetting userReceiveWays = getPlatformEnvironment().getUserSetting(receiver, "receiveways");
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
                        ResponseData res = realSendMessage(msgSenders.get(val.trim()), sender, receiver, message);
                        if (res.getCode() != 0) {
                            sendErrorCount ++;
                            errorObjects.append(res.getMessage()).append("\r\n");
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
            ResponseData res = realSendMessage(defautlMsgSender, sender, receiver,message);
            if (res.getCode() != 0) {
                sendErrorCount ++;
                errorObjects.append(res.getMessage()).append("\r\n");
            }
        }

        int notifyState = sendErrorCount ==0? 0:(sendErrorCount==sendTypeCount? 1: 2);
        String returnText = sendErrorCount>0? errorObjects.toString():"OK!";
        if(writeNoticeLog){
            wirteNotifyLog(noticeType, sender, receiver, message,
                returnText, String.valueOf(notifyState));
        }

        return ResponseData.makeErrorMessage(notifyState, returnText);
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
    public ResponseData sendMessageAppointedType(String noticeType, String sender, String receiver, NoticeMessage message) {
        ResponseData res = realSendMessage(msgSenders.get(noticeType), sender, receiver, message);
        if(writeNoticeLog) {
            wirteNotifyLog(noticeType, sender, receiver, message,
                res.getMessage(), String.valueOf(res.getCode()));
        }
        return res;
    }



     /*
     * 保存系统通知中心数据
     */
    protected void wirteNotifyLog(String noticeType, String sender, String receiver,
                                NoticeMessage message, String errorText, String notifyState ) {
        Map<String,String> sysNotify = new HashMap<>();
        sysNotify.put("sender", sender);
        sysNotify.put("receiver", receiver);
        sysNotify.put("msgSubject", message.getMsgSubject());
        sysNotify.put("msgContent", message.getMsgContent());
        sysNotify.put("noticeType", noticeType);
        if(StringUtils.isNotBlank(message.getOptId())) {
            sysNotify.put("optId", message.getOptId());
        }
        if(StringUtils.isNotBlank(message.getOptMethod())) {
            sysNotify.put("optMethod", message.getOptMethod());
        }
        if(StringUtils.isNotBlank(message.getOptTag())) {
            sysNotify.put("optTag", message.getOptTag());
        }
        sysNotify.put("notifyState", notifyState);
        sysNotify.put("errorText", errorText);

        OperationLogCenter.log(sender, "Notify","notify", JSON.toJSONString(sysNotify));

    }

    /**
     * 发送通知中心消息
     * @param messageSender 真正的消息发送器
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主题
     * @return 结果信息
     */
    public static ResponseData realSendMessage(MessageSender messageSender, String sender, String receiver, NoticeMessage message ) {
        if (null == messageSender) {
            String errorText = "找不到消息发送器，请检查Spring中的配置和数据字典 WFNotice中的配置是否一致";
            logger.error(errorText);
            return ResponseData.makeErrorMessage(ResponseData.ERROR_SYSTEM_CONFIG, errorText);
        }
        try {
            return messageSender.sendMessage(sender, receiver, message);
        } catch (Exception e) {
            String errorText = messageSender.getClass().getName() + "发送通知失败，异常信息 " + e.getMessage();
            logger.error(errorText,e);
            return ResponseData.makeErrorMessage(ResponseData.HTTP_INTERNAL_SERVER_ERROR, errorText);
        }
    }

}
