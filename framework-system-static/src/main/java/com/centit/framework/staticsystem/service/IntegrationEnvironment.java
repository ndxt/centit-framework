package com.centit.framework.staticsystem.service;

import java.util.List;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.staticsystem.po.DatabaseInfo;
import com.centit.framework.staticsystem.po.OsInfo;

public interface IntegrationEnvironment {

	/**
	 * 刷新集成环境相关信息
	 * 包括：业务系统、数据库信息
	 * @return
	 */
	boolean reloadIPEnvironmen();
	
	/**
	 * 获取框架中注册的业务系统
	 * @param osId
	 * @return
	 */
	OsInfo getOsInfo(String osId);
	/**
	 * 获取框架中注册的数据库
	 * @param databaseCode
	 * @return
	 */
	DatabaseInfo getDatabaseInfo(String databaseCode);
	/**
	 * 获取所有注册的业务系统
	 * @return
	 */
	List<OsInfo> listOsInfos();
	/**
	 * 获取所有注册的数据库
	 * @return
	 */
	List<DatabaseInfo> listDatabaseInfo();
	/**
	 * 检验用户的 访问 令牌合法性
	 * @param tokenId	
	 * @param accessKey
	 * @return 合法返回对应的用户，不合法返回null
	 */
	String checkAccessToken(String tokenId,String accessKey);
}
