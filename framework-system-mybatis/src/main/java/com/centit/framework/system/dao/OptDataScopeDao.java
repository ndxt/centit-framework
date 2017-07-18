package com.centit.framework.system.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptDataScope;

@Repository
public interface OptDataScopeDao {

	public void mergeObject(OptDataScope optDataScope);
	
	public void deleteObject(OptDataScope optDataScope);
	
    //return listObjectsAll("FROM OptDataScope WHERE optId =?", sOptID);
    public List<OptDataScope> getDataScopeByOptID(String sOptID) ;

    //"SELECT count(optScopeCode) FROM OptDataScope WHERE optId = ?", sOptID)));
    public int getOptDataScopeSumByOptID(String sOptID);


    //"DELETE FROM OptDataScope WHERE optId = ?", sOptID);
    public void deleteDataScopeOfOptID(String sOptID) ;

  
    //return DatabaseOptUtils.getNextValueOfSequence(this, "S_OPTDEFCODE");
    public String getNextOptCode();

    
   	// listObjectsAll("FROM OptDataScope WHERE optId in ?", scopeCodes)
    public List<String> listDataFiltersByIds(Map  map);
}
