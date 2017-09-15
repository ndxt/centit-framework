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
     String getOptScopeCode(); 
    /**
     * 所属业务ID
     * @return optId
     */
     String getOptId();
    /**
     * 数据范围过滤名称
     * @return 数据范围过滤名称
     */
     String getScopeName();
    /**
     * 数据范围过滤条件
     * @return 数据范围过滤条件
     */
     String getFilterCondition();
}
