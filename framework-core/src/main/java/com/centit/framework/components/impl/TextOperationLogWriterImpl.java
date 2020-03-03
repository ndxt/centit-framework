package com.centit.framework.components.impl;

import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.file.TxtLogFile;

import java.util.List;

public class TextOperationLogWriterImpl implements OperationLogWriter{

    //@PostConstruct
    public void init(){
        OperationLogCenter.initOperationLogWriter(this);
        //OperationLogCenter.registerOperationLogWriter(this);
    }

    private String optLogHomePath;

    public void setOptLogHomePath(String optLogPath){
        if(optLogPath.endsWith("/") || optLogPath.endsWith("\\")){
            this.optLogHomePath = optLogPath.substring(0,optLogPath.length()-1);
        }else {
            this.optLogHomePath = optLogPath;
        }
    }

    public String getCurrentLogPath(){
        return optLogHomePath+"/D"
                +DatetimeOpt.convertDateToString(
                        DatetimeOpt.currentUtilDate(),"yyyyMMdd")+".log";
    }

    @Override
    public void save(OperationLog optLog) {
        TxtLogFile.writeLog(getCurrentLogPath(), optLog.toString(),
                true,true);
    }

    @Override
    public void save(List<OperationLog> optLogs) {
        String logFilePath = getCurrentLogPath();
        TxtLogFile logger = new TxtLogFile();
        logger.openLogFile(logFilePath);
        for(OperationLog optLog : optLogs) {
            logger.writeLog(optLog.toString(),
                true, true);
        }
        logger.closeLogFile();
    }
}
