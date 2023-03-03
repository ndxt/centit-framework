package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IRolePower;

/**
 * FRolepower entity.
 *
 * @author MyEclipse Persistence Tools
 */

// 角色操作权限表
public class RolePower implements IRolePower, java.io.Serializable{

    // Fields
    private static final long serialVersionUID = 1L;

    private String optScopeCodes;// 操作代码
    // Constructors

    //创建人、更新人、更新时间
    /**
     * CREATOR(创建人) 创建人
     */
    private String roleCode; //角色代码

    private String optCode; //操作代码

    private String topUnit; //租户代码

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


    public String getOptScopeCodes() {
        return optScopeCodes;
    }

    public String[] getOptScopeCodeSet() {
        if(optScopeCodes==null)
            return null;
        return optScopeCodes.split(",");
    }

    public void setOptScopeCodes(String optScopeCodes) {
        this.optScopeCodes = optScopeCodes;
    }

    public void copyNotNullProperty(RolePower other) {
        if( other.getOptScopeCodes()!=null)
            this.optScopeCodes = other.getOptScopeCodes();

    }
    public void copy(RolePower other) {
        this.optScopeCodes = other.getOptScopeCodes();

    }

    @Override
    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }
}
