package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import com.centit.support.security.AESSecurityUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author tian_y
 */
@Entity
@Table(name = "F_USER_SYNC_DIRECTORY")
@ApiModel(value = "用户同步目录", description = "用户同步目录")
public class UserSyncDirectory implements java.io.Serializable {

    private static final long serialVersionUID = -6663395671528252506L;

    @Id
    @Column(name = "ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32)
    @ApiModelProperty(value = "id", name = "id", required = true)
    private String id;

    @Column(name = "NAME")
    @Length(max = 32)
    @ApiModelProperty(value = "目录名称", name = "name", required = true)
    private String name;

    @Column(name = "TYPE")
    @Length(max = 32)
    @ApiModelProperty(value = "目录类型", name = "type", required = true)
    private String type;

    @Column(name = "URL")
    @Length(max = 64)
    @ApiModelProperty(value = "目录地址", name = "url")
    private String url;

    @Column(name = "USER")
    @Length(max = 32)
    @ApiModelProperty(value = "用户名", name = "user")
    private String user;

    @Column(name = "USER_PWD")
    @Length(max = 32)
    @ApiModelProperty(value = "密码", name = "userPwd")
    private String userPwd;

    @Column(name = "SEARCH_BASE")
    @Length(max = 4096)
    @ApiModelProperty(value = "拓展参数JSON", name = "searchBase")
    private String searchBase;

    @Column(name = "DEFAULT_RANK")
    @Length(max = 32)
    @ApiModelProperty(value = "默认职务", name = "defaultRank")
    private String defaultRank;

    @Column(name = "DEFAULT_STATION")
    @Length(max = 32)
    @ApiModelProperty(value = "默认岗位", name = "defaultStation")
    private String defaultStation;

    @Column(name = "DEFAULT_USERROLE")
    @Length(max = 32)
    @ApiModelProperty(value = "默认角色代码", name = "defaultUserRole")
    private String defaultUserRole;

    @Column(name = "top_unit")
    @Length(max = 32)
    @ApiModelProperty(value = "所属租户", name = "topUnit")
    private String topUnit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserPwd() {
        return userPwd;
    }


    public void setUserPwd(String password) {
        if (StringUtils.isNotBlank(password)) {
            if (password.startsWith("cipher:")) {
                this.userPwd = password;
            } else {
                this.userPwd = "cipher:" + AESSecurityUtils.encryptAndBase64(
                    password, AESSecurityUtils.AES_DEFAULT_KEY);
            }
        }
    }

    @JSONField(serialize = false)
    public String getClearPassword() {
        if(userPwd.startsWith("cipher:"))
         return AESSecurityUtils.decryptBase64String(
            userPwd.substring(7), AESSecurityUtils.AES_DEFAULT_KEY);
        else
            return userPwd;
    }

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getDefaultRank() {
        return defaultRank;
    }

    public void setDefaultRank(String defaultRank) {
        this.defaultRank = defaultRank;
    }

    public String getDefaultStation() {
        return defaultStation;
    }

    public void setDefaultStation(String defaultStation) {
        this.defaultStation = defaultStation;
    }

    public String getDefaultUserRole() {
        return defaultUserRole;
    }

    public void setDefaultUserRole(String defaultUserRole) {
        this.defaultUserRole = defaultUserRole;
    }

    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }
}
