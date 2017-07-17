package com.centit.framework.system.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * FVUseroptlistId entity.
 *
 * @author MyEclipse Persistence Tools
 */
//用户业务操作 view的主键
@Embeddable
public class FVUserOptListId implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;
    @Column(name = "USERCODE")
    @NotNull(message = "字段不能为空")
    private String userCode;    //用户代码

    @Column(name = "OPTCODE")
    @NotNull(message = "字段不能为空")
    private String optcode;     //业务代码

    // Constructors

    /**
     * default constructor
     */
    public FVUserOptListId() {
    }

    /**
     * minimal constructor
     */
    public FVUserOptListId(String userCode, String optcode) {
        this.userCode = userCode;
        this.optcode = optcode;

    }


    // Property accessors

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOptcode() {
        return this.optcode;
    }

    public void setOptcode(String optcode) {
        this.optcode = optcode;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof FVUserOptListId))
            return false;
        FVUserOptListId castOther = (FVUserOptListId) other;

        return ((this.getUserCode() == castOther.getUserCode()) || (this
                .getUserCode() != null
                && castOther.getUserCode() != null && this.getUserCode()
                .equals(castOther.getUserCode())))
                && ((this.getOptcode() == castOther.getOptcode()) || (this
                .getOptcode() != null
                && castOther.getOptcode() != null && this.getOptcode()
                .equals(castOther.getOptcode())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getUserCode() == null ? 0 : this.getUserCode().hashCode());
        result = 37 * result
                + (getOptcode() == null ? 0 : this.getOptcode().hashCode());
        return result;
    }

}