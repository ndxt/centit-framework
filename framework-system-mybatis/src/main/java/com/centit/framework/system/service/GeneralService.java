package com.centit.framework.system.service;

import java.util.List;

import com.centit.framework.mybatis.dao.DataPowerFilter;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.UserQueryFilter;

/**
 * 将项目Service层需要用到的通用服务放在这个，供其他业务服务调用。
 * 使用方法：用下面的注解
 * 			(name = "generalService")
 *			protected GeneralService  generalService;
 * @author codefan
 *
 */
public interface GeneralService {

	/**
	 * 获取用户某个模块的查询过滤器类别，这个类别不需要开发人员维护，框架统一维护。
	 * @param userCode userCode
	 * @param modelCode modelCode
	 * @return 用户某个模块的查询过滤器类别
	 */
	 UserQueryFilter getUserDefaultFilter(String userCode,String modelCode);
	/**
	 * 获取用户某个模块默认查询过滤器
	 * @param filterNo filterNo
	 * @return 用户某个模块默认查询过滤器
	 */
	 UserQueryFilter getUserQueryFilter(Long filterNo);
	/**
	 * 获得用户摸个功能方法的数据范围权限，返回null或者size==0表示拥有所有权限
	 * @param sUserCode sUserCode
	 * @param sOptid sOptid
	 * @param sOptMethod sOptMethod
	 * @return 用户摸个功能方法的数据范围权限
	 */
	 List<String> listUserDataFiltersByOptIDAndMethod
		(String sUserCode, String sOptid, String sOptMethod);
	/**
	 * 创建用户数据范围过滤器，和上面的方法结合使用
	 * @param userDetails CentitUserDetails
	 * @return DataPowerFilter
	 */
	 DataPowerFilter createUserDataPowerFilter(CentitUserDetails userDetails);
}
