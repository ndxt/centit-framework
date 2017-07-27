package com.centit.framework.system.service.impl;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.dao.QueryParameterPrepare;
import com.centit.framework.system.dao.OptFlowNoInfoDao;
import com.centit.framework.system.dao.OptFlowNoPoolDao;
import com.centit.framework.system.po.OptFlowNoInfo;
import com.centit.framework.system.po.OptFlowNoInfoId;
import com.centit.framework.system.po.OptFlowNoPool;
import com.centit.framework.system.po.OptFlowNoPoolId;
import com.centit.framework.system.service.OptFlowNoInfoManager;
import com.centit.support.algorithm.DatetimeOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OptFlowNoInfoManagerImpl implements OptFlowNoInfoManager {

    public static final Logger logger = LoggerFactory.getLogger(OptFlowNoInfoManager.class);

    @Resource
    @NotNull
    private OptFlowNoInfoDao optFlowNoInfoDao;
    
    @Resource
    @NotNull
    private OptFlowNoPoolDao optFlowNoPoolDao;


    public synchronized void setOptFlowNoPoolDao(OptFlowNoPoolDao baseDao) {
        this.optFlowNoPoolDao = baseDao;
    }

    /**
     * 获取最新的流水号，并标记+1
     */
    @Override
    public synchronized long newNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
        Date codeDate = codeBaseDate; // DatetimeOpt.convertSqlDate(codeBaseDate);
        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        long nextCode = 1l;
        if (noInfo == null) {
            noInfo = new OptFlowNoInfo(noId, 1l, DatetimeOpt.currentUtilDate());
        } else {
            nextCode = noInfo.getCurNo() + 1;
            //检查新生产的号是否已经被预留
            while (true) {
                OptFlowNoPoolId poolId = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, nextCode);
                OptFlowNoPool poolNo = optFlowNoPoolDao.getObjectById(poolId);
                //没有被预留
                if (poolNo == null) {
                    break;
                }
                nextCode++;
            }
            noInfo.setCurNo(nextCode);
            noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());

        }
        optFlowNoInfoDao.saveObject(noInfo);
        return nextCode;
    }

    /**
     * 以天为单位记录流水号
     */
    @Override
    public long newNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    /**
     * 以月为单位记录流水号
     */
    @Override
    public long newNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    /**
     * 以年为单位记录流水号
     */
    @Override
    public long newNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long newNextLsh(String codeCode) {
        return newNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long newNextLsh(String ownerCode, String codeCode) {
        return newNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    /**
     * 查看最新流水号
     */
    @Override
    public synchronized long viewNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
        Date codeDate = codeBaseDate; // DatetimeOpt.convertSqlDate(codeBaseDate);
        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        long nextCode = 1l;
        if (noInfo != null)
            nextCode = noInfo.getCurNo() + 1;
        return nextCode;
    }

    @Override
    public long viewNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    @Override
    public long viewNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    @Override
    public long viewNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long viewNextLsh(String codeCode) {
        return viewNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long viewNextLsh(String ownerCode, String codeCode) {
        return viewNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public synchronized void recordNextLsh(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        Date codeDate = codeBaseDate;// DatetimeOpt.convertSqlDate(codeBaseDate);
        // 如果是从池中取出的，在池中删除
        OptFlowNoPoolId poolId = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, currCode);

        OptFlowNoPool poolNo = optFlowNoPoolDao.getObjectById(poolId);
        if (poolNo != null) {
            optFlowNoPoolDao.deleteObject(poolNo);
            return;
        }

        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        if (noInfo == null) {
            noInfo = new OptFlowNoInfo(noId, currCode, DatetimeOpt.currentUtilDate());
            optFlowNoInfoDao.saveObject(noInfo);
        } else {
            if (noInfo.getCurNo() < currCode) {
                noInfo.setCurNo(currCode);
                noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());
                optFlowNoInfoDao.saveObject(noInfo);
            }
        }
    }

    @Override
    public void recordNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLsh(String codeCode, long currCode) {
        recordNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public void recordNextLsh(String ownerCode, String codeCode, long currCode) {
        recordNextLsh(ownerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public synchronized long assignNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
    	Map map=new HashMap();
    	map.put("ownerCode", ownerCode);
    	map.put("codeCode", codeCode);
    	map.put("codeBaseDate", String.valueOf(codeBaseDate));
    
         long minPoolNo = optFlowNoPoolDao.fetchFirstLsh(map);
        if (minPoolNo > 0) {
            OptFlowNoPoolId obj = new OptFlowNoPoolId();
            obj.setOwnerCode(ownerCode);
            obj.setCodeDate(codeBaseDate);
            obj.setCodeCode(codeCode);
            obj.setCurNo(minPoolNo);
            optFlowNoPoolDao.deleteObjectById(obj);
            return minPoolNo;
        } else
            return newNextLsh(ownerCode, codeCode, codeBaseDate);
    }

    @Override
    public long assignNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    @Override
    public long assignNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    @Override
    public long assignNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long assignNextLsh(String ownerCode, String codeCode) {
        return assignNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long assignNextLsh(String codeCode) {
        return assignNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public void releaseLsh(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        OptFlowNoPool obj = new OptFlowNoPool();
        obj.setOwnerCode(ownerCode);
        obj.setCodeDate(codeBaseDate);
        obj.setCodeCode(codeCode);
        obj.setCurNo(currCode);
        obj.setCreateDate(DatetimeOpt.currentUtilDate());
        optFlowNoPoolDao.saveObject(obj);
    }

    @Override
    public void releaseLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), currCode);
    }

    @Override
    public void releaseLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), currCode);
    }

    @Override
    public void releaseLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), currCode);
    }

    @Override
    public void releaseLsh(String ownerCode, String codeCode, long currCode) {
        releaseLsh(ownerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public void releaseLsh(String codeCode, long currCode) {
        releaseLsh(DefaultOwnerCode, codeCode, DefaultCodeDate, currCode);
    }

    public List<OptFlowNoPool> listLshInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        Map<String, Object> filterMap = new HashMap<>();

        filterMap.put("ownerCode", ownerCode);
        filterMap.put("codeDate", codeBaseDate);
        filterMap.put("codeCode", codeCode);
        
        return optFlowNoPoolDao.pageQuery(QueryParameterPrepare.prepPageParmers(filterMap,pageDesc,optFlowNoPoolDao.pageCount(filterMap)));
    }

    public List<OptFlowNoPool> listLshBaseDayInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshBaseMonthInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshBaseYearInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshInPool(String ownerCode, String codeCode, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DefaultCodeDate, pageDesc);
    }

    public List<OptFlowNoPool> listLshInPool(String codeCode, PageDesc pageDesc) {
        return listLshInPool(DefaultOwnerCode, codeCode, DefaultCodeDate, pageDesc);
    }

    
}
