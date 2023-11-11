package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.algorithm.DatetimeOpt;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "F_V_USERROLES")
public class FVUserRoles implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7725372179862779056L;

    @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    private UserRoleId id;

    @Column(name = "ROLE_NAME")
    private String roleName; // 角色名称

  /**
   * 这个是新版本的一个新的性所有 添加了这个 默认实现
   * 用户获得这个角色的方式，
   * "D" 直接活的 ， "I" 从机构继承， "M" 从机构层级继承，至少夸一级，这个默认不打开
   */
    @Column(name = "OBTAIN_TYPE")
    private String obtainType;//

    @Column(name = "INHERITED_FROM")
    @DictionaryMap(fieldName = "inheritedFromText", value = "unitCode")
    private String inheritedFrom;


    @Column(name = "IS_VALID")
    private String isValid; // 是否生效

    @Column(name = "ROLE_DESC")
    private String roleDesc; // 角色描述

    /**
     * 角色的类别 F （Fixe）系统内置的，固有的， G （global） 全局的
     *          P （public） 公用的，指 系统全局 和 部门之间公用的
     *          D （department）部门（机构）特有的角色
     *          I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     *  角色的类别 F/G/P/D/I/W
     */
    @Column(name = "ROLE_TYPE")
    @Length(max = 1, message = "字段长度必须为{max}")
    private String roleType; // 角色类别

    @Column(name = "UNIT_CODE")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String unitCode; // 角色所属机构

    @Column(name = "OS_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String osId; // 角色所属机构

    @Column(name = "CHANGE_DESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String changeDesc; // 说明

    @Transient
    private List<RolePower> rolePowers;

    public FVUserRoles() {
        obtainType = "D";
    }

    public FVUserRoles(String userCode, String roleCode) {
        obtainType = "D";
        this.id = new UserRoleId(userCode, roleCode);
    }

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

    /**
     * 这个是新版本的一个新的性所有 添加了这个 默认实现
     * 用户获得这个角色的方式，
     * @return "D" 直接活的 ， "I" 从机构继承， "M" 从机构层级继承，至少夸一级，这个默认不打开
     */
    public String getObtainType() {
      return obtainType;
    }

    public void setObtainType(String obtainType) {
      this.obtainType = obtainType;
    }

    public String getInheritedFrom() {
        return inheritedFrom;
    }

    public void setInheritedFrom(String inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }

    /**
     * 角色的类别 F （Fixe）系统内置的，固有的， G （global） 全局的
     *          P （public） 公用的，指 系统全局 和 部门之间公用的
     *          D （department）部门（机构）特有的角色
     *          I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     *  角色的类别 F/G/P/D/I/W
     * @return 角色的类别 F/G/P/D/I/W
     */
    public String getRoleType() {
      return roleType;
    }

    /**
     * 角色的类别 F （Fixe）系统内置的，固有的， G （global） 全局的
     *          P （public） 公用的，指 系统全局 和 部门之间公用的
     *          D （department）部门（机构）特有的角色
     *          I ( Item )为项目角色 W (workflow)工作流角色 ，这两个为保留类别，暂时没有使用
     * @param roleType 角色的类别 F/G/P/D/I/W
     */
    public void setRoleType(String roleType) {
      this.roleType = roleType;
    }

    public String getUnitCode() {
      return unitCode;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    public void setUnitCode(String unitCode) {
      this.unitCode = unitCode;
    }

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
        if(CollectionUtils.isEmpty(rolePowers)) {
            return;
        }
        getRolePowers().addAll(rolePowers);
    }

    public String getChangeDesc() {
        return changeDesc;
    }

    public void setChangeDesc(String changeDesc) {
        this.changeDesc = changeDesc;
    }

    public UserRole toUserRole(){
        UserRole ur = new UserRole(new UserRoleId(this.getUserCode(), this.getRoleCode()),
            DatetimeOpt.addDays(DatetimeOpt.currentUtilDate(), -1), this.getChangeDesc());
        ur.setObtainType(this.getObtainType());
        ur.setInheritedFrom(this.getInheritedFrom());
        return ur;
    }

    public RoleInfo toRoleInfo(){
        RoleInfo roleInfo = new RoleInfo(
            this.getRoleCode(), this.getRoleName(), this.getRoleType(),
            this.getUnitCode(), this.getIsValid(), this.getRoleDesc()
        );
        roleInfo.setUnitCode(this.getUnitCode());
        roleInfo.setOsId(this.getOsId());
        roleInfo.setRolePowers(this.getRolePowers());
        return roleInfo;
    }

    public static List<UserRole> mapToUserRoles(List<FVUserRoles> roles){
        if(roles==null){
            return null;
        }
        List<UserRole> userRoles = new ArrayList<>(roles.size());
        for(FVUserRoles ur : roles){
            userRoles.add(ur.toUserRole());
        }
        return userRoles;
    }
}
