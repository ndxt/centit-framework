package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.OperationLog;

import java.util.List;
import java.util.Map;

/**
 *
 * 操作日志持久化接口，可以单独写入一个日志，和批量写入一个数组
 *
 * @author codefan
 * /@create 2015年10月14日
 * /@version
 */
//默认不支持日志的查询操作
public interface OperationLogWriter {

    void save(OperationLog optLog);

    void save(List<OperationLog> optLogs);

    default List<? extends OperationLog>
        listOptLog(String optId, Map<String, Object> filterMap, int startPos, int maxRows){
        return null;
    }

    default int
        countOptLog(String optId, Map<String, Object> filter){
        return 0;
    }
}
