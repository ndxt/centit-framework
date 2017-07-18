package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.FVUserOptMoudleList;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.OptMethodUrlMap;

@Repository
public interface OptInfoDao{
	
	 List<OptInfo> listObjects(Map<String, Object> filterMap);
	
	 List<OptInfo> listObjectsByRoleCode(String roleCode);
	
	 List<OptInfo> listObjectsByCon(@Param(value="condition")String condition);
	
	 List<OptInfo> listObjectsAll();
	
	
	
	 void deleteObject(OptInfo optMethod);
	
	 void mergeObject(OptInfo optMethod);
	
	 void deleteObjectById(String optId);
			
	 void saveNewObject(OptInfo optMethod);
	
	//"select count(1) as hasChildren from OptInfo where preOptId = ?",optId
	 int countChildrenSum(String optId);
	
	
	 OptInfo getObjectById(String optId);
	
	//"from OptInfo opt where opt.isInToolbar = 'T'";
     List<OptInfo> listValidObjects();

    // String hql = "FROM FVUserOptMoudleList where userCode=?";
     List<OptInfo> getFunctionsByUserID(String userID);

 

    //"FROM OptInfo where optUrl='...' order by orderInd ";
    //"FROM FVUserOptMoudleList where isintoolbar='Y' and userCode=? and optType = " +
    //(isAdmin ? "'S'" : "'O'") + " ORDER BY orderind";
    //return getMenuFuncs(preOpts, ls);
    // List<OptInfo> getMenuFuncByUserID(String userID, boolean isAdmin);
    
     List<OptInfo> getMenuFuncByOptUrl();
    
     List<FVUserOptMoudleList> getMenuFuncByUserID(Map map);

    /**
     * "select optScopeCodes " +
                 "from F_V_UserOptDataScopes " +
                 "where UserCode = ? and OPTID = ? and OPTMETHOD = ?";
     * @param map map
     * 参数 String userCode,String optid,String optMethod
     * @return     List
     */
     List<String> listUserDataPowerByOptMethod(Map map);

    //"FROM FVUserOptMoudleList  where userCode=? and topoptid=?" + " ORDER BY preoptid, orderind";
    //参数  String userID, String superFunctionId
     List<OptInfo> getFunctionsByUserAndSuperFunctionId(Map map);
    
    // String hql = "FROM FVUserOptList urv where urv.id.userCode=? and optid= ?";
    //参数String userCode, String optid
     List<OptMethod> getMethodByUserAndOptid(Map map);
    
    // DatabaseOptUtils.findObjectsByHql(this, "from OptMethodUrlMap");
     List<OptMethodUrlMap> listAllOptMethodUrlMap();
 
}
