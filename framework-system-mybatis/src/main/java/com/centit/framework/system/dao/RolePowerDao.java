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

	List<RolePower> listObjects();
	
	void mergeObject(RolePower rolePowers);
	
	void deleteObject(RolePower rolePowers);
	
	List<RolePower> listObjects(Map<String, Object> filterMap);
	
	//"DELETE FROM RolePower rp where rp.id.roleCode=?", rolecode
    void deleteRolePowersByRoleCode(String rolecode);
    
    //"DELETE FROM RolePower rp where rp.id.optCode=?", optecode
    void deleteRolePowersByOptCode(String optecode);
    
    //"FROM RolePower rp where rp.id.roleCode=?", rolecode
    List<RolePower> listRolePowersByRoleCode(String roleCode);
    
}
