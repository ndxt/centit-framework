package com.centit.framework.system.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.Length;


/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Embeddable
public class UserSettingId implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户代码
     */
    @Column(name = "USERCODE")
    private String userCode;

    /**
     * 参数代码
     */
    @Column(name = "PARAMCODE")
    @Length(max = 16, message = "字段长度不能大于{max}")
    private String paramCode;

    // Constructors

    /**
     * default constructor
     */
    public UserSettingId() {
    }

    /**
     * full constructor
     */
    public UserSettingId(String userCode, String paramCode) {

        this.userCode = userCode;
        this.paramCode = paramCode;
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


    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof UserSettingId))
            return false;

        UserSettingId castOther = (UserSettingId) other;
        boolean ret = true;

        ret =this.getUserCode() == castOther.getUserCode() ||
                (this.getUserCode() != null && castOther.getUserCode() != null
                        && this.getUserCode().equals(castOther.getUserCode()));

        ret = ret && (this.getParamCode() == castOther.getParamCode() ||
                (this.getParamCode() != null && castOther.getParamCode() != null
                        && this.getParamCode().equals(castOther.getParamCode())));

        return ret;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result +
                (this.getUserCode() == null ? 0 : this.getUserCode().hashCode());

        result = 37 * result +
                (this.getParamCode() == null ? 0 : this.getParamCode().hashCode());

        return result;
    }
}
