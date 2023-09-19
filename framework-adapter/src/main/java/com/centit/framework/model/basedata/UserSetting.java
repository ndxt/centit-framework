package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.support.algorithm.DatetimeOpt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

/**
 * create by scaffold
 *
 * @author codefan@codefan.com
 */
@Entity
@Table(name = "F_USERSETTING")
@ApiModel(value="系统用户设置对象",description="系统用户设置对象 UserSetting")
public class UserSetting implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    private UserSettingId cid;
    /**
     * 参数值
     */
    @Column(name = "PARAM_VALUE")
    @NotBlank(message = "不能为空")
    @Length(max = 2000, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "参数值 不能为空",name = "paramValue",required = true)
    private String paramValue;

    /**
     * 业务ID
     */
    @Column(name = "OPT_ID")
    @Length(max = 16, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "业务ID",name = "optId")
    private String optId;
    /**
     * 参数中文名称
     */
    @Column(name = "PARAM_NAME")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "参数中文名称",name = "paramName")
    private String paramName;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_DATE")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * 标识用户这个参数是来之 系统的默认设置不是用户自己设置的
     */
    @Transient
    private boolean isDefaultValue;

    /**
     * default constructor
     */
    public UserSetting() {
        this.isDefaultValue = false;
    }

    /**
     * minimal constructor
     * @param id UserSettingId
     */
    public UserSetting(UserSettingId id

    ) {
        this.cid = id;
        this.isDefaultValue = false;
    }

    public UserSetting(String userCode, String paramCode,String paramValue, String paramName) {
        this.cid = new UserSettingId(userCode, paramCode);
        this.paramValue = paramValue;
        this.optId = "SYS";
        this.paramName = paramName;
        this.createDate = DatetimeOpt.currentUtilDate();
        this.isDefaultValue = false;
    }

    public UserSetting(String userCode, String paramCode,String paramValue,
                String paramClass, String paramName) {
        this.cid = new UserSettingId(userCode, paramCode);
        this.paramValue = paramValue;
        this.optId = paramClass;
        this.paramName = paramName;
        this.createDate = DatetimeOpt.currentUtilDate();
        this.isDefaultValue = false;
    }

    public UserSetting(UserSettingId id

            , String paramValue, String paramClass, String paramName, Date createDate) {
        this.cid = id;

        this.paramValue = paramValue;
        this.optId = paramClass;
        this.paramName = paramName;
        this.createDate = createDate;
        this.isDefaultValue = false;
    }

    public UserSettingId getCid() {
        return this.cid;
    }

    public void setCid(UserSettingId id) {
        this.cid = id;
    }

    public String getUserCode() {
        if (this.cid == null)
            this.cid = new UserSettingId();
        return this.cid.getUserCode();
    }

    public void setUserCode(String userCode) {
        if (this.cid == null)
            this.cid = new UserSettingId();
        this.cid.setUserCode(userCode);
    }

    public String getParamCode() {
        if (this.cid == null) {
            this.cid = new UserSettingId();
        }
        return this.cid.getParamCode();
    }

    public void setParamCode(String paramCode) {
        if (this.cid == null)
            this.cid = new UserSettingId();
        this.cid.setParamCode(paramCode);
    }

    public boolean isDefaultValue() {
        return isDefaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        isDefaultValue = defaultValue;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String paramClass) {
        this.optId = paramClass;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

     public void copyFromUserSetting(UserSetting other) {

        if (other.getUserCode() != null)
            this.setUserCode(other.getUserCode());
        if (other.getParamCode() != null)
            this.setParamCode(other.getParamCode());

        if (other.getParamValue() != null)
            this.paramValue = other.getParamValue();
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getParamName() != null)
            this.paramName = other.getParamName();
    }

}
