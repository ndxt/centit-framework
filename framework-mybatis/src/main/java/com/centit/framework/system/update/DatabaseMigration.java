package com.centit.framework.system.update;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

public class DatabaseMigration {
	private String dbSchemas[];
	private String versionTable;
	private String sqlDirectory;
	private String javaPackage;
	
	public void setDbSchemas(String[] dbSchemas) {
		this.dbSchemas = dbSchemas;
	}

	public void setVersionTable(String versionTable) {
		this.versionTable = versionTable;
	}

	public void setSqlDirectory(String sqlDirectory) {
		this.sqlDirectory = sqlDirectory;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DatabaseMigration(){
		this.versionTable = "schema_version";
		this.sqlDirectory = "db/migration";
		this.javaPackage  = "com.centit.framework.system.update";
	}
	
	public void migrate() {
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);

		flyway.setSchemas(dbSchemas); // 设置接受flyway进行版本管理的多个数据库
		flyway.setTable(versionTable); // 设置存放flyway metadata数据的表名
		flyway.setLocations(sqlDirectory, javaPackage); // 设置flyway扫描sql升级脚本、java升级脚本的目录路径或包路径

		flyway.setBaselineOnMigrate(true);
		flyway.setEncoding("GBK"); // 设置sql脚本文件的编码 GB2312
		flyway.setValidateOnMigrate(true); // 设置执行migrate操作之前的validation行为

		flyway.migrate();
	}
}
