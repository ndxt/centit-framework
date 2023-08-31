package com.centit.framework.model.basedata;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "F_V_OPTDEF_URL_MAP")
public class OptMethodUrlMap implements java.io.Serializable {

    private static final long serialVersionUID = 396021378825483579L;

    @Id
    @Column(name = "OPT_CODE")
    //@GeneratedValue(generator = "assignedGenerator")
    private String optCode;// 操作代码

    @Column(name = "OPT_DEF_URL")
    @Length(max = 50, message = "字段长度不能大于{max}")
    private String optDefUrl; // 操作名称

    @Column(name = "OPT_REQ")
    @Length(max = 6, message = "字段长度必须为{max}")
    private String optReq;


    public OptMethodUrlMap() {
    }

    /**
     * minimal constructor
     * @param optcode String
     */
    public OptMethodUrlMap(String optcode) {
        this.optCode = optcode;
    }



    public OptMethodUrlMap(String optcode, String optdefurl, String optmethod) {
        this.optCode = optcode;
        this.optDefUrl = optdefurl;
        this.optReq = optmethod;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public String getOptDefUrl() {
        return optDefUrl;
    }

    public void setOptDefUrl(String optDefUrl) {
        this.optDefUrl = optDefUrl;
    }

    public String getOptReq() {
        return optReq;
    }

    public void setOptReq(String optReq) {
        this.optReq = optReq;
    }


}
