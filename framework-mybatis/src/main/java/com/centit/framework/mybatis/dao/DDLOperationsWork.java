package com.centit.framework.mybatis.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.centit.framework.common.SysParametersUtils;
import com.centit.support.database.ddl.DB2DDLOperations;
import com.centit.support.database.ddl.DDLOperations;
import com.centit.support.database.ddl.MySqlDDLOperations;
import com.centit.support.database.ddl.OracleDDLOperations;
import com.centit.support.database.ddl.SqlSvrDDLOperations;
import com.centit.support.database.metadata.TableField;
import com.centit.support.database.metadata.TableInfo;

public class DDLOperationsWork implements DDLOperations {

	private SqlSession sqlSession;

	public DDLOperationsWork(){
		
	}	

	public DDLOperationsWork(SqlSession sqlSession){

		this.sqlSession = sqlSession;
	}
	
	public void setBaseDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	/**
	 * 没有传入连接信息，只能用于 语句的生产，不能执行DDL操作
	 * @return  语句的生产
	 */
	public static DDLOperations createDDLOpt(){
		String dialectName = SysParametersUtils.getStringValue("jdbc.dialect");
		if (dialectName.indexOf("Oracle")>=0)
			return new OracleDDLOperations();
		if (dialectName.indexOf("DB2")>=0)
			return new DB2DDLOperations();
		if (dialectName.indexOf("SQLServer")>=0)
			return new SqlSvrDDLOperations();
		if (dialectName.indexOf("MySQL")>=0)
			return new MySqlDDLOperations();
		return new OracleDDLOperations();
	}
	
	/**
	 * 可以执行DDL操作	
	 * @param connection Connection
	 * @return 可以执行DDL操作
	 */
	public static DDLOperations createDDLOpt(Connection connection){
		String dialectName = SysParametersUtils.getStringValue("jdbc.dialect");
		if (dialectName.indexOf("Oracle")>=0)
			return new OracleDDLOperations(connection);
		if (dialectName.indexOf("DB2")>=0)
			return new DB2DDLOperations(connection);
		if (dialectName.indexOf("SQLServer")>=0)
			return new SqlSvrDDLOperations(connection);
		if (dialectName.indexOf("MySQL")>=0)
			return new MySqlDDLOperations(connection);
		return new OracleDDLOperations(connection);
	}
	
	private DDLOperations operations = null;
	public DDLOperations getDDLOperations(){
		if(operations==null)
			operations = createDDLOpt(this.sqlSession.getConnection());		
		return operations;
	}
	
	
	
	@Override
	public void createSequence(final String sequenceName) throws SQLException {
		getDDLOperations().createSequence(sequenceName);	
	}
	
	@Override
	public void createTable(final TableInfo tableInfo) throws SQLException {
		getDDLOperations().createTable(tableInfo);				
	
	}

	@Override
	public void dropTable(final String tableCode) throws SQLException {
		getDDLOperations().dropTable(tableCode);		
	}

	@Override
	public void addColumn(final String tableCode, final TableField column) throws SQLException {
		getDDLOperations().addColumn(tableCode,column);
	}

	@Override
	public void modifyColumn(final String tableCode, final TableField column) throws SQLException {
		getDDLOperations().modifyColumn(tableCode,column);
	}

	@Override
	public void dropColumn(final String tableCode, final String columnCode) throws SQLException {
		getDDLOperations().dropColumn(tableCode,columnCode);
	}

	@Override
	public void renameColumn(final String tableCode, final String columnCode, final TableField column) throws SQLException {
		getDDLOperations().renameColumn(tableCode,columnCode,column);
	}

	@Override
	public void reconfigurationColumn(final String tableCode, final String columnCode,
			final TableField column) throws SQLException {
		getDDLOperations().
			reconfigurationColumn(tableCode,columnCode,column);	
	}


	@Override
	public String makeCreateSequenceSql(String sequenceName) {
		return createDDLOpt().makeCreateSequenceSql(sequenceName);
	}
	
	@Override
	public String makeCreateTableSql(TableInfo tableInfo) {
		return createDDLOpt().makeCreateTableSql(tableInfo);
	}

	@Override
	public String makeDropTableSql(String tableCode) {
		return createDDLOpt().makeDropTableSql(tableCode);
	}

	@Override
	public String makeAddColumnSql(String tableCode, TableField column) {
		return createDDLOpt().makeAddColumnSql(tableCode,column);
	}

	@Override
	public String makeModifyColumnSql(String tableCode, TableField column) {
		return createDDLOpt().makeModifyColumnSql(tableCode,column);
	}

	@Override
	public String makeDropColumnSql(String tableCode, String columnCode) {
		return createDDLOpt().makeDropColumnSql(tableCode,columnCode);
	}

	@Override
	public String makeRenameColumnSql(String tableCode, String columnCode, TableField column) {
		return createDDLOpt().makeRenameColumnSql(tableCode,columnCode,column);
	}

	@Override
	public List<String> makeReconfigurationColumnSqls(String tableCode, String columnCode, TableField column) {
		return createDDLOpt().makeReconfigurationColumnSqls(tableCode,columnCode,column);
	}
}
