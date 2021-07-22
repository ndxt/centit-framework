package com.centit.framework.model.basedata;

import java.util.List;

/**
 *  业务模块说明
 * @author codefan@hotmail.com
 */
public interface IOptInfo {
    /**
     * 业务ID 主键
     * @return optId
     */
    String getOptId();

    /**
     * 父业务ID
     * @return 父业务ID
     */
    String getPreOptId();

    /**
     * 业务名称 :// 国际化
     * @return 业务名称
     */
    String getOptName();

    /**
     * 当前语言对应的业务名称
     * @return 当前语言对应的菜单名称
     */
    default String getLocalOptName(){
        return getOptName();
    }
    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     * @return OptType
     */
    String getOptType();

    /**
     * 业务菜单其实地址 url
     * @return 业务菜单其实地址 url
     */
    String getOptRoute();

    /**
     * 后台权限控制业务url前缀（不是必须的）
     * @return 后台权限控制业务url前缀
     */
    String getOptUrl();

    /**
     * @return 菜单所属的最高 菜单
     */
    String getTopOptId();

    /**
     * 是否是菜单项，Y:是 N:否
     * @return 是否是菜单项，Y:是 N:否
     */
    String getIsInToolbar();

    /**
     * 图标编号
     * @return 图标编号 Icon
     */
     //Long getImgIndex();
    String getIcon();

    /**
     * 页面打开方式 D: DIV I： iFrame
     * @return 页面打开方式 D: DIV I： iFrame
     */
    String getPageType();

    /**
     * 业务排序号
     * @return 业务排序号
     */
    Long getOrderInd();

    /**
     *
     * @return 返回业务的下层业务
     */
    List<? extends IOptInfo> getChildren();

    /**
     * 这个属性不需要持久化，这个字段属于DTO，只用来存储树形展示时，其子业务是否打开
     * @return 状态
     */
    String getState();
}
