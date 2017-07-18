package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.centit.framework.model.basedata.IUserSetting;
import com.centit.support.algorithm.DatetimeOpt;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "F_USERSETTING")
public class UserSetting implements IUserSetting,java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserSettingId cid;
    /**
     * 参数值
     */
    @Column(name = "PARAMVALUE")
    @NotBlank(message = "不能为空")
    @Length(max = 2000, message = "字段长度不能大于{max}")
    private String paramValue;

    /**
     * 业务ID
     */
    @Column(name = "OPTID")
    @Length(max = 16, message = "字段长度不能大于{max}")
    private String optId;
    /**
     * 参数中文名称
     */
    @Column(name = "PARAMNAME")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String paramName;
    /**
     * 创建时间
     */
    @Column(name = "CREATEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;


    /**
     * default constructor
     */
    public UserSetting() {
    }

    /**
     * minimal constructor
     * @param id UserSettingId
     */
    public UserSetting(UserSettingId id

    ) {
        this.cid = id;

    }
    
    public UserSetting(String userCode, String paramCode,String paramValue, String paramName) {
	    this.cid = new UserSettingId(userCode, paramCode);
	    this.paramValue = paramValue;
	    this.optId = "SYS";
	    this.paramName = paramName;
	    this.createDate = DatetimeOpt.currentUtilDate();
	}
    
    public UserSetting(String userCode, String paramCode,String paramValue,
    			String paramClass, String paramName) {
        this.cid = new UserSettingId(userCode, paramCode);
        this.paramValue = paramValue;
        this.optId = paramClass;
        this.paramName = paramName;
        this.createDate = DatetimeOpt.currentUtilDate();
    }

    public UserSetting(UserSettingId id

            , String paramValue, String paramClass, String paramName, Date createDate) {
        this.cid = id;

        this.paramValue = paramValue;
        this.optId = paramClass;
        this.paramName = paramName;
        this.createDate = createDate;
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

    // Property accessors

//	public UserInfo getUserInfo() {
//		return userInfo;
//	}
//
//	public void setUserInfo(UserInfo userInfo) {
//		this.userInfo = userInfo;
//	}

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

    public void copy(UserSetting other) {

        this.setUserCode(other.getUserCode());
        this.setParamCode(other.getParamCode());

        this.paramValue = other.getParamValue();
        this.optId = other.getOptId();
        this.paramName = other.getParamName();
        this.createDate = other.getCreateDate();

    }

    public void copyNotNullProperty(UserSetting other) {

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
        if (other.getCreateDate() != null)
            this.createDate = other.getCreateDate();

    }

    public void clearProperties() {

        this.paramValue = null;
        this.optId = null;
        this.paramName = null;
        this.createDate = null;

    }
}
