package com.centit.framework.mybatis.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.SysParametersUtils;
import com.centit.support.database.jsonmaptable.DB2JsonObjectDao;
import com.centit.support.database.jsonmaptable.JsonObjectDao;
import com.centit.support.database.jsonmaptable.MySqlJsonObjectDao;
import com.centit.support.database.jsonmaptable.OracleJsonObjectDao;
import com.centit.support.database.jsonmaptable.SqlSvrJsonObjectDao;
import com.centit.support.database.metadata.TableInfo;

/**
 * @author codefan
 *
 */
public class JsonObjectWork implements JsonObjectDao {

	private TableInfo tableInfo;
	private SqlSession sqlSession;

	public JsonObjectWork(){
		
	}
	
	public JsonObjectWork(TableInfo tableInfo){
		this.tableInfo = tableInfo;
	}
	
	public JsonObjectWork(SqlSession sqlSession,TableInfo tableInfo){
		this.tableInfo = tableInfo;
		this.sqlSession = sqlSession;
	}
	
	public void setBaseDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	public JsonObjectDao createDao(Connection connection){
		String dialectName = SysParametersUtils.getStringValue("jdbc.dialect");
		if (dialectName.indexOf("Oracle")>=0)
			return new OracleJsonObjectDao(connection ,tableInfo);
		if (dialectName.indexOf("DB2")>=0)
			return new DB2JsonObjectDao(connection ,tableInfo);
		if (dialectName.indexOf("SQLServer")>=0)
			return new SqlSvrJsonObjectDao(connection ,tableInfo);
		if (dialectName.indexOf("MySQL")>=0)
			return new MySqlJsonObjectDao(connection ,tableInfo);
		return new OracleJsonObjectDao(connection ,tableInfo);
	}
	
	private JsonObjectDao currentDao = null;
	public JsonObjectDao getCurrentDao(){
		if(currentDao==null)
			currentDao = createDao(this.sqlSession.getConnection());
		return currentDao;
	}
	
	
	
	@Override
	public TableInfo getTableInfo() {
		return tableInfo;
	}

	@Override
	public JSONObject getObjectById(final Object keyValue) throws SQLException, IOException {
		return getCurrentDao().getObjectById(keyValue);
	}

	@Override
	public JSONObject getObjectById(final Map<String, Object> keyValues) throws SQLException, IOException {
		return getCurrentDao().getObjectById(keyValues);
	}

	@Override
	public JSONObject getObjectByProperties(final Map<String, Object> properties) throws SQLException, IOException {
		return getCurrentDao().getObjectByProperties(properties);
	}

	@Override
	public JSONArray listObjectsByProperties(final Map<String, Object> properties) throws SQLException, IOException {
		return getCurrentDao().listObjectsByProperties(properties);
	}

	@Override
	public JSONArray listObjectsByProperties(final Map<String, Object> properties,
			final int startPos,final int maxSize)
			throws SQLException, IOException {
		return getCurrentDao().listObjectsByProperties(properties,startPos,maxSize);
	}

	@Override
	public Long fetchObjectsCount(final Map<String, Object> properties) throws SQLException, IOException {
		return getCurrentDao().fetchObjectsCount(properties);
	}

	@Override
	public int saveNewObject(final Map<String, Object> object) throws SQLException {
		return getCurrentDao().saveNewObject(object);
	}

	/**
	 * 更改部分属性
	 *
	 * @param fields 更改部分属性 属性名 集合，应为有的Map 不允许 值为null，这样这些属性 用map就无法修改为 null
	 * @param object
	 */
	@Override
	public int updateObject(Collection<String> fields, Map<String, Object> object) throws SQLException {
		return getCurrentDao().updateObject(fields,object);
	}

	@Override
	public int updateObject(final Map<String, Object> object) throws SQLException {
		return getCurrentDao().updateObject(object);
	}

	/**
	 * 更改部分属性
	 *
	 * @param fields 更改部分属性 属性名 集合，应为有的Map 不允许 值为null，这样这些属性 用map就无法修改为 null
	 * @param object
	 */
	@Override
	public int mergeObject(Collection<String> fields, Map<String, Object> object)
			throws SQLException, IOException {
		return getCurrentDao().mergeObject(fields,object);
	}

	@Override
	public int mergeObject(final Map<String, Object> object) throws SQLException, IOException {
		return getCurrentDao().mergeObject(object);
	}

	@Override
	public int updateObjectsByProperties(final Map<String, Object> fieldValues,
										 final Map<String, Object> properties)
			throws SQLException {
		return getCurrentDao().updateObjectsByProperties(fieldValues,properties);
	}

	/**
	 * 根据条件批量更新 对象
	 *
	 * @param fields      更改部分属性 属性名 集合，应为有的Map 不允许 值为null，这样这些属性 用map就无法修改为 null
	 * @param fieldValues
	 * @param properties
	 * @throws SQLException
	 */
	@Override
	public int updateObjectsByProperties(Collection<String> fields,
										 Map<String, Object> fieldValues, Map<String, Object> properties)
			throws SQLException {
		return getCurrentDao().updateObjectsByProperties(fields,fieldValues,properties);
	}

	@Override
	public int deleteObjectById(final Object keyValue) throws SQLException {
		return getCurrentDao().deleteObjectById(keyValue);
	}

	@Override
	public int deleteObjectById(final Map<String, Object> keyValues) throws SQLException {
		return getCurrentDao().deleteObjectById(keyValues);
	}

	@Override
	public int deleteObjectsByProperties(final Map<String, Object> properties) throws SQLException {
		return getCurrentDao().deleteObjectsByProperties(properties);
	}

	@Override
	public int insertObjectsAsTabulation(final JSONArray objects) throws SQLException {
		return getCurrentDao().insertObjectsAsTabulation(objects);
	}

	@Override
	public int deleteObjects(final JSONArray objects) throws SQLException {
		return getCurrentDao().deleteObjects(objects);
	}

	@Override
	public int deleteObjectsAsTabulation(final String propertyName, final Object propertyValue) throws SQLException {
		return getCurrentDao().deleteObjectsAsTabulation( propertyName,propertyValue);
	}

	@Override
	public int deleteObjectsAsTabulation(final Map<String, Object> properties) throws SQLException {
		return getCurrentDao().deleteObjectsAsTabulation(properties);
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final JSONArray dbObjects) throws SQLException {
		return getCurrentDao().replaceObjectsAsTabulation(newObjects,dbObjects);
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final String propertyName, final Object propertyValue)
			throws SQLException, IOException {
		return getCurrentDao().replaceObjectsAsTabulation(newObjects,propertyName,propertyValue);
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final Map<String, Object> properties)
			throws SQLException, IOException {
		return getCurrentDao().replaceObjectsAsTabulation(newObjects,properties);
	}

	@Override
	public Long getSequenceNextValue(final String sequenceName) throws SQLException, IOException {
		return getCurrentDao().getSequenceNextValue(sequenceName);
	}

	@Override
	public List<Object[]> findObjectsBySql(final String sSql, final Object[] values) throws SQLException, IOException {
		return getCurrentDao().findObjectsBySql(sSql,values);
	}

	@Override
	public List<Object[]> findObjectsBySql(final String sSql, final Object[] values, final int pageNo, final int pageSize)
			throws SQLException, IOException {
		return getCurrentDao().findObjectsBySql(sSql,values,pageNo,pageSize);
	}

	@Override
	public List<Object[]> findObjectsByNamedSql(final String sSql, final Map<String, Object> values)
			throws SQLException, IOException {
		return getCurrentDao().findObjectsByNamedSql(sSql,values);
	}

	@Override
	public List<Object[]> findObjectsByNamedSql(final String sSql, final Map<String, Object> values, 
			final int pageNo, final int pageSize)	
			throws SQLException, IOException {
		return getCurrentDao().findObjectsByNamedSql(sSql,values,pageNo,pageSize);
	}

	@Override
	public JSONArray findObjectsAsJSON(final String sSql, final Object[] values, final String[] fieldnames)
			throws SQLException, IOException {
		return getCurrentDao().findObjectsAsJSON(sSql,values,fieldnames);
	}

	@Override
	public JSONArray findObjectsAsJSON(final String sSql, final Object[] values, final String[] fieldnames, 
			final int pageNo, final int pageSize)
			throws SQLException, IOException {
		return getCurrentDao().findObjectsAsJSON(sSql,values,fieldnames,pageNo,pageSize);
	}

	@Override
	public JSONArray findObjectsByNamedSqlAsJSON(final String sSql, final Map<String, Object> values,
			final String[] fieldnames)
			throws SQLException, IOException {
		return getCurrentDao().findObjectsByNamedSqlAsJSON(sSql,values,fieldnames);
	}

	@Override
	public JSONArray findObjectsByNamedSqlAsJSON(final String sSql, final Map<String, Object> values, 
			final String[] fieldnames,final int pageNo, final int pageSize) throws SQLException, IOException {
		return getCurrentDao().findObjectsByNamedSqlAsJSON(sSql,values,fieldnames,pageNo,pageSize);
	}

	@Override
	public boolean doExecuteSql(final String sSql) throws SQLException {
		return getCurrentDao().doExecuteSql(sSql);
	}

	@Override
	public int doExecuteSql(final String sSql,final Object[] values) throws SQLException {
		return getCurrentDao().doExecuteSql(sSql,values);
	}

	@Override
	public int doExecuteNamedSql(final String sSql, final Map<String, Object> values) throws SQLException {
		return getCurrentDao().doExecuteNamedSql(sSql,values);
	}
}
