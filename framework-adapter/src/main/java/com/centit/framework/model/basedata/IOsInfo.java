package com.centit.framework.model.basedata;

import java.util.Date;

public interface IOsInfo {

    /**
     * 业务代码
     *
     * @return 业务代码
     */
    String getOsId();

    /**
     * 业务名称
     *
     * @return 业务名称
     */
    String getOsName();

    /**
     * 业务类别 P(PaaS平台业务） I(基于框架研发的业务） T(第三方研发的业务） O（外部业务）
     *
     * @return 业务类别
     */
    String getOsType();

    /**
     * 业务系统后台url
     */
    String getOsUrl();

    /**
     * 业务系统首页
     */
    String getOsHomePage();

    /**
     * 作为服务接口开发是的认证用户名
     *
     * @return 认证用户名
     */
    String getOauthUser();

    /**
     * 作为服务接口开发是的认证用户密码
     *
     * @return 认证用户密码
     */
    String getOauthPassword();

    /**
     * 多租户应用下的租户
     *
     * @return 租户
     */
    String getTopUnit();

    /**
     * 关联的顶层菜单ID OsType = [P/I]
     */
    String getRelOptId();

    String getPageFlow();

    String getPicId();

    Boolean getIsDelete();

    Date getCreateTime();

    Date getLastModifyDate();

    String getCreated();

}
