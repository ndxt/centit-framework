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
import com.centit.support.common.DoubleAspect;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通知中心实现，所有的消息通过此类进行发送，消息中心会通过接收用户设置的消息接收方式自行决定使用哪种消息发送方式
 */
public class NotificationCenterImpl implements NotificationCenter {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCenterImpl.class);

    protected Map<String, MessageSender> msgSenders = new HashMap<>();
    protected boolean writeNoticeLog;
    protected MessageSender defautlMsgSender;
    private boolean pushMsgAfterSended;
    protected MessageSender msgPusher;
    protected PlatformEnvironment platformEnvironment;
    private boolean useDefautlSender;

    public NotificationCenterImpl() {
        this.writeNoticeLog = false;
        this.msgPusher = null;
        this.pushMsgAfterSended = false;
        this.useDefautlSender = true;
    }

    /**
     * 这个通过spring注入
     */
    public void initDummyMsgSenders() {
        msgSenders.put("dummy", DummyMessageSenderImpl.instance);
        defautlMsgSender = DummyMessageSenderImpl.instance;
    }

    /**
     *
     * @param senderName 消息发送器名称
     * @param sender 消息发送器
     * @return 通知实体bean
     */
    public static NotificationCenterImpl createSimpleNotification(String senderName, MessageSender sender) {
        NotificationCenterImpl notification = new NotificationCenterImpl();
        notification.registerMessageSender(senderName, sender);
        notification.appointDefaultSendType(senderName);
        return notification;
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
        if(ms!=null) {
            defautlMsgSender = ms;
        }
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
        int sendSuccessCount = 0;
        int sendErrorCount = 0;
        String noticeType = "D";//default;
        StringBuilder errorObjects = new StringBuilder();
        if(!this.useDefautlSender && msgSenders.size()>1) {
            /*
             *  从用户设置中获得用户希望的接收消息的方式，可能是多个，比如用户希望同时接收到Email和短信，这样就要发送两天
             *  并在数据库中记录发送信息，在发送方式中用逗号把多个方式拼接在一起保存在对应的字段中
             */
            IUserSetting userReceiveWays = getPlatformEnvironment().getUserSetting(receiver, "receiveways");
            String receiveways = userReceiveWays == null ? null : userReceiveWays.getParamValue();
            if (StringUtils.isNotBlank(receiveways)) {
                noticeType = receiveways;
                String[] vals = receiveways.split(",");
                if (ArrayUtils.isNotEmpty(vals)) {
                    for (String val : vals) {
                        if (StringUtils.isNotBlank(val)) {
                            ResponseData res = realSendMessage(msgSenders.get(val.trim()), sender, receiver, message);
                            if (res.getCode() != 0) {
                                sendErrorCount++;
                                errorObjects.append(res.getMessage()).append("\r\n");
                            } else {
                                sendSuccessCount++;
                            }
                        }
                    }
                }
            }
        }

        if(sendSuccessCount==0){
            /*String infoText = "用户 " + CodeRepositoryUtil.getUserInfoByCode(receiver).getLoginName() + " " +
                    "未选择任何通知接收方式，默认通过内部消息发送通知";
            logger.info(infoText);
            noticeType = StringUtils.isBlank(noticeType)?"D":noticeType+",D";*/
            ResponseData res = realSendMessage(defautlMsgSender, sender, receiver,message);
            if (res.getCode() != 0) {
                sendErrorCount ++;
                errorObjects.append(res.getMessage()).append("\r\n");
            } else {
                sendSuccessCount ++;
            }
        }

        int notifyState = sendErrorCount ==0? 0:(sendSuccessCount==0? 1: 2);
        String returnText = sendErrorCount>0? errorObjects.toString():"OK!";
        if(writeNoticeLog){
            wirteNotifyLog(noticeType, sender, receiver, message,
                returnText, String.valueOf(notifyState));
        }

        if(pushMsgAfterSended && this.msgPusher != null){
            this.msgPusher.sendMessage(sender, receiver, message);
        }
        return ResponseData.makeErrorMessage(notifyState, returnText);
    }

    /**
     * 按部门发送系统消息
     *
     * @param sender     发送人内部用户编码
     * @param unitCode   接收人内部部门编码
     * @param message 消息主体
     * @param includeSubUnit  是否包括子部门
     * @return "OK" 表示成功，其他的为错误信息
     */
    @Override
    public ResponseData sendUnitMessage(String sender, String unitCode, boolean includeSubUnit, NoticeMessage message){
        Set<String> users = CodeRepositoryUtil.listUnitAllUsers(unitCode, includeSubUnit);
        return sendMessage(sender, users, message);
    }

    @Override
    public ResponseData pushMessage(String sender, String receiver, NoticeMessage message){
        if(this.msgPusher == null)
            return ResponseData.errorResponse;
        return msgPusher.sendMessage(sender, receiver, message);
    }

    @Override
    public ResponseData pushUnitMessage(String sender, String unitCode, boolean includeSubUnit, NoticeMessage message){
        /*Set<String> users = SysUserFilterEngine.calcSystemOperators(
            includeSubUnit?"D(unitCode, unitCode++)":"D(unitCode)",null,
            null,null,
            new UserUnitMapTranslate(CollectionsOpt.createHashMap("unitCode", unitCode)));
            */
        Set<String> users = CodeRepositoryUtil.listUnitAllUsers(unitCode, includeSubUnit);
        return pushMessage(sender, users, message);
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

        if(pushMsgAfterSended && this.msgPusher != null){
            msgPusher.sendMessage(sender, receiver, message);
        }
        return res;
    }

    /**
     * 广播信息
     *
     * @param sender     发送人内部用户编码
     * @param message    消息主体
     * @param userInline DoubleAspec.ON 在线用户  OFF 离线用户 BOTH 所有用户
     * @return 默认没有实现
     */
    @Override
    public ResponseData broadcastMessage(String sender, NoticeMessage message, DoubleAspect userInline) {
        if(this.msgPusher == null) return ResponseData.errorResponse;
        return msgPusher.broadcastMessage(sender, message, userInline);
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
    protected static ResponseData realSendMessage(MessageSender messageSender, String sender, String receiver, NoticeMessage message ) {
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

    public void setWriteNoticeLog(boolean writeNoticeLog) {
        this.writeNoticeLog = writeNoticeLog;
    }

    public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        this.platformEnvironment = platformEnvironment;
    }

    protected PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment == null) {
            platformEnvironment = WebOptUtils.getWebAppContextBean("platformEnvironment", PlatformEnvironment.class);
        }
        return platformEnvironment;
    }

    public void setMsgPusher(MessageSender msgPusher) {
        this.msgPusher = msgPusher;
        //this.pushMsgAfterSended = this.msgPusher!=null;
    }

    public void setPushMsgAfterSended(boolean pushMsgAfterSended) {
        this.pushMsgAfterSended = /*this.msgPusher != null &&*/ pushMsgAfterSended;
    }

    public void setUseDefautlSender(boolean useDefautlSender) {
        this.useDefautlSender = useDefautlSender;
    }
}
