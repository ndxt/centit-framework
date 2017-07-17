package com.centit.framework.system.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * FUserroleId entity.
 *
 * @author MyEclipse Persistence Tools
 */
//用户角色的主键
@Embeddable
public class UserRoleId implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 893187890652550538L;

    @Column(name = "USERCODE")
    @NotNull(message = "字段不能为空")
    private String userCode;// 用户代码

    @Column(name = "ROLECODE")
    @NotNull(message = "字段不能为空")
    private String roleCode; // 角色代码

    // Constructors

    /**
     * default constructor
     */
    public UserRoleId() {
    }

    /**
     * full constructor
     */
    public UserRoleId(String userCode, String rolecode) {
        this.userCode = userCode;
        this.roleCode = rolecode;
    }

    // Property accessors

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

  
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof UserRoleId))
            return false;
        UserRoleId castOther = (UserRoleId) other;

        return ((this.getUserCode() == castOther.getUserCode()) || (this
                .getUserCode() != null
                && castOther.getUserCode() != null && this.getUserCode()
                .equals(castOther.getUserCode())))
                && ((this.getRoleCode() == castOther.getRoleCode()) || (this
                .getRoleCode() != null
                && castOther.getRoleCode() != null && this
                .getRoleCode().equals(castOther.getRoleCode())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getUserCode() == null ? 0 : this.getUserCode().hashCode());
        result = 37 * result
                + (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
        return result;
    }

}