package com.centit.framework.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.dao.QueryParameterPrepare;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.mybatis.dao.BaseDaoSupport;
import com.centit.framework.mybatis.dao.DatabaseOptUtils;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.system.dao.OptLogDao;
import com.centit.framework.system.po.OptLog;
import com.centit.framework.system.service.OptLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ContextLoaderListener;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service("optLogManager")
public class OptLogManagerImpl implements OptLogManager,OperationLogWriter {

    public static final Logger logger = LoggerFactory.getLogger(OptLogManager.class);

    @Resource
    @NotNull
    protected OptLogDao optLogDao;


    @Autowired
    private BaseDaoSupport baseDaoSupport;
    
//    @PostConstruct
//    public void init() {
//        OperationLogWriter optLogManager =
//                ContextLoaderListener.getCurrentWebApplicationContext().
//                getBean("optLogManager",  OperationLogWriter.class);
//        // 这个地方不能直接用 this， this不是spring管理的bean，必须从容器中获取托管的 bean
//        OperationLogCenter.initOperationLogWriter(optLogManager);
//     }


    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void saveBatchObjects(List<OptLog> optLogs) {
        if (CollectionUtils.isEmpty(optLogs)) {
            return;
        }
        for (OptLog optLog : optLogs) {
            //if (null == optLog.getLogId()) {
            try {
                optLog.setLogId( DatabaseOptUtils.getNextLongSequence(
                        baseDaoSupport.getSqlSessionWithOpenedConnection(),
                        "S_SYS_LOG")
                        /*optLogDao.createNewLogId()*/);
                optLogDao.saveNewObject(optLog);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            //}
        }

    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void delete(Date begin, Date end) {
    	Map <String,String>map =new HashMap<String,String>();
    	map.put("beginDate", String.valueOf(begin));
    	map.put("endDate", String.valueOf(end));
    	 optLogDao.delete(map);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public List<String> listOptIds() {
        return optLogDao.listOptIds();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void deleteMany(Long[] logIds) {
        for (Long logId : logIds) {
            optLogDao.deleteObjectById(logId);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED) 
    public void save(final OperationLog optLog) {        
        OptLog optlog = new OptLog();
        optlog.copy(optLog);
        try {
            optlog.setLogId( DatabaseOptUtils.getNextLongSequence(
                    baseDaoSupport.getSqlSessionWithOpenedConnection(),
                    "S_SYS_LOG")
                        /*optLogDao.createNewLogId()*/);
            optLogDao.saveNewObject(optlog);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

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
    public final JSONArray listObjectsAsJson(
            String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc){
    	
    	//TODO 获取SQL SESSION	
		//SqlSession sqlSession = null;
		
       if(optLogDao==null)
 		{
 			optLogDao=ContextLoaderListener.getCurrentWebApplicationContext().
 		              getBean("optLogDao",  OptLogDao.class);
 			
 		}	
    	List<OptLog> rst=optLogDao.pageQuery(QueryParameterPrepare.prepPageParmers(filterMap, pageDesc,optLogDao.pageCount(filterMap)));
		return  DictionaryMapUtils.objectsToJSONArray(rst);
		
//		return SysDaoOptUtils.listObjectsBySqlAsJson(sqlSession,"sql",filterMap, fields,
//    			(Map<String,KeyValuePair<String,String>> )null, pageDesc);
	   /*return SysDaoOptUtils.listObjectsAsJson(optLogDao, fields, OptLog.class,
    			filterMap, pageDesc);*/
    }


	@Override
	@Transactional
	public OptLog getObjectById(Long logId) {
		return optLogDao.getObjectById(logId);
	}


	@Override
	@Transactional
	public void deleteObjectById(Long logId) {
		optLogDao.deleteObjectById(logId);
	}

}
