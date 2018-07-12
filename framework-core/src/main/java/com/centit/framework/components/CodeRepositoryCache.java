package com.centit.framework.components;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.support.common.CachedMap;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cp标签实现类，并可以通过静态方法直接调用系统缓存
 *
 * @author codefan@sina.com
 * 2015-11-3
 */
public abstract class CodeRepositoryCache {

    private final static int CACHE_FRESH_PERIOD_MINITES = 15;
    private CodeRepositoryCache()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(CodeRepositoryCache.class);

    private static <T> T getCtxBean(String beanName, Class<T> clazz ) {
        WebApplicationContext ctx = ContextLoaderListener.getCurrentWebApplicationContext();
        if(ctx==null)
            return null;
        return ctx.getBean(beanName, clazz);
    }

    private static PlatformEnvironment platformEnvironment = null;

    private static PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment==null)
            platformEnvironment = getCtxBean("platformEnvironment", PlatformEnvironment.class);
        return platformEnvironment;
    }

    /**
     * 缓存用户信息
     */
    private static CachedObject<List<? extends IUserInfo>> userInfoRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUsers(),
            CACHE_FRESH_PERIOD_MINITES);
    /**
     * 派生的缓存信息，派生缓存相当于索引
     */
    private static CachedObject<Map<String, ? extends IUserInfo>> codeToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedObject();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
            for(IUserInfo userInfo : userInfos){
                codeToUser.put(userInfo.getUserCode(), userInfo);
            }
            return codeToUser;
        },CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<Map<String, ? extends IUserInfo>> loginNameToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedObject();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
            for(IUserInfo userInfo : userInfos){
                codeToUser.put(userInfo.getLoginName(), userInfo);
            }
            return codeToUser;
        },CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<Map<String, ? extends IUserInfo>> emailToUserMap  =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedObject();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getRegEmail())) {
                    codeToUser.put(userInfo.getRegEmail(), userInfo);
                }
            }
            return codeToUser;
        },CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<Map<String, ? extends IUserInfo>> phoneToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedObject();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getRegCellPhone())) {
                    codeToUser.put(userInfo.getRegCellPhone(), userInfo);
                }
            }
            return codeToUser;
        },CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<Map<String, ? extends IUserInfo>> idcardToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedObject();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getIdCardNo())) {
                    codeToUser.put(userInfo.getIdCardNo(), userInfo);
                }
            }
            return codeToUser;
        },CACHE_FRESH_PERIOD_MINITES);

    /**
     * 缓存机构信息
     */
    private static CachedObject<List<? extends IUnitInfo>> unitInfoRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUnits(),
            CACHE_FRESH_PERIOD_MINITES);

    /**
     * 机构的派生缓存
     */
    private static CachedObject<Map<String, ? extends IUnitInfo>> codeToUnitMap =
        new CachedObject<>(()-> {
            List<? extends IUnitInfo> unitInfos = unitInfoRepo.getCachedObject();
            if(unitInfos == null)
                return null;
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size());
            for(IUnitInfo unitInfo : unitInfos){
                codeToUnit.put(unitInfo.getUnitCode(), unitInfo);
            }
            return codeToUnit;
        },CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<List<? extends IUserUnit>> userUnitsRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUserUnits(),
            CACHE_FRESH_PERIOD_MINITES);

    private static CachedMap<String, List<? extends IUserUnit>> userUnitsMap =
        new CachedMap<>(
            (key)-> getPlatformEnvironment().listUserUnits(key),
            CACHE_FRESH_PERIOD_MINITES * 2, 300);

    private static CachedObject<Map<String, List<IUserUnit>>> unitUsersMap=
        new CachedObject<>(()-> {
            List<? extends IUserUnit> userUnits = userUnitsRepo.getCachedObject();
            if(userUnits == null)
                return null;
            Map<String, List<IUserUnit>> unitToUser =
                new HashMap<>(userUnits.size() >10 ? userUnits.size() : 10);
            for(IUserUnit uu : userUnits){
                List<IUserUnit> uus = unitToUser.get(uu.getUnitCode());
                if(uus==null){
                    uus = new ArrayList<>(16);
                }
                uus.add( uu );
                unitToUser.put(uu.getUnitCode(), uus);
            }
            return unitToUser;
        },CACHE_FRESH_PERIOD_MINITES);



    public static CachedObject<Map<String, ? extends IDataCatalog>> codeToCatalogMap  =
        new CachedObject<>(()-> {
                Map<String, IDataCatalog> dataCatalogMap = new HashMap<>();
                List<? extends IDataCatalog> dataCatalogs = getPlatformEnvironment().listAllDataCatalogs();
                if(dataCatalogs==null)
                    return dataCatalogMap;
                for( IDataCatalog dataCatalog : dataCatalogs){
                    dataCatalogMap.put(dataCatalog.getCatalogCode(), dataCatalog);
                }
                return dataCatalogMap;
            },
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedMap<String, Map<String,? extends IDataDictionary>> codeToDictionaryMap =
        new CachedMap<>((sCatalog)-> {
                Map<String, IDataDictionary> dataDictionaryMap = new HashMap<>();
                List<? extends IDataDictionary> dataDictionarys =
                    getPlatformEnvironment().listDataDictionaries(sCatalog);
                if(dataDictionarys==null)
                    return dataDictionaryMap;
                for( IDataDictionary data : dataDictionarys){
                    dataDictionaryMap.put(data.getDataCode(), data);
                }
                return dataDictionaryMap;
            },
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedMap<String, List<? extends IUserRole>> userRolesMapnew =
        new CachedMap<>((sUserCode)-> getPlatformEnvironment().listUserRoles(sUserCode),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedMap<String, List<? extends IUserRole>> roleUsersMap=
        new CachedMap<>((sRoleCode)-> getPlatformEnvironment().listRoleUsers(sRoleCode),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedMap<String, List<? extends IUnitRole>> unitRolesMap=
        new CachedMap<>((sUnitCode)-> getPlatformEnvironment().listUnitRoles(sUnitCode),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedMap<String, List<? extends IUnitRole>> roleUnitsMap=
        new CachedMap<>((sRoleCode)-> getPlatformEnvironment().listRoleUnits(sRoleCode),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<List<? extends IRolePower>> allRolePower=
        new CachedObject<>( ()-> getPlatformEnvironment().listAllRolePower(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<List<? extends IOptMethod>> allOptMethod=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptMethod(),
            CACHE_FRESH_PERIOD_MINITES);

}
