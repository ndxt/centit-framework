package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.framework.model.basedata.*;
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
import java.util.List;

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
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_USER"));
            List<UserInfo> userinfos = jsonArrayToObjectList(userJSONArray, UserInfo.class);
            CodeRepositoryCache.userInfoRepo.setFreshtDate(userinfos);

            JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTINFO"));
            List<OptInfo> optinfos = jsonArrayToObjectList(optInfoJSONArray,  OptInfo.class);
            CodeRepositoryCache.optInfoRepo.setFreshtDate(optinfos);

            JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTMETHOD"));
            List<OptMethod> optmethods = jsonArrayToObjectList(optMethodsJSONArray,  OptMethod.class);
            CodeRepositoryCache.optMethodRepo.setFreshtDate(optmethods);

            JSONArray roleInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEINFO"));
            List<RoleInfo> roleinfos = jsonArrayToObjectList(roleInfoJSONArray,  RoleInfo.class);
            CodeRepositoryCache.roleInfoRepo.setFreshtDate(roleinfos);

            JSONArray rolePowerJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEPOWER"));
            List<RolePower>  rolepowers = jsonArrayToObjectList(rolePowerJSONArray,  RolePower.class);
            CodeRepositoryCache.rolePowerRepo.setFreshtDate(rolepowers);

            JSONArray userRoleJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERROLE"));
            List<UserRole> userroles = jsonArrayToObjectList(userRoleJSONArray, UserRole.class);
            allUserRoleRepo.setFreshtDate(userroles);

            JSONArray UnitInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_UNITINFO"));
            List<UnitInfo> unitinfos = jsonArrayToObjectList(UnitInfoJSONArray, UnitInfo.class);
            CodeRepositoryCache.unitInfoRepo.setFreshtDate(unitinfos);

            JSONArray userUnitJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERUNIT"));
            List<UserUnit> userunits = jsonArrayToObjectList(userUnitJSONArray, UserUnit.class);
            CodeRepositoryCache.userUnitRepo.setFreshtDate(userunits);

            JSONArray dataCatalogsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_DATACATALOG"));
            List<DataCatalog> datacatalogs = jsonArrayToObjectList(dataCatalogsJSONArray, DataCatalog.class);
            CodeRepositoryCache.catalogRepo.setFreshtDate(datacatalogs);

            JSONArray dataDictionaryJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_DICTIONARY"));
            List<DataDictionary> datadictionaies = jsonArrayToObjectList(dataDictionaryJSONArray, DataDictionary.class);
            allDictionaryRepo.setFreshtDate(datadictionaies);
        }

    }
    /**
     * 刷新数据字典
     */
    protected void reloadDictionary() {
        try {
            loadConfigFromJdbc();
        } catch (IOException | SQLException | DocumentException e) {
           logger.error(e.getLocalizedMessage());
        }
        organizeDictionaryData();
    }

    /**
     * 获取用户所有角色
     *
     * @param unitCode 机构代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return null;
    }


    /**
     * 修改用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        UserInfo ui= (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedObject().get(userCode);
        if(ui==null)
            return;
        String userNewPassword = passwordEncoder.createPassword(userPassword, userCode);
        try(Connection conn = getConnection()) {
            DatabaseAccess.doExecuteSql(conn,
                    CodeRepositoryUtil.getExtendedSql("UPDATE_USER_PASSWORD"),
                    new Object []{ userNewPassword, userCode });
            conn.commit();
        }catch (Exception e){
            //conn.rollback();
        }
    }

    /**
     * 获取所有用户，
     *
     * @return List 所有用户
     */
    @Override
    public List<? extends IUserInfo> listAllUsers() {
        //ExtendedQueryPool.loadResourceExtendedSqlMap(dataSource.getDbType());
        try(Connection conn = getConnection()) {
            JSONArray userJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_USER"));
           return jsonArrayToObjectList(userJSONArray, UserInfo.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取所有机构
     *
     * @return List 所有机构
     */
    @Override
    public List<? extends IUnitInfo> listAllUnits() {
        try(Connection conn = getConnection()) {
            JSONArray UnitInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_UNITINFO"));
            return jsonArrayToObjectList(UnitInfoJSONArray, UnitInfo.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取所有用户和机构关联关系
     *
     * @return List 所有用户和机构关联关系
     */
    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        try(Connection conn = getConnection()) {
            JSONArray userUnitJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERUNIT"));
            return jsonArrayToObjectList(userUnitJSONArray, UserUnit.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo() {
        try(Connection conn = getConnection()) {
            JSONArray roleInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEINFO"));
            return jsonArrayToObjectList(roleInfoJSONArray,  RoleInfo.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取所有角色和权限对应关系
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRolePower> listAllRolePower() {
        try(Connection conn = getConnection()) {
            JSONArray rolePowerJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_ROLEPOWER"));
            return jsonArrayToObjectList(rolePowerJSONArray,  RolePower.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo() {
        try(Connection conn = getConnection()) {
            JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTINFO"));
            return jsonArrayToObjectList(optInfoJSONArray,  OptInfo.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取操作方法信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IOptMethod> listAllOptMethod() {
        try(Connection conn = getConnection()) {
            JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_OPTMETHOD"));
            return jsonArrayToObjectList(optMethodsJSONArray,  OptMethod.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @return List 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataCatalog> listAllDataCatalogs() {
        try(Connection conn = getConnection()) {
            JSONArray dataCatalogsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_DATACATALOG"));
            return jsonArrayToObjectList(dataCatalogsJSONArray, DataCatalog.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected List<DataDictionary> listAllDataDictionary() {
        try(Connection conn = getConnection()) {
            JSONArray dataDictionaryJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_DICTIONARY"));
            return jsonArrayToObjectList(dataDictionaryJSONArray, DataDictionary.class);

        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected List<UserRole> listAllUserRole() {
        try(Connection conn = getConnection()) {
            JSONArray userRoleJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                CodeRepositoryUtil.getExtendedSql("LIST_ALL_USERROLE"));
            return jsonArrayToObjectList(userRoleJSONArray, UserRole.class);
        } catch (SQLException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
