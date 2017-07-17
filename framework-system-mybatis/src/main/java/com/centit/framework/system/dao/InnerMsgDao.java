package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.InnerMsg;
@Repository
public interface InnerMsgDao{
    
	public InnerMsg getObjectById(String msgCode);
	
	public void mergeObject(InnerMsg innerMsg);
	
	public void deleteObject(InnerMsg innerMsg);
			
	public void saveObject(InnerMsg innerMsg);
    /** 新建
     * 
     */
    // String msgCode = DatabaseOptUtils.getNextKeyBySequence(this, "S_MSGCODE",16);
    public String saveNewObject(InnerMsg o);
    
	public List<InnerMsg> listObjects(Map<String, Object> filterMap);
	
	
    //分页  //startRow  startRow
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<InnerMsg>  pageQuery(Map<String, Object> pageQureyMap);
    
}
