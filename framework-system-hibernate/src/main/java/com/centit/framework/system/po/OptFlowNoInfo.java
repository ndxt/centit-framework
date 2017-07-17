package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "F_OPTFLOWNOINFO")
public class OptFlowNoInfo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private OptFlowNoInfoId cid;

    @Column(name = "CURNO")
    private Long curNo;

    @Column(name = "LASTCODEDATE")
    private java.util.Date lastCodeDate;

    // Constructors

    /**
     * default constructor
     */
    public OptFlowNoInfo() {
    }

    /**
     * minimal constructor
     */
    public OptFlowNoInfo(OptFlowNoInfoId id

            , Long curNo) {
        this.cid = id;

        this.curNo = curNo;
    }

    /**
     * full constructor
     */
    public OptFlowNoInfo(OptFlowNoInfoId id

            , Long curNo, java.util.Date lastCodeDate) {
        this.cid = id;

        this.curNo = curNo;
        this.lastCodeDate = lastCodeDate;
    }


    public OptFlowNoInfoId getCid() {
        return this.cid;
    }

    public void setCid(OptFlowNoInfoId id) {
        this.cid = id;
    }

    public String getOwnerCode() {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        return this.cid.getOwnerCode();
    }

    public void setOwnerCode(String ownerCode) {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        this.cid.setOwnerCode(ownerCode);
    }

    public Date getCodeDate() {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        return this.cid.getCodeDate();
    }

    public void setCodeDate(Date codeDate) {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        this.cid.setCodeDate(codeDate);
    }

    public String getCodeCode() {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        return this.cid.getCodeCode();
    }

    public void setCodeCode(String codeCode) {
        if (this.cid == null)
            this.cid = new OptFlowNoInfoId();
        this.cid.setCodeCode(codeCode);
    }

    // Property accessors

    public Long getCurNo() {
        return this.curNo;
    }

    public void setCurNo(Long curNo) {
        this.curNo = curNo;
    }

    public java.util.Date getLastCodeDate() {
        return this.lastCodeDate;
    }

    public void setLastCodeDate(java.util.Date lastCodeDate) {
        this.lastCodeDate = lastCodeDate;
    }

    public void copy(OptFlowNoInfo other) {

        this.setOwnerCode(other.getOwnerCode());
        this.setCodeDate(other.getCodeDate());
        this.setCodeCode(other.getCodeCode());

        this.curNo = other.getCurNo();
        this.lastCodeDate = other.getLastCodeDate();

    }

    public void copyNotNullProperty(OptFlowNoInfo other) {

        if (other.getOwnerCode() != null)
            this.setOwnerCode(other.getOwnerCode());
        if (other.getCodeDate() != null)
            this.setCodeDate(other.getCodeDate());
        if (other.getCodeCode() != null)
            this.setCodeCode(other.getCodeCode());

        if (other.getCurNo() != null)
            this.curNo = other.getCurNo();
        if (other.getLastCodeDate() != null)
            this.lastCodeDate = other.getLastCodeDate();
    }

    public void clearProperties() {

        this.curNo = null;
        this.lastCodeDate = null;
    }

}
