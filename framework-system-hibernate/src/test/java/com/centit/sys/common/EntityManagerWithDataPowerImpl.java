package com.centit.sys.common;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.HibernatePowerFilter;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.system.service.OptInfoManager;
import com.centit.support.database.QueryAndNamedParams;

/**
 * 在基础的manager基础之上添加了数据权限控制的方法，
 * 数据权限控制主要的参数有两个，
 * 		一、用户，即当前session用户的userCode
 * 		二、业务操作ID(optCode,由于optCode是没有意义的，所以需要用 optId + method来获取optCode)
 *
 * @author codefan@sina.com
 * @create 2012-2-16
 */
@Service
public abstract class EntityManagerWithDataPowerImpl<T extends Serializable, PK extends Serializable, D extends BaseDaoImpl<T, PK>>
  extends  BaseEntityManagerImpl<T , PK , D>
  implements EntityManagerWithDataPower<T, PK> {

	public HibernatePowerFilter createDataPowerFilter(String userCode){
		HibernatePowerFilter dpf = new HibernatePowerFilter();
		IUserInfo currUser = CodeRepositoryUtil.getUserInfoByCode(userCode);
		dpf.addSourceData( currUser );
		dpf.addSourceData("PrimaryUnit",CodeRepositoryUtil.getUnitInfoByCode(currUser.getPrimaryUnit()) );
		dpf.addSourceData("UserUnits", CodeRepositoryUtil.getUserUnits(userCode));
		return dpf;
	}
	
	
	@Resource
	private OptInfoManager optInfoManager;
	public List<T> listObjecesDemo(String userCode,String optid,String method){
		List<String> filters = optInfoManager.listUserDataFiltersByOptIDAndMethod(userCode, optid, method);
		HibernatePowerFilter dpf = createDataPowerFilter(userCode);
		//dpf.setSourceDatas(sourceData);
		QueryAndNamedParams hql = dpf.makeHQL("T className" , filters, false);
		return baseDao.listObjects(hql.getHql(),hql.getParams());
	}
}
