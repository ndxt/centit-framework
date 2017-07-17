package com.centit.framework.system.service.impl;

/**
 @author codefan@centit.com
 */

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.DataCatalogDao;
import com.centit.framework.system.dao.DataDictionaryDao;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.DataDictionaryId;
import com.centit.framework.system.service.DataDictionaryManager;
import com.centit.support.algorithm.ListOpt;

@Service("dataDictionaryManager")
public class DataDictionaryManagerImpl extends BaseEntityManagerImpl<DataCatalog, String, DataCatalogDao> implements
        DataDictionaryManager {
 
    @Resource
    @NotNull
    private DataDictionaryDao dictionaryDao;

    @Resource(name = "dataCatalogDao")
    @NotNull
    @Override
    protected void setBaseDao(DataCatalogDao baseDao) {
        super.baseDao = baseDao;
    }

    @Override    
    @Transactional
    public List<DataDictionary> getDataDictionary(String catalogCode) {
        logger.info("缓存数据字典  DataDictionary ：" + catalogCode+" ......");
        return dictionaryDao.listDataDictionary(catalogCode);  
        //logger.info("loading DataDictionary end");
    }

    @Override
    @CacheEvict(value = "DataDictionary",key="'CatalogCode'")
    @Transactional
    public Serializable saveNewObject(DataCatalog o) {
        return baseDao.saveNewObject(o);
    }
    
    @Override
    @CacheEvict(value = "DataDictionary",key="'CatalogCode'")
    @Transactional
    public void updateObject(DataCatalog o) {
        baseDao.updateObject(o);
    }
    
    @Override
    @CacheEvict(value = "DataDictionary",key="'CatalogCode'")
    @Transactional
    public void mergeObject(DataCatalog o) {
        baseDao.mergeObject(o);
    }

    @Override
    @CacheEvict(value = "DataDictionary",key="'CatalogCode'")
    @Transactional
    public void deleteObject(DataCatalog o) {
        deleteDataDictionary(o.getCatalogCode());
    }

    @Override
    @Transactional
    public DataCatalog getCatalogIncludeDataPiece(String catalogCode){
        DataCatalog dc = baseDao.getObjectById(catalogCode);
        dc.addAllDataPiece(dictionaryDao.listDataDictionary(catalogCode));
        return dc;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "DataDictionary",key="#dataCatalog.catalogCode")
    public void saveCatalogIncludeDataPiece(DataCatalog dataCatalog,boolean isAdmin){
       
        baseDao.mergeObject(dataCatalog);
        
        List<DataDictionary> oldData = dictionaryDao.listDataDictionary(dataCatalog.getCatalogCode());
        List<DataDictionary> newData = dataCatalog.getDataDictionaries();
        Triple<List<DataDictionary>, List<Pair<DataDictionary,DataDictionary>>, List<DataDictionary>> 
        	dbOptList= ListOpt.compareTwoList(oldData, newData, new Comparator<DataDictionary>(){
					@Override
					public int compare(DataDictionary o1, DataDictionary o2) {
						return o1.getDataCode().compareTo(o2.getDataCode());
					}        	  
		          });
        
         if(dbOptList.getRight()!=null){
            for(DataDictionary dp: dbOptList.getRight() ){
	    		if("U".equals(dp.getDataStyle()) || (isAdmin && "S".equals(dp.getDataStyle()) ) ){
	    			dictionaryDao.deleteObject(dp);
                }
            }
        }
        if( (isAdmin || !"F".equals(dataCatalog.getCatalogStyle()))
        		&& dbOptList.getLeft()!=null ){
        	 for(DataDictionary dp: dbOptList.getLeft() ){
 	    		dictionaryDao.saveNewObject(dp);
             }
        }
        
        if(null != dbOptList.getMiddle()){
            for(Pair<DataDictionary,DataDictionary> updateDp: dbOptList.getMiddle()){
            	DataDictionary oldD = updateDp.getLeft();
            	DataDictionary newD = updateDp.getRight();
            	if("F".equals(oldD.getDataStyle())) 
            		continue;
            	if(isAdmin || "U".equals(oldD.getDataStyle())){
            		/*BeanUtils.copyProperties(newD, oldD, new String[]{"id","dataStyle"});
            		dictionaryDao.updateObject(oldD);*/
            		dictionaryDao.mergeObject(newD);
            	}
            }
        }       
        
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "DataDictionary",key="#catalogCode")
    public void deleteDataDictionary(String catalogCode){
        dictionaryDao.deleteDictionary(catalogCode);
        baseDao.deleteObjectById(catalogCode);
    }
    
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    @CacheEvict(value = "DataDictionary",key="#dd.catalogCode")
    public void deleteDataDictionaryPiece(DataDictionaryId dd) {
        //DataCatalog datacatalog = baseDao.getObjectById(dd.getCatalogCode());
        //datacatalog.setIsUpload("0");
        //baseDao.saveObject(datacatalog);
        dictionaryDao.deleteObjectById(dd);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    @CacheEvict(value = "DataDictionary",key="#dd.catalogCode")
    public void saveDataDictionaryPiece(DataDictionary dd) {
        //DataCatalog datacatalog = baseDao.getObjectById(dd.getCatalogCode());
        // datacatalog.setIsUpload("0");
        //datacatalog.setLastModifyDate(lastModifyDate);
        //baseDao.saveObject(datacatalog);
        dictionaryDao.mergeObject(dd);
    }

    public String[] getFieldsDesc(String sDesc, String sType) {
        String[] nRes = {"数据代码", "扩展代码(父代码)", "扩展代码(排序号)", "标记", "数值", "类型", "数据描述"};
        if ("T".equals(sType))
            nRes[1] = "上级代码";
        if (sDesc == null || "".equals(sDesc))
            return nRes;
        String[] s = StringUtils.split(sDesc, ';');
        if (s == null)
            return nRes;
        int n = s.length;

        for (int i = 0; i < n; i++) {
            int p = s[i].indexOf(':');
            if (p > 1)
                nRes[i] = s[i].substring(0, p);
            else
                nRes[i] = s[i];
        }
        return nRes;
    }

    @Transactional
    public DataDictionary getDataDictionaryPiece(DataDictionaryId id) {
        return dictionaryDao.getObjectById(id);
    }

    @Transactional
    public List<DataCatalog> listFixDataCatalog() {
        return baseDao.listFixCatalog();
    }

    @Transactional
    public List<DataCatalog> listSysDataCatalog() {
        return baseDao.listSysCatalog();
    }

    @Transactional
    public List<DataCatalog> listUserDataCatalog() {
        return baseDao.listUserCatalog();
    }

    @Transactional
    public List<DataCatalog> listAllDataCatalog(){
        return baseDao.listObjects();
    }
    
    @Transactional
    public List<DataDictionary> listDataCatalogs(Map<String, Object> filterDescMap) {
        return dictionaryDao.listObjects(filterDescMap);
    }

    @Transactional
    public List<DataDictionary> getWholeDictionary(){
        return dictionaryDao.getWholeDictionary();
    }
        
}
