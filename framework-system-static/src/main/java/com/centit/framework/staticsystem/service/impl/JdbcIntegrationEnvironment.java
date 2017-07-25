package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;
import com.centit.support.database.DBConnect;
import com.centit.support.database.DataSourceDescription;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.DbcpConnectPools;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class JdbcIntegrationEnvironment implements IntegrationEnvironment {

    private List<OsInfo> osInfos;
    private List<DatabaseInfo> databaseInfos;
    private List<UserAccessToken> accessTokens;

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
                //conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean reloadIPEnvironmen(){

        try {
            reloadIPEnvironmenFromJdbc();
            return true;
        } catch (IOException | SQLException | DocumentException e) {
            osInfos = new ArrayList<>();
            databaseInfos = new ArrayList<>();
            accessTokens = new ArrayList<>();
            e.printStackTrace();
            return false;
        }
    }

    private <T> List<T> jsonArrayToObjectList(JSONArray jsonArray, Class<T> clazz) {
        if(jsonArray==null)
            return new ArrayList<>();
        return jsonArray.toJavaList(clazz);
    }


    public boolean reloadIPEnvironmenFromJdbc() throws IOException, DocumentException, SQLException {

        CodeRepositoryUtil.loadExtendedSqlMap("ExtendedSqlMap.xml");

        try(DBConnect conn = getConnection()) {
            JSONArray userJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_OS"));
            osInfos = jsonArrayToObjectList(userJSONArray, OsInfo.class);
            JSONArray optInfoJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_DATABASE"));
            databaseInfos = jsonArrayToObjectList(optInfoJSONArray,  DatabaseInfo.class);
            JSONArray optMethodsJSONArray = DatabaseAccess.findObjectsAsJSON(conn,
                    CodeRepositoryUtil.getExtendedSql("LIST_ALL_ACCESSTOKEN"));
            accessTokens = jsonArrayToObjectList(optMethodsJSONArray,  UserAccessToken.class);
        }

        return true;
    }

    @Override
    public OsInfo getOsInfo(String osId) {
        if(osInfos==null)
            return null;
        for(OsInfo oi : osInfos){
            if(StringUtils.equals(oi.getOsId(),osId))
                return oi;
        }
        return null;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        if(databaseInfos==null)
            return null;
        for(DatabaseInfo di : databaseInfos){
            if(StringUtils.equals(di.getDatabaseCode(),databaseCode))
                return di;
        }
        return null;
    }

    @Override
    public List<OsInfo> listOsInfos() {
        return osInfos;
    }

    @Override
    public List<DatabaseInfo> listDatabaseInfo() {
        return databaseInfos;
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        if(accessTokens==null)
            return null;
        for(UserAccessToken at : accessTokens){
            if(StringUtils.equals(at.getTokenId(),tokenId)){
                if( StringUtils.equals(at.getIsValid(),"T")
                        && StringUtils.equals(at.getSecretAccessKey(), accessKey) )
                    return at.getUserCode();
                else
                    return null;
            }
        }
        return null;
    }

}
