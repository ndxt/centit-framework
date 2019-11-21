package com.centit.framework.components;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.CachedMap;
import com.centit.support.common.CachedObject;
import com.centit.support.common.DerivativeCachedMap;
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
    public final static int CACHE_FRESH_PERIOD_SECONDS = 900;
    /**
     * 用不过去，就把失效时间设定为一个月
     */
    public final static int CACHE_NEVER_EXPIRE = 30 * 24 * 60 * 60;

    /**
     * 短期缓存，就把失效时间设定为 5 秒
     */
    public final static int CACHE_KEEP_FRESH = 5;

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

    private static PlatformEnvironment getPlatformEnvironment() {
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
    public static void setAllCacheFreshPeriod(int periodSeconds){
        CodeRepositoryCache.userInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToUserMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.loginNameToUserMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.idcardToUserMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.emailToUserMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.phoneToUserMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.unitInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToUnitMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.depNoToUnitMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.userUnitRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.userUnitsMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.unitUsersMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.catalogRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToCatalogMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.dictionaryRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToDictionaryMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.dictionaryRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.optInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToOptMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.optDataScopeRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.optMethodRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToMethodMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.roleInfoRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.codeToRoleMap.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.rolePowerRepo.setFreshPeriod(periodSeconds);

        CodeRepositoryCache.userRolesRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.roleUsersRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.unitRolesRepo.setFreshPeriod(periodSeconds);
        CodeRepositoryCache.roleUnitsRepo.setFreshPeriod(periodSeconds);
    }

    private static void innerEvictCache(String cacheName, String mapKey){
        switch (cacheName){
            case "UserInfo":
                CodeRepositoryCache.userInfoRepo.evictCahce();
                /*CodeRepositoryCache.codeToUserMap.evictCahce();
                CodeRepositoryCache.loginNameToUserMap.evictCahce();
                CodeRepositoryCache.idcardToUserMap.evictCahce();
                CodeRepositoryCache.emailToUserMap.evictCahce();
                CodeRepositoryCache.phoneToUserMap.evictCahce();*/
                break;
            case "UnitInfo":
                CodeRepositoryCache.unitInfoRepo.evictCahce();
                /*CodeRepositoryCache.codeToUnitMap.evictCahce();
                CodeRepositoryCache.depNoToUnitMap.evictCahce();*/
                break;

            case "UserUnit":
                CodeRepositoryCache.userUnitRepo.evictCahce();
                /*CodeRepositoryCache.userUnitsMap.evictCahce();
                CodeRepositoryCache.unitUsersMap.evictCahce();*/
                break;
            case "DataCatalog":
                CodeRepositoryCache.catalogRepo.evictCahce();
                //CodeRepositoryCache.codeToCatalogMap.evictCahce();
                break;
            case "DataDictionary":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.dictionaryRepo.evictIdentifiedCache(mapKey);
                    //CodeRepositoryCache.codeToDictionaryMap.evictIdentifiedCache(mapKey);
                }else{
                    CodeRepositoryCache.dictionaryRepo.evictCahce();
                    //CodeRepositoryCache.codeToDictionaryMap.evictAll();
                }
                break;
            case "OptInfo":
                CodeRepositoryCache.optInfoRepo.evictCahce();
                //CodeRepositoryCache.codeToOptMap.evictCahce();
                break;
            case "OptMethod":
                CodeRepositoryCache.optMethodRepo.evictCahce();
                //CodeRepositoryCache.codeToMethodMap.evictCahce();
                break;
            case "optDataScope":
                CodeRepositoryCache.optDataScopeRepo.evictCahce();
                break;
            case "RoleInfo":
                CodeRepositoryCache.roleInfoRepo.evictCahce();
                //CodeRepositoryCache.codeToRoleMap.evictCahce();
                break;
            case "RolePower":
                CodeRepositoryCache.rolePowerRepo.evictCahce();
                break;
            case "UserRoles":
                CodeRepositoryCache.userRolesRepo.evictCahce();
                break;
            case "RoleUsers":
                CodeRepositoryCache.roleUsersRepo.evictCahce();
                break;
            case "UnitRoles":
                CodeRepositoryCache.unitRolesRepo.evictCahce();
                break;
            case "RoleUnits":
                CodeRepositoryCache.roleUnitsRepo.evictCahce();
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

    public static void evictAllCache(){
        CodeRepositoryCache.userInfoRepo.evictCahce();
        CodeRepositoryCache.unitInfoRepo.evictCahce();
        CodeRepositoryCache.userUnitRepo.evictCahce();
        CodeRepositoryCache.catalogRepo.evictCahce();
        CodeRepositoryCache.dictionaryRepo.evictCahce();
        CodeRepositoryCache.optInfoRepo.evictCahce();
        CodeRepositoryCache.optDataScopeRepo.evictCahce();
        CodeRepositoryCache.optMethodRepo.evictCahce();
        CodeRepositoryCache.roleInfoRepo.evictCahce();
        CodeRepositoryCache.rolePowerRepo.evictCahce();
        CodeRepositoryCache.userRolesRepo.evictCahce();
        CodeRepositoryCache.roleUsersRepo.evictCahce();
        CodeRepositoryCache.unitRolesRepo.evictCahce();
        CodeRepositoryCache.roleUnitsRepo.evictCahce();

        if(CodeRepositoryCache.evictCacheExtOpt != null){
            CodeRepositoryCache.evictCacheExtOpt.evictAllCache();
        }
    }

    /**
     * 缓存用户信息
     */
    public static CachedObject<List<? extends IUserInfo>> userInfoRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUsers(), CACHE_FRESH_PERIOD_SECONDS);
    /**
     * 派生的缓存信息，派生缓存相当于索引
     */
    public static CachedObject<Map<String, ? extends IUserInfo>> codeToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size()+5);
            for(IUserInfo userInfo : userInfos){
                codeToUser.put(userInfo.getUserCode(), userInfo);
            }
            return codeToUser;
        }, userInfoRepo);

    public static CachedObject<Map<String, ? extends IUserInfo>> loginNameToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size()+5);
            for(IUserInfo userInfo : userInfos){
                codeToUser.put(userInfo.getLoginName(), userInfo);
            }
            return codeToUser;
        },userInfoRepo);

    public static CachedObject<Map<String, ? extends IUserInfo>> emailToUserMap  =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size()+5);
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getRegEmail())) {
                    codeToUser.put(userInfo.getRegEmail(), userInfo);
                }
            }
            return codeToUser;
        },userInfoRepo);

    public static CachedObject<Map<String, ? extends IUserInfo>> phoneToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size()+5);
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getRegCellPhone())) {
                    codeToUser.put(userInfo.getRegCellPhone(), userInfo);
                }
            }
            return codeToUser;
        },userInfoRepo);

    public static CachedObject<Map<String, ? extends IUserInfo>> idcardToUserMap =
        new CachedObject<>(()-> {
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size()+5);
            for(IUserInfo userInfo : userInfos){
                if(StringUtils.isNoneBlank(userInfo.getIdCardNo())) {
                    codeToUser.put(userInfo.getIdCardNo(), userInfo);
                }
            }
            return codeToUser;
        },userInfoRepo);

    /**
     * 缓存机构信息
     */
    public static CachedObject<List<? extends IUnitInfo>> unitInfoRepo =
        new CachedObject<>(()->{
            List<? extends IUnitInfo> allunits = getPlatformEnvironment().listAllUnits();
            CollectionsOpt.sortAsTree(allunits,
                ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );
            return allunits;
         }, CACHE_FRESH_PERIOD_SECONDS);

    /**
     * 机构的派生缓存
     */
    public static CachedObject<Map<String, ? extends IUnitInfo>> codeToUnitMap =
        new CachedObject<>(()-> {
            List<? extends IUnitInfo> unitInfos = unitInfoRepo.getCachedTarget();
            if(unitInfos == null)
                return null;
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size()+5);
            for(IUnitInfo unitInfo : unitInfos){
                codeToUnit.put(unitInfo.getUnitCode(), unitInfo);
            }
            return codeToUnit;
        },unitInfoRepo);

    public static CachedObject<Map<String, ? extends IUnitInfo>> depNoToUnitMap =
        new CachedObject<>(()-> {
            List<? extends IUnitInfo> unitInfos = unitInfoRepo.getCachedTarget();
            if(unitInfos == null)
                return null;
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size()+5);
            for(IUnitInfo unitInfo : unitInfos){
                codeToUnit.put(unitInfo.getDepNo(), unitInfo);
            }
            return codeToUnit;
        },unitInfoRepo);


    public static CachedObject<List<? extends IUserUnit>> userUnitRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUserUnits(), CACHE_FRESH_PERIOD_SECONDS);
    /**
     * 派生缓存
     */
    public static CachedMap<String, List<? extends IUserUnit>> userUnitsMap =
        new CachedMap<>(
            ( userCode )-> {
                List<? extends IUserUnit> userUnits = userUnitRepo.getCachedTarget();
                if(userUnits == null)
                    return null;
                List<IUserUnit> uus = new ArrayList<>(16);
                for(IUserUnit uu : userUnits){
                    if(StringUtils.equals(userCode, uu.getUserCode())){
                        uus.add( uu );
                    }
                }
                return uus;
            },
            userUnitRepo, 300);
    /**
     * 派生缓存
     */
    public static CachedMap<String, List<IUserUnit>> unitUsersMap=
        new CachedMap<>((unitCode)-> {
            List<? extends IUserUnit> userUnits = userUnitRepo.getCachedTarget();
            if(userUnits == null)
                return null;
            List<IUserUnit> uus = new ArrayList<>(16);
            for(IUserUnit uu : userUnits){
                if(StringUtils.equals(unitCode, uu.getUnitCode() )){
                    uus.add( uu );
                }
            }
            return uus;
        },userUnitRepo, 100);


    public static CachedObject<List< ? extends IDataCatalog>> catalogRepo  =
        new CachedObject<>(()-> getPlatformEnvironment().listAllDataCatalogs(), CACHE_FRESH_PERIOD_SECONDS);
    /**
     * 派生缓存，避免对象重复
     */
    public static CachedObject<Map<String, ? extends IDataCatalog>> codeToCatalogMap  =
        new CachedObject<>(()-> {
                List<? extends IDataCatalog> dataCatalogs = catalogRepo.getCachedTarget();
                if(dataCatalogs==null)
                    return null;
                Map<String, IDataCatalog> dataCatalogMap = new HashMap<>(dataCatalogs.size()+5);
                for( IDataCatalog dataCatalog : dataCatalogs){
                    dataCatalogMap.put(dataCatalog.getCatalogCode(), dataCatalog);
                }
                return dataCatalogMap;
            },
            catalogRepo);


    public static CachedMap<String, List<? extends IDataDictionary>> dictionaryRepo =
        new CachedMap<>((sCatalog)->  getPlatformEnvironment().listDataDictionaries(sCatalog), CACHE_FRESH_PERIOD_SECONDS);

    public static DerivativeCachedMap<String,List<? extends IDataDictionary>,
            Map<String,? extends IDataDictionary>> codeToDictionaryMap =
        new DerivativeCachedMap<>( (dataDictionarys )-> {
                if(dataDictionarys==null)
                    return null;
                Map<String, IDataDictionary> dataDictionaryMap = new HashMap<>(dataDictionarys.size()+5);
                for( IDataDictionary data : dataDictionarys){
                    dataDictionaryMap.put(data.getDataCode(), data);
                }
                return dataDictionaryMap;
            },
            dictionaryRepo, 100);


    public static CachedObject<List<? extends IRoleInfo>> roleInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllRoleInfo(), CACHE_FRESH_PERIOD_SECONDS);

    public static CachedObject<Map<String, ? extends IRoleInfo>> codeToRoleMap=
            new CachedObject<>(()-> {
                List<? extends IRoleInfo> roleInfos = roleInfoRepo.getCachedTarget();
                if(roleInfos==null)
                    return null;
                Map<String, IRoleInfo> codeMap = new HashMap<>(roleInfos.size()+5);
                for( IRoleInfo data : roleInfos){
                    codeMap.put(data.getRoleCode(), data);
                }
                return codeMap;
            }, roleInfoRepo);

    public static CachedObject<List<? extends IOptInfo>> optInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptInfo(), CACHE_FRESH_PERIOD_SECONDS);

    public static CachedObject<Map<String, ? extends IOptInfo>> codeToOptMap=
        new CachedObject<>(()-> {
            List<? extends IOptInfo> optInfos = optInfoRepo.getCachedTarget();
            if(optInfos==null)
                return null;
            Map<String, IOptInfo> codeMap = new HashMap<>(optInfos.size()+5);
            for( IOptInfo data : optInfos){
                codeMap.put(data.getOptId(), data);
            }
            return codeMap;
        }, optInfoRepo);

    public static CachedObject<Map<String, List<IOptDataScope>>> optDataScopeRepo=
        new CachedObject<>(()->{
            List<? extends IOptDataScope> optDataScopes = getPlatformEnvironment().listAllOptDataScope();
            Map<String,List<IOptDataScope>> optDataScopeMap = new HashMap<>(200);
            for(IOptDataScope dataScope: optDataScopes){
                List<IOptDataScope> odss = optDataScopeMap.get(dataScope.getOptId());
                if(odss==null){
                    odss = new ArrayList<>(4);
                }
                odss.add(dataScope);
                optDataScopeMap.put(dataScope.getOptId(), odss);
            }
            return optDataScopeMap;
        }, CACHE_FRESH_PERIOD_SECONDS);


    public static CachedObject<List<? extends IOptMethod>> optMethodRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptMethod(), CACHE_FRESH_PERIOD_SECONDS);

    public static CachedObject<Map<String, ? extends IOptMethod>> codeToMethodMap=
        new CachedObject<>(()-> {
            List<? extends IOptMethod> methods = optMethodRepo.getCachedTarget();
            if(methods==null)
                return null;
            Map<String, IOptMethod> codeMap = new HashMap<>(methods.size()+5);
            for( IOptMethod data : methods){
                codeMap.put(data.getOptCode(), data);
            }
            return codeMap;
        }, optMethodRepo);


    public static CachedMap<String, List<? extends IUserRole>> userRolesRepo =
        new CachedMap<>((sUserCode)-> getPlatformEnvironment().listUserRoles(sUserCode),
            5);

    public static CachedMap<String, List<? extends IUserRole>> roleUsersRepo =
        new CachedMap<>((sRoleCode)-> getPlatformEnvironment().listRoleUsers(sRoleCode),
            5);

    public static CachedMap<String, List<? extends IUnitRole>> unitRolesRepo =
        new CachedMap<>((sUnitCode)-> getPlatformEnvironment().listUnitRoles(sUnitCode),
            5);

    public static CachedMap<String, List<? extends IUnitRole>> roleUnitsRepo =
        new CachedMap<>((sRoleCode)-> getPlatformEnvironment().listRoleUnits(sRoleCode),
            5);

    public static CachedObject<List<? extends IRolePower>> rolePowerRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllRolePower(), CACHE_FRESH_PERIOD_SECONDS);

    public static CachedObject<Map<String, List<IRolePower>>> rolePowerMap =
        new CachedObject<>(()-> {
            List<? extends IRolePower> allRowPowers = CodeRepositoryCache.rolePowerRepo.getCachedTarget();
            Map<String,List<IRolePower>> rolePowerMap = new HashMap<>(100);
            for(IRolePower rolePower: allRowPowers){
                List<IRolePower> odss = rolePowerMap.get(rolePower.getRoleCode());
                if(odss==null){
                    odss = new ArrayList<>(4);
                }
                odss.add(rolePower);
                rolePowerMap.put(rolePower.getRoleCode(), odss);
            }
            return rolePowerMap;
        }, rolePowerRepo);

}
