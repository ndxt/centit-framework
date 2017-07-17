package com.centit.framework.hibernate.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;

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
	private BaseDaoImpl<?, ?> baseDao;

	public JsonObjectWork(){
		
	}
	
	public JsonObjectWork(TableInfo tableInfo){
		this.tableInfo = tableInfo;
	}
	
	public JsonObjectWork(BaseDaoImpl<?, ?> baseDao,TableInfo tableInfo){
		this.tableInfo = tableInfo;
		this.baseDao = baseDao;
	}
	
	public void setBaseDao(BaseDaoImpl<?, ?> baseDao) {
		this.baseDao = baseDao;
	}
	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	private JsonObjectDao createDao(Connection connection){
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
	
	@Override
	public TableInfo getTableInfo() {
		return tableInfo;
	}

	@Override
	public JSONObject getObjectById(final Object keyValue) throws SQLException, IOException {

		return (JSONObject)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONObject>(){
			@Override
			public JSONObject execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.getObjectById(keyValue);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONObject getObjectById(final Map<String, Object> keyValues) throws SQLException, IOException {
		return (JSONObject)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONObject>(){
			@Override
			public JSONObject execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.getObjectById(keyValues);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONObject getObjectByProperties(final Map<String, Object> properties) throws SQLException, IOException {
		return (JSONObject)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONObject>(){
			@Override
			public JSONObject execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.getObjectByProperties(properties);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray listObjectsByProperties(final Map<String, Object> properties) throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.listObjectsByProperties(properties);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray listObjectsByProperties(final Map<String, Object> properties,
			final int startPos,final int maxSize)
			throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.listObjectsByProperties(properties,startPos,maxSize);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public Long fetchObjectsCount(final Map<String, Object> properties) throws SQLException, IOException {
		return (Long)baseDao.getCurrentSession().doReturningWork(new ReturningWork<Long>(){
			@Override
			public Long execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.fetchObjectsCount(properties);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public int saveNewObject(final Map<String, Object> object) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.saveNewObject(object);
			}			
		});		
	}

	/**
	 * 更改部分属性
	 *
	 * @param fields 更改部分属性 属性名 集合，应为有的Map 不允许 值为null，这样这些属性 用map就无法修改为 null
	 * @param object
	 */
	@Override
	public int updateObject(final Collection<String> fields, final Map<String, Object> object) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.updateObject(fields, object);
			}
		});
	}

	@Override
	public int updateObject(final Map<String, Object> object) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.updateObject(object);
			}			
		});		
	}

	/**
	 * 更改部分属性
	 *
	 * @param fields 更改部分属性 属性名 集合，应为有的Map 不允许 值为null，这样这些属性 用map就无法修改为 null
	 * @param object
	 */
	@Override
	public int mergeObject(final Collection<String> fields, final Map<String, Object> object) throws SQLException, IOException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.mergeObject(fields,object);
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}
			}
		});
	}

	@Override
	public int mergeObject(final Map<String, Object> object) throws SQLException, IOException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.mergeObject(object);
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}				
			}			
		});	
	}

	@Override
	public int updateObjectsByProperties(final Map<String, Object> fieldValues, final Map<String, Object> properties)
			throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.updateObjectsByProperties(fieldValues,properties);
			}			
		});
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
	public int updateObjectsByProperties(final Collection<String> fields,
										 final Map<String, Object> fieldValues,
										 final Map<String, Object> properties) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.updateObjectsByProperties(fields,fieldValues,properties);
			}
		});
	}

	@Override
	public int deleteObjectById(final Object keyValue) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjectById(keyValue);
			}			
		});
	}

	@Override
	public int deleteObjectById(final Map<String, Object> keyValues) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjectById(keyValues);
			}			
		});
		
	}

	@Override
	public int deleteObjectsByProperties(final Map<String, Object> properties) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjectsByProperties(properties);
			}			
		});
	}

	@Override
	public int insertObjectsAsTabulation(final JSONArray objects) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.insertObjectsAsTabulation(objects);
			}			
		});
	}

	@Override
	public int deleteObjects(final JSONArray objects) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjects(objects);
			}			
		});
	}

	@Override
	public int deleteObjectsAsTabulation(final String propertyName, final Object propertyValue) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjectsAsTabulation( propertyName,propertyValue);
			}			
		});
	}

	@Override
	public int deleteObjectsAsTabulation(final Map<String, Object> properties) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.deleteObjectsAsTabulation(properties);
			}			
		});		
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final JSONArray dbObjects) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.replaceObjectsAsTabulation(newObjects,dbObjects);
			}			
		});
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final String propertyName, final Object propertyValue)
			throws SQLException, IOException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.replaceObjectsAsTabulation(newObjects,propertyName,propertyValue);
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}				
			}			
		});
	}

	@Override
	public int replaceObjectsAsTabulation(final JSONArray newObjects, final Map<String, Object> properties)
			throws SQLException, IOException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.replaceObjectsAsTabulation(newObjects,properties);
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}				
			}			
		});
	}

	@Override
	public Long getSequenceNextValue(final String sequenceName) throws SQLException, IOException {
		return (Long)baseDao.getCurrentSession().doReturningWork(new ReturningWork<Long>(){
			@Override
			public Long execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.getSequenceNextValue(sequenceName);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public List<Object[]> findObjectsBySql(final String sSql, final Object[] values) throws SQLException, IOException {
		return (List<Object[]>)baseDao.getCurrentSession().doReturningWork(new ReturningWork<List<Object[]>>(){
			@Override
			public List<Object[]> execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsBySql(sSql,values);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public List<Object[]> findObjectsBySql(final String sSql, final Object[] values, final int pageNo, final int pageSize)
			throws SQLException, IOException {
		return (List<Object[]>)baseDao.getCurrentSession().doReturningWork(new ReturningWork<List<Object[]>>(){
			@Override
			public List<Object[]> execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsBySql(sSql,values,pageNo,pageSize);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public List<Object[]> findObjectsByNamedSql(final String sSql, final Map<String, Object> values)
			throws SQLException, IOException {
		return (List<Object[]>)baseDao.getCurrentSession().doReturningWork(new ReturningWork<List<Object[]>>(){
			@Override
			public List<Object[]> execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsByNamedSql(sSql,values);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public List<Object[]> findObjectsByNamedSql(final String sSql, final Map<String, Object> values, 
			final int pageNo, final int pageSize)	
			throws SQLException, IOException {
		return (List<Object[]>)baseDao.getCurrentSession().doReturningWork(new ReturningWork<List<Object[]>>(){
			@Override
			public List<Object[]> execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsByNamedSql(sSql,values,pageNo,pageSize);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray findObjectsAsJSON(final String sSql, final Object[] values, final String[] fieldnames)
			throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsAsJSON(sSql,values,fieldnames);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray findObjectsAsJSON(final String sSql, final Object[] values, final String[] fieldnames, 
			final int pageNo, final int pageSize)
			throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsAsJSON(sSql,values,fieldnames,pageNo,pageSize);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray findObjectsByNamedSqlAsJSON(final String sSql, final Map<String, Object> values,
			final String[] fieldnames)
			throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsByNamedSqlAsJSON(sSql,values,fieldnames);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public JSONArray findObjectsByNamedSqlAsJSON(final String sSql, final Map<String, Object> values, 
			final String[] fieldnames,final int pageNo, final int pageSize) throws SQLException, IOException {
		return (JSONArray)baseDao.getCurrentSession().doReturningWork(new ReturningWork<JSONArray>(){
			@Override
			public JSONArray execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				try {
					return dao.findObjectsByNamedSqlAsJSON(sSql,values,fieldnames,pageNo,pageSize);
				} catch (IOException e) {
					return null;
				}
			}			
		});
	}

	@Override
	public boolean doExecuteSql(final String sSql) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Boolean>(){
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.doExecuteSql(sSql);
			}			
		});
	}

	@Override
	public int doExecuteSql(final String sSql,final Object[] values) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.doExecuteSql(sSql,values);
			}			
		});
	}

	@Override
	public int doExecuteNamedSql(final String sSql, final Map<String, Object> values) throws SQLException {
		return baseDao.getCurrentSession().doReturningWork(new ReturningWork<Integer>(){
			@Override
			public Integer execute(Connection connection) throws SQLException {
				JsonObjectDao dao = createDao(connection);
				return dao.doExecuteNamedSql(sSql,values);
			}			
		});
	}
}
