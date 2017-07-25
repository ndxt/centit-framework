package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.staticsystem.po.DatabaseInfo;
import com.centit.framework.staticsystem.po.OsInfo;
import com.centit.framework.staticsystem.po.UserAccessToken;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
public class StaticIntegrationEnvironment implements IntegrationEnvironment {
    private List<OsInfo> osInfos;
    private List<DatabaseInfo> databaseInfos;
    private List<UserAccessToken> accessTokens;

    @Override
    public boolean reloadIPEnvironmen() {
        try {
            String jsonFile = SysParametersUtils.getConfigHome()+"/ip_environmen.json";
            if(!FileSystemOpt.existFile(jsonFile)){
                FileSystemOpt.createDirect(  SysParametersUtils.getConfigHome());
                FileSystemOpt.fileCopy(
                        new ClassPathResource("ip_environmen.json").getFile() ,
                        new File(jsonFile)
                );
            }
            String jsonStr = FileIOOpt.readStringFromFile(jsonFile,"UTF-8");
            JSONObject json = (JSONObject) JSON.parseObject(jsonStr);
            osInfos = (List<OsInfo>)JSON.parseArray(json.getString("osInfos"), OsInfo.class);
            databaseInfos = (List<DatabaseInfo>)JSON.parseArray(json.getString("databaseInfos"),
                    DatabaseInfo.class);
            accessTokens = (List<UserAccessToken>)JSON.parseArray(json.getString("userAccessTokens"),
                    UserAccessToken.class);
        } catch (IOException e) {
            osInfos = new ArrayList<OsInfo>();
            databaseInfos = new ArrayList<DatabaseInfo>();
            e.printStackTrace();
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
