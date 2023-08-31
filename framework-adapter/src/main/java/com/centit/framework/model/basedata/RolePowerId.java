package com.centit.framework.model.basedata;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * FRolepowerId entity.
 *
 * @author MyEclipse Persistence Tools
 */
@Embeddable
public class RolePowerId implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 1L;
    @Column(name = "ROLE_CODE")
    @NotBlank(message = "字段不能为空")
    private String roleCode; //角色代码

    @Column(name = "OPT_CODE")
    @NotBlank(message = "字段不能为空")
    private String optCode; //操作代码

    // Constructors

    /**
     * default constructor
     */
    public RolePowerId() {
    }

    /**
     * full constructor
     * @param rolecode String
     * @param optcode String
     */
    public RolePowerId(String rolecode, String optcode) {
        this.roleCode = rolecode;
        this.optCode = optcode;
    }

    // Property accessors

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

    public String getOptCode() {
        return this.optCode;
    }

    public void setOptCode(String optcode) {
        this.optCode = optcode;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof RolePowerId))
            return false;
        RolePowerId castOther = (RolePowerId) other;

        return ((this.getRoleCode() == castOther.getRoleCode()) || (this
                .getRoleCode() != null
                && castOther.getRoleCode() != null && this.getRoleCode()
                .equals(castOther.getRoleCode())))
                && ((this.getOptCode() == castOther.getOptCode()) || (this
                .getOptCode() != null
                && castOther.getOptCode() != null && this.getOptCode()
                .equals(castOther.getOptCode())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
        result = 37 * result
                + (getOptCode() == null ? 0 : this.getOptCode().hashCode());
        return result;
    }

}
