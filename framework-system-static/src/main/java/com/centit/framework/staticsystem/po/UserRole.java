package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IUserRole;

import java.util.Date;

/**
 * FUserrole entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 用户角色设定
public class UserRole implements IUserRole, java.io.Serializable {

    // Fields
    // public final SimpleDateFormat sdfDate = new
    // SimpleDateFormat("yyyy-MM-dd");

    private static final long serialVersionUID = 1L;

    private String userCode;// 用户代码

    private String roleCode; // 角色代码

    private Date obtainDate; // 获得角色时间

    private String changeDesc; // 说明

    protected Date createDate;

    private String obtainType;

    private String inheritedFrom;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String getObtainType() {
        return obtainType;
    }

    public void setObtainType(String obtainType) {
        this.obtainType = obtainType;
    }

    @Override
    public String getInheritedFrom() {
        return inheritedFrom;
    }

    public void setInheritedFrom(String inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }
    // Constructors

    /**
     * default constructor
     */
    public UserRole() {
        this.obtainType = "D";
    }

    public UserRole(String userCode, String rolecode) {
        this.obtainType = "D";
        this.roleCode = rolecode;
        this.userCode = userCode;
    }

    @Override
    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

    public Date getObtainDate() {
        return this.obtainDate;
    }

    public void setObtainDate(Date obtaindate) {

        this.obtainDate =obtaindate;
    }

    /*
     * public void setSecededate(String ssecededate) { try { this.secededate =
     * sdfDate.parse(ssecededate); } catch (ParseException e) {
     * e.printStackTrace(); } }
     */
    @Override
    public String getChangeDesc() {
        return this.changeDesc;
    }

    public void setChangeDesc(String changedesc) {
        this.changeDesc = changedesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void copy(UserRole other) {

        this.obtainDate = other.getObtainDate();
        this.changeDesc = other.getChangeDesc();

    }

    public void copyNotNullProperty(UserRole other) {

        if (other.getObtainDate() != null)
            this.obtainDate = other.getObtainDate();
        if (other.getChangeDesc() != null)
            this.changeDesc = other.getChangeDesc();

    }
}
