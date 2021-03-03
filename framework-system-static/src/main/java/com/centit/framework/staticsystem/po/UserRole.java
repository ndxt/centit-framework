package com.centit.framework.staticsystem.po;

import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.model.basedata.IUserRole;
import org.apache.commons.lang3.StringUtils;

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

    private String roleType;

    private String unitCode;

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

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getRoleCode() {
        return this.roleCode;
    }


    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    /**
     * 角色的类别 F (Fixed)系统内置的，固有的 这些角色不能删除，也不能赋给任何人，
     * public，forbidden，anonymous
     * G (global) 全局的
     * P (Public) 公用的，指 系统全局 和 部门之间公用的
     * D (Department)部门(机构)特有的角色, 租户角色
     * S (Sub System) 属于某个子系统
     * I (Item )为项目角色
     * W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     * H (HIDE)系统内置的不要显示的，是部门可以自己支配的操作权限集合
     * 角色的类别 F/G/P/D/S/I/W/H
     */
    @Override
    public String getRoleType() {
        return StringUtils.isBlank(this.roleType)?"G":this.roleType;
    }

    /**
     * 对租户的 topUnit
     *
     * @return 对租户的 topUnit ，角色属于某个租户
     */
    @Override
    public String getUnitCode() {
        return StringUtils.isBlank(this.unitCode)?
             GlobalConstValue.NO_TENANT_TOP_UNIT: this.unitCode;
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
