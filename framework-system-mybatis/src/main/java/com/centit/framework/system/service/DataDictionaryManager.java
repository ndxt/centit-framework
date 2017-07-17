package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.DataDictionaryId;

public interface DataDictionaryManager {
	
	public DataCatalog getObjectById(String catalogCode);
	
	public void mergeObject(DataCatalog dataCatalog);
	
    public List<DataCatalog> listSysDataCatalog();

    public List<DataCatalog> listUserDataCatalog();

    public List<DataCatalog> listFixDataCatalog();
    
    public List<DataCatalog> listAllDataCatalog();
    
    public List<DataDictionary> listDataDictionarys(Map<String, Object> filterDescMap);
    
    public List<DataCatalog> listObjects(Map<String, Object> filterDescMap,PageDesc pageDesc);
    
    public DataCatalog getCatalogIncludeDataPiece(String catalogCode);
    
    public void saveCatalogIncludeDataPiece(DataCatalog dataCatalog,boolean isAdmin);
    
    public void deleteDataDictionary(String catalogCode);

    public void deleteDataDictionaryPiece(DataDictionaryId id);

    public void saveDataDictionaryPiece(DataDictionary dd);

    public DataDictionary getDataDictionaryPiece(DataDictionaryId id);

    public String[] getFieldsDesc(String sDesc, String sType);

    public List<DataDictionary> getDataDictionary(String catalogCode); 
    
    public List<DataDictionary> getWholeDictionary(); 
}
