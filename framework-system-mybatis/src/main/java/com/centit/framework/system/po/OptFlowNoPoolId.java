package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class OptFlowNoPoolId implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "OWNERCODE")
    @NotNull(message = "字段不能为空")
    @Size(max = 8, message = "字段长度不能大于{max}")
    private String ownerCode;

    @Column(name = "CODEDATE")
    @NotNull(message = "字段不能为空")
    @Temporal(TemporalType.DATE)
    private Date codeDate;

    @Column(name = "CODECODE")
    @NotNull(message = "字段不能为空")
    @Size(max = 16, message = "字段长度不能大于{max}")
    private String codeCode;

    @Column(name = "CURNO")
    @NotNull(message = "字段不能为空")
    @Size(max = 6, message = "字段长度不能大于{max}")
    private Long curNo;

    // Constructors

    /**
     * default constructor
     */
    public OptFlowNoPoolId() {
    }

    /**
     * full constructor
     */
    public OptFlowNoPoolId(String ownerCode, Date codeDate, String codeCode, Long curNo) {

        this.ownerCode = ownerCode;
        this.codeDate = codeDate;
        this.codeCode = codeCode;
        this.curNo = curNo;
    }


    public String getOwnerCode() {
        return this.ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public Date getCodeDate() {
        return this.codeDate;
    }

    public void setCodeDate(Date codeDate) {
        this.codeDate = codeDate;
    }

    public String getCodeCode() {
        return this.codeCode;
    }

    public void setCodeCode(String codeCode) {
        this.codeCode = codeCode;
    }

    public Long getCurNo() {
        return curNo;
    }

    public void setCurNo(Long curNo) {
        this.curNo = curNo;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof OptFlowNoPoolId))
            return false;

        OptFlowNoPoolId castOther = (OptFlowNoPoolId) other;
        boolean ret = true;

        ret = this.getOwnerCode() == castOther.getOwnerCode() ||
                (this.getOwnerCode() != null && castOther.getOwnerCode() != null
                        && this.getOwnerCode().equals(castOther.getOwnerCode()));

        ret = ret && (this.getCodeDate() == castOther.getCodeDate() ||
                (this.getCodeDate() != null && castOther.getCodeDate() != null
                        && this.getCodeDate().equals(castOther.getCodeDate())));

        ret = ret && (this.getCodeCode() == castOther.getCodeCode() ||
                (this.getCodeCode() != null && castOther.getCodeCode() != null
                        && this.getCodeCode().equals(castOther.getCodeCode())));

        ret = ret && (this.getCurNo() == castOther.getCurNo() ||
                (this.getCurNo() != null && castOther.getCurNo() != null
                        && this.getCurNo().equals(castOther.getCurNo())));

        return ret;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result +
                (this.getOwnerCode() == null ? 0 : this.getOwnerCode().hashCode());

        result = 37 * result +
                (this.getCodeDate() == null ? 0 : this.getCodeDate().hashCode());

        result = 37 * result +
                (this.getCodeCode() == null ? 0 : this.getCodeCode().hashCode());

        result = 37 * result +
                (this.getCurNo() == null ? 0 : this.getCurNo().hashCode());

        return result;
    }

}
