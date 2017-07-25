package com.centit.framework.staticsystem.service.impl;

import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.core.common.ResponseJSON;
import com.centit.framework.staticsystem.po.DatabaseInfo;
import com.centit.framework.staticsystem.po.OsInfo;
import com.centit.framework.staticsystem.po.UserAccessToken;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class IPClientIntegrationEnvironment implements IntegrationEnvironment {
    private String platServerUrl;

    public IPClientIntegrationEnvironment() {

    }


    public void setPlatServerUrl(String platServerUrl) {
        this.platServerUrl = platServerUrl;
    }


    @Override
    @CacheEvict(value ="IPEnvironmen",allEntries = true)
    public boolean reloadIPEnvironmen() {
        return true;
    }

    @Override
    public OsInfo getOsInfo(String osId) {
        for(OsInfo oi : listOsInfos()){
            if(StringUtils.equals(oi.getOsId(),osId))
                return oi;
        }
        return null;
    }

    @Override
    public DatabaseInfo getDatabaseInfo(String databaseCode) {
        for(DatabaseInfo di : listDatabaseInfo()){
            if(StringUtils.equals(di.getDatabaseCode(),databaseCode))
                return di;
        }
        return null;
    }

    @Override
    @Cacheable(value="IPEnvironment",key="'OsInfo'")
    public List<OsInfo> listOsInfos() {
        ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                platServerUrl + "/ipenvironment/osinfo");
        if(resJson==null)
            return null;
        return resJson.getDataAsArray(OsInfo.class);
    }

    @Override
    @Cacheable(value="IPEnvironment",key="'DatabaseInfo'")
    public List<DatabaseInfo> listDatabaseInfo() {
        ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                platServerUrl + "/ipenvironment/databaseinfo");
        if(resJson==null)
            return null;
        return resJson.getDataAsArray(DatabaseInfo.class);
    }

    @Override
    public String checkAccessToken(String tokenId, String accessKey) {
        ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                platServerUrl + "/ipenvironment/userToken/"+tokenId);
        UserAccessToken at = resJson.getDataAsObject(UserAccessToken.class);
        if(at==null)
            return null;
        if(StringUtils.equals(at.getTokenId(),tokenId)){
            if( StringUtils.equals(at.getIsValid(),"T")
                    && StringUtils.equals(at.getSecretAccessKey(), accessKey) )
                return at.getUserCode();
            else
                return null;
        }
        return null;
    }

}
