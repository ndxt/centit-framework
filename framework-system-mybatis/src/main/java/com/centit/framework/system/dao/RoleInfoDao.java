package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.VOptTree;

@Repository
public interface RoleInfoDao{
	
	public List<RoleInfo> listObjects(Map<String, Object> filterMap);
	
	
    public int  pageCount(Map<String, Object> filterDescMap);
    public List<RoleInfo>  pageQuery(Map<String, Object> pageQureyMap);

	public List<RoleInfo> listObjectsAll();
	
	public void saveNewObject(RoleInfo o);

	public void deleteObjectById(String roleCode);
	
	public void mergeObject(RoleInfo o);
	
	
	public RoleInfo getObjectById(String roleCode);
	
    //DatabaseOptUtils.findObjectsByHql(this,"FROM VOptTree");
    public List<VOptTree> getVOptTreeList();
    
    /**
     *         String hql = "select new map(def.optName as def_optname, def.optCode as def_optcode) "
                + "from OptMethod def, RolePower pow where def.optCode = pow.id.optCode and pow.id.roleCode = ?";

     * @param rolecode
     * @return
     */
    public List<Object> listRoleOptMethods(String rolecode);

    /**
     * select count(1) from f_userrole where rolecode=?
     * @param roleCode
     * @return
     */
    public int countRoleUserSum(String roleCode);
    
}
