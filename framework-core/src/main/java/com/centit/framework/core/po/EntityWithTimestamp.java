package com.centit.framework.core.po;

import java.util.Date;

/**
 * PO实体实现这个接口，数据中新建和更新时会自动修改这个时间戳，
 * 前提条件是调用Dao的创建、更新、逻辑删除接口，而不是直接调用HQL
 *
 */
public interface EntityWithTimestamp{
    /**
     * 获取最后更新时间戳
     * @return
     */
    public Date getLastModifyDate();
    /**
     * 设置最后更新时间戳
     * @param lastModifyDate
     */
    public void setLastModifyDate(Date lastModifyDate);

}
