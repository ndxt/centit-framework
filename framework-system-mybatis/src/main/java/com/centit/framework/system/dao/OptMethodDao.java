package com.centit.framework.system.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptMethod;

@Repository
public interface OptMethodDao{

	public List<OptMethod> listObjects();
	
	public OptMethod getObjectById(String optCode);	
	
	public void mergeObject(OptMethod optMethod);
	
	public void deleteObject(OptMethod optMethod);
	
	public void deleteObjectById(String optCode);
			
	public void saveNewObject(OptMethod optMethod);
		
	
    //return listObjects("FROM OptMethod WHERE optId =?", sOptID);
    public List<OptMethod> listOptMethodByOptID(String sOptID);

    /**
     *  listObjects("FROM OptMethod WHERE optCode in "
        		+ "(select id.optCode from RolePower where id.roleCode = ?)"
        		+ " order by optId", roleCode);
     * @param roleCode
     * @return
     */
    public List<OptMethod> listOptMethodByRoleCode(String roleCode);
    
    // "SELECT count(optcode) FROM OptMethod WHERE optId = ?", sOptID)));
    public int getOptMethodSumByOptID(String sOptID);

    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM OptMethod WHERE optId = ?", sOptID);
    public void deleteOptMethodsByOptID(String sOptID);  
   
    //return DatabaseOptUtils.getNextValueOfSequence(this, "S_OPTDEFCODE");
    public String getNextOptCode();

}
