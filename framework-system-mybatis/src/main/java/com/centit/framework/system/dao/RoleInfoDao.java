package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.VOptTree;

@Repository
public interface RoleInfoDao{
	
	 List<RoleInfo> listObjects(Map<String, Object> filterMap);
	
	
     int  pageCount(Map<String, Object> filterDescMap);
     List<RoleInfo>  pageQuery(Map<String, Object> pageQureyMap);

	 List<RoleInfo> listObjectsAll();
	
	 void saveNewObject(RoleInfo o);

	 void deleteObjectById(String roleCode);
	
	 void mergeObject(RoleInfo o);
	
	
	 RoleInfo getObjectById(String roleCode);
	
    //DatabaseOptUtils.findObjectsByHql(this,"FROM VOptTree");
     List<VOptTree> getVOptTreeList();
    
    /**
     *         String hql = "select new map(def.optName as def_optname, def.optCode as def_optcode) "
                + "from OptMethod def, RolePower pow where def.optCode = pow.id.optCode and pow.id.roleCode = ?";

     * @param rolecode rolecode
     * @return List
     */
     List<Object> listRoleOptMethods(String rolecode);

    /**
     * select count(1) from f_userrole where rolecode=?
     * @param roleCode roleCode
     * @return int
     */
     int countRoleUserSum(String roleCode);
    
}
