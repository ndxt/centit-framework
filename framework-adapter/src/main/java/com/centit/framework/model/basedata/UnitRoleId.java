package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * FUserroleId entity.
 *
 * @author MyEclipse Persistence Tools
 */
//用户角色的主键
@Embeddable
public class UnitRoleId implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 893187890652550538L;

    @Column(name = "UNIT_CODE")
    @NotBlank
    @DictionaryMap(value="unitCode", fieldName = "unitName")
    private String unitCode;// 用户代码

    @Column(name = "ROLE_CODE")
    @NotBlank
    @DictionaryMap(value="roleCode", fieldName = "roleName")
    private String roleCode; // 角色代码

    // Constructors

    /**
     * default constructor
     */
    public UnitRoleId() {
    }

    /**
     * full constructor
     * @param unitCode String
     * @param rolecode String
     */
    public UnitRoleId(String unitCode, String rolecode) {
        this.unitCode = unitCode;
        this.roleCode = rolecode;
    }

    // Property accessors

    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitCode(String userCode) {
        this.unitCode = userCode;
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
        if (!(other instanceof UnitRoleId))
            return false;
        UnitRoleId castOther = (UnitRoleId) other;

        return ((this.getUnitCode() == castOther.getUnitCode()) || (this
                .getUnitCode() != null
                && castOther.getUnitCode() != null && this.getUnitCode()
                .equals(castOther.getUnitCode())))
                && ((this.getRoleCode() == castOther.getRoleCode()) || (this
                .getRoleCode() != null
                && castOther.getRoleCode() != null && this
                .getRoleCode().equals(castOther.getRoleCode())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getUnitCode() == null ? 0 : this.getUnitCode().hashCode());
        result = 37 * result
                + (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
        return result;
    }

}
