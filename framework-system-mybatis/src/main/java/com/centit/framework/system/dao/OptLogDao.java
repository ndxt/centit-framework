package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptLog;

@Repository
public interface OptLogDao{
	//生成一个新的id序列	 S_SYS_LOG
	public Long createNewLogId();
	
	public OptLog getObjectById(Long logId);
	
	public Long saveNewObject(OptLog o);
	
	public void deleteObjectById(Long logId);

	//final String hql = "select DISTINCT f.optId from OptLog f";
    public List<String> listOptIds();

    //设置主键 DatabaseOptUtils.getNextLongSequence(this, "S_SYS_LOG"));
    public OptLog mergeObject(OptLog o);

    //"delete from OptLog o where 1=1 ";  "and o.optTime > ?" "and o.optTime < ?";
    //参数 String beginDate, String endDate
    public void delete(Map map );
    
    
    //分页  //startRow  startRow
    public int  pageCount(Map<String, Object> filterDescMap);
    
    public List<OptLog>  pageQuery(Map<String, Object> pageQureyMap);

}
