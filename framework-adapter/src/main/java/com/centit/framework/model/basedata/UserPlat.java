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

// 用户所属平台关联表
@Entity
@Table(name = "F_USERPLAT")
@ApiModel(value = "系统用户与第三方认证平台关联", description = "系统用户与第三方认证平台关联 UserPlat")
public class UserPlat implements java.io.Serializable {

    private static final long serialVersionUID = -6663395671528252506L;

    @Id
    @Column(name = "USER_PLAT_ID")
    //@GeneratedValue(generator = "assignedGenerator")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32)
    @ApiModelProperty(value = "用户平台ID", name = "userPlatId")
    private String userPlatId;

    @Column(name = "PLAT_ID")
    @Length(max = 32)
    @ApiModelProperty(value = "平台ID", name = "platId")
    private String platId;

    @Column(name = "USER_CODE")
    @DictionaryMap(fieldName = "userName", value = "userCode")
    @ApiModelProperty(value = "用户代码", name = "userCode")
    private String userCode; // 用户代码

    @Column(name = "CORP_ID")
    @Length(max = 64)
    @ApiModelProperty(value = "公司id", name = "corpId")
    private String corpId;

    @Column(name = "APP_KEY")
    @Length(max = 64)
    @ApiModelProperty(value = "平台应用AppKey", name = "appKey")
    private String appKey; // appKey

    @Column(name = "APP_SECRET")
    @Length(max = 64)
    @ApiModelProperty(value = "平台应用AppSecret", name = "appSecret")
    private String appSecret; // appSecret

    @Column(name = "UNION_ID")
    @ApiModelProperty(value = "第三方用户unionid,部分平台可能没有", name = "unionId")
    private String unionId;

    @Column(name = "USER_ID")
    @ApiModelProperty(value = "第三方用户ID", name = "userId")
    private String userId;

    @Column(name = "CHANGE_DESC")
    @Length(max = 256)
    private String changeDesc; // 备注

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    protected Date createDate;

    //创建人、更新人、更新时间
    /**
     * CREATOR(创建人) 创建人
     */
    @Column(name = "CREATOR")
    @Length(max = 32)
    private String creator;
    /**
     * UPDATOR(更新人) 更新人
     */
    @Column(name = "UPDATOR")
    @Length(max = 32)
    private String updator;
    /**
     * UPDATEDATE(更新时间) 更新时间
     */
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value = "today()")
    private Date updateDate;

    /**
     * 第三方用户名
     */
    @Column(name = "USER_NAME")
    @ApiModelProperty(value = "微信名", name = "weChatName")
    private String userName;

    private String isUsed;

    private String weChatName;
    //结束

    // Constructors

    /**
     * default constructor
     */
    public UserPlat() {
    }

    public String getUserPlatId() {
        return userPlatId;
    }

    public void setUserPlatId(String userPlatId) {
        this.userPlatId = userPlatId;
    }

    /**
     * minimal constructor
     *
     * @param id        String
     */
    public UserPlat(String id) {
        this.userPlatId = id;
    }

    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getChangeDesc() {
        return changeDesc;
    }

    public void setChangeDesc(String changeDesc) {
        this.changeDesc = changeDesc;
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

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    //创建人、更新人、更新时间
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return this.updator;
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

    public Date getLastModifyDate() {
        return updateDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWeChatName() {
        return weChatName;
    }

    public void setWeChatName(String weChatName) {
        this.weChatName = weChatName;
    }
}
