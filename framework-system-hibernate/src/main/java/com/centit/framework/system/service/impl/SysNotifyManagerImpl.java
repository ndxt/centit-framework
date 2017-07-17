package com.centit.framework.system.service.impl;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.SysNotifyDao;
import com.centit.framework.system.po.SysNotify;
import com.centit.framework.system.service.SysNotifyManager;

@Service
@Transactional
public class SysNotifyManagerImpl extends BaseEntityManagerImpl<SysNotify, Long, SysNotifyDao>
        implements SysNotifyManager {

    @Override
    @NotNull
    @Resource(name = "sysNotifyDao")
    protected void setBaseDao(SysNotifyDao baseDao) {
        super.baseDao = baseDao;
    }

    public void saveObject(SysNotify o){
        o.setNotifyId( DatabaseOptUtils.getNextLongSequence(baseDao, "S_NOTIFY_ID"));
        // this.getNextLongSequence("S_NOTIFY_ID") );
        baseDao.saveNewObject(o);
    }

}
