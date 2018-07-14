package com.centit.framework.components;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.CachedMap;
import com.centit.support.common.CachedObject;
import com.centit.support.common.DerivativeCachedMap;
import com.centit.support.common.ICachedObject;
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
            case "RoleInfo":
                CodeRepositoryCache.roleInfoRepo.evictCahce();
                //CodeRepositoryCache.codeToRoleMap.evictCahce();
                break;
            case "RolePower":
                CodeRepositoryCache.rolePowerRepo.evictCahce();
                break;
        }
    }

    public static void evictCache(String cacheName){
        CodeRepositoryCache.evictCache(cacheName, null);
    }

    public static void evictAllCache(){
        CodeRepositoryCache.userInfoRepo.evictCahce();
        CodeRepositoryCache.unitInfoRepo.evictCahce();
        CodeRepositoryCache.userUnitRepo.evictCahce();
        CodeRepositoryCache.catalogRepo.evictCahce();
        CodeRepositoryCache.dictionaryRepo.evictCahce();
        CodeRepositoryCache.optInfoRepo.evictCahce();
        CodeRepositoryCache.optMethodRepo.evictCahce();
        CodeRepositoryCache.roleInfoRepo.evictCahce();
        CodeRepositoryCache.rolePowerRepo.evictCahce();
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
            List<? extends IUserInfo> userInfos = userInfoRepo.getCachedTarget();
            if(userInfos == null)
                return null;
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
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
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
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
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
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
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
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
            Map<String, IUserInfo> codeToUser = new HashMap<>(userInfos.size());
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
         }, CACHE_FRESH_PERIOD_MINITES);

    /**
     * 机构的派生缓存
     */
    public static CachedObject<Map<String, ? extends IUnitInfo>> codeToUnitMap =
        new CachedObject<>(()-> {
            List<? extends IUnitInfo> unitInfos = unitInfoRepo.getCachedTarget();
            if(unitInfos == null)
                return null;
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size());
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
            Map<String, IUnitInfo> codeToUnit = new HashMap<>(unitInfos.size());
            for(IUnitInfo unitInfo : unitInfos){
                codeToUnit.put(unitInfo.getDepNo(), unitInfo);
            }
            return codeToUnit;
        },unitInfoRepo);


    public static CachedObject<List<? extends IUserUnit>> userUnitRepo =
        new CachedObject<>(()-> getPlatformEnvironment().listAllUserUnits(),
            CACHE_FRESH_PERIOD_MINITES);
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
        new CachedObject<>(()-> getPlatformEnvironment().listAllDataCatalogs(),
            CACHE_FRESH_PERIOD_MINITES);
    /**
     * 派生缓存，避免对象重复
     */
    public static CachedObject<Map<String, ? extends IDataCatalog>> codeToCatalogMap  =
        new CachedObject<>(()-> {
                Map<String, IDataCatalog> dataCatalogMap = new HashMap<>();
                List<? extends IDataCatalog> dataCatalogs = catalogRepo.getCachedTarget();
                if(dataCatalogs==null)
                    return dataCatalogMap;
                for( IDataCatalog dataCatalog : dataCatalogs){
                    dataCatalogMap.put(dataCatalog.getCatalogCode(), dataCatalog);
                }
                return dataCatalogMap;
            },
            catalogRepo);


    public static CachedMap<String, List<? extends IDataDictionary>> dictionaryRepo =
        new CachedMap<>((sCatalog)->  getPlatformEnvironment().listDataDictionaries(sCatalog),
            CACHE_FRESH_PERIOD_MINITES );

    public static DerivativeCachedMap<String,List<? extends IDataDictionary>,
            Map<String,? extends IDataDictionary>> codeToDictionaryMap =
        new DerivativeCachedMap<>( (dataDictionarys )-> {
                Map<String, IDataDictionary> dataDictionaryMap = new HashMap<>();
                if(dataDictionarys==null)
                    return dataDictionaryMap;
                for( IDataDictionary data : dataDictionarys){
                    dataDictionaryMap.put(data.getDataCode(), data);
                }
                return dataDictionaryMap;
            },
            dictionaryRepo, 100);


    public static CachedObject<List<? extends IRoleInfo>> roleInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllRoleInfo(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IRoleInfo>> codeToRoleMap=
            new CachedObject<>(()-> {
                Map<String, IRoleInfo> codeMap = new HashMap<>();
                List<? extends IRoleInfo> roleInfos = roleInfoRepo.getCachedTarget();
                if(roleInfos==null)
                    return codeMap;
                for( IRoleInfo data : roleInfos){
                    codeMap.put(data.getRoleCode(), data);
                }
                return codeMap;
            }, roleInfoRepo);

    public static CachedObject<List<? extends IOptInfo>> optInfoRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptInfo(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IOptInfo>> codeToOptMap=
        new CachedObject<>(()-> {
            Map<String, IOptInfo> codeMap = new HashMap<>();
            List<? extends IOptInfo> optInfos = optInfoRepo.getCachedTarget();
            if(optInfos==null)
                return codeMap;
            for( IOptInfo data : optInfos){
                codeMap.put(data.getOptId(), data);
            }
            return codeMap;
        }, optInfoRepo);


    public static CachedObject<List<? extends IOptMethod>> optMethodRepo=
        new CachedObject<>(()-> getPlatformEnvironment().listAllOptMethod(),
            CACHE_FRESH_PERIOD_MINITES);

    public static CachedObject<Map<String, ? extends IOptMethod>> codeToMethodMap=
        new CachedObject<>(()-> {
            Map<String, IOptMethod> codeMap = new HashMap<>();
            List<? extends IOptMethod> methods = optMethodRepo.getCachedTarget();
            if(methods==null)
                return codeMap;
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
        new CachedObject<>(()-> getPlatformEnvironment().listAllRolePower(),
            CACHE_FRESH_PERIOD_MINITES);
}
