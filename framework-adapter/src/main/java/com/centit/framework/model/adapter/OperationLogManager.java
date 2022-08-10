package com.centit.framework.model.adapter;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface OperationLogManager extends OperationLogWriter {

    void saveOptLog(OperationLog optLog);

    //批量保存
    void saveBatchOptLogs(List<OperationLog> optLogs);

    OperationLog getOptLogById(String logId);

    JSONArray listOptLogsAsJson(String[] fields, Map<String, Object> filterMap, PageDesc pageDesc);

    void deleteOptLogById(String logId);

    //清理此日期之前的日志信息
    int delete(String begin);

    void deleteMany(String[] logIds);
}
