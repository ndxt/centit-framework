package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptFlowNoPool;
import com.centit.framework.system.po.OptFlowNoPoolId;

@Repository
public interface OptFlowNoPoolDao {
	 OptFlowNoPool getObjectById(OptFlowNoPoolId cid);
	
	 void deleteObject(OptFlowNoPool optFlowNoPool);
	
	 void deleteObjectById(OptFlowNoPoolId cid);
	
	 void saveObject(OptFlowNoPool optMethod);
	
	
     int  pageCount(Map<String, Object> filterDescMap);
     List<OptFlowNoPool>  pageQuery(Map<String, Object> pageQureyMap);
	
	
    /**
     *  "select min(CurNo) as MinNo from F_OptFlowNoPool" +
                " where OwnerCode = " + QueryUtils.buildStringForQuery(ownerCode) +
                " and CodeCode = " + QueryUtils.buildStringForQuery(ownerCode) +
                " and CodeDate = to_date(" + QueryUtils.buildStringForQuery(
                DatetimeOpt.convertDatetimeToString(codeBaseDate))
                + ",'YYYY-MM-DD HH:MI:SS')");
     * @param map Map
     * @return long
     */
     long fetchFirstLsh(Map map);
}
