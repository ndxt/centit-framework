package com.centit.framework.staticsystem.service.impl;

import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.staticsystem.po.DatabaseInfo;
import com.centit.framework.staticsystem.po.OsInfo;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;

import java.util.List;
import java.util.Map;

/**
 * Created by codefan on 16-12-16.
 */
public class IntegrationEnvironmentProxy implements IntegrationEnvironment
{
    private List<IntegrationEnvironment> evrnMangers;
    public IntegrationEnvironmentProxy(){

    }

    public void setEvrnMangers(List<IntegrationEnvironment> evrnMangers) {
        this.evrnMangers = evrnMangers;
    }


    /**
     * 刷新集成环境相关信息
     * 包括：业务系统、数据库信息
     *
     * @return
     */
    @Override
    public boolean reloadIPEnvironmen() {
        boolean res=true;
        for(IntegrationEnvironment evrnManger:evrnMangers){
            if(!evrnManger.reloadIPEnvironmen())
                res = false;
        }
        return res;
    }

    /**
     * 获取框架中注册的业务系统
     *
     * @param osId
     * @return
     */
    @Override
    public OsInfo getOsInfo(String osId) {
        for(IntegrationEnvironment evrnManger:evrnMangers){
            OsInfo osi = evrnManger.getOsInfo(osId);
            if(osi!=null)
                return osi;
        }
        return null;
    }

    /**
     * 获取框架中注册的数据库
     *
     * @param databaseCode
     * @return
     */
    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        for(IntegrationEnvironment evrnManger:evrnMangers){
            DatabaseInfo dbi = evrnManger.getDatabaseInfo(databaseCode);
            if(dbi!=null)
                return dbi;
        }
        return null;
    }

    /**
     * 获取所有注册的业务系统
     *
     * @return
     */
    @Override
    public List<OsInfo> listOsInfos() {
        for(IntegrationEnvironment evrnManger:evrnMangers){
            List<OsInfo> oss = evrnManger.listOsInfos();
            if(oss!=null)
                return oss;
        }
        return null;
    }

    /**
     * 获取所有注册的数据库
     *
     * @return
     */
    @Override
    public List<DatabaseInfo> listDatabaseInfo() {
        for(IntegrationEnvironment evrnManger:evrnMangers){
            List<DatabaseInfo> dbs = evrnManger.listDatabaseInfo();
            if(dbs!=null)
                return dbs;
        }
        return null;
    }

    /**
     * 检验用户的 访问 令牌合法性
     *
     * @param tokenId
     * @param accessKey
     * @return 合法返回对应的用户，不合法返回null
     */
    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        for(IntegrationEnvironment evrnManger:evrnMangers){
            String skey = evrnManger.checkAccessToken(tokenId,accessKey);
            if(skey!=null)
                return skey;
        }
        return null;
    }

}
