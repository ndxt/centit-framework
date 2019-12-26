package com.centit.framework.model.basedata;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class NoticeMessage implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    private static String defaultMsgType = "msg";
    /**
     * 消息类别 默认值 msg
     * 客户端可以根据不同的消息类别提供不同的 响应方式 或者 展示方式
      */
    private String msgType;
    /** 消息主题 这不是必须的
     * */
    private String msgSubject;
    /** 消息内容，为一个文本，不同的消息类别 可以有不同的格式
     */
    private String msgContent;
    /** 关联业务
     */
    private String optId;
    /**关联业务中的 方法、功能、模块
     */
    private String optMethod;
    /** 关联对象组件，多个主键用url参数的方式链接
     */
    private String optTag;

    public static void setDefaultMsgType(String defaultMsgType) {
        NoticeMessage.defaultMsgType = defaultMsgType;
    }

    public NoticeMessage(){
        this.msgType = defaultMsgType;
    }

    public static NoticeMessage create(){
        return new NoticeMessage();
    }

    public NoticeMessage typeOf(String stype){
        this.msgType = stype;
        return this;
    }

    public NoticeMessage subject(String ssubject){
        this.msgSubject = ssubject;
        return this;
    }

    public NoticeMessage content(String scontent){
        this.msgContent = scontent;
        return this;
    }

    public NoticeMessage operation(String soptid){
        this.optId = soptid;
        return this;
    }

    public NoticeMessage method(String smethod){
        this.optMethod = smethod;
        return this;
    }

    public NoticeMessage tag(String stag){
        this.optTag = stag;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
