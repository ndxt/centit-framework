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
    public String getOptCode(); 
    /**
     * 所属业务ID
     * @return
     */
    public String getOptId();
    /**
     * 操作名称
     * @return
     */
    public String getOptName();
    /**
     * 操作方法
     * @return
     */
    public String getOptMethod();
    /**
     * 操作URL
     * @return
     */
    public String getOptUrl();
    /**
     * 操作url请求方法 CRUD 
     * @return
     */
    public String getOptReq();
    /**
     * 操作方法排序
     * @return
     */
    public Integer getOptOrder();
}
