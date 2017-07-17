package com.centit.framework.system.service;

import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptMethod;

public interface OptMethodManager extends BaseEntityManager<OptMethod, String> {

	public List<OptMethod> listOptMethodByOptID(String sOptID);

    String getNextOptCode();

}
