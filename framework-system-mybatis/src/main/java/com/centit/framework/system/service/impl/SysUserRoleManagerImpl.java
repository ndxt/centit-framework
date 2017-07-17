package com.centit.framework.system.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.mybatis.dao.DatabaseOptUtils;
import com.centit.framework.system.dao.UserRoleDao;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.system.po.UserRoleId;
import com.centit.framework.system.service.SysUserRoleManager;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-10-28
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class SysUserRoleManagerImpl implements SysUserRoleManager {
 
    @Resource
    @NotNull
    protected UserRoleDao userRoleDao;

    @Override
    public void mergeObject(UserRole dbUserRole, UserRole userRole) {
        userRoleDao.deleteObject(dbUserRole);

        userRoleDao.mergeObject(userRole);
    }

	@Override
	public List<UserRole> listObjects(Map<String, Object> filterMap, PageDesc pageDesc) {
		return userRoleDao.pageQuery(DatabaseOptUtils.prepPageParmers(filterMap,pageDesc,userRoleDao.pageCount(filterMap)));
	}

	@Override
	public UserRole getObjectById(UserRoleId id) {
		return userRoleDao.getObjectById(id);
	}

	@Override
	public void mergeObject(UserRole dbUserRole) {
		userRoleDao.mergeObject(dbUserRole);
	}

	@Override
	public void deleteObjectById(UserRoleId id) {
		userRoleDao.deleteObjectById(id);
	}
}
