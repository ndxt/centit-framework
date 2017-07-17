package com.centit.framework.staticsystem.po;

import java.util.Date;

import com.centit.framework.model.basedata.IUserSetting;
import com.centit.support.algorithm.DatetimeOpt;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */

public class UserSetting implements IUserSetting, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    private String userCode;

    /**
     * 参数代码
     */
    private String paramCode;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 业务ID
     */
    private String optId;
    /**
     * 参数中文名称
     */
    private String paramName;
    /**
     * 创建时间
     */
    private Date createDate;


    /**
     * default constructor
     */
    public UserSetting() {
    }

    /**
     * minimal constructor
     */
   
    public UserSetting(String userCode, String paramCode,String paramValue, String paramName) {
    	this.userCode = userCode;
        this.paramCode = paramCode;
	    this.paramValue = paramValue;
	    this.optId = "SYS";
	    this.paramName = paramName;
	    this.createDate = DatetimeOpt.currentUtilDate();
	}

    /**
     * full constructor
     */
    public UserSetting(String userCode, String paramCode,
    		String paramValue, String paramClass, String paramName, Date createDate) {
    	this.userCode = userCode;
        this.paramCode = paramCode;
        this.paramValue = paramValue;
        this.optId = paramClass;
        this.paramName = paramName;
        this.createDate = createDate;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getParamCode() {
        return this.paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
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
