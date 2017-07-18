package com.centit.framework.system.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.hibernate.dao.DataPowerFilter;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.dao.OptDataScopeDao;
import com.centit.framework.system.dao.OptInfoDao;
import com.centit.framework.system.dao.UserQueryFilterDao;
import com.centit.framework.system.po.UserQueryFilter;
import com.centit.framework.system.service.GeneralService;
import com.centit.support.algorithm.StringBaseOpt;


public abstract class GeneralServiceImpl implements GeneralService {

	@Resource(name = "optInfoDao")
	@NotNull
	protected OptInfoDao optInfoDao;

	@Resource(name = "optDataScopeDao")
	@NotNull
	protected OptDataScopeDao dataScopeDao;

	@Resource(name = "userQueryFilterDao")
	@NotNull
	protected UserQueryFilterDao userQueryFilterDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserQueryFilter getUserDefaultFilter(String userCode,
			String modelCode) {
		if (StringBaseOpt.isNvl(userCode) || StringBaseOpt.isNvl(modelCode))
			return null;

		return userQueryFilterDao.getUserDefaultFilterByModle(userCode,
				modelCode);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserQueryFilter getUserQueryFilter(Long filterNo) {
		return userQueryFilterDao.getObjectById(filterNo);
	}

	/**
	 * 获取用户数据权限过滤器
	 * 
	 * @param sUserCode sUserCode
	 * @param sOptid 业务名称
	 * @param sOptMethod 对应的方法名称
	 * @return 过滤条件列表，null或者空位不过来
	 */
	@Override
	@Transactional
	public List<String> listUserDataFiltersByOptIDAndMethod(String sUserCode,
			String sOptid, String sOptMethod) {

		List<String> dataScopes = optInfoDao.listUserDataPowerByOptMethod(
				sUserCode, sOptid, sOptMethod);
		if (dataScopes == null || dataScopes.size() == 0)
			return null;

		Set<String> scopeCodes = new HashSet<String>();
		for (String scopes : dataScopes) {
			if (scopes == null || "null".equalsIgnoreCase(scopes)
					|| "all".equalsIgnoreCase(scopes))
				return null;
			String[] codes = scopes.split(",");
			for (String code : codes) {
				if (code != null && !"".equals(code.trim()))
					scopeCodes.add(code.trim());
			}
		}
		if (scopeCodes.size() == 0)
			return null;
		return dataScopeDao.listDataFiltersByIds(scopeCodes);
	}
	/**
	 * 创建用户数据范围过滤器
	 *
	 * @param userDetails CentitUserDetails
	 * @return  DataPowerFilter
	 */
	@Override
	@Transactional
	public DataPowerFilter createUserDataPowerFilter(
			CentitUserDetails userDetails) {
		DataPowerFilter dpf = new DataPowerFilter();
		//当前用户信息
		dpf.addSourceData("currentUser", userDetails);
		//当前用户主机构信息
		dpf.addSourceData("primaryUnit", CodeRepositoryUtil
				.getUnitInfoByCode(userDetails.getPrimaryUnit()));
		//当前用户所有机构关联关系信息
		dpf.addSourceData("userUnits",
				CodeRepositoryUtil.getUserUnits(userDetails.getUserCode()));
		//当前用户的角色信息
		dpf.addSourceData("userRoles", userDetails.getUserRoleCodes());

		return dpf;
	}
}
