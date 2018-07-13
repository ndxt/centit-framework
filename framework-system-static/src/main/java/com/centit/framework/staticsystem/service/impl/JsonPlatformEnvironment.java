package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.staticsystem.po.*;
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
        List<UserInfo>  userinfos = JSON.parseArray(json.getString("userInfos"), UserInfo.class);
        CodeRepositoryCache.userInfoRepo.setFreshtDate(userinfos);

        List<OptInfo> optinfos = JSON.parseArray(json.getString("optInfos"), OptInfo.class);
        CodeRepositoryCache.optInfoRepo.setFreshtDate(optinfos);

        List<OptMethod> optmethods = JSON.parseArray(json.getString("optMethods"), OptMethod.class);
        CodeRepositoryCache.optMethodRepo.setFreshtDate(optmethods);

        List<RoleInfo> roleinfos = JSON.parseArray(json.getString("roleInfos"), RoleInfo.class);
        CodeRepositoryCache.roleInfoRepo.setFreshtDate(roleinfos);

        List<RolePower> rolepowers = JSON.parseArray(json.getString("rolePowers"), RolePower.class);
        CodeRepositoryCache.rolePowerRepo.setFreshtDate(rolepowers);

        List<UserRole> userroles = JSON.parseArray(json.getString("userRoles"), UserRole.class);
        allUserRoleRepo.setFreshtDate(userroles);

        List<UnitInfo> unitinfos = JSON.parseArray(json.getString("unitInfos"), UnitInfo.class);
        CodeRepositoryCache.unitInfoRepo.setFreshtDate(unitinfos);

        List<UserUnit> userunits = JSON.parseArray(json.getString("userUnits"), UserUnit.class);
        CodeRepositoryCache.userUnitRepo.setFreshtDate(userunits);

        List<DataCatalog> datacatalogs = JSON.parseArray(json.getString("dataCatalogs"), DataCatalog.class);
        CodeRepositoryCache.catalogRepo.setFreshtDate(datacatalogs);
        List<DataDictionary> datadictionaies = JSON.parseArray(json.getString("dataDictionaries"), DataDictionary.class);
        allDictionaryRepo.setFreshtDate(datadictionaies);
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
            CodeRepositoryCache.evictAllCache();
            String jsonstr = loadJsonStringFormConfigFile("/static_system_config.json");
            loadConfigFromJSONString(jsonstr);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        organizeDictionaryData();
        //static_system_user_pwd.json
        try {
            String jsonStr = loadJsonStringFormConfigFile("/static_system_user_pwd.json");
            JSONObject json = JSON.parseObject(jsonStr);
            for(IUserInfo u :CodeRepositoryCache.userInfoRepo.getCachedObject()){
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
        UserInfo ui= (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedObject().get(userCode);
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
            ui.setUserPin(passwordEncoder.createPassword(userPassword, userCode));
            json.put(userCode,ui.getUserPin());
            FileIOOpt.writeStringToFile(json.toJSONString(),appHome +"/config"+jsonFile);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

}
