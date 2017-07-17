package com.centit.framework.model.basedata;

/**
 *  业务模块说明
 * @author codefan@hotmail.com
 */
public interface IOptInfo {
	/**
	 * 业务ID 主键
	 * @return
	 */
    public String getOptId();
    /**
     * 父业务ID
     * @return
     */
    public String getPreOptId();
    /**
     * 业务名称
     * @return
     */
    public String getOptName();
    
    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     * @return
     */
    public String getOptType();
    /**
     * 业务菜单其实地址 url
     * @return
     */
    public String getOptRoute();
    /**
     * 后台权限控制业务url前缀（不是必须的）
     * @return
     */
    public String getOptUrl();
    
    /**
     * 是否是菜单项，Y:是 N:否
     * @return
     */    
    public String getIsInToolbar();
    /**
     * 图标编号
     * @return
     */
    public Long getImgIndex();
    
    /**
     * 页面打开方式 D: DIV I： iFrame
     * @return
     */
    public String getPageType();
    /**
     * 业务排序号
     * @return
     */
    public Long getOrderInd();
}
