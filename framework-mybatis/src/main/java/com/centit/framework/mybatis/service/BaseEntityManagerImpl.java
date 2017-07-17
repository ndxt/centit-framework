package com.centit.framework.mybatis.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.support.algorithm.ReflectionOpt;

/**
 * 数据库的基本操作工具类
 * <p/>
 * 基本上是对Dao进行再一次简单的封装 注解Manager，添加默认事务
 *
 * @author codefan
 * @create 2012-2-16
 */
public abstract class BaseEntityManagerImpl<T extends Serializable, 
 		PK extends Serializable> implements
        BaseEntityManager<T, PK> {
	
    protected Logger logger = LoggerFactory.getLogger(BaseEntityManagerImpl.class);
    /**
     * 日志系统是否开启debug模式
     */
    protected boolean logDebug = logger.isDebugEnabled();
    
    public void copyObjectNotNullProperty(T des, T sou) {
        ReflectionOpt.invokeBinaryOpt(des, "copyNotNullProperty", sou);
    }

    public void clearObjectProperties(T des) {
        ReflectionOpt.invokeNoParamFunc(des, "clearProperties");

    }

    public void copyObject(T des, T sou) {
        ReflectionOpt.invokeBinaryOpt(des, "copy", sou);
    }
   
}
