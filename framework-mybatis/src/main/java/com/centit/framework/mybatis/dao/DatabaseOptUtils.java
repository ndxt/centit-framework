package com.centit.framework.mybatis.dao;

import java.io.IOException;

/**
 * 意图将sqlSession中公共的部分独立出来，减少类的函数数量，
 * 因为每一个继承sqlSessionImpl的类都有这些函数，而这些行数基本上都是一样的
 */

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.core.dao.PageDesc;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.DBType;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.QueryUtils;
@Transactional
public class DatabaseOptUtils {
	
	public static final int ORACLE_TYPES_CURSOR = -10; // oracle.jdbc.OracleTypes.CURSOR
	
    protected static Logger logger = LoggerFactory.getLogger(DatabaseOptUtils.class);
    
    protected static String dialectName;
    
    private DatabaseOptUtils() {

    }

    /**
     * 处理翻页参数
     * @param pageQureyMap pageQureyMap
     * @param pageDesc pageDesc
     * @param total total
     * @return Map类型
     */
    public final static  Map<String, Object> prepPageParmers(Map<String, Object> pageQureyMap,PageDesc pageDesc,int total) {

        int pageNo=pageDesc.getPageNo() ;
        int pageSize=pageDesc.getPageSize();
        int _pageNo = (total - 1) / pageSize + 1;
        if (_pageNo < pageNo) {
            pageNo = _pageNo;// 页码校验
        }
        int start = (pageNo - 1) * pageSize;
        int end = pageNo * pageSize;
        pageQureyMap.put("startRow", new Integer(start));
        pageQureyMap.put("endRow", new Integer(end));
        pageQureyMap.put("maxSize", new Integer(pageSize));
        //回写总数量
        pageDesc.setTotalRows(total);
        //System.err.println("pageQureyMap========"+JSON.toJSONString(pageQureyMap));
        return pageQureyMap;
    }
    /**
     * 直接运行SQL,update delete insert
     * @param sqlSession sqlSession
     * @param sSql sSql
     * @throws SQLException SQLException
     */
    public final static void doExecuteSql(SqlSession sqlSession, String sSql) throws SQLException {
    	DatabaseAccess.doExecuteSql(sqlSession.getConnection(), sSql);
    }

    /**
     * 直接运行行带参数的 SQL,update delete insert
     * @param sqlSession sqlSession
     * @param sSql sSql
     * @param values values
     * @throws SQLException SQLException
     */
    public final static void doExecuteSql(SqlSession sqlSession, String sSql,
            Object[] values) throws SQLException {
    	DatabaseAccess.doExecuteSql(sqlSession.getConnection(), sSql,values);
    }


    /**
     * 直接运行行带参数的 SQL,update delete insert
     *
     * @param sqlSession sqlSession
     * @param sSql sSql
     * @param values values
     * @throws SQLException SQLException
     */
    public final static void doExecuteSql(SqlSession sqlSession, String sSql,
            Map<String,Object> values) throws SQLException {
    	DatabaseAccess.doExecuteNamedSql(sqlSession.getConnection(), sSql,values);
    }
    
    public final static void setDialectName(String dialectName) {
    	DatabaseOptUtils.dialectName=dialectName;
    }
    /**
     * 获取数据类型
     * @return 数据类型
     */
    public final static String getDialectName() {
    	if(StringUtils.isBlank(dialectName))
    		dialectName = SysParametersUtils.getStringValue("jdbc.dialect");
        return dialectName;
    }

    /**
     * 获取分页语句
     * @param sql sql
     * @param offset offset
     * @param maxsize 等同于 pageSize
     * @return 获取分页语句
     */
    public final static String buildLimitQuerySQL(String sql,int offset,int maxsize){
    	DBType dbType = DBType.mapDialectToDBType(getDialectName());
    	switch(dbType){
	  	case SqlServer:
	  		return QueryUtils.buildSqlServerLimitQuerySQL(sql, offset, maxsize);
	  	case Oracle:
	  		return QueryUtils.buildOracleLimitQuerySQL(sql, offset, maxsize, false);
	  	case DB2:
	  		return QueryUtils.buildDB2LimitQuerySQL(sql, offset, maxsize);
	  	case MySql:
	  		return QueryUtils.buildMySqlLimitQuerySQL(sql, offset, maxsize, false);
	  	default: 
	  		return sql;
	  }
    }
    /**
     * 获取sequence的当前值 只有oracle DB2支持
     * @param sqlSession sqlSession
     * @param sequenceName sequenceName
     * @return sequence的当前值
     * @throws IOException IOException
     * @throws SQLException SQLException
     */
    public final static String getCurrValueOfSequence(
            SqlSession sqlSession, String sequenceName) throws SQLException, IOException {
    	Long seq = getNextLongSequence(sqlSession, sequenceName);
    	if(seq==null)
    		return null;
    	return seq.toString();
    }

    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     * @param sqlSession sqlSession
     * @param sequenceName sequenceName
     * @param codeLen 代码长度，左侧补零 ，如果 codeLen 为0 则不补零
     * @return sequence的下一个新值
     * @throws IOException  IOException
     * @throws SQLException SQLException
     */
    public final static String getNextKeyBySequence(SqlSession sqlSession,
            String sequenceName, int codeLen) throws SQLException, IOException {
        String sKey = getNextValueOfSequence(sqlSession, sequenceName);
        if (codeLen > 0)
            sKey = StringBaseOpt.fillZeroForString(sKey, codeLen);
        return sKey;
    }

    
    /** 用表来模拟sequence 
	 * create table sequence_table 
	 * (sequence_Name varchar(100) not null primary key, 
	 * current_value integer);
     * @param sqlSession sqlSession
     * @param sequenceName sequenceName
     * @throws IOException  IOException
     * @throws SQLException SQLException
     * @return 结果集数量
     */
	public static Long getSequenceNextValueUseTable(final SqlSession sqlSession,final String sequenceName) throws SQLException, IOException {
		Object object = getSingleObjectBySql(
                sqlSession,
				 "SELECT count(1) hasValue from sequence_table "
				 + " where sequence_Name = ?",
                new Object[]{sequenceName});
		Long l = NumberBaseOpt.castObjectToLong(object);
		if(l==0){
			getSingleObjectBySql(
                    sqlSession,
					"insert into sequence_table(sequence_Name,current_value)"
					+ " values(?,?)", new Object[]{sequenceName,1});
			return 1l;
		}else{
			getSingleObjectBySql(
                    sqlSession,
					"update sequence_table current_value = current_value + 1 "
					+ "where sequence_Name= ?", new Object[]{sequenceName});
			object = getSingleObjectBySql(
                    sqlSession,
					 "SELECT current_value from sequence_table "
					 + " where sequence_Name = ?",
	                 new Object[]{sequenceName});
		}
		return NumberBaseOpt.castObjectToLong(object);
	}
    
    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     *
     * @param sqlSession sqlSession
     * @param sequenceName sequenceName
     * @return sequence的下一个新值
     * @throws IOException IOException
     * @throws SQLException SQLException
     */
    public final static Long getNextLongSequence(SqlSession sqlSession,
            String sequenceName) throws SQLException, IOException {
        String dn = getDialectName();
        if(dn.indexOf("Oracle")>=0)//Oracle
            return ((BigDecimal) getSingleObjectBySql(
                    sqlSession, "SELECT " + sequenceName
                            + ".nextval from dual")).longValue();

        if(dn.indexOf("DB2")>=0)//DB2
            return ((BigDecimal) getSingleObjectBySql(
                    sqlSession, "SELECT nextval for "
                            + sequenceName + " from sysibm.sysdummy1"))
                    .longValue();        

        if(dn.indexOf("MySQL")>=0) // my sql
            return Long.valueOf(getSingleObjectBySql(
                    sqlSession, "SELECT sequence_nextval ('"+ sequenceName+"');")
                    .toString());
        
        // if("SQLServerDialect".endsWith(dn))
        // sql server 没有 sequence 所以用 GUID 代替
        try {
			return getSequenceNextValueUseTable(sqlSession,sequenceName);
		} catch (Exception e) {
			return null;
		}
    }

    /**
     * 获取sequence的下一个新值 只有oracle DB2支持
     *
     * @param sqlSession sqlSession
     * @param sequenceName sequenceName
     * @return sequence的下一个新值
     * @throws IOException IOException
     * @throws SQLException SQLException
     */
    public final static String getNextValueOfSequence(
            SqlSession sqlSession, String sequenceName) throws SQLException, IOException {
        Long sequence = getNextLongSequence(sqlSession, sequenceName);
        if (null == sequence) {
            return "";
        }
        return String.valueOf(sequence);
    }

 

    public final static Object getSingleObjectBySql(SqlSession sqlSession,
            final String sSql) throws SQLException, IOException {
    	return DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(),sSql);
    }

    public final static Object getSingleObjectBySql(SqlSession sqlSession,
            final String sSql, final Object paramObject)  throws SQLException, IOException {
    	return DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(),sSql,paramObject);
    }

    public final static Object getSingleObjectBySql(SqlSession sqlSession,
            final String sSql, final Object[] paramObjects)  throws SQLException, IOException {
    	return DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(),sSql,paramObjects);
    }

    public final static Object getSingleObjectBySql(SqlSession sqlSession,
            final String sSql, final String paramName, final Object paramObject)  throws SQLException, IOException {
    	return DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(),sSql,
    			QueryUtils.createSqlParamsMap(paramName,paramObject));
    }

    public final static Object getSingleObjectBySql(SqlSession sqlSession,
            final String sSql, final Map<String, Object> paramObjects) throws SQLException, IOException {
    	return DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(),sSql,paramObjects);
    }

    
    
    
    /**
     * 获取唯一的一个整形数据的SQL
     * @param sqlSession sqlSession
     * @param sSql sSql
     * @return long
     * @throws IOException IOException
     * @throws SQLException SQLException
     */
    public final static long getSingleIntBySql(SqlSession sqlSession,
            final String sSql) throws SQLException, IOException {
        Object obj = getSingleObjectBySql(sqlSession,sSql);
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
    
    public final static long getSingleIntBySql(SqlSession sqlSession,
            final String sSql, final Object paramObject) throws SQLException, IOException {
    	 Object obj = getSingleObjectBySql(sqlSession, sSql,  paramObject);
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
     * 获取唯一的一个字符串数据的SQL
     * @param sqlSession sqlSession
     * @param sSql sSql
     * @return 唯一的一个字符串数据的SQL
     * @throws IOException IOException
     * @throws SQLException SQLException
     */
    public final static String getSingleStringBySql(SqlSession sqlSession,
            final String sSql) throws SQLException, IOException {
        Object obj = getSingleObjectBySql(sqlSession,sSql);

        if (obj == null)
            return "";
        
        return String.valueOf(obj);
    }

   
    public final static boolean callProcedure(SqlSession sqlSession,
            String procName, Object... paramObjs) {        
    	//TODO 用mybatis的方式调用存
        return true;       
    }
    
    
    
    /**
     * 执行一个Oracle存储过程 返回一个数据集，这个数据集是一个out的游标,这个参数必需为存储过程的最后一个参数
     * @param conn conn
     * @param procName procName
     * @param paramObjs paramObjs
     * @return 一个数据集，这个数据集是一个out的游标,这个参数必需为存储过程的最后一个参数
     * @throws SQLException SQLException
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
        for (int i = 0; i < n; i++) {
            if (paramObjs[i] == null)
                stmt.setNull(i + 1, Types.NULL);
            else if (paramObjs[i] instanceof java.util.Date)
                stmt.setObject(i + 1, DatetimeOpt
                        .convertSqlDate((java.util.Date) paramObjs[i]));
            else
                stmt.setObject(i + 1, paramObjs[i]);
        }
        stmt.registerOutParameter(n + 1, ORACLE_TYPES_CURSOR);
        stmt.execute();
        ResultSet rs = (ResultSet) stmt.getObject(n + 1);
        return rs;      
    }
    
    public final static ResultSet callProcedureOutRS(SqlSession sqlSession,
            String procName, Object... paramObjs) throws SQLException {
    	return callProcedureOutRS(sqlSession.getConnection(), procName, paramObjs);
    }
    
    public final static Object callFunction(SqlSession sqlSession,
            String procName,int resultType, Object... paramObjs) {
    	//TODO 用mybatis的方式调用存
        return null;       
    }
 

    /**
     * 执行原生的SQL查询语句，返回的类型为 List
     * 
     * @param sSql sSql
     * @param sqlSession sqlSession
     * @return List
     * @throws IOException  IOException
     * @throws SQLException  SQLException
     */
    public final static List<Object[]> findObjectsBySql(SqlSession sqlSession, String sSql) throws SQLException, IOException {
    	return DatabaseAccess.findObjectsBySql(sqlSession.getConnection(), sSql);
    }

  
    /**
     * 用原生SQL 查询数据库 objectType 如果没有的话可以为NULL
     * @param <T> T
     * @param sqlSession sqlSession
     * @param ssql ssql
     * @param objectType 制定返回的结构类型
     * @return 用原生SQL 查询数据库 objectType 如果没有的话可以为NULL
     */
    public final static <T> List<T> findObjectsBySql(SqlSession sqlSession,
            String ssql, Class<T> objectType) {    	
    	 BaseDaoImpl mapper = new BaseDaoImpl(sqlSession);
         return  mapper.selectList(ssql,objectType);
    }
   

 
 
    /**
     * 获取 符合条件的记录数量 
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param sSql  sql语句，这个语句中的参数用？作为占位符
     * @param values 参数对应的变量
     * @return 符合条件的记录数量
     * @throws IOException  IOException
     * @throws SQLException SQLException
     */
    public final static int getSqlReturnObjectCounts(SqlSession sqlSession, String sSql,
            Object[] values) throws SQLException, IOException { 
    	Object obj = DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(), sSql,values);
        return Integer.valueOf(obj.toString());
    }
    
    /**
     * 获取 符合条件的记录数量 
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param sSql sql语句，这个语句必须用命名参数
     * @param values	命名参数对应的变量
     * @return 符合条件的记录数量
     * @throws IOException IOException
     * @throws SQLException SQLException
     */    
    public final static int getSqlReturnObjectCounts(SqlSession sqlSession, String sSql,
            Map<String, Object> values) throws SQLException, IOException {
    	Object obj = DatabaseAccess.getScalarObjectQuery(sqlSession.getConnection(), sSql,values);
    	return Integer.valueOf(obj.toString());
    }
    // -----------------------------------------------------------------------------------

    /**
     * 返回的类型为 List
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param values  命名参数对应的变量
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @param objectType 是一个Po类型，将查询结果封装成对象，要求查询返回的字段和Po中的字段定义一致。
     * @return List
     */
    public final static List<?> findObjectsBySql(SqlSession sqlSession, String ssql,
            Map<String, Object> values, PageDesc pageDesc,Class<?> objectType) {
        
    	String pageSql;
    	int startPos = 0;
        int maxSize = 0;
        if(pageDesc!=null){
            startPos = pageDesc.getRowStart();
            maxSize = pageDesc.getPageSize();
            pageSql = buildLimitQuerySQL(ssql,startPos,maxSize);
        }else{
        	pageSql = ssql;
        }
        
        BaseDaoImpl mapper = new BaseDaoImpl(sqlSession);
        List<?> listT = null;
        if(objectType==null){
        	listT = mapper.selectList(pageSql,values);
        }else{
        	listT = mapper.selectList(pageSql,values ,objectType);
        }
        
        if(listT!=null && pageDesc!=null){
        	if(maxSize>0){
        		List<Map<String, Object>> rowSumMap =
        				mapper.selectList(QueryUtils.buildGetCountSQL(ssql),values);
        		if( rowSumMap !=null && rowSumMap.size()>0)
        			pageDesc.setTotalRows(Integer.valueOf(rowSumMap.get(0).get("rowcount").toString()));
        		else
        			pageDesc.setTotalRows(listT.size());
        	}else
        		pageDesc.setTotalRows(listT.size());
        }
        return listT;
    }
    

    /**
     * 返回的类型为 List
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param values  命名参数对应的变量
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return List
     */
    public final static List<?> findObjectsBySql(SqlSession sqlSession, String ssql,
            Map<String, Object> values, PageDesc pageDesc) {        
        return findObjectsBySql( sqlSession,  ssql,
                 values,  pageDesc,null);       
    }    
    
    public final static List<?> findObjectsBySql(SqlSession sqlSession, String shql,
            Map<String, Object> values) {
        return findObjectsBySql(sqlSession,  shql,
                values , new PageDesc(-1,-1),null);
    }   


     
    /**
     * 
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param ssql sql语句，这个语句必须用命名参数
     * @param fieldnames 字段名称作为json中Map的key，没有这个参数的函数会自动从sql语句中解析字段名作为json中map的
     * @param values 命名参数对应的变量
     * @param pageDesc 获取记录其实位置 和 maxSize 一起完成分页操作
     *               返回的最大记录数
     * @return JSONArray实现了List接口，JSONObject实现了Map接口。所以可以直接转换为List
     */
    public final static JSONArray findObjectsAsJSonBySql(SqlSession sqlSession, String ssql, 
            String [] fieldnames, Map<String,Object> values, PageDesc pageDesc) {
    	
    	List<?> dataList = findObjectsBySql(sqlSession,ssql,values,pageDesc,null);
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
    
   
    
    public final static JSONArray findObjectsAsJSonBySql(SqlSession sqlSession, String ssql, String [] fieldNames) {
        return findObjectsAsJSonBySql(sqlSession, ssql,fieldNames,(Map<String,Object>) null , new PageDesc(-1,-1));
    }
    
 
    
    public final static JSONArray findObjectsAsJSonBySql(SqlSession sqlSession, String shql, 
            Map<String,Object> values, PageDesc pageDesc) {
        return findObjectsAsJSonBySql(sqlSession,  shql, null, 
                values,  pageDesc);
    }    
  
    
    public final static JSONArray findObjectsAsJSonBySql(SqlSession sqlSession, String ssql) {
        return findObjectsAsJSonBySql(sqlSession, ssql,null,(Map<String,Object>) null ,  new PageDesc(-1,-1));
    }
    
    /**
     * 刷新Session缓存中的数据进行强制提交
     * 
     * @param dbSession HibernateDaoSupport
     *
     */
    public final static void flush(SqlSession dbSession) {
        dbSession.clearCache();
    }
}
