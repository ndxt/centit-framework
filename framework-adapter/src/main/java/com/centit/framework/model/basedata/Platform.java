package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

//登录平台表
@Entity
@Table(name = "F_PLATFORM")
@ApiModel(value = "第三方认证平台", description = "第三方认证平台 PlatForm")
public class Platform implements java.io.Serializable {

    private static final long serialVersionUID = 4369493468390674196L;

    @Id
    @Column(name = "PLAT_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32)
    @ApiModelProperty(value = "平台ID", name = "platId")
    private String platId;

    @Column(name = "PLAT_SOURCE_CODE")
    @DictionaryMap(fieldName = "platSourceName", value = "platSourceCode")
    @ApiModelProperty(value = "平台来源代码", name = "platSourceCode")
    private String platSourceCode; // 平台代码

    @Column(name = "PLAY_TYPE")
    @DictionaryMap(fieldName = "platName", value = "platType")
    @Length(max = 16, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "平台类型 C:常用平台,T:技术平台,F:国外平台,Q:企业平台,O:其他平台", name = "platType")
    private String platType; // 平台类型

    @Column(name = "APP_KEY")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "AppKey", name = "appKey")
    private String appKey; // appKey

    @Column(name = "APP_SECRET")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "AppSecret", name = "appSecret")
    private String appSecret; // appSecret

    @Column(name = "PLAT_DESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String platDesc; // 备注

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    protected Date createDate;

    @Column(name = "CREATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String creator;

    @Column(name = "UPDATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String updator;

    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value = "today()")
    private Date updateDate;

    @Column(name = "CORP_ID")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "公司id", name = "corpId")
    private String corpId;

    public Platform() {
    }

    public Platform(String platId) {
        this.platId = platId;
    }

    public Platform(String platId, String platSourceCode, String platType, String appKey, String appSecret) {
        this.platId = platId;
        this.platSourceCode = platSourceCode;
        this.platType = platType;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }

    public String getPlatSourceCode() {
        return platSourceCode;
    }

    public void setPlatSourceCode(String platSourceCode) {
        this.platSourceCode = platSourceCode;
    }

    public String getPlatType() {
        return platType;
    }

    public void setPlatType(String platType) {
        this.platType = platType;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getPlatDesc() {
        return platDesc;
    }

    public void setPlatDesc(String platDesc) {
        this.platDesc = platDesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return updator;
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
}
