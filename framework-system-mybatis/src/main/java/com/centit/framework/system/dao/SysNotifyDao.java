package com.centit.framework.system.dao;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.SysNotify;

@Repository
public interface SysNotifyDao{
	
	//设置主键 o.setNotifyId(DatabaseOptUtils.getNextLongSequence(this, "S_MSGCODE"));
    public SysNotify mergeObject(SysNotify o);
}
