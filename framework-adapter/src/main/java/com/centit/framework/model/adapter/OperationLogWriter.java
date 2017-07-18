package com.centit.framework.model.adapter;

import java.util.List;

import com.centit.framework.model.basedata.OperationLog;

/**
 * 
 * 操作日志持久化接口，可以单独写入一个日志，和批量写入一个数组
 * 
 * @author codefan
 * /@create 2015年10月14日
 * /@version
 */
public interface OperationLogWriter {

    void save(OperationLog optLog);

    void save(List<OperationLog> optLogs);

}
