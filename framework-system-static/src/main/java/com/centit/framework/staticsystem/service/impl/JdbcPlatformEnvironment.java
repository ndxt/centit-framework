package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.database.DBConnect;
import com.centit.support.database.DataSourceDescription;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.DbcpConnectPools;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPlatformEnvironment extends AbstractStaticPlatformEnvironment
	implements PlatformEnvironment {

	private DataSourceDescription dataSource;

	private DBConnect getConnection() throws SQLException {
		return DbcpConnectPools.getDbcpConnect(dataSource);
	}

	public void setDataBaseConnectInfo(String connectURI, String username, String pswd){
		this.dataSource = new DataSourceDescription( connectURI,  username,  pswd);
	}

	public void close(DBConnect conn){
		if(conn!=null){
			try {
				conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		conn = null;
	}


	public void init(){

		reloadDictionary();
		reloadSecurityMetadata();
	}

	private <T> List<T> jsonArrayToObjectList(JSONArray jsonArray, Class<T> clazz) {
		if(jsonArray==null)
			return new ArrayList<>();
		/*List<T> resList =  new ArrayList<>(jsonArray.size()+1);
		for(int i=0;i<jsonArray.size();i++){
			resList.add( jsonArray.getObject(i,clazz));
		}*/
		return jsonArray.toJavaList(clazz);
	}

	public void loadConfigFromJdbc() throws SQLException, IOException,DocumentException {

		CodeRepositoryUtil.loadExtendedSqlMap("ExtendedSqlMap.xml");

		try(DBConnect conn = getConnection()) {
			JSONArray userJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_USER"));
			userinfos = jsonArrayToObjectList(userJSONArray, UserInfo.class);
			JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTINFO"));
			optinfos = jsonArrayToObjectList(optInfoJSONArray,  OptInfo.class);
			JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTMETHOD"));
			optmethods = jsonArrayToObjectList(optMethodsJSONArray,  OptMethod.class);
			JSONArray roleInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEINFO"));
			roleinfos = jsonArrayToObjectList(roleInfoJSONArray,  RoleInfo.class);
			JSONArray rolePowerJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEPOWER"));
			rolepowers = jsonArrayToObjectList(rolePowerJSONArray,  RolePower.class);
			JSONArray userRoleJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERROLE"));
			userroles = jsonArrayToObjectList(userRoleJSONArray, UserRole.class);
			JSONArray UnitInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_UNITINFO"));
			unitinfos = jsonArrayToObjectList(UnitInfoJSONArray, UnitInfo.class);
			JSONArray userUnitJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERUNIT"));
			userunits = jsonArrayToObjectList(userUnitJSONArray, UserUnit.class);
			JSONArray dataCatalogsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_DATACATALOG"));
			datacatalogs = jsonArrayToObjectList(dataCatalogsJSONArray, DataCatalog.class);
			JSONArray dataDictionaryJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
					CodeRepositoryUtil.getExtendedSql("LIST_ALL_DICTIONARY"));
			datadictionaies = jsonArrayToObjectList(dataDictionaryJSONArray, DataDictionary.class);
		}

	}
	/**
	 * 刷新数据字典
	 *
	 * @return  boolean 刷新数据字典
	 */
	@Override
	public boolean reloadDictionary() {
		try {
			loadConfigFromJdbc();
		} catch (IOException | SQLException | DocumentException e) {
			userinfos = new ArrayList<>();
			optinfos = new ArrayList<>();
			optmethods = new ArrayList<>();
			roleinfos = new ArrayList<>();
			rolepowers = new ArrayList<>();
			userroles = new ArrayList<>();
			unitinfos = new ArrayList<>();
			userunits = new ArrayList<>();
			datacatalogs = new ArrayList<>();
			datadictionaies = new ArrayList<>();
			e.printStackTrace();
		}
		organizeDictionaryData();

		return true;
	}


	/**
	 * 修改用户密码
	 *
	 * @param userCode userCode
	 * @param userPassword userPassword
	 */
	@Override
	public void changeUserPassword(String userCode, String userPassword) {
		UserInfo ui= getUserInfoByUserCode(userCode);
		if(ui==null)
			return;
		String userNewPassword = passwordEncoder.createPassword(userPassword, userCode);
		try(DBConnect conn = getConnection()) {
			DatabaseAccess.doExecuteSql(conn,
					CodeRepositoryUtil.getExtendedSql("UPDATE_USER_PASSWORD"),
					new Object []{ userNewPassword, userCode });
			conn.commit();
		}catch (Exception e){
			//conn.rollback();
		}
	}

}
