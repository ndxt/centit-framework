package com.centit.framework.model.basedata;

/**
 * 业务操作范围定义
 * @author codefan@hotmail.com
 */
public interface IOptDataScope {
	/**
	 * 数据范围过滤主键
	 * @return 数据范围过滤主键
	 */
    public String getOptScopeCode(); 
    /**
     * 所属业务ID
     * @return optId
     */
    public String getOptId();
    /**
     * 数据范围过滤名称
     * @return 数据范围过滤名称
     */
	public String getScopeName();
	/**
	 * 数据范围过滤条件
	 * @return 数据范围过滤条件
	 */
	public String getFilterCondition();
}
