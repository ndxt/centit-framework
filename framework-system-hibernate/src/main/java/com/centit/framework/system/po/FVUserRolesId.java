package com.centit.framework.system.po;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FVUserRolesId implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7725372179862779056L;

    /**
     * 用户代码
     */
    @Column(name = "USERCODE")
    private String userCode;

    /**
     * 角色代码
     */
    @Column(name = "ROLECODE")
    private String roleCode; //

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }


}
