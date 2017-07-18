package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.DataCatalog;

@Repository
public interface DataCatalogDao{
	
	public List<DataCatalog> listObjects();
	
	public DataCatalog getObjectById(String catalogCode);
	
	public String saveNewObject(DataCatalog dataCatalog);
	
	public void mergeObject(DataCatalog dataCatalog);
	
	public void deleteObjectById(String catalogCode);
	//listObjectsAll("FROM DataCatalog WHERE catalogStyle='F'");
    public List<DataCatalog> listFixCatalog();

    //listObjectsAll("FROM DataCatalog WHERE catalogStyle='U'");
    public List<DataCatalog> listUserCatalog();

    //listObjectsAll("FROM DataCatalog WHERE catalogStyle='S'");
    public List<DataCatalog> listSysCatalog();
    
    //分页  //startRow  startRow
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<DataCatalog>  pageQuery(Map<String, Object> pageQureyMap);
}
