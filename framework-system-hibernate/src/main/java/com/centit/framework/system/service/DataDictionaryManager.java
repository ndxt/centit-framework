package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.DataDictionaryId;

public interface DataDictionaryManager extends BaseEntityManager<DataCatalog, String> {
    public List<DataCatalog> listSysDataCatalog();

    public List<DataCatalog> listUserDataCatalog();

    public List<DataCatalog> listFixDataCatalog();
    
    public List<DataCatalog> listAllDataCatalog();
   
    public DataCatalog getCatalogIncludeDataPiece(String catalogCode);
    
    public void saveCatalogIncludeDataPiece(DataCatalog dataCatalog,boolean isAdmin);
    
    public void deleteDataDictionary(String catalogCode);

    public void deleteDataDictionaryPiece(DataDictionaryId id);

    public void saveDataDictionaryPiece(DataDictionary dd);

    public List<DataDictionary> listDataCatalogs(Map<String, Object> filterDescMap);

    public DataDictionary getDataDictionaryPiece(DataDictionaryId id);

    public String[] getFieldsDesc(String sDesc, String sType);

    public List<DataDictionary> getDataDictionary(String catalogCode); 
    
    public List<DataDictionary> getWholeDictionary(); 
}
