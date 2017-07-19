package com.centit.framework.system.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptMethod;

@Repository
public interface OptMethodDao{

	 List<OptMethod> listObjects();
	
	 OptMethod getObjectById(String optCode);	
	
	 void mergeObject(OptMethod optMethod);
	
	 void deleteObject(OptMethod optMethod);
	
	 void deleteObjectById(String optCode);
			
	 void saveNewObject(OptMethod optMethod);
		
	
    //return listObjectsAll("FROM OptMethod WHERE optId =?", sOptID);
     List<OptMethod> listOptMethodByOptID(String sOptID);

    /**
     *  listObjectsAll("FROM OptMethod WHERE optCode in "
        		+ "(select id.optCode from RolePower where id.roleCode = ?)"
        		+ " order by optId", roleCode);
     * @param roleCode roleCode
     * @return List OptMethod
     */
     List<OptMethod> listOptMethodByRoleCode(String roleCode);
    
    // "SELECT count(optcode) FROM OptMethod WHERE optId = ?", sOptID)));
     int getOptMethodSumByOptID(String sOptID);

    //DatabaseOptUtils.doExecuteHql(this, "DELETE FROM OptMethod WHERE optId = ?", sOptID);
     void deleteOptMethodsByOptID(String sOptID);  
   
    //return DatabaseOptUtils.getNextValueOfSequence(this, "S_OPTDEFCODE");
     String getNextOptCode();

}
