package com.centit.framework.model.basedata;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

/**
 * create by scaffold 2020-08-18 13:38:15
 *
 * @author codefan@sina.com
 * <p>
 * 项目组成员
 */
@Embeddable
public class WorkGroupParameter implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组id")
    @Column(name = "group_id")
    @NotBlank
    private  String groupId;

    @ApiModelProperty(value = "用户代码")
    @NotBlank
    @Column(name = "user_code")
    private  String userCode;

    @ApiModelProperty(value = "角色代码")
    @NotBlank
    @Column(name = "role_code")
    private  String  roleCode;

    public WorkGroupParameter() {
    }

    public WorkGroupParameter(String groupId, String userCode, String roleCode) {
        this.groupId = groupId;
        this.userCode = userCode;
        this.roleCode = roleCode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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
