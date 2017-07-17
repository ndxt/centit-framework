package com.centit.framework.system.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.OptMethodDao;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.service.OptMethodManager;

@Service("optMethodManager")
@Transactional
public class OptMethodManagerImpl extends BaseEntityManagerImpl<OptMethod, String, OptMethodDao>
	implements OptMethodManager {

    @Resource(name = "optMethodDao")
    @NotNull
    @Override
    protected void setBaseDao(OptMethodDao baseDao) {
        super.baseDao = baseDao;
    }

    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    public void mergeObject(OptMethod o) {
        super.mergeObject(o);
    }

    /*private OptMethod getObject(OptMethod object) {
        if (object == null)
            return null;
        OptMethod newObj = baseDao.getObjectById(object.getOptCode());
        if (newObj == null) {
            newObj = object;
            newObj.setOptCode(baseDao.getNextOptCode());
        }
        return newObj;
    }
    */
    
    @Override
    public List<OptMethod> listOptMethodByOptID(String sOptID) {
        return baseDao.listOptMethodByOptID(sOptID);
    }

    @Override
    public String getNextOptCode() {
        return baseDao.getNextOptCode();
    }

}
