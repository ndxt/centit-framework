package com.centit.framework.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
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
public class SysUserRoleManagerImpl extends BaseEntityManagerImpl<UserRole, UserRoleId, UserRoleDao> 
    implements SysUserRoleManager {
 
    @Resource(name = "userRoleDao")
    @NotNull
    @Override
    protected void setBaseDao(UserRoleDao baseDao) {
        this.baseDao=baseDao;
    }
    @Override
    public void mergeObject(UserRole dbUserRole, UserRole userRole) {
        baseDao.deleteObject(dbUserRole);

        baseDao.mergeObject(userRole);
    }
	@Override
	@SuppressWarnings("unchecked")
	public List<UserRole> listObjectBysql(Map<String, Object> filterMap,
			PageDesc pageDesc) {
		String sql = "select f.* from F_USERROLE  f left join F_USERINFO  t on f.USERCODE=t.USERCODE where 1=1 ";
		String userName = (String) filterMap.get("userName");
		String roleCode = (String) filterMap.get("roleCode");
		List<String> values = new ArrayList<String>();
		if(StringUtils.isNotBlank(userName)){
			sql+=" and t.USERNAME like ?";
			values.add("%"+userName+"%");
		}
		if(StringUtils.isNotBlank(roleCode)){
			sql+=" and f.ROLECODE = ?";
			values.add(roleCode);
		}
		
		List<UserRole> objs = (List<UserRole>)
				DatabaseOptUtils.findObjectsBySql(baseDao, sql, values.toArray(), pageDesc,UserRole.class);
		return objs;
	}
}
