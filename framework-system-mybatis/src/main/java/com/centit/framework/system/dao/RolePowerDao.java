package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.RolePower;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-29
 * Time: 下午3:18
 * To change this template use File | Settings | File Templates.
 */

@Repository
public interface RolePowerDao{

	public List<RolePower> listObjects();	
	
	public void mergeObject(RolePower rolePowers);
	
	public void deleteObject(RolePower rolePowers);
	
	public List<RolePower> listObjects(Map<String, Object> filterMap);
	
	//"DELETE FROM RolePower rp where rp.id.roleCode=?", rolecode
    public void deleteRolePowersByRoleCode(String rolecode);
    
    //"DELETE FROM RolePower rp where rp.id.optCode=?", optecode
    public void deleteRolePowersByOptCode(String optecode);
    
    //"FROM RolePower rp where rp.id.roleCode=?", rolecode
    public List<RolePower> listRolePowersByRoleCode(String roleCode);
    
}
