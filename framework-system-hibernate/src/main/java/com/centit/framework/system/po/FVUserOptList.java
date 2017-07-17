package com.centit.framework.system.po;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

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
    @EmbeddedId
    private FVUserOptListId id; //主键

    @Column(name = "OPTNAME")
    @Length(max = 50, message = "字段长度不能大于{max}")
    private String optName;     //业务名字

    @Column(name = "OPTID")
    @Length(max = 8, message = "字段长度不能大于{max}")
    private String optId;       //业务编号

    @Column(name = "OPTMETHOD")
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
     */
    public FVUserOptList(FVUserOptListId id) {
        this.id = id;
    }

    /**
     * full constructor
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

    public void copy(FVUserOptList other) {
        this.id = other.getId();
        this.optName = other.getOptName();
        this.optId = other.getOptId();
        this.optMethod = other.getOptMethod();
    }

    public void copyNotNullProperty(FVUserOptList other) {

        if (other.getOptName() != null)
            this.optName = other.getOptName();
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getOptMethod() != null)
            this.optMethod = other.getOptMethod();
    }
}
