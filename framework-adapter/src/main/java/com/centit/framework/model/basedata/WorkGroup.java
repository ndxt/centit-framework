package com.centit.framework.model.basedata;

import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * create by scaffold 2020-08-18 13:38:15
 * @author codefan@sina.com
 * <p>
 * 项目组成员
 */
@Data
@Entity
@Table(name = "work_group")
public class WorkGroup implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public static final String WORKGROUP_ROLE_CODE_LEADER = "组长";
    public static final String WORKGROUP_ROLE_CODE_MEMBER = "组员";
    public static final String WORKGROUP_ROLE_CODE_ADMIN = "ZHGLY";

    // 主键
//   @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    WorkGroupParameter workGroupParameter;

    @ApiModelProperty(value = "是否生效")
    @Column(name = "is_valid")
    private String  isValid;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @Column(name = "AUTH_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date authTime;

    @ApiModelProperty(value = "创建人")
    @Column(name = "creator")
    private String  creator;

    @ApiModelProperty(value = "更新人")
    @Column(name = "updator")
    private String updator;

    @ApiModelProperty(value = "更新时间",hidden = true)
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date updateDate;

    @ApiModelProperty(value = "排序号")
    @Column(name = "USER_ORDER")
    private BigDecimal userOrder;

    @ApiModelProperty(value = "运行令牌， 角色参数")
    @Column(name = "RUN_TOKEN")
    private String  runToken;

    @ApiModelProperty(value = "授权说明")
    @Column(name = "AUTH_DESC")
    private String authDesc;

    /**
     * 这个roleCode是用作更新主键中的roleCode使用
     */
    @ApiModelProperty(value = "角色代码")
    private String  roleCode;

    public String getGroupId() {
        if (null == workGroupParameter){
            return "";
        }
        return workGroupParameter.getGroupId();
    }

    public String getUserCode() {
        if (null == workGroupParameter){
            return "";
        }
        return workGroupParameter.getUserCode();
    }

    public String getRoleCode() {
        if (null == workGroupParameter){
            return "";
        }
        return workGroupParameter.getRoleCode();
    }

    public void setGroupId(String groupId) {
        if (null == workGroupParameter){
            workGroupParameter = new WorkGroupParameter();
        }
        workGroupParameter.setGroupId(groupId);
    }

    public void setUserCode(String userCode) {
        if (null == workGroupParameter){
            workGroupParameter = new WorkGroupParameter();
        }
        workGroupParameter.setUserCode(userCode);
    }

    public void setRoleCode(String roleCode) {
        if (null == workGroupParameter){
            workGroupParameter = new WorkGroupParameter();
        }
        workGroupParameter.setRoleCode(roleCode);
    }
}
