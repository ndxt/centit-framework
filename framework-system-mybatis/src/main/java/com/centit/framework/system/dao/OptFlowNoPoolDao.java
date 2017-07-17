package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptFlowNoPool;
import com.centit.framework.system.po.OptFlowNoPoolId;

@Repository
public interface OptFlowNoPoolDao {
	public OptFlowNoPool getObjectById(OptFlowNoPoolId cid);
	
	public void deleteObject(OptFlowNoPool optFlowNoPool);
	
	public void deleteObjectById(OptFlowNoPoolId cid);
	
	public void saveObject(OptFlowNoPool optMethod);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<OptFlowNoPool>  pageQuery(Map<String, Object> pageQureyMap);
	
	
    /**
     *  "select min(CurNo) as MinNo from F_OptFlowNoPool" +
                " where OwnerCode = " + QueryUtils.buildStringForQuery(ownerCode) +
                " and CodeCode = " + QueryUtils.buildStringForQuery(ownerCode) +
                " and CodeDate = to_date(" + QueryUtils.buildStringForQuery(
                DatetimeOpt.convertDatetimeToString(codeBaseDate))
                + ",'YYYY-MM-DD HH:MI:SS')");
     * @param ownerCode
     * @param codeCode
     * @param codeBaseDate
     * @return
     */
    public long fetchFirstLsh(Map map);
}
