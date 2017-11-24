package com.centit.framework.model.adapter;

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
@Deprecated
public abstract class SystemDataRepository {

    private final static int CACHE_FRESH_PERIOD_MINITES = 15;
    private SystemDataRepository()
    {
        throw new IllegalAccessError("Utility class");
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SystemDataRepository.class);

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
                    (key)-> getPlatformEnvironment().listUserUnits(key)
                    ,CACHE_FRESH_PERIOD_MINITES * 2, 300);

    private static CachedObject<Map<String, List<IUserUnit>>> unitUsersMap=
            new CachedObject<>(()-> {
                List<? extends IUserUnit> userUnits = userUnitsRepo.getCachedObject();
                if(userUnits == null)
                    return null;
                Map<String, List<IUserUnit>> unitToUser =
                        new HashMap<>(userUnits.size() >10?userUnits.size()/4:10);
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

    private static CachedObject<Map<String,  ? extends IRoleInfo>> codeToRoleMap  =
            new CachedObject<>(()-> getPlatformEnvironment().getRoleRepo(),
                    CACHE_FRESH_PERIOD_MINITES);

    private static CachedObject<Map<String, ? extends IDataCatalog>> codeToCatalogMap;

    private static CachedObject<Map<String, Map<String,? extends IDataDictionary>>> codeToDictionaryMap;

    private static CachedObject<Map<String, List<IUserRole>>> userRolesMap;

    private static CachedObject<Map<String, List<IUserRole>>> roleUsersMap;

    private static CachedObject<Map<String, List<IUnitRole>>> unitRolesMap;

    private static CachedObject<Map<String, List<IUnitRole>>> roleUnitsMap;

    private static CachedObject<Map<String, List<IOptMethod>>> userMethodsMap;

    private static CachedObject<Map<String, List<IOptMethod>>> roleMethodsMap;

    public static List<? extends IUserInfo> getUserInfoRepo() {
        return userInfoRepo.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getCodeToUserMap() {
        return codeToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getLoginNameToUserMap() {
        return loginNameToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getEmailToUserMap() {
        return emailToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getPhoneToUserMap() {
        return phoneToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getIdcardToUserMap() {
        return idcardToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUnitInfo> getCodeToUnitMap() {
        return codeToUnitMap.getCachedObject();
    }

    public static List<? extends IUnitInfo> getUnitInfoRepo() {
        return unitInfoRepo.getCachedObject();
    }

    public static List<? extends IUserUnit> getUserUnitsMap(String userCode) {
        return userUnitsMap.getCachedObject(userCode);
    }

    public static Map<String, List<IUserUnit>> getUnitUsersMap() {
        return unitUsersMap.getCachedObject();
    }

    public static Map<String, ? extends IRoleInfo> getCodeToRoleMap() {
        return codeToRoleMap.getCachedObject();
    }

    public static Map<String, List<IUserRole>> getUserRolesMap() {
        return userRolesMap.getCachedObject();
    }

    public static Map<String, List<IOptMethod>> getUserMethodsMap() {
        return userMethodsMap.getCachedObject();
    }

    public static Map<String, List<IUserRole>> getRoleUsersMap() {
        return roleUsersMap.getCachedObject();
    }

    public static Map<String, List<IOptMethod>> getRoleMethodsMap() {
        return roleMethodsMap.getCachedObject();
    }

    public static Map<String, ? extends IDataCatalog> getCodeToCatalogMap() {
        return codeToCatalogMap.getCachedObject();
    }

    public static Map<String, Map<String, ? extends IDataDictionary>> getCodeToDictionaryMap() {
        return codeToDictionaryMap.getCachedObject();
    }

    public static Map<String, List<IUnitRole>> getUnitRolesMap() {
        return unitRolesMap.getCachedObject();
    }

    public static Map<String, List<IUnitRole>> getRoleUnitsMap() {
        return roleUnitsMap.getCachedObject();
    }
}
