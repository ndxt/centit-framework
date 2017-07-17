package com.centit.framework.model.basedata;

/**
 * 业务操作范围定义
 * @author codefan@hotmail.com
 */
public interface IOptDataScope {
	/**
	 * 数据范围过滤主键
	 * @return
	 */
    public String getOptScopeCode(); 
    /**
     * 所属业务ID
     * @return
     */
    public String getOptId();
    /**
     * 数据范围过滤名称
     * @return
     */
	public String getScopeName();
	/**
	 * 数据范围过滤条件
	 * @return
	 */
	public String getFilterCondition();
}
