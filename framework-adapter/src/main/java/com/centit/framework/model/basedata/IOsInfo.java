package com.centit.framework.model.basedata;

public interface IOsInfo {

    String getOsId();

    String getOsName();
    /**
     * 业务系统后台url
     */
    String getOsUrl();
    /**
     * 业务系统首页
     */
    String getOsHomePage();

    String getOauthUser();

    String getOauthPassword();

    String getTopUnit();
    /**
     * 关联的顶层菜单ID
     */
    String getRelOptId();

}
