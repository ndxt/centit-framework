package com.centit.framework.components.impl;

import com.alibaba.fastjson.JSON;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  配置日志级别为log
 *   <category name="com.centit.framework.components.impl.Log4jOperationLogWriterImpl">
 *   <priority value="info"/>
 *   <appender-ref ref="appender-info"/>
 *   </category>
 */
@SuppressWarnings("unused")
public class Log4jOperationLogWriterImpl implements OperationLogWriter{

	protected Logger logger = LoggerFactory.getLogger(Log4jOperationLogWriterImpl.class);

	
	@Override
	public void save(OperationLog optLog) {
		logger.info( JSON.toJSONString(optLog) );
	}

	@Override
	public void save(List<OperationLog> optLogs) {
		for(OperationLog optLog : optLogs)
			logger.info( JSON.toJSONString(optLog) );
	}
}
