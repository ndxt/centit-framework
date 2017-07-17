package com.centit.framework.system.service;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.InnerMsg;

public interface InnerMsgManager extends BaseEntityManager<InnerMsg, String>{
    void mergeMInnerMsg(InnerMsg msgCopy, InnerMsg msg);
    void deleteMsgById(String msgCode);
}
