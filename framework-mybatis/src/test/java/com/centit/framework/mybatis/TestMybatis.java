package com.centit.framework.mybatis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.centit.framework.mybatis.dao.BaseDaoImpl;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.QueryUtils;

public class TestMybatis {

	public static SqlSessionFactory createSqlSessionFactory(){
		PooledDataSource dataSource =new PooledDataSource();
		dataSource.setDriver("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@192.168.131.81:1521:orcl");
		dataSource.setUsername("workflow");
		dataSource.setPassword("workflow");
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("develpment",transactionFactory,dataSource);
		
		Configuration  configuration = new Configuration(environment);
		
		configuration.addMapper(UserInfoDao.class);
		
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);  
		
		return sqlSessionFactory;
	}
	
	public static void main(String[] args) {
		testNativeSql();
	}
	
	
	/**
	 *  public abstract class SqlSessionDaoSupport extends DaoSupport {
	 * 
	    getSqlSession().getConfiguration().getEnvironment().getDataSource().getConnection()
	   
	    SqlSessionTemplate st = (SqlSessionTemplate) getSqlSession();

		Connection connection = SqlSessionUtils.getSqlSession(
                st.getSqlSessionFactory(), st.getExecutorType(),
                st.getPersistenceExceptionTranslator()).getConnection();
	 */
	
	public static void testNativeSql() {
		SqlSessionFactory sqlSessionFactory = createSqlSessionFactory();  
	   
        //定义 sqlSession  
        SqlSession sqlSession = null;  
        try {  
            //用sqlSessionFactory创建sqlSession  
            sqlSession = sqlSessionFactory.openSession();           
     
        	Connection connection =sqlSession.getConnection();        	
        	
        	List<Object[]> users = DatabaseAccess.findObjectsByNamedSql(connection,
            "select userCode,userPin,userName,isValid,userType,loginName,englishName,userDesc "
			+ " from f_userinfo where userCode like :uc and loginName like :ln",
			QueryUtils.createSqlParamsMap("uc","U00%","ln","%o%","pp","notUse"));
            
            //打印信息  
            System.out.println(users.size());  
        } catch (SQLException |IOException e) {
			e.printStackTrace();
		} finally {  
            //使用完后要记得关闭sqlSession资源  
            if (sqlSession != null) {  
                sqlSession.close();  
            }  
            System.out.println("done!");  
        }  
	}
	
	
	public static void testSqlMapper() {
		SqlSessionFactory sqlSessionFactory = createSqlSessionFactory();  
	   
        //定义 sqlSession  
        SqlSession sqlSession = null;  
        try {  
            //用sqlSessionFactory创建sqlSession  
            sqlSession = sqlSessionFactory.openSession();  
            
            BaseDaoImpl mapper = new BaseDaoImpl(sqlSession);
            
            /*List<ParameterMapping> params = new ArrayList<>();
            params.add(new ParameterMapping.Builder(sqlSession.getConfiguration(),
            		"uc", String.class).expression("U00%").build());
            params.add(new ParameterMapping.Builder(sqlSession.getConfiguration(),
            		"ln", String.class).expression("%o%").build());
            */
            
            List<UserInfo> users = mapper.selectList(
            "select userCode,userPin,userName,isValid,userType,loginName,englishName,userDesc "
			+ " from f_userinfo where userCode like #{uc} and loginName like #{ln}",
			QueryUtils.createSqlParamsMap("uc","U00%","ln","%o%","pp","notUse"),
			UserInfo.class);
            
            //打印信息  
            System.out.println(users.size());  
        } finally {  
            //使用完后要记得关闭sqlSession资源  
            if (sqlSession != null) {  
                sqlSession.close();  
            }  
            System.out.println("done!");  
        }  
	}
	
	public static void testSelectUser() {
		SqlSessionFactory sqlSessionFactory = createSqlSessionFactory();  
		   
        //定义 sqlSession  
        SqlSession sqlSession = null;  
        try {  
            //用sqlSessionFactory创建sqlSession  
            sqlSession = sqlSessionFactory.openSession();  
            //获取Mapper  
            UserInfoDao userMapper = sqlSession.getMapper(UserInfoDao.class);  
            //执行Mapper接口方法.  
            UserInfo user = userMapper.getUser("U0001586");
            //打印信息  
            System.out.println(user.getUserName());  
        } finally {  
            //使用完后要记得关闭sqlSession资源  
            if (sqlSession != null) {  
                sqlSession.close();  
            }  
        }  
	}

}
