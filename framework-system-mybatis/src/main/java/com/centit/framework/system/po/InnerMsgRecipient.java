package com.centit.framework.system.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.centit.framework.core.dao.DictionaryMap;

//M_InnerMsg_Recipient
@Entity
@Table(name="M_INNERMSG_RECIPIENT")
public class InnerMsgRecipient implements Serializable{
    
    /**
     * 接收人主键
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String id;//
    
    /**
     * 消息编码
     */
    
    @ManyToOne
    @JoinColumn(name="MSGCODE",updatable=false)
    //@JSONField(serialize=false)
    private InnerMsg mInnerMsg;
    
    private String msgCode;
    
    
   
    
    
    /**
     * 接收人编号
     */
    @Column(name = "RECEIVE")
    @NotNull
    @Size(max = 2048, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="receiverName",value="userCode")
    private String receive;
    
    /**
     * 回复消息
     */
    @Column(name = "REPLYMSGCODE")
    private int replyMsgCode;
    
    /**
     *  接收人类别:
        P=个人为消息
        A=机构为公告
        M=消息
     */
    @Column(name = "RECEIVETYPE")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String receiveType;
    
    /**
     *  消息类型:
        T=收件人
        C=抄送
        B=密送
     */
    @Column(name = "MAILTYPE")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String mailType;
    
    /**
     * 消息状态：
     *  U=未读
        R=已读
        D=删除
     */
    @Column(name = "MSGSTATE")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String msgState;
    
        
    public void setMInnerMsg(InnerMsg InnerMsg){
        this.mInnerMsg=InnerMsg;
    }
    public InnerMsgRecipient(){
        
    }
    
    public String getMsgCode() {
        return this.getMInnerMsg().getMsgCode();
    }
    
   
    public InnerMsg getMInnerMsg() {
        return this.mInnerMsg;
    }
    
    public String getSender() {
        if (null != getMInnerMsg()) {
            return getMInnerMsg().getSender();
        }        
        return "";
    }
    
    public String getMsgTitle() {
        if (null != getMInnerMsg()) {
            return getMInnerMsg().getMsgTitle();
        }
        
        return "";
    }
    
    public Date getSendDate() {
        if (null != getMInnerMsg()) {
            return getMInnerMsg().getSendDate();
        }
        
        return null;
    }

    public String getMsgState() {
        return msgState;
    }

    public void setMsgState(String msgState) {
        this.msgState = msgState;
    }
    
    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public int getReplyMsgCode() {
        return replyMsgCode;
    }

    public void setReplyMsgCode(int replyMsgCode) {
        this.replyMsgCode = replyMsgCode;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getMailType() {
        return mailType;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getMsgContent(){
        if(null!=this.getMInnerMsg())
            return this.getMInnerMsg().getMsgContent();
        else
           return null;
    }
    
    public String getMsgTypeText(){
        switch(getMInnerMsg().getMsgType()){
            case("P"):{
                return "个人消息";
            }
            case("A"):{
                return "公告";
            }
            default:{
                return "其他";
            }
        }
    }
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
    
}
