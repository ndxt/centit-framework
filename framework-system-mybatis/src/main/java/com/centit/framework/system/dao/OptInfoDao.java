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
	
	public List<OptInfo> listObjects(Map<String, Object> filterMap);
	
	public List<OptInfo> listObjectsByRoleCode(String roleCode);
	
	public List<OptInfo> listObjectsByCon(@Param(value="condition")String condition);
	
	public List<OptInfo> listObjectsAll();
	
	
	
	public void deleteObject(OptInfo optMethod);
	
	public void mergeObject(OptInfo optMethod);
	
	public void deleteObjectById(String optId);
			
	public void saveNewObject(OptInfo optMethod);
	
	//"select count(1) as hasChildren from OptInfo where preOptId = ?",optId
	public int countChildrenSum(String optId);
	
	
	public OptInfo getObjectById(String optId);
	
	//"from OptInfo opt where opt.isInToolbar = 'T'";
    public List<OptInfo> listValidObjects();

    // String hql = "FROM FVUserOptMoudleList where userCode=?";
    public List<OptInfo> getFunctionsByUserID(String userID);

 

    //"FROM OptInfo where optUrl='...' order by orderInd ";
    //"FROM FVUserOptMoudleList where isintoolbar='Y' and userCode=? and optType = " +
    //(isAdmin ? "'S'" : "'O'") + " ORDER BY orderind";
    //return getMenuFuncs(preOpts, ls);
    //public List<OptInfo> getMenuFuncByUserID(String userID, boolean isAdmin);
    
    public List<OptInfo> getMenuFuncByOptUrl();
    
    public List<FVUserOptMoudleList> getMenuFuncByUserID(Map map);

    /**
     * "select optScopeCodes " +
                 "from F_V_UserOptDataScopes " +
                 "where UserCode = ? and OPTID = ? and OPTMETHOD = ?";
     * @param userCode
     * @param optid
     * @param optMethod
     * 参数 String userCode,String optid,String optMethod
     */
    public List<String> listUserDataPowerByOptMethod(Map map);

    //"FROM FVUserOptMoudleList  where userCode=? and topoptid=?" + " ORDER BY preoptid, orderind";
    //参数  String userID, String superFunctionId
    public List<OptInfo> getFunctionsByUserAndSuperFunctionId(Map map);
    
    // String hql = "FROM FVUserOptList urv where urv.id.userCode=? and optid= ?";
    //参数String userCode, String optid
    public List<OptMethod> getMethodByUserAndOptid(Map map);
    
    // DatabaseOptUtils.findObjectsByHql(this, "from OptMethodUrlMap");
    public List<OptMethodUrlMap> listAllOptMethodUrlMap();
 
}
