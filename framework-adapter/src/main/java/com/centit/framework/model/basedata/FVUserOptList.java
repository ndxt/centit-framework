package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * FVUseroptlist entity.
 *
 * @author MyEclipse Persistence Tools
 */
//用户业务操作 view
@Entity
@Table(name = "F_V_USEROPTLIST")
public class FVUserOptList implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;
    @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    private FVUserOptListId id; //主键

    @Column(name = "OPT_NAME")
    @Length(max = 50, message = "字段长度不能大于{max}")
    private String optName;     //业务名字

    @Column(name = "OPT_ID")
    @Length(max = 8, message = "字段长度不能大于{max}")
    private String optId;       //业务编号

    @Column(name = "OPT_METHOD")
    @Length(max = 50, message = "字段长度不能大于{max}")
    private String optMethod;   //业务方法？？

    // Constructors

    /**
     * default constructor
     */
    public FVUserOptList() {
    }

    /**
     * full constructor
     *
     * @param id FVUserOptListId
     */
    public FVUserOptList(FVUserOptListId id) {
        this.id = id;
    }

    /**
     * full constructor
     *
     * @param id FVUserOptListId
     * @param optname String
     * @param optid String
     * @param optmethod String
     */
    public FVUserOptList(FVUserOptListId id, String optname,
                         String optid, String optmethod) {
        this.id = id;
        this.optName = optname;
        this.optId = optid;
        this.optMethod = optmethod;
    }
    // Property accessors
    public FVUserOptListId getId() {
        return this.id;
    }

    public void setId(FVUserOptListId id) {
        this.id = id;
    }

    public String getUserCode() {
        if(this.id==null)
            this.id = new FVUserOptListId();
        return this.id.getUserCode();
    }

    public void setUserCode(String userCode) {
        if(this.id==null)
            this.id = new FVUserOptListId();
        this.id.setUserCode(userCode);
    }

    public String getOptcode() {
        if(this.id==null)
            this.id = new FVUserOptListId();
        return this.id.getOptcode();
    }

    public void setOptcode(String optcode) {
        if(this.id==null)
            this.id = new FVUserOptListId();
        this.id.setOptcode(optcode);
    }

    public String getOptName() {
        return this.optName;
    }

    public void setOptName(String optname) {
        this.optName = optname;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String optid) {
        this.optId = optid;
    }

    public String getOptMethod() {
        return this.optMethod;
    }

    public void setOptMethod(String optmethod) {
        this.optMethod = optmethod;
    }

}
