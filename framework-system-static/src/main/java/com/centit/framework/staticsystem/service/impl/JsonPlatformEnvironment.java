package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.common.ListAppendMap;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class JsonPlatformEnvironment extends AbstractStaticPlatformEnvironment {

    private static Log logger = LogFactory.getLog(JsonPlatformEnvironment.class);

    protected String appHome;

    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    private void loadConfigFromJSONString(String jsonStr){
        JSONObject json = JSON.parseObject(jsonStr);
        JSONArray tempJa= json.getJSONArray("userInfos");
        if(tempJa!=null) {
            List<UserInfo> userinfos = tempJa.toJavaList(UserInfo.class);
            CodeRepositoryCache.userInfoRepo.setFreshData(
                GlobalConstValue.NO_TENANT_TOP_UNIT, new ListAppendMap<>(userinfos,
                    UserInfo::getUserCode));
        }
        tempJa= json.getJSONArray("optInfos");
        if(tempJa!=null) {
            List<OptInfo> optinfos = tempJa.toJavaList(OptInfo.class);
            CodeRepositoryCache.optInfoRepo.setFreshData(GlobalConstValue.NO_TENANT_TOP_UNIT,
                optinfos);
        }
        tempJa= json.getJSONArray("optMethods");
        if(tempJa!=null) {
            List<OptMethod> optmethods = tempJa.toJavaList(OptMethod.class);
            allOptMethod.setFreshData(optmethods);
        }

        tempJa= json.getJSONArray("optDataScopes");
        if(tempJa!=null) {
            List<OptDataScope> dataScopes = tempJa.toJavaList(OptDataScope.class);
            optDataScopes.setFreshData(dataScopes);
        }

        tempJa= json.getJSONArray("roleInfos");
        if(tempJa!=null) {
            List<RoleInfo> roleinfos = tempJa.toJavaList(RoleInfo.class);
            CodeRepositoryCache.roleInfoRepo.setFreshData(GlobalConstValue.NO_TENANT_TOP_UNIT,
                roleinfos);
        }

        tempJa= json.getJSONArray("rolePowers");
        if(tempJa!=null) {
            List<RolePower> rolepowers = tempJa.toJavaList(RolePower.class);
            allRolePower.setFreshData(rolepowers);
        }

        tempJa = json.getJSONArray("userRoles");
        if (tempJa != null) {
            List<UserRole> userroles = tempJa.toJavaList(UserRole.class);
            allUserRoleRepo.setFreshData(userroles);
        }

        tempJa = json.getJSONArray("unitInfos");
        if (tempJa != null) {
            List<UnitInfo> unitinfos = tempJa.toJavaList(UnitInfo.class);
            CodeRepositoryCache.unitInfoRepo.setFreshData(GlobalConstValue.NO_TENANT_TOP_UNIT,
                new ListAppendMap<>(unitinfos, UnitInfo::getUnitCode));
        }

        tempJa = json.getJSONArray("userUnits");
        if (tempJa != null) {
            List<UserUnit> userunits = tempJa.toJavaList(UserUnit.class);
            allUserUnitRepo.setFreshData(userunits);
        }

        tempJa = json.getJSONArray("dataCatalogs");
        if (tempJa != null) {
            List<DataCatalog> datacatalogs = tempJa.toJavaList(DataCatalog.class);
            catalogRepo.setFreshData(datacatalogs);
        }

        tempJa = json.getJSONArray("osInfos");
        if (tempJa != null) {
            List<OsInfo> osInfos = tempJa.toJavaList(OsInfo.class);
            CodeRepositoryCache.osInfoCache.setFreshData(GlobalConstValue.NO_TENANT_TOP_UNIT,
                osInfos);
        }

        tempJa = json.getJSONArray("dataDictionaries");
        if (tempJa != null) {
            List<DataDictionary> datadictionaies = tempJa.toJavaList(DataDictionary.class);
            allDictionaryRepo.setFreshData(datadictionaies);
        }
    }

    public String loadJsonStringFormConfigFile(String fileName) throws IOException {
        String jsonFile = appHome +"/config"+ fileName;
        if(FileSystemOpt.existFile(jsonFile)) {
            return FileIOOpt.readStringFromFile(jsonFile,"UTF-8");
        }else{
            return FileIOOpt.readStringFromInputStream(
                    new ClassPathResource(fileName).getInputStream(),"UTF-8");
        }
    }
    /**
     * 刷新数据字典
     */
    protected synchronized void reloadPlatformData() {
        try {
            //CodeRepositoryCache.evictAllCache();
            String jsonstr = loadJsonStringFormConfigFile("/static_system_config.json");
            loadConfigFromJSONString(jsonstr);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        organizePlatformData();
        //static_system_user_pwd.json
        try {
            String jsonStr = loadJsonStringFormConfigFile("/static_system_user_pwd.json");
            JSONObject json = JSON.parseObject(jsonStr);
            for(IUserInfo u :CodeRepositoryCache.userInfoRepo
                    .getCachedValue(GlobalConstValue.NO_TENANT_TOP_UNIT)
                    .getListData()){
                String spwd = json.getString(u.getUserCode());
                if(StringUtils.isNotBlank(spwd))
                    ((UserInfo)u).setUserPin(spwd);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 修改用户密码
     *
     * @param userCode userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {
        UserInfo ui= (UserInfo)CodeRepositoryCache.userInfoRepo
            .getCachedValue(GlobalConstValue.NO_TENANT_TOP_UNIT)
            .getAppendMap().get(userCode);
        if(ui==null)
            return;
        JSONObject json = null;
        String jsonFile = "/static_system_user_pwd.json";
        try {
            String jsonstr = loadJsonStringFormConfigFile(jsonFile);
            json = JSON.parseObject(jsonstr);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        if(json==null)
            json = new JSONObject();
        try {
            ui.setUserPin(passwordEncoder.encodePassword(userPassword, userCode));
            json.put(userCode,ui.getUserPin());
            FileIOOpt.writeStringToFile(json.toJSONString(),appHome +"/config"+jsonFile);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

}
