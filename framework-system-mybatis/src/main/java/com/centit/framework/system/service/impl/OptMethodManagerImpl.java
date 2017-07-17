package com.centit.framework.system.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.system.dao.OptMethodDao;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.service.OptMethodManager;

@Service("optMethodManager")
@Transactional
public class OptMethodManagerImpl implements OptMethodManager {

    @Resource
    @NotNull
    protected OptMethodDao optMethodDao;

    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    public void mergeObject(OptMethod o) {
    	optMethodDao.mergeObject(o);
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
        return optMethodDao.listOptMethodByOptID(sOptID);
    }

    @Override
    public String getNextOptCode() {
        return optMethodDao.getNextOptCode();
    }

	@Override
	public List<OptMethod> listObjects() {
		return optMethodDao.listObjects();
	}

	@Override
	public OptMethod getObjectById(String optCode) {
		return optMethodDao.getObjectById(optCode);
	}

	@Override
	public void deleteObjectById(String optCode) {
		optMethodDao.deleteObjectById(optCode);		
	}

	@Override
	public String saveNewObject(OptMethod optMethod) {
		 optMethodDao.saveNewObject(optMethod);
		 return optMethod.getOptCode();
	}

}
