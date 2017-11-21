package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.*;
import com.centit.support.common.CachedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

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

    public static PlatformEnvironment getPlatformEnvironment() {
        if(platformEnvironment==null)
            platformEnvironment = getCtxBean("platformEnvironment", PlatformEnvironment.class);
        return platformEnvironment;
    }

    public static CachedObject<Map<String, ? extends IUserInfo>> codeToUserMap =
            new CachedObject<>(()-> getPlatformEnvironment().getUserRepo(),15);

    public static Map<String, ? extends IUserInfo> loginNameToUserMap;
    public static Map<String, ? extends IUserInfo> emailToUserMap;
    public static Map<String, ? extends IUserInfo> phoneToUserMap;
    public static Map<String, ? extends IUserInfo> idcardToUserMap;

    public static Map<String, ? extends IUnitInfo> codeToUnitMap;

    public static Map<String, List<? extends IUserUnit>> userUnitsMap;
    public static Map<String, List<? extends IUserUnit>> unitUsersMap;

    public static Map<String, List<? extends IRoleInfo>> codeToRoleMap;

    public static Map<String, List<? extends IUserRole>> userRolesMap;
    public static Map<String, List<? extends IUserRole>> roleUsersMap;

    public static Map<String, List<? extends IOptMethod>> userMethodsMap;
    public static Map<String, List<? extends IOptMethod>> roleMethodsMap;

    public static Map<String, ? extends IDataCatalog> codeToCatalogMap;
    public static Map<String, Map<String,? extends IDataDictionary>> codeToDictionaryMap;


    public static Map<String, ? extends IUserInfo> getCodeToUserMap() {
        return codeToUserMap.getCachedObject();
    }

    public static Map<String, ? extends IUserInfo> getLoginNameToUserMap() {
        return loginNameToUserMap;
    }

    public static Map<String, ? extends IUserInfo> getEmailToUserMap() {
        return emailToUserMap;
    }

    public static Map<String, ? extends IUserInfo> getPhoneToUserMap() {
        return phoneToUserMap;
    }

    public static Map<String, ? extends IUserInfo> getIdcardToUserMap() {
        return idcardToUserMap;
    }

    public static Map<String, ? extends IUnitInfo> getCodeToUnitMap() {
        return codeToUnitMap;
    }

    public static Map<String, List<? extends IUserUnit>> getUserUnitsMap() {
        return userUnitsMap;
    }

    public static Map<String, List<? extends IUserUnit>> getUnitUsersMap() {
        return unitUsersMap;
    }

    public static Map<String, List<? extends IRoleInfo>> getCodeToRoleMap() {
        return codeToRoleMap;
    }

    public static Map<String, List<? extends IUserRole>> getUserRolesMap() {
        return userRolesMap;
    }

    public static Map<String, List<? extends IOptMethod>> getUserMethodsMap() {
        return userMethodsMap;
    }

    public static Map<String, List<? extends IUserRole>> getRoleUsersMap() {
        return roleUsersMap;
    }

    public static Map<String, List<? extends IOptMethod>> getRoleMethodsMap() {
        return roleMethodsMap;
    }

    public static Map<String, ? extends IDataCatalog> getCodeToCatalogMap() {
        return codeToCatalogMap;
    }

    public static Map<String, Map<String, ? extends IDataDictionary>> getCodeToDictionaryMap() {
        return codeToDictionaryMap;
    }
}
