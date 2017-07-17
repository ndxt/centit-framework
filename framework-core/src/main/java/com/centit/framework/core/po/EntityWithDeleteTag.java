package com.centit.framework.core.po;

/**
 * PO实体需要实现这个接口，在执行删除操作时自动设置一个逻辑删除标记，
 * 	不会真正的从数据库汇中删除，
 * 当然前提条件是你调用Dao中的接口删除，而不是直接删除
 *
 */
public interface EntityWithDeleteTag  {

 
	/**
	 * 判断是否为已删除
	 */
	public boolean isDeleted();

	/**
	 * 设置删除标志
	 */
	public void setDeleted(boolean isDeleted);
}
