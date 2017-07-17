package com.centit.sys.common;

import java.io.Serializable;
import java.util.List;

import com.centit.framework.core.service.BaseEntityManager;

/**
 * 数据库的基本操作工具类
 * <p/>
 * 基本上是对Dao进行再一次简单的封装 注解Manager，添加默认事务
 *
 * @author codefan
 * @create 2012-2-16
 */

public interface EntityManagerWithDataPower<T extends Serializable, PK extends Serializable> 
		extends	BaseEntityManager<T, PK> {
	
	public List<T> listObjecesDemo(String method);

}
