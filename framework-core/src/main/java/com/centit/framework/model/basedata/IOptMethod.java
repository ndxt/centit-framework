package com.centit.framework.model.basedata;

/**
 * 业务操作定义
 * @author codefan@hotmail.com
 */
public interface IOptMethod{
	/**
	 * 操作方法主键
	 * @return
	 */
    String getOptCode(); 
    /**
     * 所属业务ID
     * @return
     */
    String getOptId();
    /**
     * 操作名称
     * @return
     */
    String getOptName();
    /**
     * 操作方法
     * @return
     */
    String getOptMethod();
    /**
     * 操作URL
     * @return
     */
    String getOptUrl();
    /**
     * 操作url请求方法 CRUD 
     * @return
     */
    String getOptReq();
    /**
     * 操作方法排序
     * @return
     */
    Integer getOptOrder();
}
