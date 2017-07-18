package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.DataDictionaryId;

@Repository
public interface DataDictionaryDao {

	public List<DataDictionary> listObjects(Map<String, Object> filterDescMap);
	
	public DataDictionary getObjectById(DataDictionaryId dd);
	
	public void saveNewObject(DataDictionary dataDictionary);	
	
	public void deleteObject(DataDictionary dataDictionary);
	
	public void deleteObjectById(DataDictionaryId dd);
	
	public void mergeObject(DataDictionary dataDictionary);
	//listObjectsAll("FROM DataDictionary ORDER BY id.catalogCode, dataOrder");
    public List<DataDictionary> getWholeDictionary();
    
    //listObjectsAll("FROM DataDictionary WHERE id.catalogCode = ? ORDER BY dataOrder", catalogCode);
    public List<DataDictionary> listDataDictionary(String catalogCode);
    
    //用序列生成
    public String getNextPrimarykey();

    //批量删除 DatabaseOptUtils.doExecuteHql(this, "delete from DataDictionary where id.catalogCode =?", catalog);
    public void deleteDictionary(String catalog);

}
