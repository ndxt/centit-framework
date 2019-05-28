package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.framework.model.basedata.IOptDataScope;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.DbcpConnectPools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcPlatformEnvironment extends AbstractStaticPlatformEnvironment {

    private static Log logger = LogFactory.getLog(JdbcPlatformEnvironment.class);

    private DataSourceDescription dataSource;

    private Connection getConnection() throws SQLException {
        return DbcpConnectPools.getDbcpConnect(dataSource);
    }

    public void setDataBaseConnectInfo(String connectURI, String username, String pswd){
        this.dataSource = new DataSourceDescription( connectURI,  username,  pswd);
    }

    public void close(Connection conn){
        DbcpConnectPools.closeConnect(conn);
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

    private void loadConfigFromJdbc() throws SQLException, IOException,DocumentException {

        ExtendedQueryPool.loadResourceExtendedSqlMap(dataSource.getDbType());

        try(Connection conn = getConnection()) {
            JSONArray userJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_USER"));
            List<UserInfo> userinfos = jsonArrayToObjectList(userJSONArray, UserInfo.class);
            CodeRepositoryCache.userInfoRepo.setFreshtDate(userinfos);

            JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_OPTINFO"));
            List<OptInfo> optinfos = jsonArrayToObjectList(optInfoJSONArray,  OptInfo.class);
            CodeRepositoryCache.optInfoRepo.setFreshtDate(optinfos);

            JSONArray optDataScopesJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_OPTDATASCOPE"));
            optDataScopes = jsonArrayToObjectList(optDataScopesJSONArray, OptDataScope.class);

            JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_OPTMETHOD"));
            List<OptMethod> optmethods = jsonArrayToObjectList(optMethodsJSONArray,  OptMethod.class);
            CodeRepositoryCache.optMethodRepo.setFreshtDate(optmethods);

            JSONArray roleInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_ROLEINFO"));
            List<RoleInfo> roleinfos = jsonArrayToObjectList(roleInfoJSONArray,  RoleInfo.class);
            CodeRepositoryCache.roleInfoRepo.setFreshtDate(roleinfos);

            JSONArray rolePowerJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_ROLEPOWER"));
            List<RolePower>  rolepowers = jsonArrayToObjectList(rolePowerJSONArray,  RolePower.class);
            CodeRepositoryCache.rolePowerRepo.setFreshtDate(rolepowers);

            JSONArray userRoleJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_USERROLE"));
            List<UserRole> userroles = jsonArrayToObjectList(userRoleJSONArray, UserRole.class);
            allUserRoleRepo.setFreshtDate(userroles);

            JSONArray UnitInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_UNITINFO"));
            List<UnitInfo> unitinfos = jsonArrayToObjectList(UnitInfoJSONArray, UnitInfo.class);
            CodeRepositoryCache.unitInfoRepo.setFreshtDate(unitinfos);

            JSONArray userUnitJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_USERUNIT"));
            List<UserUnit> userunits = jsonArrayToObjectList(userUnitJSONArray, UserUnit.class);
            CodeRepositoryCache.userUnitRepo.setFreshtDate(userunits);

            JSONArray dataCatalogsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_DATACATALOG"));
            List<DataCatalog> datacatalogs = jsonArrayToObjectList(dataCatalogsJSONArray, DataCatalog.class);
            CodeRepositoryCache.catalogRepo.setFreshtDate(datacatalogs);

            JSONArray dataDictionaryJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                ExtendedQueryPool.getExtendedSql("LIST_ALL_DICTIONARY"));
            List<DataDictionary> datadictionaies = jsonArrayToObjectList(dataDictionaryJSONArray, DataDictionary.class);
            allDictionaryRepo.setFreshtDate(datadictionaies);
        }

    }
    /**
     * 刷新数据字典
     */
    protected synchronized void reloadPlatformData() {
        try {
            CodeRepositoryCache.evictAllCache();
            loadConfigFromJdbc();
        } catch (IOException | SQLException | DocumentException e) {
           logger.error(e.getLocalizedMessage());
        }
        organizePlatformData();
    }

    /**
     * 修改用户密码
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        UserInfo ui= (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedTarget().get(userCode);
        if(ui==null)
            return;
        String userNewPassword = passwordEncoder.createPassword(userPassword, userCode);
        try(Connection conn = getConnection()) {
            DatabaseAccess.doExecuteSql(conn,
                ExtendedQueryPool.getExtendedSql("UPDATE_USER_PASSWORD"),
                    new Object []{ userNewPassword, userCode });
            conn.commit();
        }catch (Exception e){
            //conn.rollback();
        }
    }
}
