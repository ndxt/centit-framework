package com.centit.framework.system.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.OptLog;

public interface OptLogManager {
	
	 OptLog getObjectById(Long logId);
	
	
	 void deleteObjectById(Long logId);
    /**
     * 批量保存
     *
     * @param optLogs List OptLog
     */
    void saveBatchObjects(List<OptLog> optLogs) throws IOException, SQLException;
    /**
     * 清理此日期之间的日志信息
     *
     * @param begin Date
     * @param end Date
     */
    void delete(Date begin, Date end);

    List<String> listOptIds();

    void deleteMany(Long[] logIds);
    
     JSONArray listObjectsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc);
}
