package com.centit.framework.hibernate.dao;

import java.io.IOException;

/**
 * 意图将BaseDao中公共的部分独立出来，减少类的函数数量，
 * 因为每一个继承BaseDaoImpl的类都有这些函数，而这些行数基本上都是一样的
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.core.dao.PageDesc;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.QueryUtils;
@Transactional
public class DatabaseOptUtils {
	
    protected static Logger logger = LoggerFactory.getLogger(DatabaseOptUtils.class);
    
    protected static String dialectName;
    
    private DatabaseOptUtils() {

    }

    /**
     * 保存任意对象，hibernate 托管的对象
     */
    public final static int saveBatchObjects(BaseDaoImpl<?, ?> baseDao,
            Collection<? extends Object> objects) {	
    	int i=0;  
		try {  	
	        for (Object obj:objects) {	
	        	if(obj!=null){
	                baseDao.getCurrentSession().saveOrUpdate(obj);
	                i++;
	        	}
	            //批量刷新数据库，并不是提交事务，只是处罚update操作
	            if (0 == i % 20) {
	                flush( baseDao.getCurrentSession());               
	            }
   
	        }
    	}catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
                throw e;
         }
		return i;
    }

    /**
     * 批量保存对象集合
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param objects 这个Collection中的对象必须都是Po对象，但并要求是同个类型。
     */
    public static final int mergeBatchObjects(BaseDaoImpl<?, ?> baseDao,
            Collection<? extends Object> objects) {		
    	int i=0;  
		try {  	
	        for (Object obj:objects) {	
	        	if(obj!=null){
	                baseDao.getCurrentSession().merge(obj);
	                i++;
	        	}
	            //批量刷新数据库，并不是提交事务，只是处罚update操作
	            if (0 == i % 20) {
	                flush( baseDao.getCurrentSession());               
	            }
   
	        }
    	}catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
                throw e;
         }
		return i;
    }

    /**
     * 批量删除任意对象，hibernate 托管的对象
     */
    public final static int deleteBatchObject(BaseDaoImpl<?, ?> baseDao,
    		Collection<? extends Object> objects) {
    	int i=0;  
		try {  	
	        for (Object obj:objects) {	
	        	if(obj!=null){
	                baseDao.getCurrentSession().delete(obj);
	                i++;
	        	}
	            //批量刷新数据库，并不是提交事务，只是处罚update操作
	            if (0 == i % 20) {
	                flush( baseDao.getCurrentSession());               
	            }
   
	        }
    	}catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
                throw e;
         }
		return i;
    }

    /**
     * 直接运行 HQL,update delete insert

     */
    public final static int doExecuteHql(BaseDaoImpl<?, ?> baseDao, String shql) {
        return baseDao.getCurrentSession().createQuery(shql).executeUpdate();
    }
    
    /**
     * 直接运行带参数的 HQL,update delete insert
     */
    public final static int doExecuteHql(BaseDaoImpl<?, ?> baseDao, String shql,
            Object value) {
        Query q = baseDao.getCurrentSession().createQuery(shql);
        setQueryParameter(q,new Object[]{value});
        return q.executeUpdate();
    }

    /**
     * 直接运行带参数的 HQL,update delete insert
     * @param values
     */
    public final static int doExecuteHql(BaseDaoImpl<?, ?> baseDao, String shql,
            Object[] values) {
        Query q = baseDao.getCurrentSession().createQuery(shql);
        setQueryParameter(q,values);
        return q.executeUpdate();
    }

    /**
     * 直接运行SQL,update delete insert
     * 
     * @param sSql
     */
    public final static int doExecuteSql(BaseDaoImpl<?, ?> baseDao, String sSql) {
        NativeQuery q = baseDao.getCurrentSession().createNativeQuery(sSql);
        return q.executeUpdate();
    }

    /**
     * 直接运行行带参数的 SQL,update delete insert
     * 
     * @param sSql
     */
    public final static int doExecuteSql(BaseDaoImpl<?, ?> baseDao, String sSql,
            Object[] values) {
        NativeQuery q = baseDao.getCurrentSession().createNativeQuery(sSql);
        setQueryParameter(q,values);
        return q.executeUpdate();
    }

    
    public final static void setQueryParameters(Query q, Map<String,Object> values) {
    	if (values != null) {
       	 	for (String sParam : q.getParameterMetadata().getNamedParameterNames()) {
                Object value = values.get(sParam);
                if (value == null) {
                    q.setParameter(sParam, null);
                } else if (value instanceof Collection) {
                    q.setParameterList(sParam, (Collection<?>) value);
                } else if (value instanceof Object[]) {
                    q.setParameterList(sParam, (Object[]) value);
                } else {
                    q.setParameter(sParam, value);
                }
           }
       }
	}
    
    /**
     * 直接运行行带参数的 HQL,update delete insert
     * 
     * @param sSql
     */
    public final static int doExecuteHql(BaseDaoImpl<?, ?> baseDao, String sSql,
            Map<String,Object> values) {
        Query q = baseDao.getCurrentSession().createQuery(sSql);
        setQueryParameters(q,values);
        return q.executeUpdate();
    }
    
    /**
     * 直接运行行带参数的 SQL,update delete insert
     * 
     * @param sSql
     */
    public final static int doExecuteSql(BaseDaoImpl<?, ?> baseDao, String sSql,
            Map<String,Object> values) {
        NativeQuery q = baseDao.getCurrentSession().createNativeQuery(sSql);
        setQueryParameters(q,values);
        return q.executeUpdate();
    }
    
    /**
     * 获取某个表某个字段的最大数值，一定是数字
     * 
     * @param fieldName
     * @return
     */
    public final String getNextKeyByMaxInteger(BaseDaoImpl<?, ?> baseDao,
            String fieldName, String sObjectName) {
        try {
            Query q = baseDao.getCurrentSession().createQuery("SELECT MAX(cast(" + fieldName
                    + " as int)) FROM " + sObjectName);
            return String
                    .valueOf(Integer.valueOf(q.list().get(0).toString()) + 1);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "0";
        }
    }

    /**
     * 获取一个表中最大值作为主键生成主键的基础，这样的主键生成方式不建议在平凡操作的业务中使用。 可以在一些系统维护的业务中使用
     * 
     * @param fieldName
     *            字段名
     *            hibernate查询语句
     * @param codeLen
     *            代码长度，左侧补零 ，如果 codeLen 为0 则不补零
     * @return 编码从 1 开始
     */
    public final static String getNextKeyByHqlStrOfMax(BaseDaoImpl<?, ?> baseDao,
            String fieldName, String sObjectName, int codeLen) {
        Query q = baseDao.getCurrentSession().createQuery("SELECT MAX(" + fieldName + ")  FROM "
                + sObjectName);

        return StringBaseOpt.nextCode(
        		StringBaseOpt.fillZeroForString(StringBaseOpt.objectToString(q.list().get(0)),
                codeLen));
    }

    /**
     * 获取一个表中最大值作为主键生成主键的基础 ，这样的主键生成方式不建议在平凡操作的业务中使用。 可以在一些系统维护的业务中使用
     * 
     * @param fieldName 字段名
     *            hibernate查询语句
     * @return 编码从 1 开始
     */
    public final static String getNextKeyByHqlStrOfMax(BaseDaoImpl<?, ?> baseDao,
            String fieldName, String sObjectName) {
        return getNextKeyByHqlStrOfMax(baseDao, fieldName, sObjectName, 0);
    }

    public final static void setDialectName(String dialectName) {
    	DatabaseOptUtils.dialectName=dialectName;
    }
    /**
     * 获取数据类型
     * @return
     */
    public final static String getDialectName() {
    	if(StringUtils.isBlank(dialectName))
    		dialectName = SysParametersUtils.getStringValue("jdbc.dialect");
        return dialectName;
    }

    /**
     * 获取sequence的当前值 只有oracle DB2支持
     * 
     * @param sequenceName
     * @return
     */
    public final static String getCurrValueOfSequence(
            BaseDaoImpl<?, ?> baseDao, String sequenceName) {
    	Long seq = getNextLongSequence(baseDao, sequenceName);
    	if(seq==null)
    		return null;
    	return seq.toString();
    }

    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     * 
     * @param sequenceName
     * @param codeLen
     *            代码长度，左侧补零 ，如果 codeLen 为0 则不补零
     * @return
     */
    public final static String getNextKeyBySequence(BaseDaoImpl<?, ?> baseDao,
            String sequenceName, int codeLen) {
        String sKey = getNextValueOfSequence(baseDao, sequenceName);
        if (codeLen > 0)
            sKey = StringBaseOpt.fillZeroForString(sKey, codeLen);
        return sKey;
    }

    
    /** 用表来模拟sequence 
	 * create table sequence_table 
	 * (sequence_Name varchar(100) not null primary key, 
	 * current_value integer);
	 */
	public static Long getSequenceNextValueUseTable(final BaseDaoImpl<?, ?> baseDao,final String sequenceName) throws SQLException, IOException {
		Object object = getSingleObjectBySql(
                baseDao,
				 "SELECT count(1) hasValue from sequence_table "
				 + " where sequence_Name = ?",
                new Object[]{sequenceName});
		Long l = NumberBaseOpt.castObjectToLong(object);
		if(l==0){
			getSingleObjectBySql(
                    baseDao,
					"insert into sequence_table(sequence_Name,current_value)"
					+ " values(?,?)", new Object[]{sequenceName,1});
			return 1l;
		}else{
			getSingleObjectBySql(
                    baseDao,
					"update sequence_table current_value = current_value + 1 "
					+ "where sequence_Name= ?", new Object[]{sequenceName});
			object = getSingleObjectBySql(
                    baseDao,
					 "SELECT current_value from sequence_table "
					 + " where sequence_Name = ?",
	                 new Object[]{sequenceName});
		}
		return NumberBaseOpt.castObjectToLong(object);
	}
    
    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     * 
     * @param sequenceName
     * @return
     */
    public final static Long getNextLongSequence(BaseDaoImpl<?, ?> baseDao,
            String sequenceName) {
        String dn = getDialectName();
        if(dn.indexOf("Oracle")>=0)//Oracle
            return NumberBaseOpt.castObjectToLong(getSingleObjectBySql(
                    baseDao, "SELECT " + sequenceName
                            + ".nextval from dual"));

        if(dn.indexOf("DB2")>=0)//DB2
            return NumberBaseOpt.castObjectToLong( getSingleObjectBySql(
                    baseDao, "SELECT nextval for "
                            + sequenceName + " from sysibm.sysdummy1"));

        if(dn.indexOf("MySQL")>=0) // my sql
            return Long.valueOf(getSingleObjectBySql(
                    baseDao, "SELECT sequence_nextval ('"+ sequenceName+"');")
                    .toString());
        
        // if("SQLServerDialect".endsWith(dn))
        // sql server 没有 sequence 所以用 GUID 代替
        try {
			return getSequenceNextValueUseTable(baseDao,sequenceName);
		} catch (Exception e) {
			return null;
		}
    }

    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     * 
     * @param sequenceName
     * @return
     */
    public final static String getNextValueOfSequence(
            BaseDaoImpl<?, ?> baseDao, String sequenceName) {
        Long sequence = getNextLongSequence(baseDao, sequenceName);
        if (null == sequence) {
            return "";
        }
        return String.valueOf(sequence);
    }

    /**
     * 从只返回一条记录的语句中获取对象
     * 
     * @param baseDao
     * @param sHql
     * @return
     */
    public final static Object getSingleObjectByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql) {
        Query queryObject = baseDao.getCurrentSession().createQuery(sHql);
        return queryObject.uniqueResult();
    }

    public final static Object getSingleObjectByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql, final Object paramObject) {
        Query queryObject = baseDao.getCurrentSession().createQuery(sHql);
        setQueryParameter(queryObject,new Object[] {paramObject});
        return queryObject.uniqueResult();
    }

    public final static Object getSingleObjectByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql, final Object[] paramObjects) {

        Query queryObject = baseDao.getCurrentSession().createQuery(sHql);
        setQueryParameter(queryObject,paramObjects);
        return queryObject.uniqueResult();

    }

    public final static Object getSingleObjectByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql, final String paramName, final Object paramObject) {
        Query queryObject = baseDao.getCurrentSession().createQuery(sHql);
        queryObject.setParameter(paramName, paramObject);
        return queryObject.uniqueResult();
    }

    public final static Object getSingleObjectByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql, final Map<String, Object> paramObjects) {

        Query queryObject = baseDao.getCurrentSession().createQuery(sHql);
        setQueryParameters(queryObject,paramObjects);
        return queryObject.uniqueResult();

    }

    public final static Object getSingleObjectBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql) {
        NativeQuery queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        return queryObject.uniqueResult();
    }

    public final static Object getSingleObjectBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql, final Object paramObject) {
        NativeQuery queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        setQueryParameter(queryObject,new Object[] {paramObject});
        return queryObject.uniqueResult();
    }

    public final static Object getSingleObjectBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql, final Object[] paramObjects) {
        NativeQuery queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        setQueryParameter(queryObject,new Object[] {paramObjects});
        return queryObject.uniqueResult();

    }

    public final static Object getSingleObjectBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql, final String paramName, final Object paramObject) {
        NativeQuery queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        queryObject.setParameter(paramName, paramObject);
        return queryObject.uniqueResult();

    }

    public final static Object getSingleObjectBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql, final Map<String, Object> paramObjects) {

        NativeQuery queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        setQueryParameters(queryObject,paramObjects);       
        return queryObject.uniqueResult();

    }

    
    public final static long getSingleIntByHql(BaseDaoImpl<?, ?> baseDao,
            final String sHql, final Object paramObject) {
    	 Object obj = getSingleObjectByHql(baseDao, sHql,  paramObject);
		 if (obj == null)
	         return 0;
	     if (obj instanceof Long)
	         return ((Long) obj).longValue();
	     if (obj instanceof String)
	         return Long.valueOf(obj.toString()).longValue();
	     if (obj instanceof BigDecimal)
	         return ((BigDecimal) obj).longValue();
	     return 0;
    }
    
    /**
     * 获取唯一的一个整形数据的HQL
     */
    public final static long getSingleIntByHql(BaseDaoImpl<?, ?> baseDao,
            final String hql) {

        Query queryObject = baseDao.getCurrentSession().createQuery(hql);
        Object obj = queryObject.uniqueResult();

        if (obj == null)
            return 0;
        if (obj instanceof Long)
            return ((Long) obj).longValue();
        if (obj instanceof String)
            return Long.valueOf(obj.toString()).longValue();
        if (obj instanceof BigDecimal)
            return ((BigDecimal) obj).longValue();
        if (obj instanceof BigInteger)
            return ((BigInteger) obj).longValue();
        return 0;
    }

    
    /**
     * 获取唯一的一个整形数据的SQL
     */
    public final static long getSingleIntBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql) {

        Query queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        Object obj = queryObject.uniqueResult();

        if (obj == null)
            return 0;
        if (obj instanceof Long)
            return ((Long) obj).longValue();
        if (obj instanceof String)
            return Long.valueOf(obj.toString()).longValue();
        if (obj instanceof BigDecimal)
            return ((BigDecimal) obj).longValue();
        return 0;
    }
    
    public final static long getSingleIntBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql, final Object paramObject) {
    	 Object obj = getSingleObjectBySql(baseDao, sSql,  paramObject);
		 if (obj == null)
	         return 0;
	     if (obj instanceof Long)
	         return ((Long) obj).longValue();
	     if (obj instanceof String)
	         return Long.valueOf(obj.toString()).longValue();
	     if (obj instanceof BigDecimal)
	         return ((BigDecimal) obj).longValue();
	     return 0;
    }
    
    /**
     * 获取唯一的一个字符串数据的HQL
     */
    public final static String getSingleStringByHql(BaseDaoImpl<?, ?> baseDao,
            final String hql) {

        Query queryObject = baseDao.getCurrentSession().createQuery(hql);
        Object obj = queryObject.uniqueResult();

        if (obj == null)
            return "";
        
        return String.valueOf(obj);
    }

    
    /**
     * 获取唯一的一个字符串数据的SQL
     */
    public final static String getSingleStringBySql(BaseDaoImpl<?, ?> baseDao,
            final String sSql) {

        Query queryObject = baseDao.getCurrentSession().createNativeQuery(sSql);
        Object obj = queryObject.uniqueResult();

        if (obj == null)
            return "";
        
        return String.valueOf(obj);
    }

   
    public final static boolean callProcedure(BaseDaoImpl<?, ?> baseDao,
            String procName, Object... paramObjs) {
        
        ProcedureWork pwork = new ProcedureWork(procName,paramObjs);        
        baseDao.getCurrentSession().doWork(pwork);
        return pwork.hasBeSucceedExecuted();       
    }
    
    
    
    /**
     * 执行一个Oracle存储过程 返回一个数据集，这个数据集是一个out的游标,这个参数必需为存储过程的最后一个参数
     * 
     * @param procName
     * @param paramObjs
     * @return
     */
    public final static ResultSet callProcedureOutRS(Connection conn,
            String procName, Object... paramObjs) throws SQLException {
        int n = paramObjs.length;
        StringBuilder procDesc = new StringBuilder("{call ");
        procDesc.append(procName).append("(");
        for (int i = 1; i < n; i++) {
            procDesc.append("?,");
        }
        procDesc.append("?)}");
        CallableStatement stmt = null;
        
        stmt = conn.prepareCall(procDesc.toString());
        //stmt.getParameterMetaData().
        for (int i = 0; i < n; i++) {
            if (paramObjs[i] == null)
                stmt.setNull(i + 1, Types.NULL);
            else if (paramObjs[i] instanceof java.util.Date)
                stmt.setObject(i + 1, DatetimeOpt
                        .convertSqlDate((java.util.Date) paramObjs[i]));
            else
                stmt.setObject(i + 1, paramObjs[i]);
        }
        stmt.registerOutParameter(n + 1, ProcedureWork.ORACLE_TYPES_CURSOR);
        stmt.execute();
        ResultSet rs = (ResultSet) stmt.getObject(n + 1);
        return rs;      
    }
    
    public final static ResultSet callProcedureOutRS(BaseDaoImpl<?, ?> baseDao,
            String procName, Object... paramObjs) {
        ProcedureWork pwork = new ProcedureWork(procName,paramObjs);
        pwork.setOracleProcedureWithReturnCursor(true);
        baseDao.getCurrentSession().doWork(pwork);
        return pwork.getRetrunResultSet();        
    }
    
    public final static Object callFunction(BaseDaoImpl<?, ?> baseDao,
            String procName,int resultType, Object... paramObjs) {
        FunctionWork pwork = new FunctionWork(procName,resultType,paramObjs);
        baseDao.getCurrentSession().doWork(pwork);
        return pwork.getRetrunObject();        
    }
   
    /**
  	 *
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql hql语句，这个语句中的参数用？作为占位符
     * @param values hql语句中的变量值
     * @param pageDesc  分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return
     */
    public final static List<?> findObjectsByHql(BaseDaoImpl<?, ?> baseDao, String shql,
            Object[] values, PageDesc pageDesc) {
    	
    	int startPos = -1;
        int maxSize = 0;
        if(pageDesc!=null){
            startPos = pageDesc.getRowStart();
            maxSize = pageDesc.getPageSize();
        }       
        
        // HashMaps for search variety fields
        // StringBuffer hql = BuilderHql(shql,filterDesc);

        Query q = baseDao.getCurrentSession().createQuery(shql);
        setQueryParameter(q,values);
        if (maxSize > 0)
            q.setMaxResults(maxSize);
        if (startPos >= 0)
            q.setFirstResult(startPos);
        List<?> l = q.list();
        
        if(l != null && pageDesc!=null){
        	if(maxSize>0){
		        q = baseDao.getCurrentSession().createQuery(QueryUtils.buildGetCountHQL(shql));
                setQueryParameter(q,values);
		        pageDesc.setTotalRows(Integer.valueOf(q.list().get(0).toString()));
        	}else
        		pageDesc.setTotalRows(l.size());
        }
        		
        return l;
    }

    /**
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql sql语句，这个语句必须用命名参数
     * @param values	命名参数对应的变量
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return
     */
    public final static List<?> findObjectsByHql(BaseDaoImpl<?, ?> baseDao, String shql,
            Map<String, Object> values, PageDesc pageDesc) {
        int startPos = 0;
        int maxSize = 0;
        if(pageDesc!=null){
            startPos = pageDesc.getRowStart();
            maxSize = pageDesc.getPageSize();
        }
        // HashMaps for search variety fields
        // StringBuffer hql = BuilderHql(shql,filterDesc);
        Query q = baseDao.getCurrentSession().createQuery(shql);
        setQueryParameters(q,values);
        
        if (maxSize > 0)
            q.setMaxResults(maxSize);
        if (startPos >= 0)
            q.setFirstResult(startPos);
        List<?> l = q.list();

        if(l!=null && pageDesc!=null){
        	if(maxSize>0){
	            q = baseDao.getCurrentSession().createQuery(QueryUtils.buildGetCountHQL(shql));
	            setQueryParameters(q,values);
	            pageDesc.setTotalRows(Integer.valueOf(q.list().get(0).toString()));
        	}else
        		pageDesc.setTotalRows(l.size());
        }
        return l;
    }
    
    /**
     * 查询所有的不分页
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql sql语句，这个语句必须用命名参数
     * @param values	命名参数对应的变量
     * @return
     */
    public final static List<?> findObjectsByHql(BaseDaoImpl<?, ?> baseDao, String shql,
            Map<String, Object> values) {
        return findObjectsByHql(baseDao,  shql,
                values , null);
    }

  
    /**
     * 不分页查询 返回所有的记录
  	 *
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql hql语句，这个语句中的参数用？作为占位符
     * @param values hql语句中的变量值
     * @return
     */
    public final static List<?> findObjectsByHql(BaseDaoImpl<?, ?> baseDao,
            String shql, Object[] values) {
       return findObjectsByHql(baseDao,shql,values,new PageDesc(-1,-1));
    }


    /**
     * 不分页查询 返回所有的记录
     * 无参数的HQL 
  	 *
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql hql语句
     * @return
     */
    public final static List<?> findObjectsByHql(BaseDaoImpl<?, ?> baseDao, String shql) {
        try {
            return baseDao.getCurrentSession().createQuery(shql).list();
        } catch (Exception e) {
            //logger.error(e.getMessage());
            return null;
        }
    }
    


    /**
     * 执行原生的SQL查询语句，返回的类型为 List<Object[]>
     * @return List<Object[]>
     */
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql) {
        try {
            return baseDao.getCurrentSession().createNativeQuery(ssql).list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

  
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao,
            String ssql, Object[] values) {
    	return findObjectsBySql( baseDao,  ssql,
                values,  new PageDesc(-1,-1),null);   
    }

    /**
     * 用原生SQL 查询数据库 objectType 如果没有的话可以为NULL
     * @param objectType
     *            制定返回的结构类型
     * @return
     */
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao,
            String ssql, Class<?> objectType) {

        try {
            NativeQuery q = baseDao.getCurrentSession().createNativeQuery(ssql);
            q.addEntity(objectType);
            return q.list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    private final static void setQueryParameter(Query query, Object[] values ){
        if (values != null) {
            int basePostion =
                    query.getParameterMetadata().isOrdinalParametersZeroBased()?0:1;
            for (int i = 0; i < values.length; i++) {
                query.setParameter(basePostion+ i, values[i]);
            }
        }
    }
  
   

    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql,
            Object[] values, PageDesc pageDesc, Class<?> objectType) {
        
    	int startPos = 0;
        int maxSize = 0;
        if(pageDesc!=null){
            startPos = pageDesc.getRowStart();
            maxSize = pageDesc.getPageSize();
        }

        NativeQuery q = baseDao.getCurrentSession().createNativeQuery(ssql);
        setQueryParameter(q,values);

        if (maxSize > 0)
            q.setMaxResults(maxSize);
        if (startPos >= 0)
            q.setFirstResult(startPos);
        
        if(objectType!=null)
        	q.addEntity(objectType);
        
        List<?> l = q.list();

        if(l!=null && pageDesc!=null){
        	if(maxSize>0){
	            q = baseDao.getCurrentSession().createNativeQuery(QueryUtils.buildGetCountSQL(ssql));
                setQueryParameter(q,values);
	            pageDesc.setTotalRows(Integer.valueOf(q.list().get(0).toString()));
        	}else
        		pageDesc.setTotalRows(l.size());
        }
        return l;
    }
   
    /**
     * 获取 符合条件的记录数量 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql  hql语句，这个语句中的参数用？作为占位符
     * @param values 参数对应的变量
     * @return
     */
    public final static int getHqlReturnObjectCounts(BaseDaoImpl<?, ?> baseDao, String shql,
            Object[] values) {
 
        Query q = baseDao.getCurrentSession().createQuery(QueryUtils.buildGetCountHQL(shql));
        setQueryParameter(q,values);
        return Integer.valueOf(q.list().get(0).toString());
    }
    /**
     * 获取 符合条件的记录数量 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param shql sql语句，这个语句必须用命名参数
     * @param values	命名参数对应的变量
     * @return
     */
    public final static int getHqlReturnObjectCounts(BaseDaoImpl<?, ?> baseDao, String shql,
            Map<String, Object> values) {
 
        Query q = baseDao.getCurrentSession().createQuery(QueryUtils.buildGetCountHQL(shql));
        setQueryParameters(q,values);
        return Integer.valueOf(q.list().get(0).toString());
    }
    
    /**
     * 获取 符合条件的记录数量 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql  sql语句，这个语句中的参数用？作为占位符
     * @param values 参数对应的变量
     * @return
     */
    public final static int getSqlReturnObjectCounts(BaseDaoImpl<?, ?> baseDao, String ssql,
            Object[] values) { 
        Query q = baseDao.getCurrentSession().createNativeQuery(QueryUtils.buildGetCountSQL(ssql));
        setQueryParameter(q,values);
        return Integer.valueOf(q.list().get(0).toString());
    }
    
    /**
     * 获取 符合条件的记录数量 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param sSql sql语句，这个语句必须用命名参数
     * @param values	命名参数对应的变量
     * @return
     */    
    public final static int getSqlReturnObjectCounts(BaseDaoImpl<?, ?> baseDao, String sSql,
            Map<String, Object> values) {
 
        Query q = baseDao.getCurrentSession().createNativeQuery(QueryUtils.buildGetCountSQL(sSql));
        setQueryParameters(q,values);
        return Integer.valueOf(q.list().get(0).toString());
    }
    // -----------------------------------------------------------------------------------

    /**
     * 返回的类型为 List<objectType>
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param values  命名参数对应的变量
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @param objectType 是一个Po类型，将查询结果封装成对象，要求查询返回的字段和Po中的字段定义一致。
     * @return List<objectType>
     */
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql,
            Map<String, Object> values, PageDesc pageDesc,Class<?> objectType) {
        
        int startPos = 0;
        int maxSize = 0;
        if(pageDesc!=null){
            startPos = pageDesc.getRowStart();
            maxSize = pageDesc.getPageSize();
        }
        // HashMaps for search variety fields
        // StringBuffer hql = BuilderHql(shql,filterDesc);
        NativeQuery q = baseDao.getCurrentSession().createNativeQuery(ssql);
        setQueryParameters(q,values);
        if (maxSize > 0)
            q.setMaxResults(maxSize);
        if (startPos >= 0)
            q.setFirstResult(startPos);
        
        if(objectType!=null)
        	q.addEntity(objectType);
        
        List<?> l = q.list();

        if(l!=null && pageDesc!=null){
        	if(maxSize>0){
	            q = baseDao.getCurrentSession().createNativeQuery(QueryUtils.buildGetCountSQL(ssql));
	            setQueryParameters(q,values);
	            pageDesc.setTotalRows(Integer.valueOf(q.list().get(0).toString()));
        	}else
        		pageDesc.setTotalRows(l.size());
        }
        return l;
    }
    

    /**
     * 返回的类型为 List<Object[]>
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param values  命名参数对应的变量
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return List<Object[]>
     */
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql,
            Map<String, Object> values, PageDesc pageDesc) {        
        return findObjectsBySql( baseDao,  ssql,
                 values,  pageDesc,null);       
    }    
    
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String shql,
            Map<String, Object> values) {
        return findObjectsBySql(baseDao,  shql,
                values , new PageDesc(-1,-1),null);
    }   

   
    /**
     * 用原带参数的SQL 查询数据库 objectType 如果没有的话可以为NULL
     * @param values
     *            参数
     * @param objectType
     *            制定返回的结构类型
     * @return
     */
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql,
            Object[] values, Class<?> objectType) {
    	return findObjectsBySql( baseDao,  ssql,
                values,  new PageDesc(-1,-1),objectType);
    }
    
    public final static List<?> findObjectsBySql(BaseDaoImpl<?, ?> baseDao, String ssql,
            Object[] values, PageDesc pageDesc) {
    	return findObjectsBySql( baseDao,  ssql,
                values,pageDesc,null);
    }
    
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String shql, 
            String [] fieldnames,Object[] values, PageDesc pageDesc) {
    	
    	List<?> dataList = findObjectsByHql(baseDao,shql,values,pageDesc);
    	if(dataList==null || dataList.size()==0)
    		return null;
    	
    	String [] fieldNames  = fieldnames;
    	if(fieldNames==null){
	    	 List<String> fields = QueryUtils.getSqlFiledNames(shql);
	         if(fields==null || fields.size()<1)
	             return null;
	         fieldNames = fields.toArray(new String[fields.size()]);
    	}

        JSONArray ja = new JSONArray();
        for(int j=0; j<dataList.size();j++ ){
            JSONObject jo = new JSONObject();
            for(int i=0;i<fieldNames.length;i++){
            	Object obj = DatabaseAccess.fetchLobField( ((Object[])dataList.get(j) )[i],false);
            	jo.put(fieldNames[i], obj);	
            }
            ja.add(jo);
   
        }
        
        return ja;    
    }
    
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String shql, 
            String [] fieldnames,Map<String,Object> values, PageDesc pageDesc) {
    	
    	List<?> dataList = findObjectsByHql(baseDao,shql,values,pageDesc);
    	if(dataList==null || dataList.size()==0)
    		return null;
    	
    	String [] fieldNames  = fieldnames;
    	if(fieldNames==null){
	    	 List<String> fields = QueryUtils.getSqlFiledNames(shql);
	         if(fields==null || fields.size()<1)
	             return null;
	         fieldNames = fields.toArray(new String[fields.size()]);
    	}

        JSONArray ja = new JSONArray();
        for(int j=0; j<dataList.size();j++ ){
            /*if(fieldNames.length == 1){
                ja.add(((Object [])dataList.get(j))[0]);
            }else{*/
                JSONObject jo = new JSONObject();
                for(int i=0;i<fieldNames.length;i++){                	
                    jo.put(fieldNames[i],
                    		DatabaseAccess.fetchLobField( ((Object [])dataList.get(j))[i],false));
                }
                ja.add(jo);
            //}      
        }
        
        return ja;   
    }
    
  
   
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String shql, String [] fieldNames) {
        return findObjectsAsJSonByHql(baseDao, shql,fieldNames,(Object []) null ,new PageDesc(-1,-1));
    }
    
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String shql, 
            Object[] values, PageDesc pageDesc) {
        return findObjectsAsJSonByHql(baseDao, shql,null, values ,new PageDesc(-1,-1));
    }
    
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String shql, 
            Map<String,Object> values, PageDesc pageDesc) {
        return findObjectsAsJSonByHql(baseDao, shql,null, values ,new PageDesc(-1,-1));
    }
 
    public final static JSONArray findObjectsAsJSonByHql(BaseDaoImpl<?, ?> baseDao, String ssql) {
        return findObjectsAsJSonByHql(baseDao, ssql, null ,(Object[])null, new PageDesc(-1,-1));
    }
    
    /**
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param fieldnames 字段名称作为json中Map的key，没有这个参数的函数会自动从sql语句中解析字段名作为json中map的
     * @param values 命名参数对应的变量
     * @param pageDesc 获取记录其实位置 和 maxSize 一起完成分页操作
              返回的最大记录数
     * @return JSONArray实现了List<JSONObject>接口，JSONObject实现了Map<String, Object>接口。所以可以直接转换为List<Map<String,Object>>
     */
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String ssql, 
            String [] fieldnames, Object[] values, PageDesc pageDesc) {
    	
    	List<?> dataList = findObjectsBySql(baseDao,ssql,values,pageDesc,null);
    	if(dataList==null || dataList.size()==0)
    		return null;
    	
    	String [] fieldNames  = fieldnames;
    	if(fieldNames==null){
	    	 List<String> fields = QueryUtils.getSqlFiledNames(ssql);
	         if(fields==null || fields.size()<1)
	             return null;
	         fieldNames = fields.toArray(new String[fields.size()]);
    	}

        JSONArray ja = new JSONArray();
        for(int j=0; j<dataList.size();j++ ){
            /*if(fieldNames.length == 1){
                ja.add(((Object [])dataList.get(j))[0]);
            }else{*/
                JSONObject jo = new JSONObject();
                for(int i=0;i<fieldNames.length;i++){
                    jo.put(fieldNames[i], DatabaseAccess.fetchLobField(
                    		((Object [])dataList.get(j))[i],false));
                }
                ja.add(jo);
            //}      
        }
        
        return ja;
    }
    
    
    /**
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param fieldnames 字段名称作为json中Map的key，没有这个参数的函数会自动从sql语句中解析字段名作为json中map的
     * @param values 命名参数对应的变量
     * @param pageDesc 获取记录其实位置 和 maxSize 一起完成分页操作
         返回的最大记录数
     * @return JSONArray实现了List<JSONObject>接口，JSONObject实现了Map<String, Object>接口。所以可以直接转换为List<Map<String,Object>>
     */
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String ssql, 
            String [] fieldnames, Map<String,Object> values, PageDesc pageDesc) {
    	
    	List<?> dataList = findObjectsBySql(baseDao,ssql,values,pageDesc,null);
    	if(dataList==null || dataList.size()==0)
    		return null;
    	
    	String [] fieldNames  = fieldnames;
    	if(fieldNames==null){
	    	 List<String> fields = QueryUtils.getSqlFiledNames(ssql);
	         if(fields==null || fields.size()<1)
	             return null;
	         fieldNames = fields.toArray(new String[fields.size()]);
    	}

        JSONArray ja = new JSONArray();
        for(int j=0; j<dataList.size();j++ ){
            /*if(fieldNames.length == 1){
                ja.add(((Object [])dataList.get(j))[0]);
            }else{*/
                JSONObject jo = new JSONObject();
                for(int i=0;i<fieldNames.length;i++){
                    jo.put(fieldNames[i], 
                    		DatabaseAccess.fetchLobField(
                    				((Object [])dataList.get(j))[i],false));
                }
                ja.add(jo);
            //}      
        }
        
        return ja;
    }
    
   
    
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String ssql, String [] fieldNames) {
        return findObjectsAsJSonBySql(baseDao, ssql,fieldNames,(Object[]) null , new PageDesc(-1,-1));
    }
    
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String shql, 
            Object[] values, PageDesc pageDesc) {
        return findObjectsAsJSonBySql(baseDao,  shql, null, 
                values,  pageDesc);
    }
    
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String shql, 
            Map<String,Object> values, PageDesc pageDesc) {
        return findObjectsAsJSonBySql(baseDao,  shql, null, 
                values,  pageDesc);
    }    
  
    
    public final static JSONArray findObjectsAsJSonBySql(BaseDaoImpl<?, ?> baseDao, String ssql) {
        return findObjectsAsJSonBySql(baseDao, ssql,null,(Object[]) null ,  new PageDesc(-1,-1));
    }
    
    
    
    /**
     * 刷新Session缓存中的数据进行强制提交
     * 
     * @param dbSession
     *            HibernateDaoSupport
     */
    public final static void flush(Session dbSession) {
        dbSession.flush();
        dbSession.clear();
    }
}
