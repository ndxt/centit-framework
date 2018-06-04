package com.centit.framework.model.basedata;

import com.alibaba.fastjson.JSON;

public class NoticeMessage implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    private static String defaultMsgType = "msg";
    // 消息类别 默认值 msg
    private String msgType;
    // 消息主题
    private String msgSubject;
    // 消息内容
    private String msgContent;
    // 关联业务
    private String optId;
    // 关联的 方法、功能、模块
    private String optMethod;
    // 关联对象组件，多个主键用url参数的方式链接
    private String optTag;

    public static void setDefaultMsgType(String defaultMsgType) {
        NoticeMessage.defaultMsgType = defaultMsgType;
    }

    public NoticeMessage(){
        msgType = defaultMsgType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
