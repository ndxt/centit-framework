package com.centit.framework.system.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 系统通知中心，对系统中所有实现MessageSender接口的类中发送消息接口进行统一管理，此类对发送信息进行维护
 */
@Entity
@Table(name = "F_SYS_NOTIFY")
public class SysNotify implements Serializable {

    /**
     * 通知ID
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Notify_ID")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private Long notifyId;

    /**
     * 发送人
     */
    @Column(name = "Notify_Sender")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @NotBlank
    private String notifySender;
    /**
     * 发送人
     */
    @Column(name = "Notify_Receiver")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @NotBlank
    private String notifyReceiver;
    /**
     * 主题
     */
    @Column(name = "Msg_Subject")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @NotBlank
    private String msgSubject;
    /**
     * 内容
     */
    @Column(name = "Msg_Content")
    @Length(max = 2000, message = "字段长度不能大于{max}")
    private String msgContent;
    /**
     * 通知方式
     */
    @Column(name = "notice_Type")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @NotBlank
    private String notifyType;
    /**
     * 发送状态
     */
    @Column(name = "Notify_State")
    @Pattern(regexp = "[012]", message = "字段输入必须为0、1、2")
    @NotBlank
    private String notifyState;
    /**
     * 失败原因
     */
    @Column(name = "Error_Msg")
    @Length(max = 500, message = "字段长度不能大于{max}")
    private String errorMsg;

    /**
     *功能模块 */
    @Column(name="OPTID")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String optId;
    
    /**
     *操作方法 */
    @Column(name="OPTMETHOD")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String optMethod;
    
    /**
     *操作业务标记 */
    @Column(name="OPTTAG")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String optTag;
    
    /**
     * 发送时间
     */
    @Column(name = "Notify_Time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notifyTime;


    public SysNotify() {
    }

    public SysNotify(String notifySender, String notifyReceiver, String msgSubject,
            String msgContent, String notifyType,
            String optId, String optMethod, String optTag,
            String notifyState, String errorMsg) {
        this.notifySender = notifySender;
        this.notifyReceiver = notifyReceiver;
        this.msgSubject = msgSubject;
        this.msgContent = msgContent;
        this.notifyType = notifyType;
        this.notifyState = notifyState;
        this.errorMsg = errorMsg;
        this.optId = optId;
        this.optMethod = optMethod;
        this.optTag = optTag;
        this.notifyTime = new Date();
    }

    public Long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public String getNotifySender() {
        return notifySender;
    }

    public void setNotifySender(String notifySender) {
        this.notifySender = notifySender;
    }

    public String getNotifyReceiver() {
        return notifyReceiver;
    }

    public void setNotifyReceiver(String notifyReceiver) {
        this.notifyReceiver = notifyReceiver;
    }

    public String getMsgSubject() {
        return msgSubject;
    }

    public void setMsgSubject(String msgSubject) {
        this.msgSubject = msgSubject;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(String notifyState) {
        this.notifyState = notifyState;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }
    
    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptMethod() {
        return optMethod;
    }

    public void setOptMethod(String optMethod) {
        this.optMethod = optMethod;
    }

    public String getOptTag() {
        return optTag;
    }

    public void setOptTag(String optTag) {
        this.optTag = optTag;
    }
}
