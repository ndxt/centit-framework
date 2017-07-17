package com.centit.framework.system.po;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.annotation.JSONField;
@Entity
@Table(name="M_MSGANNEX")
public class MsgAnnex implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JSONField(serialize=false)
    @JoinColumn(name = "MsgCode", insertable = false, updatable = false)
    private InnerMsg mInnerMsg;//
    
    @Column(name = "INFOCODE")
    @Size( max=16,message = "字段长度必须小于{max}")
    private String infoCode;//消息代码
    
    @Id
    @Column(name="MSGANNEXID")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String msgAnnexId;//附件主键

    public String getMsgAnnexId() {
        return msgAnnexId;
    }

    public InnerMsg getmInnerMsg() {
        return mInnerMsg;
    }

    public void setmInnerMsg(InnerMsg mInnerMsg) {
        this.mInnerMsg = mInnerMsg;
    }

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }
    
}
