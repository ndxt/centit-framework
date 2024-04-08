package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FRoleinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 角色信息表
@Entity
@Table(name = "F_ROLEINFO")
@ApiModel(value = "系统角色信息对象", description = "系统角色信息对象 RoleInfo")
public class RoleInfo implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ROLE_CODE")
    //@ValueGenerator(strategy = GeneratorType.SEQUENCE, value = "S_ROLECODE:R:8:0")
    @ValueGenerator(strategy = GeneratorType.RANDOM_ID, value = "7:R")
    @ApiModelProperty(value = "角色代码,可以选择不填后台自动生成", name = "roleCode")
    private String roleCode; // 角色代码

    @Column(name = "ROLE_NAME")
    @Length(max = 300)
    @ApiModelProperty(value = "角色名称", name = "roleName", required = true)
    private String roleName; // 角色名称

    @Column(name = "IS_VALID")
    @ValueGenerator(strategy = GeneratorType.CONSTANT, occasion = GeneratorTime.NEW, value = "T")
    @Length(max = 1)
    @Pattern(regexp = "[TFA]")
    @ApiModelProperty(value = "是否生效 T:生效 F:失效 A:新建可以删除", name = "isValid", required = true)
    private String isValid; // 是否生效
    /**
     * 角色的类别 F (Fixe)系统内置的，固有的 这些角色不能删除，也不能赋给任何人，
     *          G (global) 全局的
     *          P (Public) 公用的，指 系统全局 和 部门之间公用的
     *          D (Department)部门(机构)特有的角色
     *          S (Sub System) 属于某个子系统
     *          I (Item )为项目角色
     *          W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     *          H (HIDE)系统内置的不要显示的，是部门可以自己支配的操作权限集合
     * 角色的类别 F/G/P/D/S/I/W/H
     */
    @Column(name = "ROLE_TYPE")
    @Length(max = 1)
    @NotBlank
    @ApiModelProperty(value = "角色的类别 F:系统内置的 G:全局的 P:公用的 D:部门（机构）特有的角色 I:项目角色 W:工作流角色", name = "roleType", required = true)
    @DictionaryMap(fieldName = "roleTypeText", value = "RoleType")
    private String roleType; // 角色类别
    /**
     * 这个 应该改成owner_code 如果是 部门角色就是 unitCode 如果是 子系统 就是系统代码
     */
    @Column(name = "UNIT_CODE")
    @Length(max = 32)
    @DictionaryMap(fieldName="topUnitName", value="unitCode")
    @ApiModelProperty(value = "topUnit 租户代码", name = "unitCode")
    private String unitCode; // 角色所属机构


    @Column(name = "OS_ID")
    @Length(max = 32)
    @ApiModelProperty(value = "角色所属项目", name = "osId")
    private String osId; // 角色所属项目

    @Column(name = "ROLE_DESC")
    @Length(max = 256)
    private String roleDesc; // 角色描述

    /**
     * CREATOR(创建人) 创建人
     */
    @Column(name = "CREATOR")
    @Length(max = 32)
    private String creator;

    /**
     * UPDATOR(更新人) 更新人
     */
    @Column(name = "UPDATOR")
    @Length(max = 32)
    private String updator;

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW, value = "today()")
    protected Date createDate;

    /**
     * UPDATEDATE(更新时间) 更新时间
     */
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date updateDate;

    @Transient
    private List<RolePower> rolePowers;

    /**
     * default constructor
     */
    public RoleInfo() {
        roleType = "G";
    }

    /**
     * minimal constructor
     *
     * @param rolecode String
     * @param isvalid  String
     */
    public RoleInfo(String rolecode, String isvalid) {
        this.roleCode = rolecode;
        this.isValid = isvalid;
        this.roleType = "G";
    }

    public RoleInfo(String rolecode, String rolename, String roleType,
                    String unitCode, String isvalid, String roledesc) {
        this.roleCode = rolecode;
        this.roleName = rolename;
        this.roleType = roleType;
        this.unitCode = unitCode;
        this.isValid = isvalid;
        this.roleDesc = roledesc;
    }

    // Property accessors
    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String rolename) {
        this.roleName = rolename;
    }

    public String toString() {
        return this.roleName;
    }

    public String getIsValid() {
        return this.isValid;
    }

    public void setIsValid(String isvalid) {
        this.isValid = isvalid;
    }

    public String getRoleDesc() {
        return this.roleDesc;
    }

    public void setRoleDesc(String roledesc) {
        this.roleDesc = roledesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 角色的类别 F （Fixe）系统内置的，固有的， G （global） 全局的
     * P （public） 公用的，指 系统全局 和 部门之间公用的
     * D （department）部门（机构）特有的角色
     * I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     * H (HIDE)系统内置的不要显示的，是部门可以自己支配的操作权限集合
     * 角色的类别 F/G/P/D/I/W
     *
     * @return 角色的类别 F/G/P/D/I/W
     */
    public String getRoleType() {
        return roleType;
    }

    /**
     * 角色的类别 F （Fixed）系统内置的，固有的， G （global） 全局的
     * P （public） 公用的，指 系统全局 和 部门之间公用的
     * D （department）部门（机构）特有的角色
     * I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     * H (HIDE)系统内置的不要显示的，是部门可以自己支配的操作权限集合
     *
     * @param roleType 角色的类别 F/G/P/D/I/W/H
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getOsId() {
        return this.osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    public Date getLastModifyDate() {
        return updateDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return this.updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    //@JSONField(serialize=true,deserialize=false,label="rolePowers")
    public List<RolePower> getRolePowers() {
        if (null == rolePowers) {
            rolePowers = new ArrayList<>();
        }
        return rolePowers;
    }

    public void setRolePowers(List<RolePower> rolePowers) {
        this.rolePowers = rolePowers;
    }

    public void addAllRolePowers(List<RolePower> rolePowers) {
        getRolePowers().clear();
        if (CollectionUtils.isEmpty(rolePowers)) {
            return;
        }
        getRolePowers().addAll(rolePowers);
    }
    public void addRolePower(RolePower rp){
        getRolePowers().add(rp);
    }
}
