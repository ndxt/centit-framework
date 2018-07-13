package com.centit.framework.components;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.support.algorithm.CollectionsOpt;
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
 * 框架所有数据缓存的地方  缓存时间默认为半小时
 * @author codefan@sina.com
 * 2018-6-3
 */
public abstract class CodeRepositoryCache {

    public final static int CACHE_FRESH_PERIOD_MINITES = 15;
    public final static int CACHE_NEVER_EXPIRE = 365 * 24 * 60;

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

    public static void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        if(platformEnvironment!=null) {
            CodeRepositoryCache.platformEnvironment = platformEnvironment;
        }
    }

    private static PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment==null)
            platformEnvironment = getCtxBean("platformEnvironment", PlatformEnvironment.class);
        //Assert.checkNonNull(platformEnvironment);
        return platformEnvironment;
    }

    public static void evictCache(String cacheName, String mapKey){
        switch (cacheName){
            case "UserInfo":
                CodeRepositoryCache.userInfoRepo.evictObject();
                CodeRepositoryCache.codeToUserMap.evictObject();
                CodeRepositoryCache.loginNameToUserMap.evictObject();
                CodeRepositoryCache.idcardToUserMap.evictObject();
                CodeRepositoryCache.emailToUserMap.evictObject();
                CodeRepositoryCache.phoneToUserMap.evictObject();
                break;
            case "UnitInfo":
                CodeRepositoryCache.unitInfoRepo.evictObject();
                CodeRepositoryCache.codeToUnitMap.evictObject();
                CodeRepositoryCache.depNoToUnitMap.evictObject();
                break;

            case "UserUnit":
                CodeRepositoryCache.userUnitRepo.evictObject();
                CodeRepositoryCache.userUnitsMap.evictAll();
                CodeRepositoryCache.unitUsersMap.evictAll();
                break;
            case "DataCatalog":
                CodeRepositoryCache.catalogRepo.evictObject();
                CodeRepositoryCache.codeToCatalogMap.evictObject();
                break;
            case "DataDictionary":
                if(StringUtils.isNotBlank(mapKey)){
                    CodeRepositoryCache.dictionaryRepo.evictObject(mapKey);
                    CodeRepositoryCache.codeToDictionaryMap.evictObject(mapKey);
                }else{
                    CodeRepositoryCache.dictionaryRepo.evictAll();
                    CodeRepositoryCache.codeToDictionaryMap.evictAll();
                }
                break;
            case "OptInfo":
                CodeRepositoryCache.optInfoRepo.evictObject();
                CodeRepositoryCache.codeToOptMap.evictObject();
                break;
            case "OptMethod":
                CodeRepositoryCache.optMethodRepo.evictObject();
                CodeRepositoryCache.codeToMethodMap.evictObject();
                break;
            case "RoleInfo":
                CodeRepositoryCache.roleInfoRepo.evictObject();
                CodeRepositoryCache.codeToRoleMap.evictObject();
                break;
            case "RolePower":
                CodeRepositoryCache.rolePowerRepo.evictObject();
                break;
        }
    }

    public static void evictCache(String cacheName){
        CodeRepositoryCache.evictCache(cacheName, null);
    }

    public static void evictAllCache(){
        CodeRepositoryCache.catalogRepo.evictObject();
        CodeRepositoryCache.codeToCatalogMap.evictObject();
        CodeRepositoryCache.codeToDictionaryMap.evictAll();
        CodeRepositoryCache.codeToMethodMap.evictObject();
        CodeRepositoryCache.codeToOptMap.evictObject();
        CodeRepositoryCache.codeToRoleMap.evictObject();
        CodeRepositoryCache.codeToUnitMap.evictObject();
        CodeRepositoryCache.codeToUserMap.evictObject();
        CodeRepositoryCache.depNoToUnitMap.evictObject();
        CodeRepositoryCache.dictionaryRepo.evictAll();
        CodeRepositoryCache.emailToUserMap.evictObject();
        CodeRepositoryCache.idcardToUserMap.evictObject();
        CodeRepositoryCache.loginNameToUserMap.evictObject();
        CodeRepositoryCache.optInfoRepo.evictObject();
        CodeRepositoryCache.optMethodRepo.evictObject();
        CodeRepositoryCache.phoneToUserMap.evictObject();
        CodeRepositoryCache.roleInfoRepo.evictObject();
        CodeRepositoryCache.rolePowerRepo.evictObject();
        CodeRepositoryCache.roleUnitsRepo.evictAll();
        CodeRepositoryCache.roleUsersRepo.evictAll();
        CodeRepositoryCache.unitInfoRepo.evictObject();
        CodeRepositoryCache.unitRolesRepo.evictAll();
        CodeRepositoryCache.unitUsersMap.evictAll();
        CodeRepositoryCache.userInfoRepo.evictObject();
        CodeRepositoryCache.userRolesRepo.evictAll();
        CodeRepositoryCache.userUnitRepo.evictObject();
        CodeRepositoryCache.userUnitsMap.evictAll();
    }

    /**
     * 缓存用户信息
     */
    public static CachedObject<List<? extends IUserInfo>> userInfoRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUsers(),
            CACHE_FRESH_PERIOD_MINITES);
    /**
     * 派生的缓存信息，派生缓存相当于索引
     */
    public static CachedObject<Map<String, ? extends IUserInfo>> codeToUserMap =
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

    public static CachedObject<Map<String, ? extends IUserInfo>> loginNameToUserMap =
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

    public static CachedObject<Map<String, ? extends IUserInfo>> emailToUserMap  =
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

    public static CachedObject<Map<String, ? extends IUserInfo>> phoneToUserMap =
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

    public static CachedObject<Map<String, ? extends IUserInfo>> idcardToUserMap =
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
    public static CachedObject<List<? extends IUnitInfo>> unitInfoRepo =
        new CachedObject<>(()->{
            List<? extends IUnitInfo> allunits = getPlatformEnvironment().listAllUnits();
            CollectionsOpt.sortAsTree(allunits,
                ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );
            return allunits;
         }, CACHE_FRESH_PERIOD_MINITES);

    /**
     * 机构的派生缓存
     */
    public static CachedObject<Map<String, ? extends IUnitInfo>> codeToUnitMap =
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

    public static CachedObject<Map<String, ? extends IUnitInfo>> depNoToUnitMap =
        new CachedObject<>(()-> {
            List<? extends IUnitInfo> unitInfos = unitInfoRepo.getCachedObject();
            if(unitInfos == null)
                return null;
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size());
            for(IUnitInfo unitInfo : unitInfos){
                codeToUnit.put(unitInfo.getDepNo(), unitInfo);
            }
            return codeToUnit;
        },CACHE_FRESH_PERIOD_MINITES);


    public static CachedObject<List<? extends IUserUnit>> userUnitRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUserUnits(),
            CACHE_FRESH_PERIOD_MINITES);
    /**
     * 派生缓存
     */
    public static CachedMap<String, List<? extends IUserUnit>> userUnitsMap =
        new CachedMap<>(
            (userCode)-> {
                List<? extends IUserUnit> userUnits = userUnitRepo.getCachedObject();
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
            CACHE_FRESH_PERIOD_MINITES, 300);
    /**
     * 派生缓存
     */
    public static CachedMap<String, List<IUserUnit>> unitUsersMap=
        new CachedMap<>((unitCode)-> {
            List<? extends IUserUnit> userUnits = userUnitRepo.getCachedObject();
            if(userUnits == null)
                return null;
            List<IUserUnit> uus = new ArrayList<>(16);
            for(IUserUnit uu : userUnits){
                if(StringUtils.equals(unitCode, uu.getUnitCode() )){
                    uus.add( uu );
                }
            }
            return uus;
        },CACHE_FRESH_PERIOD_MINITES, 100);


    public static CachedObject<List< ? extends IDataCatalog>> catalogRepo  =
        new CachedObject<>(()-> getPlatformEnvironment().listAllDataCatalogs(),
            CACHE_FRESH_PERIOD_MINITES);
    /**
     * 派生缓存，避免对象重复
     */
    public static CachedObject<Map<String, ? extends IDataCatalog>> codeToCatalogMap  =
        new CachedObject<>(()-> {
                Map<String, IDataCatalog> dataCatalogMap = new HashMap<>();
                List<? extends IDataCatalog> dataCatalogs = catalogRepo.getCachedObject();
                if(dataCatalogs==null)
                    return dataCatalogMap;
                for( IDataCatalog dataCatalog : dataCatalogs){
                    dataCatalogMap.put(dataCatalog.getCatalogCode(), dataCatalog);
                }
                return dataCatalogMap;
            },
            CACHE_FRESH_PERIOD_MINITES);


    public static CachedMap<String, List<? extends IDataDictionary>> dictionaryRepo =
        new CachedMap<>((sCatalog)->  getPlatformEnvironment().listDataDictionaries(sCatalog),
            CACHE_FRESH_PERIOD_MINITES );

    public static CachedMap<String, Map<String,? extends IDataDictionary>> codeToDictionaryMap =
        new CachedMap<>((sCatalog)-> {
                Map<String, IDataDictionary> dataDictionaryMap = new HashMap<>();
                List<? extends IDataDictionary> dataDictionarys = dictionaryRepo.getCachedObject(sCatalog);
                if(dataDictionarys==null)
                    return dataDictionaryMap;
                for( IDataDictionary data : dataDictionarys){
                    dataDictionaryMap.put(data.getDataCode(), data);
                }
                return dataDictionaryMap;
            },
            CACHE_FRESH_PERIOD_MINITES);


    public static CachedObject<List<? extends IRoleInfo>> roleInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllRoleInfo(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IRoleInfo>> codeToRoleMap=
            new CachedObject<>(()-> {
                Map<String, IRoleInfo> codeMap = new HashMap<>();
                List<? extends IRoleInfo> roleInfos = roleInfoRepo.getCachedObject();
                if(roleInfos==null)
                    return codeMap;
                for( IRoleInfo data : roleInfos){
                    codeMap.put(data.getRoleCode(), data);
                }
                return codeMap;
            },
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<List<? extends IOptInfo>> optInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptInfo(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IOptInfo>> codeToOptMap=
        new CachedObject<>(()-> {
            Map<String, IOptInfo> codeMap = new HashMap<>();
            List<? extends IOptInfo> optInfos = optInfoRepo.getCachedObject();
            if(optInfos==null)
                return codeMap;
            for( IOptInfo data : optInfos){
                codeMap.put(data.getOptId(), data);
            }
            return codeMap;
        }, CACHE_FRESH_PERIOD_MINITES);


    public static CachedObject<List<? extends IOptMethod>> optMethodRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptMethod(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IOptMethod>> codeToMethodMap=
        new CachedObject<>(()-> {
            Map<String, IOptMethod> codeMap = new HashMap<>();
            List<? extends IOptMethod> methods = optMethodRepo.getCachedObject();
            if(methods==null)
                return codeMap;
            for( IOptMethod data : methods){
                codeMap.put(data.getOptCode(), data);
            }
            return codeMap;
        }, CACHE_FRESH_PERIOD_MINITES);


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
        new CachedObject<>(()-> getPlatformEnvironment().listAllRolePower(),
            CACHE_FRESH_PERIOD_MINITES);
}
