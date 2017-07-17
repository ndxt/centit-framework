package com.centit.framework.system.po;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "F_V_USERROLES")
public class FVUserRoles implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7725372179862779056L;


    @EmbeddedId
    private UserRoleId id;

    @Column(name = "ROLENAME")
    private String roleName; // 角色名称

    @Column(name = "ISVALID")
    private String isValid; // 是否生效

    @Column(name = "ROLEDESC")
    private String roleDesc; // 角色描述

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getIsValid() {
        return this.isValid;
    }

    public void setIsValid(String isvalid) {
        this.isValid = isvalid;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public void setUserCode(String userCode) {
        if (null == id) {
            id = new  UserRoleId();
        }
        id.setUserCode(userCode);
    }


    public void setRoleCode(String roleCode) {
        if (null == id) {
            id = new  UserRoleId();
        }
        id.setRoleCode(roleCode);
    }
    
    public String getUserCode() {
        if (null == id) {
            return null;
        }
        return id.getUserCode();
    }


    public String getRoleCode() {
        if (null == id) {
            return null;
        }
        return id.getRoleCode();
    }
}
