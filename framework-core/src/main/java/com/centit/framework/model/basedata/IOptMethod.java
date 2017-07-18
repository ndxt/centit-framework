package com.centit.framework.model.basedata;

/**
 * 业务操作定义
 * @author codefan@hotmail.com
 */
public interface IOptMethod{
	/**
	 * 操作方法主键
	 * @return 操作方法主键
	 */
    String getOptCode(); 
    /**
     * 所属业务ID
     * @return 所属业务ID
     */
    String getOptId();
    /**
     * 操作名称
     * @return 操作名称
     */
    String getOptName();
    /**
     * 操作方法
     * @return 操作方法
     */
    String getOptMethod();
    /**
     * 操作URL
     * @return 操作URL
     */
    String getOptUrl();
    /**
     * 操作url请求方法 CRUD 
     * @return 操作url请求方法 CRUD
     */
    String getOptReq();
    /**
     * 操作方法排序
     * @return 操作方法排序
     */
    Integer getOptOrder();
}
