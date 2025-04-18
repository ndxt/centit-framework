package com.centit.framework.components;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.common.OptionItem;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedMap;
import com.centit.support.common.CachedObject;
import com.centit.support.common.ListAppendMap;
import com.centit.support.compiler.Lexer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cp标签实现类，并可以通过静态方法直接调用系统缓存
 *
 * @author codefan@sina.com
 * 2015-11-3
 */
@SuppressWarnings("unused")
public abstract class CodeRepositoryUtil {
    public static final String TOP_UNIT_NO_TENANT = "root";
    public static final String SYSTEM_ADMIN_TENANT = "system";

    public static final String USER_CODE = "userCode";
    public static final String UNIT_CODE = "unitCode";
    public static final String ROLE_CODE = "roleCode";
    public static final String OS_ID = "osId";
    public static final String OPT_ID = "optId";
    public static final String OPT_CODE = "optCode";
    public static final String T = "T";
    public static final String F = "F";

    public static boolean cacheByTopUnit = true;

    public static void setCacheByTopUnit(boolean cacheByTopUnit) {
        CodeRepositoryUtil.cacheByTopUnit = cacheByTopUnit;
    }

    private CodeRepositoryUtil(){
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(CodeRepositoryUtil.class);

    public final static Map<String, CachedObject<Map<String, String>>> extendedCodeRepo =
        new ConcurrentHashMap<>(16);

    public static boolean hasExtendedDictionary(String dataCatalog){
        return extendedCodeRepo.containsKey(dataCatalog);
    }

    public static void registeExtendedCodeRepo(String catalog, Map<String, String> repo){
        CachedObject<Map<String, String>> cachedObject =
            new CachedObject<>(()-> repo, CachedObject.NOT_REFRESH_PERIOD * 1000);
        cachedObject.setFreshData(repo);
        extendedCodeRepo.put(catalog, cachedObject);
    }

    public static void registeExtendedCodeRepo(String catalog, CachedObject<Map<String, String>> repo){
        extendedCodeRepo.put(catalog, repo);
    }

    /**
     * 获取数
     *
     * @param sCatalog 字典类别代码
     * @return List 数据字典
     */
    public static List<DataDictionary> getDictionary(String sCatalog) {
        ListAppendMap<DataDictionary> listAppendMap = CodeRepositoryCache.dictionaryRepo
            .getCachedValue(sCatalog);
        if(listAppendMap==null) return Collections.emptyList();
        return listAppendMap.getListData();
    }

    public static List<UnitInfo> listAllUnits(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        ListAppendMap<UnitInfo> listAppendMap = CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit);
        if(listAppendMap==null) return Collections.emptyList();
        return listAppendMap.getListData();
    }

    public static Map<String, UnitInfo> getUnitRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        ListAppendMap<UnitInfo> listAppendMap = CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit);
        if(listAppendMap==null) return Collections.emptyMap();
        return listAppendMap.getAppendMap();
    }

    public static List<UserInfo> listAllUsers(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        ListAppendMap<UserInfo> listAppendMap = CodeRepositoryCache.userInfoRepo
            .getCachedValue(topUnit);
        if(listAppendMap==null) return Collections.emptyList();
        return listAppendMap.getListData();
    }

    public static List<UserUnit> listAllUserUnits(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.userUnitRepo.getCachedValue(topUnit);
    }

    public static Map<String,UserInfo> getUserRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        ListAppendMap<UserInfo> listAppendMap = CodeRepositoryCache.userInfoRepo
            .getCachedValue(topUnit);
        if(listAppendMap!=null){
            return listAppendMap.getAppendMap();
        }
        return Collections.emptyMap();
    }

    public static List<UserUnit> listUserUnits(String topUnit, String userCode) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.userUnitsMap
                    .getCachedValue(topUnit)
                .getCachedValue(userCode);
    }

    public static List<UserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryCache.unitUsersMap.getCachedValue(unitCode);
    }

    public static List<OsInfo> listOsInfo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.osInfoCache.getCachedValue(topUnit);
    }

    public static CachedMap<String, List<UserRole>> getUserRolesRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.userRolesRepo
            .getCachedValue(topUnit);
    }

    public static CachedMap<String, List<UserRole>> getRoleUsersRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.roleUsersRepo
            .getCachedValue(topUnit);
    }

    public static List<RoleInfo> listAllRole(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.roleInfoRepo.getCachedValue(topUnit);
    }

    public static List<OptInfo> getOptInfoRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.optInfoRepo.getCachedValue(topUnit);
    }

    public static ListAppendMap<OptMethod> getOptMethodRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.optMethodRepo.getCachedValue(topUnit);
    }

    public static Map<String, List<OptDataScope>> getOptDataScopeRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.optDataScopeRepo.getCachedValue(topUnit);
    }

    public static Map<String, List<RolePower>> getRolePowerRepo(String topUnit) {
        topUnit = CodeRepositoryUtil.cacheByTopUnit ? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        return CodeRepositoryCache.rolePowerMap.getCachedValue(topUnit);
    }

    /**
     * 获取用户在某个职务的用户组列表
     * @param topUnit 用户代码
     * @param userCode 用户代码
     * @param rank 职务代码
     * @return 用户组列表
     */
    public static List<UserUnit> listUserUnitsByRank(String topUnit, String userCode, String rank){
        List<UserUnit> result = new ArrayList<>();
        for(UserUnit un: listUserUnits(topUnit, userCode)){
            if(Objects.equals(un.getUserRank(),rank)){
                result.add(un);
            }
        }
        return result;
    }

    /**
     * 获取用户在某个岗位的用户组列表
     * @param topUnit 用户代码
     * @param userCode 用户代码
     * @param station 岗位代码
     * @return 用户组列表
     */
    public static List<UserUnit> listUserUnitsByStation(String topUnit, String userCode, String station){
        List<UserUnit> result = new ArrayList<>();
        for(UserUnit un: listUserUnits(topUnit, userCode)){
            if(Objects.equals(un.getUserStation(),station)){
                result.add(un);
            }
        }
        return result;
    }

    /**
     * 获取某个机构下某个岗位的用户组列表
     * @param unitCode 机构代码
     * @param station 岗位代码
     * @return 用户组列表 当unitCode没有对应机构或station没有对应岗位时返回空列表
     */
    public static List<UserUnit> listUserUnitsByUnitStation(String unitCode, String station){
        List<UserUnit> result = new ArrayList<>();
        for(UserUnit un: listUnitUsers(unitCode)){
            if(Objects.equals(un.getUserStation(),station)){
                result.add(un);
            }
        }
        return result;
    }

    /**
     * 获取数据字典对应的值，
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     *         userCode 用户信息 unitCode机构信息
     *         roleCode 角色信息 optId 业务信息
     * @param sKey     字典代码
     * @return  数据字典对应的值
     */
    public static String getValue(String sCatalog, String sKey) {
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        return getValue(sCatalog, sKey,
            request == null ? GlobalConstValue.NO_TENANT_TOP_UNIT : WebOptUtils.getCurrentTopUnit(request),
            request == null ? "zh_CN" : WebOptUtils.getCurrentLang(request)
        );
    }
    /**
     * 获取数据字典对应的值，
     *
     * @param sCatalog 字典类别代码
     * @param sKey     字典代码
     * @param localLang String类型
     * @return 数据字典对应的值
     */
    public static String getValue(String sCatalog, String sKey, String topUnit, String localLang) {

        if(sCatalog.startsWith("userInfo.")){
            UserInfo ui= getUserRepo(topUnit).get(sKey);
            if(ui==null)
                return sKey;
            return StringBaseOpt.castObjectToString(
                ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)));
        }

        if(sCatalog.startsWith("unitInfo.")){
            UnitInfo ui= getUnitRepo(topUnit).get(sKey);
            if(ui==null)
                return sKey;
            return StringBaseOpt.castObjectToString(
                ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)));
        }

        try {
            switch (sCatalog) {
                case CodeRepositoryUtil.USER_CODE:{
                    UserInfo ui= getUserRepo(topUnit).get(sKey);
                    if(ui==null)
                        return sKey;
                    return ui.getUserName();
                }

                case "userOrder":{
                    UserInfo ui= getUserRepo(topUnit).get(sKey);
                    if(ui==null)
                        return "0";
                    return ui.getUserOrder() == null ? "0" :
                        String.valueOf(ui.getUserOrder());
                }

                case "loginName":{
                    List<UserInfo> userInfos = listAllUsers(topUnit);
                    if(userInfos==null)
                        return sKey;
                    for(UserInfo userInfo:userInfos){
                        if(StringUtils.equals(sKey, userInfo.getLoginName()))
                            return userInfo.getUserName();
                    }
                    return sKey;
                }

                case CodeRepositoryUtil.UNIT_CODE:{
                    UnitInfo ui=getUnitRepo(topUnit).get(sKey);
                    if(ui==null)
                        return sKey;
                    return ui.getUnitName();
                }

                case CodeRepositoryUtil.ROLE_CODE: {
                    List<RoleInfo> roleInfos = listAllRole(topUnit);
                    if (roleInfos == null) {
                        return sKey;
                    }
                    for (RoleInfo roleInfo : roleInfos) {
                        if(StringUtils.equals(sKey, roleInfo.getRoleCode())){
                            return roleInfo.getRoleName();
                        }
                    }
                    return sKey;
                }

                case CodeRepositoryUtil.OPT_ID:{
                    List<OptInfo> optInfos = getOptInfoRepo(topUnit);
                    if (optInfos == null) {
                        return sKey;
                    }
                    for (OptInfo optInfo : optInfos) {
                        if(StringUtils.equals(sKey, optInfo.getOptId())){
                            return optInfo.getLocalOptName();
                        }
                    }
                    return sKey;
                }

                case CodeRepositoryUtil.OS_ID: {
                    List<OsInfo> osInfos = listOsInfo(topUnit);
                    if(osInfos==null){
                        return sKey;
                    }
                    for (OsInfo osInfo : osInfos){
                        if(StringUtils.equals(sKey, osInfo.getOsId())){
                            return osInfo.getOsName();
                        }
                    }
                    return sKey;
                }

                default:
                    CachedObject<Map<String, String>> extendRepo = extendedCodeRepo.get(sCatalog);
                    if(extendRepo != null){
                        String svalue = extendRepo.getCachedTarget().get(sKey);
                        return svalue != null ? svalue : sKey;
                    }
                    DataDictionary dictPiece = getDataPiece(sCatalog, sKey, topUnit);
                    if (dictPiece == null) {
                        return sKey;
                    }
                    return dictPiece.getLocalDataValue(localLang);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return sKey;
        }
    }

    /**
     * 转换租户初始化字典catalog
     *
     * @param sCatalog 需要被转换的catalog
     * @param topUnit  租户机构
     * @return
     */
    private static String transformTenantInitCatalog(String sCatalog, String topUnit) {
        switch (sCatalog) {
            case GlobalConstValue.DATA_CATALOG_UESR_TYPE:
                sCatalog = topUnit + GlobalConstValue.DATA_CATALOG_UESR_TYPE_SUFFIX;
                break;
            case GlobalConstValue.DATA_CATALOG_UNIT_TYPE:
                sCatalog = topUnit + GlobalConstValue.DATA_CATALOG_UNIT_TYPE_SUFFIX;
                break;
            case GlobalConstValue.DATA_CATALOG_RANK:
                sCatalog = topUnit + GlobalConstValue.DATA_CATALOG_RANK_SUFFIX;
                break;
            case GlobalConstValue.DATA_CATALOG_STATION:
                sCatalog = topUnit + GlobalConstValue.DATA_CATALOG_STATION_SUFFIX;
                break;
            case GlobalConstValue.DATA_CATALOG_POSTRANK:
                sCatalog = topUnit + GlobalConstValue.DATA_CATALOG_POSTRANK_SUFFIX;
                break;
        }
        return sCatalog;
    }

    /**
     * 通过数据字典值 或者 代码
     *
     * @param sCatalog 数据字典代码
     * @param sValue   数据字典值
     * @return 部门编码映射key值
     */
    public static String getCode(String sCatalog, String sValue) {
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        return getCode(sCatalog, sValue,
            request == null ? GlobalConstValue.NO_TENANT_TOP_UNIT : WebOptUtils.getCurrentTopUnit(request),
            request == null ? "zh_CN" : WebOptUtils.getCurrentLang(request));
    }

    public static String getCode(String sCatalog, String sValue, String topUnit, String localLang) {
        if (StringUtils.isBlank(sValue)) {
            logger.info("sValue 为空中空字符串");
            return "";
        }
        if(sCatalog.startsWith("userInfo.")){
            List<UserInfo> usList= listAllUsers(topUnit);
            if(usList==null)
                return sValue;
            for(UserInfo ui : usList){
                if(StringUtils.equals(sValue, StringBaseOpt.castObjectToString(
                    ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)))))
                    return ui.getUserCode();
            }
            return sValue;
        }

        if(sCatalog.startsWith("unitInfo.")){
            List<UnitInfo> uuList= listAllUnits(topUnit);
            if(uuList==null)
                return sValue;
            for(UnitInfo ui : uuList){
                if(StringUtils.equals(sValue, StringBaseOpt.castObjectToString(
                    ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)))))
                    return ui.getUnitCode();
            }
            return sValue;
        }

        try {
            switch (sCatalog) {
                case CodeRepositoryUtil.USER_CODE:{
                    List<UserInfo> usList= listAllUsers(topUnit);
                    if(usList==null)
                        return sValue;
                    for(UserInfo ui : usList){
                        if(StringUtils.equals(sValue, ui.getUserName()))
                            return ui.getUserCode();
                    }
                    return sValue;
                }
                case CodeRepositoryUtil.UNIT_CODE:{
                    List<UnitInfo> uuList= listAllUnits(topUnit);
                    if(uuList==null)
                        return sValue;
                    for(UnitInfo ui : uuList){
                        if(StringUtils.equals(sValue, ui.getUnitName()))
                            return ui.getUnitCode();
                    }
                    return sValue;
                }
                default:
                    CachedObject<Map<String, String>> extendRepo = extendedCodeRepo.get(sCatalog);
                    if(extendRepo != null){
                        for(Map.Entry<String, String> ent : extendRepo.getCachedTarget().entrySet()) {
                            if(StringUtils.equals(ent.getValue(), sValue)){
                                return ent.getKey();
                            }
                        }
                        return sValue;
                    }
                    DataDictionary dictPiece = getDataPieceByValue(sCatalog, sValue, localLang);
                    if (dictPiece == null) {
                        return sValue;
                    }
                    return dictPiece.getDataCode();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return sValue;
        }
    }

    /**
     * 把表达式中的字典代码都 转换为 数据字典值，其他的字符 位置不变，
     *
     * @param sCatalog    数据字典代码
     * @param sExpression 表达式
     * @return 达式中的字典代码 转换为 数据字典值
     */
    public static String transExpression(String sCatalog, String sExpression) {
        StringBuilder sb = new StringBuilder();
        Lexer lex = new Lexer();
        lex.setFormula(sExpression);

        while (true) {
            String aWord = lex.getAString();
            if (StringUtils.isBlank(aWord)) {
                break;
            }
            aWord = getValue(sCatalog, aWord);
            sb.append(aWord);
        }

        return sb.toString();
    }
    public static String transExpression(String sCatalog, String sExpression, String topUnit, String localLang) {
        StringBuilder sb = new StringBuilder();
        Lexer lex = new Lexer();
        lex.setFormula(sExpression);
        while (true) {
            String aWord = lex.getAString();
            if (StringUtils.isBlank(aWord)) {
                break;
            }
            aWord = getValue(sCatalog, aWord, topUnit, localLang);
            sb.append(aWord);
        }
        return sb.toString();
    }
    /**
     * 获得数据字典条目的状态
     *
     * @param sCatalog 字典类别代码
     * @param sKey     字典代码
     * @param topUnit
     * @return 数据字典条目的状态
     */
    public static String getItemState(String sCatalog, String sKey,String topUnit) {
        try {
            DataDictionary dictPiece = getDataPiece(sCatalog, sKey,topUnit);
            if (dictPiece == null) {
                return "";
            }
            return dictPiece.getDataTag()==null?"N":dictPiece.getDataTag();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return sKey;
        }
    }

    /**
     * 按类别获取 业务定义信息
     * @param topUnit 顶级菜单目录
     * @return List 业务定义信息
     */
    public static List<OptInfo> getOptinfoList(String topUnit,String superOptId) {
        List<OptInfo> allOpts = getOptInfoRepo(topUnit);
        List<OptInfo> optList = new ArrayList<>();
        if("A".equals(superOptId)){
            optList.addAll(allOpts);
        }else {
            for (OptInfo optInfo : allOpts) {
                if (superOptId.equals(optInfo.getTopOptId())) {
                    optList.add(optInfo);
                }
            }
        }
        Collections.sort(optList, (o1,o2) -> // Long.compare(o1.getOrderInd() , o2.getOrderInd())) ;
            ( o2.getOrderInd() == null && o1.getOrderInd() == null)? 0 :
                ( (o2.getOrderInd() == null)? 1 :
                    (( o1.getOrderInd() == null)? -1 :
                        ((o1.getOrderInd() > o2.getOrderInd())? 1:(
                            o1.getOrderInd() < o2.getOrderInd()?-1:0 ) ))));
        return optList;
    }

    /**
     * 获得一个业务下面的操作定义
     * @param topUnit applicationId 应用的ID
     * @param sOptID optId
     * @return List 一个业务下面的操作定义
     */
    public static List<OptMethod> getOptMethodByOptID(String topUnit, String sOptID) {
        List<OptMethod> optList = new ArrayList<>();
        for (OptMethod value : getOptMethodRepo(topUnit).getListData()) {
            if (sOptID.equals(value.getOptId())) {
                optList.add(value);
            }
        }
        return optList;
    }

    /**
     * 获取一个机构下面的所有以这个机构为主机构的用户，并且根据排序号排序
     *
     * @param unitCode unitCode
     * @return List 一个机构下面的所有以这个机构为主机构的用户
     */
    public static List<UserInfo> getSortedPrimaryUnitUsers(String topUnit, String unitCode) {
        List<UserUnit> unitUsers = listUnitUsers(unitCode);
        if (null == unitUsers) {
            return null;
        }

        List<UserInfo> users = new ArrayList<>();
        for (UserUnit uu :unitUsers) {
            // 归属部门 或者 借入部门
            if (!"T".equals(uu.getRelType()) &&
                !"I".equals(uu.getRelType())) {
                continue;
            }

            UserInfo user = getUserRepo(topUnit).get(uu.getUserCode());
            if (user != null) {
                if (CodeRepositoryUtil.T.equals(user.getIsValid())) {
                    if (!users.contains(user)) {
                        users.add(user);
                    }
                }
            }
        }

        Collections.sort(users, (o1, o2) ->
            (o1.getUserOrder() == null && o2.getUserOrder() == null) ? 0 :
                ((o1.getUserOrder() == null) ? 1 :
                    ((o2.getUserOrder() == null) ? -1 :
                        ((o1.getUserOrder() > o2.getUserOrder()) ? 1 :
                            ((o1.getUserOrder() < o2.getUserOrder()) ? -1 : 0)))));

        return users;
    }

    /**
     * 获取一个机构下面的所有用户，并且根据排序号排序
     *
     * @param unitCode unitCode
     * @return List 一个机构下面的所有用户
     */
    public static List<UserInfo> getSortedUnitUsers(String topUnit, String unitCode) {
        List<UserUnit> unitUsers = listUnitUsers(unitCode);
        if (null == unitUsers) {
            return null;
        }

        List<UserInfo> users = new ArrayList<>();
        for (UserUnit uu :unitUsers) {
            UserInfo user = getUserRepo(topUnit).get(uu.getUserCode());
            if (user != null) {
                if (CodeRepositoryUtil.T.equals(user.getIsValid())) {
                    if (!users.contains(user)) {
                        users.add(user);
                    }
                }
            }
        }

        Collections.sort(users, (o1, o2) ->
            (o1.getUserOrder() == null && o2.getUserOrder() == null) ? 0 :
                (o1.getUserOrder() == null ? 1 :
                    (o2.getUserOrder() == null ? -1 :
                        (o1.getUserOrder() > o2.getUserOrder() ? 1 :
                            (o1.getUserOrder() < o2.getUserOrder() ? -1 : 0)))));
        return users;
    }

    /**
     * 获取机构下面的所有下级机构，并且排序
     *
     * @param unitCode unitCode
     * @param unitType unitType
     * @return List 机构下面的所有下级机构
     */
    public static List<UnitInfo> getSortedSubUnits(String topUnit, String unitCode, String unitType) {
        List<UnitInfo> units = new ArrayList<>();

        UnitInfo ui = CodeRepositoryUtil.getUnitRepo(topUnit).get(unitCode);
        for (UnitInfo unit : CodeRepositoryUtil.getSubUnits(topUnit, ui.getUnitCode())) {
            if (unit != null) {
                if (CodeRepositoryUtil.T.equals(unit.getIsValid())
                    && (unitType == null || "A".equals(unitType)
                    || unitType.contains(unit.getUnitType()))) {
                    units.add(unit);
                }
            }
        }

        units.sort((o1, o2) ->
            (o1.getUnitOrder() == null && o2.getUnitOrder() == null) ? 0 :
                (o1.getUnitOrder() == null ? 1 :
                    (o2.getUnitOrder() == null ? -1 :
                        (o1.getUnitOrder() > o2.getUnitOrder() ? 1 :
                            (o1.getUnitOrder() < o2.getUnitOrder() ? -1 :0)))));
        return units;
    }

    /**
     * 获取一个机构所有用户，没有排序
     *
     * @param unitCode unitCode
     * @return Set 一个机构所有用户
     */
    public static Set<UserInfo> getUnitUsers(String topUnit, String unitCode) {
        List<UserUnit> uus = CodeRepositoryUtil.listUnitUsers(unitCode);
        Set<UserInfo> users = new HashSet<>();
        for (UserUnit uu : uus) {
            UserInfo user = CodeRepositoryUtil.getUserRepo(topUnit).get(uu.getUserCode());
            if (user != null) {
                if (CodeRepositoryUtil.T.equals(user.getIsValid())) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    /**
     * 获取一个用户所有机构，没有排序
     *
     * @param userCode userCode
     * @return Set 一个用户所有机构
     */
    public static Set<UnitInfo> getUserUnits(String topUnit, String userCode) {

        List<UserUnit> uus = listUserUnits(topUnit, userCode);
        Set<UnitInfo> units = new HashSet<>();
        if(uus==null)
            return units;
        for (UserUnit uu : uus) {
            UnitInfo unit = getUnitRepo(topUnit).get(uu.getUnitCode());
            if (unit != null) {
                if (CodeRepositoryUtil.T.equals(unit.getIsValid())) {
                    units.add(unit);
                }
            }
        }
        return units;
    }


    /**
     * listRoleUserByRoleCode(roleCode);
     * 获取 拥有指定角色的所有用户
     * @param roleCode 角色代码
     * @return 返回拥有这个角色的所有用户
     */
    public static List<UserInfo> listUsersByRoleCode(String topUnit, String roleCode) {
        List<UserRole> userRoles =getRoleUsersRepo(topUnit)
                .getCachedValue(roleCode);
        if(userRoles==null){
            return null;
        }
        Map<String,UserInfo> userRepo = getUserRepo(topUnit);
        List<UserInfo> userInfos = new ArrayList<>(userRoles.size()+1);
        for(UserRole ur : userRoles){
            userInfos.add(userRepo.get(ur.getUserCode()));
        }
        return userInfos;
    }

    /**
     * listUserRolesByUserCode
     * 获取用户拥有的角色
     * @param userCode 用户代码
     * @return 返回该用户拥有的所有角色，包括从机构继承来的角色
     */
    public static List<String> listRolesByUserCode(String topUnit, String userCode) {
        List<UserRole> roleUsers =  getUserRolesRepo(topUnit).getCachedValue(userCode);
        if(roleUsers==null){
            return null;
        }
        return CollectionsOpt.mapCollectionToList(roleUsers, (ur) -> ur.getRoleCode());
    }

    /**
     * 获取 用户角色关系
     * @param roleCode 角色代码
     * @return 返回拥有这个角色的所有用户
     */
    public static List<UserRole> listRoleUsers(String topUnit, String roleCode) {
        return getRoleUsersRepo(topUnit).getCachedValue(roleCode);
    }

    /**
     * 获取 角色用户关系
     * @param userCode 用户代码
     * @return 返回该用户拥有的所有角色，包括从机构继承来的角色
     */
    public static List<UserRole> listUserRoles(String topUnit, String userCode) {
        return getUserRolesRepo(topUnit).getCachedValue(userCode);
    }

    /**
     * 判断用户是否具有角色
     * @param userCode 用户代码
     * @param roleCode 角色代码
     * @return 返回该用户拥有的所有角色，包括从机构继承来的角色
     */
    public static boolean checkUserRole(String topUnit, String userCode, String roleCode) {
        List<UserRole> userRoles = getUserRolesRepo(topUnit)
                .getCachedValue(userCode);
        if (userRoles != null) {
            for (UserRole ur : userRoles) {
                if (roleCode.equals(ur.getRoleCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据用户号获得用户信息
     *
     * @param userCode userCode
     * @return  用户信息
     */
    public static UserInfo getUserInfoByCode(String topUnit, String userCode) {
        return getUserRepo(topUnit).get(userCode);
    }

    /**
     * 根据用户代码获取用户名称
     *
     * @param userCode 用户代码
     * @return 用户名称
     */
    public static String getUserName(String topUnit, String userCode) {
        UserInfo userInfo = getUserRepo(topUnit).get(userCode);
        if (userInfo == null) {
            return "";
        }
        return userInfo.getUserName();
    }

    /**
     * 根据用户代码获取机构信息
     * @param sUsers 用户代码集合
     * @return 用户信息
     */
    public static List<UserInfo> getUserInfosByCodes(String topUnit, Collection<String> sUsers) {
        Map<String, UserInfo> allUsers = getUserRepo(topUnit);
        List<UserInfo> users = new ArrayList<>();
        for(String uc : sUsers){
            UserInfo userInfo = allUsers.get(uc);
            if(userInfo!=null) {
                users.add(userInfo);
            }
        }
        return users;
    }

    public static UserUnit getUserPrimaryUnit(String topUnit, String userCode) {
        List<UserUnit> uus = listUserUnits(topUnit, userCode);
        if(uus==null || uus.size()<1)
            return null;
        UserUnit primaryUnit = uus.get(0);
        for (UserUnit uu : uus) {
            if ("T".equals(uu.getRelType())) {
                primaryUnit = uu;
            }
        }
        return primaryUnit;
    }

    public static String getUserPrimaryUnitCode(String topUnit, String userCode) {
        UserUnit primaryUnit = getUserPrimaryUnit(topUnit, userCode);
        if(primaryUnit==null)
            return null;
        return primaryUnit.getUnitCode();
    }

    /**
     * 获取用户行政角色
     *
     * @param userCode userCode
     * @param unitCode 机构代码如果是 null 系统会默认的找用户的主机构
     * @return 用户行政角色
     */
    public static String getUserUnitXzRank(String topUnit, String userCode, String unitCode) {
        if (userCode == null) {
            return UserUnit.MAX_XZ_RANK;
        }
        UserInfo ui = getUserRepo(topUnit).get(userCode);
        if (ui == null) {
            return UserUnit.MAX_XZ_RANK;
        }
        String rankUnitCode = (unitCode == null) ? ui.getPrimaryUnit() : unitCode;
        if (StringUtils.isBlank(rankUnitCode)) {
            return UserUnit.MAX_XZ_RANK;
        }

        List<UserUnit> uus = listUserUnits(topUnit, userCode);
        String nRank = UserUnit.MAX_XZ_RANK;
        for (UserUnit uu : uus) {
            if (StringUtils.equals(rankUnitCode, uu.getUnitCode())
                && StringUtils.compare(uu.getPostRank(), nRank) < 0) {
                nRank = uu.getPostRank();
            }
        }
        return nRank;
    }

    /**
     * 获得已知机构 下级的所有有效机构并返回map
     *
     * @param sParentUnit sParentUnit
     * @return Map 已知机构下级的所有有效机构
     */
    public static Map<String, UnitInfo> getUnitMapByParaent(String topUnit, String sParentUnit) {
        Map<String, UnitInfo> units = new HashMap<>();

        for (Map.Entry<String,UnitInfo> ent : getUnitRepo(topUnit).entrySet()) {
            UnitInfo value = ent.getValue();
            if (CodeRepositoryUtil.T.equals(value.getIsValid()) && sParentUnit.equals(value.getParentUnit())) {
                units.put(ent.getKey(), value);
            }
        }
        return units;
    }

    /**
     * 根据机构代码获取机构信息
     *
     * @param sUnit sUnit 机构代码
     * @return 机构信息
     */
    public static UnitInfo getUnitInfoByCode(String topUnit, String sUnit) {
        //TODO 修改成 从缓存获取后判空，如果为null调用platformEnvironment对应的接口
        //这个代码 写道 CodeRepositoryCache 中
        return getUnitRepo(topUnit).get(sUnit);
    }

    /**
     * 根据机构代码获取机构信息
     * @param sUnits sUnit 机构代码集合
     * @return 机构信息
     */
    public static List<UnitInfo>  getUnitInfosByCodes(String topUnit, Collection<String> sUnits) {
        Map<String, UnitInfo> allUnts = getUnitRepo(topUnit);
        List<UnitInfo> units = new ArrayList<>();
        for(String uc : sUnits){
            UnitInfo unitInfo = allUnts.get(uc);
            if(unitInfo!=null) {
                units.add(unitInfo);
            }
        }
        return units;
    }

    /**
     * 根据机构代码获取本机构的上级机构信息
     * @param sUnit sUnit 机构代码
     * @return 上级结构机构信息
     */
    public static UnitInfo getParentUnitInfo(String topUnit, String sUnit) {
        UnitInfo uinfo = getUnitRepo(topUnit).get(sUnit);
        if(uinfo==null){
            return null;
        }
        return getUnitRepo(topUnit).get(uinfo.getParentUnit());
    }

    /**
     * 根据机构代码获取本机构的符合要求（类别或者标签相符）的上级机构信息
     * @param sUnit sUnit 机构代码
     * @param typeOrTag 结构类别或者标签
     * @return 上级结构机构信息
     */
    public static UnitInfo getParentUnitInfo(String topUnit, String sUnit,String typeOrTag) {
        UnitInfo uinfo = getUnitRepo(topUnit).get(sUnit);
        while(uinfo!=null){
            if(typeOrTag.equals(uinfo.getUnitType()) ||
                SysUnitFilterEngine.matchUnitTag(typeOrTag, uinfo.getUnitTag())){
                return uinfo;
            }
            String puc = uinfo.getParentUnit();
            if ((puc == null) || ("0".equals(puc)) || ("".equals(puc))) {
                return null;
            }
            uinfo = getUnitRepo(topUnit).get(puc);
        }
        return null;//getUnitRepo().get(uinfo.getParentUnit());
    }
    /**
     * 根据状态获取所有机构信息，
     *
     * @param sState A表示所有状态
     * @return List 所有机构信息
     */
    public static List<UnitInfo> getAllUnits(String topUnit, String sState) {

        List<UnitInfo> allunits = CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit).getListData();
        /*缓存中已经排好序了
        CollectionsOpt.sortAsTree(allunits,
                ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );*/

        List<UnitInfo> units = new ArrayList<>();
        if("A".equals(sState)){
            units.addAll(allunits);
            return units;
        }

        for (UnitInfo unit : allunits) {
            if (sState.equals(unit.getIsValid())) {
                units.add(unit);
            }
        }
        return units;
    }

    /**
     * 根据状态获取所有机构信息，
     *
     * @param sState A表示所有状态
     * @return List 所有机构信息
     */
    public static List<UserInfo> getAllUsers(String topUnit, String sState) {

        List<UserInfo> allUsers = CodeRepositoryCache.userInfoRepo
            .getCachedValue(topUnit).getListData();

        List<UserInfo> users = new ArrayList<>();
        if("A".equals(sState)){
            users.addAll(allUsers);
            return users;
        }

        for (UserInfo user : allUsers) {
            if (sState.equals(user.getIsValid())) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * 获得已知机构 下级的所有机构并返回map，包括失效的机构
     *
     * @param sParentUnit sParentUnit
     * @return Map 已知机构下级的所有机构，包括失效的机构
     */
    public static Map<String, UnitInfo> getAllUnitMapByParaent(String topUnit, String sParentUnit) {
        Map<String, UnitInfo> units = new HashMap<>();

        for (Map.Entry<String,UnitInfo> ent : getUnitRepo(topUnit).entrySet()) {
            UnitInfo value = ent.getValue();
            if (sParentUnit.equals(value.getParentUnit())) {
                units.put(ent.getKey(), value);
            }
        }
        return units;
    }

    /**
     * 获得已知机构 下级的所有有效机构并返回map，包括下级机构的下级机构
     *
     * @param sParentUnit sParentUnit
     * @return Map 已知机构下级的所有有效机构，包括下级机构的下级机构
     */
    public static Map<String, UnitInfo> getUnitMapBuyParaentRecurse(String topUnit, String sParentUnit) {
        Map<String, UnitInfo> units = new HashMap<>();
        List<String> sParentUnits = new ArrayList<>();
        List<String> sNewUnits = new ArrayList<>();
        sParentUnits.add(sParentUnit);

        while (sParentUnits.size() > 0) {
            sNewUnits.clear();
            for (int i = 0; i < sParentUnits.size(); i++) {
                String sPNC = sParentUnits.get(i);
                for (Map.Entry<String, UnitInfo> ent : getUnitRepo(topUnit).entrySet()) {
                    UnitInfo value = ent.getValue();
                    if (CodeRepositoryUtil.T.equals(value.getIsValid()) && sPNC.equals(value.getParentUnit())) {
                        units.put(ent.getKey(), value);
                        sNewUnits.add(ent.getKey());
                    }
                }
            }
            List<String> tempList = sParentUnits;
            sParentUnits = sNewUnits;
            sNewUnits = tempList;
        }

        return units;
    }

    /**
     * 获取数据字典，并整理为json机构
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @return 数据字典，整理为json机构
     */
    public static String getDictionaryAsJson(String sCatalog) {
        List<DataDictionary> lsDictionary = getDictionary(sCatalog);

        List<Map<String, Object>> dataMap = new ArrayList<>();
        for (DataDictionary dict : lsDictionary) {
            Map<String, Object> dm = new HashMap<>();
            dm.put("id", dict.getDataCode());
            dm.put("pId", dict.getExtraCode());
            dm.put("name", dict.getDataValue());
            dm.put("t", dict.getDataValue());
            dm.put("right", false);
            dataMap.add(dm);
        }
        return JSONObject.toJSONString(dataMap);
    }



    /**
     * 获取数据字典 ，忽略 tag 为 'D'的条目 【delete】
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @return 数据字典,忽略 tag 为 'D'的条目
     */
    public static List<DataDictionary> getDictionaryIgnoreD(String sCatalog) {
        List<DataDictionary> dcRetMap = new ArrayList<>();
        List<DataDictionary> dcMap = getDictionary(sCatalog);
        if (dcMap != null) {
            for (DataDictionary value : dcMap) {
                if (!"D".equals(value.getDataTag())) {// getDatatag
                    //value.setDataValue(value.getLocalDataValue(lang));
                    dcRetMap.add(value);
                }
            }
        }
        return dcRetMap;
    }

    /**
     * 获取 数据字典 key value 对
     * @param sCatalog 数据字典类别，或者系统内置的类别
     *         userCode 用户信息 unitCode机构信息
     *         roleCode 角色信息 optId 业务信息
     * @return Map  数据字典
     */
    public static Map<String, String> getAllLabelValueMap(String sCatalog){
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        return innerGetLabelValueMap(sCatalog,
            WebOptUtils.getCurrentTopUnit(request),
            WebOptUtils.getCurrentLang(request), true);
    }

    /**
     * 获取 数据字典 key value
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @param localLang localLang
     * @return Map  数据字典
     */
    public static Map<String,String> getAllLabelValueMap(String topUnit, String sCatalog, String localLang) {
        return innerGetLabelValueMap(sCatalog, topUnit, localLang, true);
    }

    /**
     * 获取 数据字典 key value 对， 忽略 禁用的 条目
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     *         userCode 用户信息 unitCode机构信息
     *         roleCode 角色信息 optId 业务信息
     * @return  数据字典,忽略禁用的条目
     */
    public static Map<String,String> getLabelValueMap(String sCatalog){
        HttpServletRequest request = RequestThreadLocal.getLocalThreadWrapperRequest();
        return innerGetLabelValueMap(sCatalog,
            WebOptUtils.getCurrentTopUnit(request),
            WebOptUtils.getCurrentLang(request), false);
    }

    /**
     * 获取 数据字典 key value 对， 忽略 禁用的 条目
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @param localLang localLang
     * @return 数据字典,忽略禁用的条目
     */
    public static Map<String,String> getLabelValueMap(String topUnit, String sCatalog, String localLang) {
        return innerGetLabelValueMap(sCatalog, topUnit, localLang, false);
    }

    private static Map<String,String> innerGetLabelValueMap(String sCatalog, String topUnit, String localLang, boolean allItem) {
        Map<String,String> lbvs = new HashMap<>();

        if(sCatalog.startsWith("userInfo.")){
            for (Map.Entry<String, UserInfo> ent : getUserRepo(topUnit).entrySet()) {
                UserInfo value = ent.getValue();
                if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.put(value.getUserCode(),
                        StringBaseOpt.castObjectToString(
                            ReflectionOpt.getFieldValue(value, sCatalog.substring(9))));
                }
            }
            return lbvs;
        }

        if(sCatalog.startsWith("unitInfo.")){
            for (UnitInfo value : listAllUnits(topUnit)) {
                if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.put(value.getUnitCode(),
                        StringBaseOpt.castObjectToString(
                            ReflectionOpt.getFieldValue(value, sCatalog.substring(9))));
                }
            }
            return lbvs;
        }

        switch (sCatalog) {
            case CodeRepositoryUtil.USER_CODE: {
                for (Map.Entry<String, UserInfo> ent : getUserRepo(topUnit).entrySet()) {
                    UserInfo value = ent.getValue();
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getUserCode(), value.getUserName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.UNIT_CODE: {
                for (UnitInfo value : listAllUnits(topUnit)) {
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getUnitCode(), value.getUnitName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.ROLE_CODE: {
                List<RoleInfo> roleInfos = listAllRole(topUnit);
                if (roleInfos != null) {
                    for (RoleInfo roleInfo : roleInfos) {
                        lbvs.put(roleInfo.getRoleCode(), roleInfo.getRoleName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OPT_ID:{
                List<OptInfo> optInfos = getOptInfoRepo(topUnit);
                for (OptInfo optInfo : optInfos) {
                    lbvs.put(optInfo.getOptId(), optInfo.getLocalOptName());
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OS_ID: {
                List<OsInfo> osInfos = listOsInfo(topUnit);
                if (osInfos != null) {
                    for (OsInfo osInfo : osInfos) {
                        lbvs.put(osInfo.getOsId(), osInfo.getOsName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OPT_CODE: {
                for (OptMethod value :
                        getOptMethodRepo(topUnit).getListData()) {
                    lbvs.put(value.getOptCode(), value.getOptName());
                }
                return lbvs;
            }

            default:
                CachedObject<Map<String, String>> extendRepo = extendedCodeRepo.get(sCatalog);
                if(extendRepo != null){
                    return extendRepo.getCachedTarget();
                }
                sCatalog = transformTenantInitCatalog(sCatalog, topUnit);
                List<DataDictionary> dcMap = getDictionary(sCatalog);
                if (dcMap != null) {
                    for (DataDictionary value : dcMap) {
                        if (allItem || (value.getDataTag() != null && !"D".equals(value.getDataTag()))) {
                            lbvs.put(value.getDataCode(), value.getLocalDataValue(localLang));
                        }
                    }

                }
                return lbvs;
        }
    }

    /**
     * 获取 数据字典 key value 对， 忽略 禁用的 条目
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @param localLang localLang
     * @return 数据字典,忽略禁用的条目
     */
    public static List<OptionItem> getOptionForSelect(String topUnit, String sCatalog, String localLang) {
        List<OptionItem> lbvs = new ArrayList<>();

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.USER_CODE)) {
            for (Map.Entry<String,UserInfo> ent : getUserRepo(topUnit).entrySet()) {
                UserInfo value = ent.getValue();
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getUserName(),
                        value.getUserCode(), value.getPrimaryUnit()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.UNIT_CODE)) {
            for (UnitInfo value : listAllUnits(topUnit)) {
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getUnitName(),
                        value.getUnitCode(), value.getParentUnit()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.ROLE_CODE)) {
            List<RoleInfo> roleInfos = listAllRole(topUnit);
            if (roleInfos != null) {
                for (RoleInfo roleInfo : roleInfos) {
                    lbvs.add(new OptionItem(roleInfo.getRoleName(), roleInfo.getRoleCode()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OPT_ID)) {
            List<OptInfo> optInfos = getOptInfoRepo(topUnit);
            if (optInfos != null) {
                for (OptInfo optInfo : optInfos) {
                    lbvs.add(new OptionItem(optInfo.getLocalOptName(), optInfo.getOptId()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OS_ID)) {
            List<OsInfo> osInfos = listOsInfo(topUnit);
            if (osInfos != null) {
                for (OsInfo osInfo : osInfos) {
                    lbvs.add(new OptionItem(osInfo.getOsName(), osInfo.getOsId()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OPT_CODE)) {
            for (OptMethod value : getOptMethodRepo(topUnit).getListData()) {
                lbvs.add(new OptionItem(value.getOptName(), value.getOptCode(),value.getOptId()));
            }
            return lbvs;
        }

        List<DataDictionary> dcMap = getDictionary(sCatalog);
        if (dcMap != null) {
            for (DataDictionary value : dcMap) {
                if (value.getDataTag() != null && !"D".equals(value.getDataTag())) {
                    lbvs.add(new OptionItem(value.getLocalDataValue(localLang) ,
                        value.getDataCode(),value.getExtraCode()));
                }
            }
        }
        return lbvs;
    }

    /**
     * 获取字典条目
     *
     * @param sCatalog 字典类别代码
     * @param sKey     字典代码
     * @return 字典条目
     */
    public static DataDictionary getDataPiece(String sCatalog, String sKey, String topUnit) {
        if (!GlobalConstValue.NO_TENANT_TOP_UNIT.equals(topUnit) && StringUtils.isNotBlank(topUnit)) {
            sCatalog = transformTenantInitCatalog(sCatalog, topUnit);
        }
        Map<String, DataDictionary> dcMap =
            CodeRepositoryCache.dictionaryRepo.getCachedValue(sCatalog).getAppendMap();
        return dcMap==null? null : dcMap.get(sKey);
    }

    /**
     * 获取字典条目
     *
     * @param sCatalog 字典类别代码
     * @param sValue   字典值
     * @return 字典条目
     */
    public static DataDictionary getDataPieceByValue(String sCatalog, String sValue, String localLang) {
        List<DataDictionary> dcList = getDictionary(sCatalog);
        if (dcList == null) {
            return null;
        }

        for (DataDictionary fd : dcList) {
            if (StringUtils.equals(fd.getLocalDataValue(localLang),sValue)) {
                return fd;
            }
        }
        return null;
    }

    /**
     * 根据机构代码获取机构名称
     *
     * @param unitCode unitCode
     * @return 机构名称
     */
    public static String getUnitName(String topUnit, String unitCode) {
        UnitInfo unitInfo = getUnitRepo(topUnit).get(unitCode);
        if (unitInfo == null) {
            return "";
        }
        return unitInfo.getUnitName();
    }

    /**
     * 获取机构的下级机构
     *
     * @param allunits List allunits
     * @param parentUnitCode parentUnitCode
     * @return 机构的下级机构
     */
    private static List<UnitInfo> fetchSubUnits(List<UnitInfo> allunits,
                                                 String parentUnitCode) {
        if(allunits==null)
            return null;
        List<UnitInfo> units = new ArrayList<>();
        if(StringUtils.isBlank(parentUnitCode)){
            for (UnitInfo uc : allunits) {
                if (StringUtils.isBlank(uc.getParentUnit()) &&
                    CodeRepositoryUtil.T.equals(uc.getIsValid())) {
                    units.add(uc);
                }
            }
        } else {
            for (UnitInfo uc : allunits) {
                if (parentUnitCode.equals(uc.getParentUnit()) &&
                    CodeRepositoryUtil.T.equals(uc.getIsValid())) {
                    units.add(uc);
                }
            }
        }
        return units;
    }

    /**
     * 获取机构的下级机构
     *
     * @param unitCode unitCode
     * @return 机构的下级机构
     */
    public static List<UnitInfo> getSubUnits(String topUnit, String unitCode) {
        List<UnitInfo> units = CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit).getListData();
        return fetchSubUnits(units, unitCode);
    }
    /**
     * 获取机构的下级机构，并按照树形排列
     *
     * @param unitCode unitCode
     * @return 机构的下级机构,并按照树形排列
     */
    public static List<UnitInfo> fetchAllSubUnits(String topUnit, String unitCode, boolean includeRoot) {
        List<UnitInfo> allunits = listAllUnits(topUnit);
        List<UnitInfo> units = new ArrayList<>();
        if(includeRoot){
            units.add(getUnitInfoByCode(topUnit, unitCode));
        }
        List<UnitInfo> subunits = fetchSubUnits(allunits, unitCode);
        while( subunits!=null && subunits.size()>0){
            units.addAll(subunits);
            List<UnitInfo> subunits1 = new ArrayList<>();
            for(UnitInfo u1: subunits){
                List<UnitInfo> subunits2 = fetchSubUnits(allunits, u1.getUnitCode());
                if(subunits2!=null)
                    subunits1.addAll(subunits2);
            }
            subunits = subunits1;
        }
        CollectionsOpt.sortAsTree(units,
            (p,c) -> StringUtils.equals(p.getUnitCode(), c.getParentUnit()));
        return units;
    }

    public static List<UnitInfo> getAllSubUnits(String topUnit, String unitCode) {
        return fetchAllSubUnits(topUnit, unitCode, false);
    }

    public static Set<String> listUnitAllUsers(String topUnit, String unitCode, boolean includeSubUnit) {
        List<UserUnit> userUnits = CodeRepositoryUtil.listUnitUsers(unitCode);

        Set<String> users = new HashSet<>(100);
        if(userUnits!=null) {
            for (UserUnit user : userUnits) {
                users.add(user.getUserCode());
            }
        }
        if(includeSubUnit) {
            List<UnitInfo> unitInfos = CodeRepositoryUtil.getAllSubUnits(topUnit, unitCode);
            if(unitInfos != null){
                for (UnitInfo unit : unitInfos) {
                    userUnits = CodeRepositoryUtil.listUnitUsers(unit.getUnitCode());
                    if(userUnits!=null) {
                        for (UserUnit user : userUnits) {
                            users.add(user.getUserCode());
                        }
                    }
                }
            }
        }
        return users;
    }

    /**
     * 验证当前用户是否有某个操作方法的权限
     *
     * @param optId optId
     * @param OptMethod OptMethod
     * @param request HttpServletRequest
     * @return 操作方法的权限
     */
    public static boolean checkUserOptPower(String optId, String OptMethod, HttpServletRequest request) {
        Object userDetails = WebOptUtils.getLoginUser(request);
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).checkOptPower(optId, OptMethod);
        }
        return false;
    }

    /**
     * 获取用户所有的 操作方法
     * @return 返回一个map，key为optid+‘-’+method value 为 'T'
     */

    public static Map<String,String> getUserAllOptPowers(HttpServletRequest request) {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUserOptList();
    }

    /**
     * 获取System.properties文件属性值
     * @return System.properties文件属性值
     */
    private static Properties loadProperties() {
        /*try{
            return PropertiesLoaderUtils.loadAllProperties("system.properties");
        } catch (IOException e) {*/
        Properties prop = new Properties();
        try (InputStream resource = CodeRepositoryUtil.class.getResourceAsStream("/system.properties")){
            if(resource == null) {
                try(InputStream resource2 = ClassLoader.getSystemResourceAsStream("/system.properties")){
                    if(resource2 != null) {
                        prop.load(resource2);
                    }
                }
            }else {
                prop.load(resource);
            }
        } catch (IOException e2) {
            logger.error("获取系统参数出错！" + e2.getMessage());
        }
        return prop;
        //}
    }

    public static String getSysConfigValue(String key) {
        Properties properties = loadProperties();//.getProperty(key);
        String value= properties.getProperty(key);
        if(StringUtils.isNotBlank(value)){
            PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${","}");
            value = helper.replacePlaceholders(value, properties);
        }
        return value;
    }

    public static Map<String, Object> getSysConfigByPrefix(String prefix){
        Properties properties = loadProperties();
        Map<String, Object> map = new HashMap<>(16);
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${","}");
        for(Map.Entry<Object,Object> ent :  properties.entrySet()){
            String paramName = StringBaseOpt.castObjectToString(ent.getKey());
            if(StringUtils.startsWithIgnoreCase(paramName, prefix)){
                if(ent.getValue() instanceof String) {
                    map.put(paramName, helper.replacePlaceholders((String)ent.getValue(), properties));
                } else {
                    map.put(paramName, ent.getValue());
                }
            }
        }
        return map;
    }

    /*
     * 判断用户权限不能调用这个接口，这个仅仅是用于 用户过滤器，主要用于工作流的权限引擎
     */
    public static List<String> listAllUserRoles(String topUnit, String sUserCode){
        List<String> userRoles = new ArrayList<>(20);
        userRoles.add(SecurityContextUtils.PUBLIC_ROLE_CODE);
        List<UserRole> urs = getUserRolesRepo(topUnit).getCachedValue(sUserCode);
        if(urs!=null && urs.size()>0) {
            for (UserRole ur : urs) {
                userRoles.add(ur.getRoleCode());
            }
        }
        /*List<UserUnit> uus = CodeRepositoryCache.userUnitsMap
            .getCachedValue(topUnit).getCachedValue(sUserCode);
        if(uus!=null && uus.size()>0) {
            for (UserUnit uu : uus) {
                List<UnitRole> uurs = CodeRepositoryCache.unitRolesRepo.getCachedValue(uu.getUnitCode());
                if (uurs != null && uurs.size() > 0) {
                    for (IUnitRole ur : uurs) {
                        userRoles.add(ur.getRoleCode());
                    }
                }
            }
        }*/
        return userRoles;
    }

    public static List<String> listUserDataFiltersByOptIdAndMethod(
        String topUnit, String sUserCode, String sOptId, String sOptMethod) {
        Map<String, List<OptDataScope>> optDataScope = getOptDataScopeRepo(topUnit);
        if(optDataScope==null){
            return null;
        }
        List<OptDataScope> odss = optDataScope.get(sOptId);
        if(odss == null || odss.size()<1){
            return null;
        }

        Set<String> dataScopes = new HashSet<>();
        List<String> allUserRole = listAllUserRoles(topUnit, sUserCode);

        for (String rolecode : allUserRole) {
            //RoleInfo ri = CodeRepositoryCache.roleInfoRepo.getCachedTarget().get(rolecode);
            if(getRolePowerRepo(topUnit).get(rolecode)!=null) {
                for (RolePower rp : getRolePowerRepo(topUnit).get(rolecode)) {
                    // 需要过滤掉 不是 sOptId 下面的方式（不过滤也不会影响结果）; 但是这个过滤可能并不能提高效率
                    OptMethod om = getOptMethodRepo(topUnit)
                        .getAppendMap().get(rp.getOptCode());
                    if (om != null) {
                        if (StringUtils.equals(sOptId, om.getOptId()) && StringUtils.equals(om.getOptMethod(), sOptMethod)) {
                            String[] oscs = rp.getOptScopeCodeSet();
                            if (oscs != null) {
                                Collections.addAll(dataScopes, oscs);
                            }
                        }
                    }
                }
            }
        }

        List<String> filters = new ArrayList<>();
        for(OptDataScope ods : odss){
            if(dataScopes.contains(ods.getOptScopeCode())){
                filters.add(ods.getFilterCondition());
            }
        }
        if(filters.size()>0){
            return filters;
        }
        return null;
    }

    /**
     * 获取框架中注册的业务系统
     * @param osId osId
     * @return 框架中注册的业务系统
     */
    public static OsInfo getOsInfo(String topUnit, String osId){
        List<OsInfo> osInfos = listOsInfo(topUnit);
        if(osInfos!=null) {
            for (OsInfo osInfo : osInfos) {
                if (StringUtils.equals(osId, osInfo.getOsId())) {
                    return osInfo;
                }
            }
        }
        return null;
    }
    public final static String OPT_INFO_FORM_CODE_COMMON = "C";
    public static OptInfo getCommonOptId(String topUnit,String optId){
        topUnit = CodeRepositoryUtil.cacheByTopUnit? topUnit : GlobalConstValue.NO_TENANT_TOP_UNIT;
        List<OptInfo> optInfos = CodeRepositoryUtil.getOptInfoRepo(topUnit);
        if (CollectionUtils.sizeIsEmpty(optInfos)){
            return null;
        }
        OptInfo optInfo=optInfos.stream().filter(key-> key.getOptId().equals(optId)).findAny().orElse(null);
        if(optInfo!=null){
            if(OPT_INFO_FORM_CODE_COMMON.equals(optInfo.getFormCode())){
                return optInfo;
            }
            List<OptInfo> osOptInfos=CodeRepositoryUtil.getOptinfoList(topUnit,optInfo.getTopOptId());
            return osOptInfos.stream().filter(key-> OPT_INFO_FORM_CODE_COMMON.equals(key.getFormCode())).findAny().orElse(null);
        }
        return null;
    }

}
