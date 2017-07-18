package com.centit.framework.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptLog;

public interface OptLogManager extends BaseEntityManager<OptLog, Long> {
    /**
     * 批量保存
     *
     * @param optLogs
     */
    void saveBatchObjects(List<OptLog> optLogs);
    /**
     * 清理此日期之间的日志信息
     *
     * @param begin begin
     * @param end end
     */
    void delete(Date begin, Date end);

    List<String> listOptIds();

    void deleteMany(Long[] logIds);
    
    public JSONArray listObjectsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc);
}
