package com.centit.framework.model.basedata;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;


@ApiModel(value = "租户基本信息", description = "租户基本信息")
@Entity
@Data
@Table(name = "f_tenant_info")
public class TenantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "top_unit")
    @ValueGenerator(strategy = GeneratorType.RANDOM_ID, value = "7:T")
    @Length(max = 32)
    @ApiModelProperty(value = "机构id", name = "topUnit")
    private String topUnit;

    @Column(name = "unit_name")
    @Length(max = 300)
    @ApiModelProperty(value = "机构名", name = "unitName")
    @NotEmpty
    private String unitName;

    @Column(name = "source_url")
    @Length(max = 300)
    @ApiModelProperty(value = "资源地址", name = "sourceUrl")
    private String sourceUrl;

    @Column(name = "use_limittime")
    @ApiModelProperty(value = "使用时限", name = "useLimittime")
    private Date useLimittime;

    @Column(name = "tenant_fee")
    @ApiModelProperty(value = "租用费用", name = "tenantFee")
    private int tenantFee;

    @Column(name = "own_user")
    @Length(max = 32)
    @ApiModelProperty(value = "租户所有人", name = "ownUser")
    private String ownUser;

    @Column(name = "is_available")
    @Length(max = 1)
    @ApiModelProperty(value = "受否可用,T:可用，F：不可用", name = "isAvailable")
    private String isAvailable;

    @Column(name = "apply_time")
    @ApiModelProperty(value = "申请时间", name = "applyTime")
    private Date applyTime;

    @Column(name = "pass_time")
    @ApiModelProperty(value = "通过时间", name = "passTime")
    private Date passTime;

    @Column(name = "memo")
    @ApiModelProperty(value = "备注", name = "memo")
    private String memo;

    @Column(name = "remarks")
    @Length(max = 500)
    @ApiModelProperty(value = "备注", name = "remarks")
    private String remarks;

    @Column(name = "creator")
    @Length(max = 32)
    @ApiModelProperty(value = "申请人", name = "creator")
    private String creator;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间", name = "createTime")
    private Date createTime;

    @Column(name = "updator")
    @Length(max = 32)
    @ApiModelProperty(value = "更新人", name = "updator")
    private String updator;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间", name = "updateTime")
    private Date updateTime;

    @Column(name = "database_number_limit")
    @ApiModelProperty(value = "数据库个数上限 ,数据类型：D", name = "databaseNumberLimit")
    private Integer databaseNumberLimit;

    @Column(name = "os_number_limit")
    @ApiModelProperty(value = "应用个数上限 数据类型：O", name = "osNumberLimit")
    private Integer osNumberLimit;

    @Column(name = "file_space_limit")
    @ApiModelProperty(value = "文件服务空间上限 数据类型：F", name = "fileSpaceLimit")
    private Integer fileSpaceLimit;

    @Column(name = "data_space_limit")
    @ApiModelProperty(value = "数据空间上限 数据类型：C", name = "dataSpaceLimit")
    private Integer dataSpaceLimit;

    @Column(name = "user_number_limit")
    @ApiModelProperty(value = "租户下用户总数上限 ", name = "userNumberLimit")
    private Integer userNumberLimit;

    @Column(name = "unit_number_limit")
    @ApiModelProperty(value = "租户下单位个数上限 ", name = "unitNumberLimit")
    private Integer unitNumberLimit;

    @ApiModelProperty(value = "图片id")
    @Column(name = "PIC_ID")
    @Length(max = 64)
    private String picId;

    @ApiModelProperty(value = "网页logo图片主键")
    @Column(name = "LOGO_FILE_ID")
    @Length(max = 64)
    private String logoFileId;

    @Override
    public String toString() {
        return "TenantInfo{" +
            "topUnit='" + topUnit + '\'' +
            ", unitName='" + unitName + '\'' +
            ", sourceUrl='" + sourceUrl + '\'' +
            ", useLimittime=" + useLimittime +
            ", tenantFee=" + tenantFee +
            ", ownUser='" + ownUser + '\'' +
            ", isAvailable='" + isAvailable + '\'' +
            ", applyTime=" + applyTime +
            ", passTime=" + passTime +
            ", memo='" + memo + '\'' +
            ", remarks='" + remarks + '\'' +
            ", creator='" + creator + '\'' +
            ", createTime=" + createTime +
            ", updator='" + updator + '\'' +
            ", updateTime=" + updateTime +
            ", databaseNumberLimit=" + databaseNumberLimit +
            ", osNumberLimit=" + osNumberLimit +
            ", fileSpaceLimit=" + fileSpaceLimit +
            ", dataSpaceLimit=" + dataSpaceLimit +
            ", userNumberLimit=" + userNumberLimit +
            ", unitNumberLimit=" + unitNumberLimit +
            '}';
    }
}
