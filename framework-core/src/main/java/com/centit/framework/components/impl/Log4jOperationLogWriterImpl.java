package com.centit.framework.components.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  配置日志级别为log
 *   &lt;category name="com.centit.framework.components.impl.Log4jOperationLogWriterImpl"&gt;
 *   &lt;priority value="info"/&gt;
 *   &lt;appender-ref ref="appender-info"/&gt;
 *   &lt;/category&gt;
 */
@SuppressWarnings("unused")
public class Log4jOperationLogWriterImpl implements OperationLogWriter{

    protected Logger logger = LoggerFactory.getLogger(Log4jOperationLogWriterImpl.class);

    @Override
    public void save(OperationLog optLog) {
        logger.info(optLog.toString());
    }

    @Override
    public void save(List<OperationLog> optLogs) {
        for(OperationLog optLog : optLogs)
            logger.info(optLog.toString());
    }
}
