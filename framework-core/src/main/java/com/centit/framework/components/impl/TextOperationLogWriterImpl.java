package com.centit.framework.components.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.file.TxtLogFile;

public class TextOperationLogWriterImpl implements OperationLogWriter{

	//@PostConstruct
	public void init(){
		OperationLogCenter.initOperationLogWriter(this);
		//OperationLogCenter.registerOperationLogWriter(this);
	}
	
	public String getCurrentLogPath(){
		return SysParametersUtils.getLogHome()+"/D"
				+DatetimeOpt.convertDateToString(
						DatetimeOpt.currentUtilDate(),"yyyyMMdd")+".log";
	}
	
	@Override
	public void save(OperationLog optLog) {
		TxtLogFile.writeLog(getCurrentLogPath(), JSON.toJSONString(optLog),
				true,true);
	}

	@Override
	public void save(List<OperationLog> optLogs) {
		String logFilePath = getCurrentLogPath();
		TxtLogFile logger = new TxtLogFile();
		logger.openLogFile(logFilePath);
		for(OperationLog optLog : optLogs)
			logger.writeLog(JSON.toJSONString(optLog),
				true,true);
		logger.closeLogFile();
	}
}
