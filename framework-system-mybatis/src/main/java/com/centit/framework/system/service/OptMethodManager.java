package com.centit.framework.system.service;

import java.util.List;

import com.centit.framework.system.po.OptMethod;

public interface OptMethodManager{

	public List<OptMethod> listOptMethodByOptID(String sOptID);

    String getNextOptCode();
    
    public List<OptMethod> listObjects();
	
	public OptMethod getObjectById(String optCode);
	
	public void mergeObject(OptMethod optMethod);
	
	public void deleteObjectById(String optCode);
			
	public String saveNewObject(OptMethod optMethod);

}
