package com.centit.framework.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.dao.SysDaoOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.dao.OptLogDao;
import com.centit.framework.system.po.OptLog;
import com.centit.framework.system.service.OptLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("optLogManager")
public class OptLogManagerImpl extends BaseEntityManagerImpl<OptLog, Long, OptLogDao>
    implements OptLogManager,OperationLogWriter {

    public static final Logger logger = LoggerFactory.getLogger(OptLogManager.class);

    @Resource(name = "optLogDao")
    @NotNull
    @Override
    protected void setBaseDao(OptLogDao baseDao) {
        super.baseDao = baseDao;
    }


    /*public void init() {
        *//*OperationLogWriter optLogManager =
                ContextLoaderListener.getCurrentWebApplicationContext().
                getBean("optLogManager",  OperationLogWriter.class);
        // 这个地方不能直接用 this， this不是spring管理的bean，必须从容器中获取托管的 bean
        OperationLogCenter.initOperationLogWriter(optLogManager);*//*
    }*/

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void saveBatchObjects(List<OptLog> optLogs) {
        if (CollectionUtils.isEmpty(optLogs)) {
            return;
        }
        for (OptLog optLog : optLogs) {
            //if (null == optLog.getLogId()) {
                optLog.setLogId(DatabaseOptUtils.getNextLongSequence(baseDao, "S_SYS_LOG"));
            //}
        }

        DatabaseOptUtils.mergeBatchObjects(baseDao, optLogs);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void delete(Date begin, Date end) {
        baseDao.delete(begin, end);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public List<String> listOptIds() {
        return baseDao.listOptIds();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void deleteMany(Long[] logIds) {
        for (Long logId : logIds) {
            baseDao.deleteObjectById(logId);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void save(OperationLog optLog) {        
        OptLog optlog = new OptLog();
        optlog.copy(optLog);
        optlog.setLogId(DatabaseOptUtils.getNextLongSequence(baseDao, "S_SYS_LOG"));        
        baseDao.saveNewObject(optlog);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void save(List<OperationLog> optLogs) {
        List<OptLog> optlogs = new ArrayList<OptLog>();
        for(OperationLog ol : optLogs){
            OptLog optlog = new OptLog();
            optlog.copy(ol);
            optlogs.add(optlog);
        }
        saveBatchObjects(optlogs);
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public JSONArray listObjectsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc){
    	return SysDaoOptUtils.listObjectsAsJson(baseDao, fields, OptLog.class,
    			filterMap, pageDesc);
    }

}
