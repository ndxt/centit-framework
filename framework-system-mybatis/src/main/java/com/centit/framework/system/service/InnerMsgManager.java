package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.InnerMsg;

public interface InnerMsgManager{
	public List<InnerMsg> listObjects(Map<String, Object> filterMap);

	public List<InnerMsg> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);
	
	public InnerMsg getObjectById(String msgCode);
	
    void mergeMInnerMsg(InnerMsg msgCopy, InnerMsg msg);
    void deleteMsgById(String msgCode);
}
