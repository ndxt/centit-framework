package com.centit.framework.mybatis.dao;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

@SuppressWarnings("unused")
public class BaseDaoSupport extends SqlSessionDaoSupport {

	/**
	 * 直接调用 getSqlSession().getConnection() 有时返回为空；
	 * 
	 * 调用 getSqlSession().getConfiguration().getEnvironment().getDataSource().getConnection()
	 * 返回的是一个新建的Connection这样和当前的SQLSession不在统一事务中（也不在统一个session中）。
	 * 
	 * @return 一个新建的Connection这样和当前的SQLSession不在统一事务中（也不在统一个session中）
	 */
	public SqlSession getSqlSessionWithOpenedConnection() {
		
		SqlSessionTemplate st = (SqlSessionTemplate) getSqlSession();

		return SqlSessionUtils.getSqlSession(
                st.getSqlSessionFactory(), st.getExecutorType(),
                st.getPersistenceExceptionTranslator());//.getConnection();
	}
}
