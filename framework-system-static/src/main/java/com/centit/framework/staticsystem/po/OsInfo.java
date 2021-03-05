package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IOsInfo;

public class OsInfo implements IOsInfo, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String osId;

    private String osName;

    private String osUrl;

    private String osType;

    private String topUnit;

    private String osHomePage;

    private String oauthUser;

    private String oauthPassword;

    private String relOptId; // 顶层业务编号

    public OsInfo() {
    }

    public OsInfo(String osId, String osName) {
        this.osId = osId;
        this.osName = osName;
    }

    @Override
    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    @Override
    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    @Override
    public String getOsUrl() {
        return osUrl;
    }

    public void setOsUrl(String osUrl) {
        this.osUrl = osUrl;
    }

    @Override
    public String getOsHomePage() {
        return osHomePage;
    }

    public void setOsHomePage(String osHomePage) {
        this.osHomePage = osHomePage;
    }

    @Override
    public String getOauthUser() {
        return oauthUser;
    }

    public void setOauthUser(String oauthUser) {
        this.oauthUser = oauthUser;
    }

    @Override
    public String getOauthPassword() {
        return oauthPassword;
    }

    public void setOauthPassword(String oauthPassword) {
        this.oauthPassword = oauthPassword;
    }

    @Override
    public String getRelOptId() {
        return relOptId;
    }

    public void setRelOptId(String relOptId) {
        this.relOptId = relOptId;
    }

    @Override
    public String getTopUnit() {
        return topUnit;
    }


    @Override
    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }
}
