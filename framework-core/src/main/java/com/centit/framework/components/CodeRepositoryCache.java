package com.centit.framework.components;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.CentitSecurityMetadata;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.CachedMap;
import com.centit.support.common.ICachedObject;
import com.centit.support.common.ListAppendMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cp标签实现类，并可以通过静态方法直接调用系统缓存
 * 框架所有数据缓存的地方  缓存时间默认为半小时
 * @author codefan@sina.com
 * 2018-6-3
 */
public abstract class CodeRepositoryCache {
    /**
     * 默认15分钟，900秒
     */
    public final static long CACHE_FRESH_PERIOD_SECONDS =
        ICachedObject.DEFAULT_REFRESH_PERIOD;
    /**
     * 永不过期，就把失效时间设定为一个月
     */
    public final static long CACHE_NEVER_EXPIRE = 30 * 24 * 60 * 60L;
    public final static long CACHE_EXPIRE_EVERY_DAY = 24 * 60 * 60L;
    /**
     * 短期缓存，就把失效时间设定为 5 秒
     */
    public final static long CACHE_KEEP_FRESH = 5L;

    private CodeRepositoryCache()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(CodeRepositoryCache.class);

    public interface EvictCacheExtOpt{
        void evictCache(String cacheName, String mapKey);
        void evictCache(String cacheName);
        void evictAllCache();
    }

    private static EvictCacheExtOpt evictCacheExtOpt = null;
    private static PlatformEnvironment platformEnvironment = null;

    public static void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        if(platformEnvironment!=null) {
            CodeRepositoryCache.platformEnvironment = platformEnvironment;
        }
    }

    public static void setEvictCacheExtOpt(EvictCacheExtOpt evictCacheExtOpt) {
        CodeRepositoryCache.evictCacheExtOpt = evictCacheExtOpt;
    }

    public static PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment==null) {
            platformEnvironment = WebOptUtils.getWebAppContextBean("platformEnvironment", PlatformEnvironment.class);
        }
        //Assert.checkNonNull(platformEnvironment);
        return platformEnvironment;
    }

    /**
     * 设置所有的缓存刷新时间 单位秒
     * @param periodSeconds 缓存刷新时间 单位秒
     */
    public static void setAllCacheFreshPeriod(long periodSeconds){
        CodeRepositoryCache.userInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.unitInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.userUnitsMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.unitUsersMap.setFreshPeriod(periodSeconds);

        CodeRepositoryCache.dictionaryRepo.setFreshPeriod(periodSeconds);

        CodeRepositoryCache.roleInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.roleUsersRepo.setFreshPeriod(periodSeconds);

        CodeRepositoryCache.optInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.optDataScopeRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.optMethodRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.rolePowerRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.osInfoCache.setFreshPeriod(periodSeconds);
    }

    private static void innerEvictCache(String cacheName, String mapKey){
        switch (cacheName){
            case "UserInfo":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.userInfoRepo.evictIdentifiedCache(mapKey);
                }else{
                    CodeRepositoryCache.userInfoRepo.evictCache();
                }
                break;
            case "UnitInfo":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.unitInfoRepo.evictIdentifiedCache(mapKey);
                }else{
                    CodeRepositoryCache.unitInfoRepo.evictCache();
                }
                break;
            case "UnitUser":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.unitUsersMap.evictIdentifiedCache(mapKey);
                }else{
                    CodeRepositoryCache.unitUsersMap.evictCache();
                }
                break;
            case "UserUnit":
                CodeRepositoryCache.userUnitRepo.evictCache();
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.userUnitsMap.evictIdentifiedCache(mapKey);
                }else{
                    CodeRepositoryCache.userUnitsMap.evictCache();
                }
                break;
            case "DataDictionary":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.dictionaryRepo.evictIdentifiedCache(mapKey);;
                }else{
                    CodeRepositoryCache.dictionaryRepo.evictCache();
                }
                break;
            case "OptInfo":
                CodeRepositoryCache.optInfoRepo.evictCache();
                break;
            case "RoleInfo":
                CodeRepositoryCache.roleInfoRepo.evictCache();
                break;
            case "UserRoles":
                CodeRepositoryCache.userRolesRepo.evictCache();
                break;

            case "OptMethod":
                CodeRepositoryCache.optMethodRepo.evictCache();
                break;
            case "optDataScope":
                CodeRepositoryCache.optDataScopeRepo.evictCache();
                break;
            case "RolePower":
                CodeRepositoryCache.rolePowerRepo.evictCache();
                break;
            case "OsInfo":
                CodeRepositoryCache.osInfoCache.evictCache();
                break;
            case "SystemOpt":
                CentitSecurityMetadata.evictCache(0);
                break;
        }
    }

    public static void evictCache(String cacheName, String mapKey){
        CodeRepositoryCache.innerEvictCache(cacheName, mapKey);
        if(CodeRepositoryCache.evictCacheExtOpt != null){
            CodeRepositoryCache.evictCacheExtOpt.evictCache(cacheName, mapKey);
        }
    }

    public static void evictCache(String cacheName){
        CodeRepositoryCache.innerEvictCache(cacheName, null);

        if(CodeRepositoryCache.evictCacheExtOpt != null){
            CodeRepositoryCache.evictCacheExtOpt.evictCache(cacheName);
        }
    }

    public static void refreshAsyncCache(){
        //CodeRepositoryCache.userInfoRepo.asyncRefreshData();
        //CodeRepositoryCache.unitInfoRepo.asyncRefreshData();
        //CodeRepositoryCache.userUnitRepo.asyncRefreshData();
    }

    public static void evictAllCache(){
        CodeRepositoryCache.userInfoRepo.evictCache();
        CodeRepositoryCache.unitInfoRepo.evictCache();
        //CodeRepositoryCache.catalogRepo.evictCache();
        CodeRepositoryCache.userUnitRepo.evictCache();
        CodeRepositoryCache.dictionaryRepo.evictCache();
        CodeRepositoryCache.optInfoRepo.evictCache();
        CodeRepositoryCache.optMethodRepo.evictCache();
        CodeRepositoryCache.rolePowerRepo.evictCache();
        CodeRepositoryCache.osInfoCache.evictCache();
        CodeRepositoryCache.optDataScopeRepo.evictCache();
        if(CodeRepositoryCache.evictCacheExtOpt != null){
            CodeRepositoryCache.evictCacheExtOpt.evictAllCache();
        }
    }

    /**
     * 缓存用户信息, 按照租户隔离
     */
    public static CachedMap<String, ListAppendMap<UserInfo>> userInfoRepo =
        new CachedMap<>( (topUnit) ->
               new ListAppendMap<>(
                    getPlatformEnvironment().listAllUsers(topUnit),
                    (ui)-> ui.getUserCode()),
            CACHE_FRESH_PERIOD_SECONDS);

    /**
     * 缓存机构信息, 按照租户隔离
     */
    public static CachedMap<String, ListAppendMap<UnitInfo>> unitInfoRepo =
        new CachedMap<>((topUnit)->{
            List<UnitInfo> allunits = getPlatformEnvironment().listAllUnits(topUnit);
            CollectionsOpt.sortAsTree(allunits,
                ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );

            return new ListAppendMap<>((List<UnitInfo>) allunits, (ui)-> ui.getUnitCode());
         }, CACHE_FRESH_PERIOD_SECONDS);

    /**
     * 用户机构缓存
     */
    public static CachedMap<String, List<UserUnit>> userUnitRepo =
        new CachedMap<>((topUnit) -> getPlatformEnvironment().listAllUserUnits(topUnit),
            CACHE_FRESH_PERIOD_SECONDS);

     /**
     * 用户机构缓存
     */
    public static CachedMap<String, CachedMap<String, List<UserUnit>>> userUnitsMap =
         new CachedMap<>((topUnit)->
             new CachedMap<>((userCode)-> getPlatformEnvironment().listUserUnits(topUnit, userCode),
                CACHE_FRESH_PERIOD_SECONDS, 300),
             ICachedObject.NOT_REFRESH_PERIOD);
    /**
     * 机构用户缓存
     */
    public static CachedMap<String, List<UserUnit>> unitUsersMap=
        new CachedMap<>(
            (unitCode)-> getPlatformEnvironment().listUnitUsers(unitCode),
            CACHE_FRESH_PERIOD_SECONDS, 100);


    public static CachedMap<String, ListAppendMap<DataDictionary>> dictionaryRepo =
        new CachedMap<>((sCatalog)->
            new ListAppendMap<>(
                getPlatformEnvironment().listDataDictionaries(sCatalog),
                (ui)->ui.getDataCode()),
            CACHE_FRESH_PERIOD_SECONDS);

    /**
     * 权限相关缓存
     */
    public static CachedMap<String, List<OsInfo>> osInfoCache =
        new CachedMap<>((topUnit)->getPlatformEnvironment().listOsInfos(topUnit),
            CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, CachedMap<String, List<UserRole>>> userRolesRepo =
        new CachedMap<>((topUnit)->
            new CachedMap<>((sUserCode)-> getPlatformEnvironment().listUserRoles(topUnit, sUserCode),
                 CACHE_FRESH_PERIOD_SECONDS),
            ICachedObject.NOT_REFRESH_PERIOD);

    public static CachedMap<String, CachedMap<String, List<UserRole>>> roleUsersRepo =
        new CachedMap<>((topUnit)->
            new CachedMap<>((sRoleCode)-> getPlatformEnvironment().listRoleUsers(topUnit, sRoleCode),
                CACHE_FRESH_PERIOD_SECONDS),
            ICachedObject.NOT_REFRESH_PERIOD);

    /*
     * 下面的缓存信息用于spring security 中的用户的权限信息缓存
     */

    public static CachedMap<String, List<RoleInfo>> roleInfoRepo=
        new CachedMap<>((topUnit)-> getPlatformEnvironment().listAllRoleInfo(topUnit),
            CACHE_FRESH_PERIOD_SECONDS);


    public static CachedMap<String, List<OptInfo>> optInfoRepo=
        new CachedMap<>((topUnit)-> getPlatformEnvironment().listAllOptInfo(topUnit),
            CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, List<OptInfo>> roleOptInfoMap=
        new CachedMap<>((roleCode)-> getPlatformEnvironment().listOptInfoByRole(roleCode),
            CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, ListAppendMap<OptMethod>> optMethodRepo=
        new CachedMap<>((topUnit)-> {
                List<OptMethod> optInfos =
                    getPlatformEnvironment().listAllOptMethod(topUnit);
                return new ListAppendMap<>(optInfos, OptMethod::getOptCode);
            },
            CACHE_FRESH_PERIOD_SECONDS);



    public static CachedMap<String, ListAppendMap<OptMethod>> roleOptMethodMap=
        new CachedMap<>((roleCode)-> {
            List<OptMethod> optInfos =
                getPlatformEnvironment().listOptMethodByRoleCode(roleCode);
            return new ListAppendMap<>(optInfos, OptMethod::getOptCode);
    }, CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, Map<String, List<OptDataScope>>> optDataScopeRepo=
        new CachedMap<>((topUnit)->{
            List<OptDataScope> optDataScopes = getPlatformEnvironment().listAllOptDataScope(topUnit);
            Map<String, List<OptDataScope>> optDataScopeMap = new HashMap<>(200);
            if(optDataScopes != null) {
                for (OptDataScope dataScope : optDataScopes) {
                    List<OptDataScope> odss = optDataScopeMap.get(dataScope.getOptId());
                    if (odss == null) {
                        odss = new ArrayList<>(4);
                    }
                    odss.add(dataScope);
                    optDataScopeMap.put(dataScope.getOptId(), odss);
                }
            }
            return optDataScopeMap;
        }, CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, List<RolePower>> rolePowerRepo =
        new CachedMap<>((topUnit)->
            getPlatformEnvironment().listAllRolePower(topUnit), CACHE_FRESH_PERIOD_SECONDS);

    public static CachedMap<String, Map<String, List<RolePower>>> rolePowerMap =
        new CachedMap<>((topUnit)->{
            List<RolePower> allRowPowers = rolePowerRepo.getCachedValue(topUnit);
            Map<String, List<RolePower>> rolePowerMap = new HashMap<>(100);
            for(RolePower rolePower: allRowPowers){
                List<RolePower> odss = rolePowerMap.get(rolePower.getRoleCode());
                if(odss==null){
                    odss = new ArrayList<>(4);
                }
                odss.add(rolePower);
                rolePowerMap.put(rolePower.getRoleCode(), odss);
            }
            return rolePowerMap;
        }, rolePowerRepo);
}

