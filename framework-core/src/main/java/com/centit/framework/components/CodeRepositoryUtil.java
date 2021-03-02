package com.centit.framework.components;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.OptionItem;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedObject;
import com.centit.support.compiler.Lexer;
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

    @Deprecated
    public static final int MAXXZRANK = IUserUnit.MAX_XZ_RANK;

    private CodeRepositoryUtil(){
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(CodeRepositoryUtil.class);

    public static List<? extends IOsInfo> listOsInfo(String topUnit) {
        return CodeRepositoryCache.osInfoCache.getCachedValue(topUnit);
    }

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
    public static List<? extends IDataDictionary> getDictionary(String sCatalog) {
        return CodeRepositoryCache.dictionaryRepo
            .getCachedValue(sCatalog).getListData();
    }

    public static List<? extends IUnitInfo> listAllUnits(String topUnit) {
        return CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit).getListData();
    }

    public static Map<String, ? extends IUnitInfo> getUnitRepo(String topUnit) {
        return CodeRepositoryCache.unitInfoRepo
            .getCachedValue(topUnit).getAppendMap();
    }

    public static List<? extends IUserInfo> listAllUsers(String topUnit) {
        return CodeRepositoryCache.userInfoRepo
            .getCachedValue(topUnit).getListData();
    }

    public static Map<String,? extends IUserInfo> getUserRepo(String topUnit) {
        return CodeRepositoryCache.userInfoRepo
            .getCachedValue(topUnit).getAppendMap();
    }

    public static List<? extends IUserUnit> listUserUnits(String userCode) {
        return CodeRepositoryCache.userUnitsMap.getCachedValue(userCode);
    }

    public static List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryCache.unitUsersMap.getCachedValue(unitCode);
    }

    /**
     * 获取用户在某个职务的用户组列表
     * @param userCode 用户代码
     * @param rank 职务代码
     * @return 用户组列表
     */
    public static List<? extends IUserUnit> listUserUnitsByRank(String userCode, String rank){
        List<IUserUnit> result = new ArrayList<>();
        for(IUserUnit un: listUserUnits(userCode)){
            if(Objects.equals(un.getUserRank(),rank)){
                result.add(un);
            }
        }
        return result;
    }

    /**
     * 获取用户在某个岗位的用户组列表
     * @param userCode 用户代码
     * @param station 岗位代码
     * @return 用户组列表
     */
    public static List<? extends IUserUnit> listUserUnitsByStation(String userCode, String station){
        List<IUserUnit> result = new ArrayList<>();
        for(IUserUnit un: listUserUnits(userCode)){
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
    public static List<? extends IUserUnit> listUserUnitsByUnitStation(String unitCode, String station){
        List<IUserUnit> result = new ArrayList<>();
        for(IUserUnit un: listUnitUsers(unitCode)){
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
        return getValue(sCatalog,sKey,
            WebOptUtils.getCurrentTopUnit(request),
            WebOptUtils.getCurrentLang(request)
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
            IUserInfo ui= getUserRepo(topUnit).get(sKey);
            if(ui==null)
                return sKey;
            return StringBaseOpt.castObjectToString(
                ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)));
        }

        if(sCatalog.startsWith("unitInfo.")){
            IUnitInfo ui= getUnitRepo(topUnit).get(sKey);
            if(ui==null)
                return sKey;
            return StringBaseOpt.castObjectToString(
                ReflectionOpt.getFieldValue(ui, sCatalog.substring(9)));
        }

        try {
            switch (sCatalog) {
                case CodeRepositoryUtil.USER_CODE:{
                    IUserInfo ui= getUserRepo(topUnit).get(sKey);
                    if(ui==null)
                        return sKey;
                    return ui.getUserName();
                }
                case "userOrder":{
                    IUserInfo ui= getUserRepo(topUnit).get(sKey);
                    if(ui==null)
                        return "0";
                    return ui.getUserOrder() == null ? "0" :
                        String.valueOf(ui.getUserOrder());
                }

                case CodeRepositoryUtil.UNIT_CODE:{
                    IUnitInfo ui=getUnitRepo(topUnit).get(sKey);
                    if(ui==null)
                        return sKey;
                    return ui.getUnitName();
                }

                case CodeRepositoryUtil.OS_ID: {
                    List<? extends IOsInfo> osInfos = listOsInfo(topUnit);
                    if(osInfos==null){
                        return sKey;
                    }
                    for (IOsInfo osInfo : osInfos){
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

                    IDataDictionary dictPiece = getDataPiece(sCatalog, sKey);
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
     * 通过数据字典值 或者 代码
     *
     * @param sCatalog 数据字典代码
     * @param sValue   数据字典值
     * @return 部门编码映射key值
     */
    public static String getCode(String sCatalog, String sValue) {
        if (StringUtils.isBlank(sValue)) {
            logger.info("sValue 为空中空字符串");
            return "";
        }
        try{
            CachedObject<Map<String, String>> extendRepo = extendedCodeRepo.get(sCatalog);
            if(extendRepo != null){
                for(Map.Entry<String, String> ent : extendRepo.getCachedTarget().entrySet()) {
                    if(StringUtils.equals(ent.getValue(),sValue)){
                        return ent.getKey();
                    }
                }
                return sValue;
            }

            IDataDictionary dictPiece = getDataPieceByValue(sCatalog, sValue);
            if (dictPiece == null) {
                return sValue;
            }
            return dictPiece.getDataCode();
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
            String aWord = lex.getAWord();
            if (StringUtils.isBlank(aWord)) {
                break;
            }
            aWord = getValue(sCatalog, aWord);
            sb.append(aWord);
        }

        return sb.toString();
    }

    /**
     * 获得数据字典条目的状态
     *
     * @param sCatalog 字典类别代码
     * @param sKey     字典代码
     * @return 数据字典条目的状态
     */
    public static String getItemState(String sCatalog, String sKey) {
        try {
            IDataDictionary dictPiece = getDataPiece(sCatalog, sKey);
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
     *
     * @param superOptId 顶级菜单目录
     * @return List 业务定义信息
     */
    public static List<IOptInfo> getOptinfoList(String superOptId) {
        List<IOptInfo> optList = new ArrayList<>();
        optList.addAll(CodeRepositoryCache.optInfoRepo.getCachedValue(superOptId));

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
     * @param superOptId applicationId 应用的ID
     * @param sOptID optId
     * @return List 一个业务下面的操作定义
     */
    public static List<? extends IOptMethod> getOptMethodByOptID(String superOptId, String sOptID) {
        List<IOptMethod> optList = new ArrayList<>();
        for (IOptMethod value : CodeRepositoryCache.optMethodRepo.getCachedValue(superOptId)) {
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
    public static List<IUserInfo> getSortedPrimaryUnitUsers(String unitCode) {
        List<? extends IUserUnit> unitUsers = listUnitUsers(unitCode);
        if (null == unitUsers) {
            return null;
        }

        List<IUserInfo> users = new ArrayList<>();
        for (IUserUnit uu :unitUsers) {
            if (!CodeRepositoryUtil.T.equals(uu.getIsPrimary())) {
                continue;
            }
            IUserInfo user = getUserRepo().get(uu.getUserCode());
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
    public static List<IUserInfo> getSortedUnitUsers(String unitCode) {
        List<? extends IUserUnit> unitUsers = listUnitUsers(unitCode);
        if (null == unitUsers) {
            return null;
        }

        List<IUserInfo> users = new ArrayList<>();
        for (IUserUnit uu :unitUsers) {
            IUserInfo user = getUserRepo().get(uu.getUserCode());
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
    public static List<IUnitInfo> getSortedSubUnits(String unitCode, String unitType) {
        List<IUnitInfo> units = new ArrayList<>();

        IUnitInfo ui = CodeRepositoryUtil.getUnitRepo().get(unitCode);
        for (IUnitInfo unit : CodeRepositoryUtil.getSubUnits(ui.getUnitCode())) {
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
    public static Set<IUserInfo> getUnitUsers(String unitCode) {

        List<? extends IUserUnit> uus = CodeRepositoryUtil.listUnitUsers(unitCode);
        Set<IUserInfo> users = new HashSet<>();
        for (IUserUnit uu : uus) {
            IUserInfo user = CodeRepositoryUtil.getUserRepo().get(uu.getUserCode());
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
    public static Set<IUnitInfo> getUserUnits(String userCode) {

        List<? extends IUserUnit> uus = listUserUnits(userCode);
        Set<IUnitInfo> units = new HashSet<>();
        if(uus==null)
            return units;
        for (IUserUnit uu : uus) {
            IUnitInfo unit = getUnitRepo().get(uu.getUnitCode());
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
    public static List<? extends IUserInfo> listUsersByRoleCode(String roleCode) {
        List<? extends IUserRole> userRoles = CodeRepositoryCache.roleUsersRepo.getCachedValue(roleCode);
        if(userRoles==null){
            return null;
        }
        Map<String,? extends IUserInfo> userRepo = getUserRepo();
        List<IUserInfo> userInfos = new ArrayList<>(userRoles.size()+1);
        for(IUserRole ur : userRoles){
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
    public static List<? extends IRoleInfo> listRolesByUserCode(String userCode) {
        List<? extends IUserRole> roleUsers =  CodeRepositoryCache.userRolesRepo.getCachedValue(userCode);
        if(roleUsers==null){
            return null;
        }
        Map<String,? extends IRoleInfo> roeleRepo = getRoleRepo();
        List<IRoleInfo> roleInfos = new ArrayList<>(roleUsers.size()+1);
        for(IUserRole ur : roleUsers){
            roleInfos.add(roeleRepo.get(ur.getRoleCode()));
        }
        return roleInfos;
    }

    /**
     * 获取 用户角色关系
     * @param roleCode 角色代码
     * @return 返回拥有这个角色的所有用户
     */
    public static List<? extends IUserRole> listRoleUsers(String roleCode) {
        return CodeRepositoryCache.roleUsersRepo.getCachedValue(roleCode);
    }

    /**
     * 获取 角色用户关系
     * @param userCode 用户代码
     * @return 返回该用户拥有的所有角色，包括从机构继承来的角色
     */
    public static List<? extends IUserRole> listUserRoles(String userCode) {
        return CodeRepositoryCache.userRolesRepo.getCachedValue(userCode);
    }

    /**
     * 判断用户是否具有角色
     * @param userCode 用户代码
     * @param roleCode 角色代码
     * @return 返回该用户拥有的所有角色，包括从机构继承来的角色
     */
    public static boolean checkUserRole(String userCode, String roleCode) {
        List<? extends IUserRole> userRoles = CodeRepositoryCache.userRolesRepo.getCachedValue(userCode);
        if (userRoles != null) {
            for (IUserRole ur : userRoles) {
                if (roleCode.equals(ur.getRoleCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 获取机构所有角色
     * @param unitCode 机构代码
     * @return  List 机构所有菜单功能
     * */
    public static List<? extends IRoleInfo> listUnitRolesByUnitCode(String unitCode){
        List<? extends IUnitRole> urs = CodeRepositoryCache.unitRolesRepo.getCachedValue(unitCode);
        if(urs == null || urs.size()==0)
            return null;
        List<IRoleInfo> roleInfos = new ArrayList<>(urs.size());
        Map<String, ? extends IRoleInfo> roleMap =
            CodeRepositoryUtil.getRoleRepo();
        for(IUnitRole ur : urs){
            roleInfos.add( roleMap.get(ur.getRoleCode()));
        }
        return roleInfos;
    }


    /** 获取拥有该角色的所有用户
     * @param roleCode 角色代码
     * @return  List
     * */
    public static List<? extends IUnitInfo> listRoleUnitByRoleCode(String roleCode){
        List<? extends IUnitRole> urs = CodeRepositoryCache.roleUnitsRepo.getCachedValue(roleCode);
        if(urs == null || urs.size()==0)
            return null;
        List<IUnitInfo> unitInfos = new ArrayList<>(urs.size());
        Map<String, ? extends IUnitInfo> unitInfoMap = CodeRepositoryUtil.getUnitRepo();
        for(IUnitRole ur : urs){
            unitInfos.add( unitInfoMap.get(ur.getUnitCode()));
        }
        return unitInfos;
    }

    /**
     * 获取用户所有角色
     * @param unitCode 机构代码
     * @return  List 用户所有菜单功能
     */
    public static List<? extends IUnitRole> listUnitRoles(String unitCode){
        return CodeRepositoryCache.unitRolesRepo.getCachedValue(unitCode);
    }

    /**
     * 获取拥有该角色的所有用户
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    public static List<? extends IUnitRole> listRoleUnits(String roleCode){
        return CodeRepositoryCache.roleUnitsRepo.getCachedValue(roleCode);
    }

    /**
     * 根据用户号获得用户信息
     *
     * @param userCode userCode
     * @return  用户信息
     */
    public static IUserInfo getUserInfoByCode(String userCode) {
        return getUserRepo().get(userCode);
    }

    /**
     * 根据用户代码获取用户名称
     *
     * @param userCode 用户代码
     * @return 用户名称
     */
    public static String getUserName(String userCode) {
        IUserInfo userInfo = getUserRepo().get(userCode);
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
    public static List<IUserInfo> getUserInfosByCodes(Collection<String> sUsers) {
        Map<String, ? extends IUserInfo> allUsers = getUserRepo();
        List<IUserInfo> users = new ArrayList<>();
        for(String uc : sUsers){
            IUserInfo userInfo = allUsers.get(uc);
            if(userInfo!=null) {
                users.add(userInfo);
            }
        }
        return users;
    }

    public static IUserInfo getUserInfoByLoginName(String loginName) {
        return CodeRepositoryCache.loginNameToUserMap.getCachedTarget().get(loginName);
    }

    public static IUserUnit getUserPrimaryUnit(String userCode) {
        List<? extends IUserUnit> uus = listUserUnits(userCode);
        if(uus==null || uus.size()<1)
            return null;
        IUserUnit primaryUnit = uus.get(0);
        for (IUserUnit uu : uus) {
            if ("T".equals(uu.getIsPrimary())) {
                primaryUnit = uu;
            }
        }
        return primaryUnit;
    }

    public static String getUserPrimaryUnitCode(String userCode) {
        IUserUnit primaryUnit = getUserPrimaryUnit(userCode);
        if(primaryUnit==null)
            return null;
        return primaryUnit.getUnitCode();
    }

    private static int getXzRank(String rankCode){
        IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", rankCode);
        if(dd!=null)
            return Integer.valueOf(dd.getExtraCode());
        return IUserUnit.MAX_XZ_RANK;
    }

    /**
     * 获取用户行政角色
     *
     * @param userCode userCode
     * @param unitCode 机构代码如果是 null 系统会默认的找用户的主机构
     * @return 用户行政角色
     */
    public static Integer getUserUnitXzRank(String userCode, String unitCode) {
        if (userCode == null) {
            return IUserUnit.MAX_XZ_RANK;
        }
        IUserInfo ui = getUserRepo().get(userCode);
        if (ui == null) {
            return IUserUnit.MAX_XZ_RANK;
        }
        String rankUnitCode = (unitCode == null) ? ui.getPrimaryUnit() : unitCode;
        if (StringUtils.isBlank(rankUnitCode)) {
            return IUserUnit.MAX_XZ_RANK;
        }

        List<? extends IUserUnit> uus = listUserUnits(userCode);
        Integer nRank = IUserUnit.MAX_XZ_RANK;
        for (IUserUnit uu : uus) {
            if (StringUtils.equals(rankUnitCode,uu.getUnitCode())
                && getXzRank(uu.getUserRank()) < nRank) {
                nRank = getXzRank(uu.getUserRank());
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
    public static Map<String, IUnitInfo> getUnitMapByParaent(String sParentUnit) {
        Map<String, IUnitInfo> units = new HashMap<>();

        for (Map.Entry<String,? extends IUnitInfo> ent : getUnitRepo().entrySet()) {
            IUnitInfo value = ent.getValue();
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
    public static IUnitInfo getUnitInfoByCode(String sUnit) {
        //TODO 修改成 从缓存获取后判空，如果为null调用platformEnvironment对应的接口
        //这个代码 写道 CodeRepositoryCache 中
        return getUnitRepo().get(sUnit);
    }

    /**
     * 根据机构代码获取机构信息
     * @param sUnits sUnit 机构代码集合
     * @return 机构信息
     */
    public static List<IUnitInfo>  getUnitInfosByCodes(Collection<String> sUnits) {
        Map<String, ? extends IUnitInfo> allUnts = getUnitRepo();
        List<IUnitInfo> units = new ArrayList<>();
        for(String uc : sUnits){
            IUnitInfo unitInfo = allUnts.get(uc);
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
    public static IUnitInfo getParentUnitInfo(String sUnit) {
        IUnitInfo uinfo = getUnitRepo().get(sUnit);
        if(uinfo==null){
            return null;
        }
        return getUnitRepo().get(uinfo.getParentUnit());
    }

    /**
     * 根据机构代码获取本机构的符合要求（类别或者标签相符）的上级机构信息
     * @param sUnit sUnit 机构代码
     * @param typeOrTag 结构类别或者标签
     * @return 上级结构机构信息
     */
    public static IUnitInfo getParentUnitInfo(String sUnit,String typeOrTag) {
        IUnitInfo uinfo = getUnitRepo().get(sUnit);
        while(uinfo!=null){
            if(typeOrTag.equals(uinfo.getUnitType()) ||
                SysUnitFilterEngine.matchUnitTag(typeOrTag, uinfo.getUnitTag())){
                return uinfo;
            }
            String puc = uinfo.getParentUnit();
            if ((puc == null) || ("0".equals(puc)) || ("".equals(puc))) {
                return null;
            }
            uinfo = getUnitRepo().get(puc);
        }
        return null;//getUnitRepo().get(uinfo.getParentUnit());
    }
    /**
     * 根据状态获取所有机构信息，
     *
     * @param sState A表示所有状态
     * @return List 所有机构信息
     */
    public static List<IUnitInfo> getAllUnits(String sState) {

        List<? extends IUnitInfo> allunits = CodeRepositoryCache.unitInfoRepo.getCachedTarget();
        /*缓存中已经排好序了
        CollectionsOpt.sortAsTree(allunits,
                ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );*/

        List<IUnitInfo> units = new ArrayList<>();
        if("A".equals(sState)){
            units.addAll(allunits);
            return units;
        }

        for (IUnitInfo unit : allunits) {
            if (sState.equals(unit.getIsValid())) {
                units.add(unit);
            }
        }
        return units;
    }

    /**
     * 获得已知机构 下级的所有机构并返回map，包括失效的机构
     *
     * @param sParentUnit sParentUnit
     * @return Map 已知机构下级的所有机构，包括失效的机构
     */
    public static Map<String, IUnitInfo> getAllUnitMapByParaent(String sParentUnit) {
        Map<String, IUnitInfo> units = new HashMap<>();

        for (Map.Entry<String,? extends IUnitInfo> ent : getUnitRepo().entrySet()) {
            IUnitInfo value = ent.getValue();
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
    public static Map<String, IUnitInfo> getUnitMapBuyParaentRecurse(String sParentUnit) {
        Map<String, IUnitInfo> units = new HashMap<>();
        List<String> sParentUnits = new ArrayList<>();
        List<String> sNewUnits = new ArrayList<>();
        sParentUnits.add(sParentUnit);

        while (sParentUnits.size() > 0) {
            sNewUnits.clear();
            for (int i = 0; i < sParentUnits.size(); i++) {
                String sPNC = sParentUnits.get(i);
                for (Map.Entry<String, ? extends IUnitInfo> ent : getUnitRepo().entrySet()) {
                    IUnitInfo value = ent.getValue();
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
        List<? extends IDataDictionary> lsDictionary = getDictionary(sCatalog);

        List<Map<String, Object>> dataMap = new ArrayList<>();
        for (IDataDictionary dict : lsDictionary) {
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
    public static List<IDataDictionary> getDictionaryIgnoreD(String sCatalog) {
        List<IDataDictionary> dcRetMap = new ArrayList<>();
        List<? extends IDataDictionary> dcMap = getDictionary(sCatalog);
        if (dcMap != null) {
            for (IDataDictionary value : dcMap) {
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
    public static Map<String,String> getAllLabelValueMap(String sCatalog){
        return innerGetLabelValueMap(sCatalog,WebOptUtils.getCurrentLang(
            RequestThreadLocal.getLocalThreadWrapperRequest()), true);
    }

    /**
     * 获取 数据字典 key value
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @param localLang localLang
     * @return Map  数据字典
     */
    public static Map<String,String> getAllLabelValueMap(String sCatalog, String localLang) {
        return innerGetLabelValueMap(sCatalog, localLang, true);
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
        return innerGetLabelValueMap(sCatalog,
            WebOptUtils.getCurrentLang(
                RequestThreadLocal.getLocalThreadWrapperRequest()), false);
    }

    /**
     * 获取 数据字典 key value 对， 忽略 禁用的 条目
     *
     * @param sCatalog 数据字典类别，或者系统内置的类别
     * @param localLang localLang
     * @return 数据字典,忽略禁用的条目
     */
    public static Map<String,String> getLabelValueMap(String sCatalog, String localLang) {
        return innerGetLabelValueMap(sCatalog, localLang, false);
    }

    private static Map<String,String> innerGetLabelValueMap(String sCatalog, String localLang, boolean allItem) {
        Map<String,String> lbvs = new HashMap<>();

        if(sCatalog.startsWith("userInfo.")){
            for (Map.Entry<String, ? extends IUserInfo> ent : getUserRepo().entrySet()) {
                IUserInfo value = ent.getValue();
                if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.put(value.getUserCode(),
                        StringBaseOpt.castObjectToString(
                            ReflectionOpt.getFieldValue(value, sCatalog.substring(9))));
                }
            }
            return lbvs;
        }

        if(sCatalog.startsWith("unitInfo.")){
            for (IUnitInfo value : listAllUnits()) {
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
                for (Map.Entry<String, ? extends IUserInfo> ent : getUserRepo().entrySet()) {
                    IUserInfo value = ent.getValue();
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getUserCode(), value.getUserName());
                    }
                }
                return lbvs;
            }
            case CodeRepositoryUtil.LOGIN_NAME: {
                for (Map.Entry<String, ? extends IUserInfo> ent : getUserRepo().entrySet()) {
                    IUserInfo value = ent.getValue();
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getUserCode(), value.getLoginName());
                    }
                }
                return lbvs;
            }
            case CodeRepositoryUtil.UNIT_CODE: {
                for (IUnitInfo value : listAllUnits()) {
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getUnitCode(), value.getUnitName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.DEP_NO: {
                for (Map.Entry<String, ? extends IUnitInfo> ent : getDepNo().entrySet()) {
                    IUnitInfo value = ent.getValue();
                    // System.out.println(value.getIsvalid());
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getDepNo(), value.getUnitName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.ROLE_CODE: {
                for (Map.Entry<String, ? extends IRoleInfo> ent : getRoleRepo().entrySet()) {
                    IRoleInfo value = ent.getValue();
                    if (allItem || CodeRepositoryUtil.T.equals(value.getIsValid())) {
                        lbvs.put(value.getRoleCode(), value.getRoleName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OPT_ID: {
                for (Map.Entry<String, ? extends IOptInfo> ent : getOptRepo().entrySet()) {
                    IOptInfo value = ent.getValue();
                    lbvs.put(value.getOptId(), value.getOptName());
                }
                lbvs.put("mainframe", "框架平台");
                return lbvs;
            }

            case CodeRepositoryUtil.OS_ID: {
                List<? extends IOsInfo> osInfos = listOsInfo();
                if (osInfos != null) {
                    for (IOsInfo osInfo : osInfos) {
                        lbvs.put(osInfo.getOsId(), osInfo.getOsName());
                    }
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OPT_CODE: {
                for (Map.Entry<String, ? extends IOptMethod> ent : getPowerRepo().entrySet()) {
                    IOptMethod value = ent.getValue();
                    lbvs.put(value.getOptCode(), value.getOptName());
                }
                return lbvs;
            }

            case CodeRepositoryUtil.OPT_DESC: {
                for (Map.Entry<String, ? extends IOptMethod> ent : getPowerRepo().entrySet()) {
                    IOptMethod optdef = ent.getValue();
                    IOptInfo value = getOptRepo().get(optdef.getOptId());
                    lbvs.put(optdef.getOptCode(), value.getOptName() + "-" + optdef.getOptName());
                }
                return lbvs;
            }

            default:
                CachedObject<Map<String, String>> extendRepo = extendedCodeRepo.get(sCatalog);
                if(extendRepo != null){
                    return extendRepo.getCachedTarget();
                }

                List<? extends IDataDictionary> dcMap = getDictionary(sCatalog);
                if (dcMap != null) {
                    for (IDataDictionary value : dcMap) {
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
    public static List<OptionItem> getOptionForSelect(String sCatalog, String localLang) {
        List<OptionItem> lbvs = new ArrayList<>();

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.USER_CODE)) {
            for (Map.Entry<String,? extends IUserInfo> ent : getUserRepo().entrySet()) {
                IUserInfo value = ent.getValue();
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getUserName(),
                        value.getUserCode(), value.getPrimaryUnit()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.LOGIN_NAME)) {
            for (Map.Entry<String,? extends IUserInfo> ent : getUserRepo().entrySet()) {
                IUserInfo value = ent.getValue();
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getLoginName(),
                        value.getUserCode(), value.getPrimaryUnit()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.UNIT_CODE)) {
            for (IUnitInfo value : listAllUnits()) {
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getUnitName(),
                        value.getUnitCode(), value.getParentUnit()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.DEP_NO)) {
            for (Map.Entry<String,? extends IUnitInfo> ent : getDepNo().entrySet()) {
                IUnitInfo value = ent.getValue();
                // System.out.println(value.getIsvalid());
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    IUnitInfo parentUnit = CodeRepositoryUtil.getUnitInfoByCode(value.getParentUnit());
                    lbvs.add(new OptionItem(value.getUnitName(),
                        value.getDepNo(),
                        parentUnit==null?null:parentUnit.getDepNo()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.ROLE_CODE)) {
            for (Map.Entry<String,? extends IRoleInfo> ent : getRoleRepo().entrySet()) {
                IRoleInfo value = ent.getValue();
                if (CodeRepositoryUtil.T.equals(value.getIsValid())) {
                    lbvs.add(new OptionItem(value.getRoleName(), value.getRoleCode()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OPT_ID)) {
            for (Map.Entry<String,? extends IOptInfo> ent : getOptRepo().entrySet()) {
                IOptInfo value = ent.getValue();
                lbvs.add(new OptionItem(value.getOptName(), value.getOptId()));
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OS_ID)) {
            List<? extends IOsInfo> osInfos = listOsInfo();
            if (osInfos != null) {
                for (IOsInfo osInfo : osInfos) {
                    lbvs.add(new OptionItem(osInfo.getOsName(), osInfo.getOsId()));
                }
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OPT_CODE)) {
            for (Map.Entry<String,? extends IOptMethod> ent : getPowerRepo().entrySet()) {
                IOptMethod value = ent.getValue();
                lbvs.add(new OptionItem(value.getOptName(), value.getOptCode(),value.getOptId()));
            }
            return lbvs;
        }

        if (sCatalog.equalsIgnoreCase(CodeRepositoryUtil.OPT_DESC)) {
            for (Map.Entry<String,? extends IOptMethod> ent : getPowerRepo().entrySet()) {
                IOptMethod optdef = ent.getValue();
                IOptInfo value = getOptRepo().get(optdef.getOptId());
                lbvs.add(new OptionItem(value.getOptName() + "-" + optdef.getOptName(),
                    optdef.getOptCode(),optdef.getOptId()));
            }
            return lbvs;
        }

        List<? extends IDataDictionary> dcMap = getDictionary(sCatalog);
        if (dcMap != null) {
            for (IDataDictionary value : dcMap) {
                if (value.getDataTag() != null && !"D".equals(value.getDataTag())) {
                    lbvs.add(new OptionItem(value.getLocalDataValue(localLang) ,
                        value.getDataCode(),value.getExtraCode()));
                }
            }
        }
        return lbvs;
    }

    public static List<OptionItem> getOptionForSelect(String sCatalog) {
        return getOptionForSelect(sCatalog,
            WebOptUtils.getCurrentLang(
                RequestThreadLocal.getLocalThreadWrapperRequest()));
    }

    /**
     * 获取字典条目
     *
     * @param sCatalog 字典类别代码
     * @param sKey     字典代码
     * @return 字典条目
     */
    public static IDataDictionary getDataPiece(String sCatalog, String sKey) {
        Map<String, ? extends IDataDictionary> dcMap =
            CodeRepositoryCache.codeToDictionaryMap.getCachedValue(sCatalog);
        return  dcMap==null?null:dcMap.get(sKey);
    }

    /**
     * 获取字典条目
     *
     * @param sCatalog 字典类别代码
     * @param sValue   字典值
     * @return 字典条目
     */
    public static IDataDictionary getDataPieceByValue(String sCatalog, String sValue) {
        List<? extends IDataDictionary> dcList = getDictionary(sCatalog);
        if (dcList == null) {
            return null;
        }

        for (IDataDictionary fd : dcList)
            if (fd.getDataValue().equals(sValue)) {
                return fd;
            }
        return null;
    }

    /**
     * 根据机构代码获取机构名称
     *
     * @param unitCode unitCode
     * @return 机构名称
     */
    public static String getUnitName(String unitCode) {
        IUnitInfo unitInfo = getUnitRepo().get(unitCode);
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
    private static List<IUnitInfo> fetchSubUnits(List<? extends IUnitInfo> allunits,
                                                 String parentUnitCode) {
        if(allunits==null)
            return null;
        List<IUnitInfo> units = new ArrayList<>();
        if(StringUtils.isBlank(parentUnitCode)){
            for (IUnitInfo uc : allunits) {
                if (StringUtils.isBlank(uc.getParentUnit()) &&
                    CodeRepositoryUtil.T.equals(uc.getIsValid())) {
                    units.add(uc);
                }
            }
        } else {
            for (IUnitInfo uc : allunits) {
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
    public static List<IUnitInfo> getSubUnits(String unitCode) {
        List<? extends IUnitInfo> units = CodeRepositoryCache.unitInfoRepo.getCachedValue(unitCode);
        return fetchSubUnits(units, unitCode);
    }
    /**
     * 获取机构的下级机构，并按照树形排列
     *
     * @param unitCode unitCode
     * @return 机构的下级机构,并按照树形排列
     */
    public static List<IUnitInfo> getAllSubUnits(String unitCode) {
        //TODO 先判断属于那个租户；
        List<? extends IUnitInfo> allunits = CodeRepositoryCache.unitInfoRepo.getCachedTarget();
        List<IUnitInfo> units = new ArrayList<>();
        List<IUnitInfo> subunits = fetchSubUnits(allunits,unitCode);
        while( subunits!=null && subunits.size()>0){
            units.addAll(subunits);
            List<IUnitInfo> subunits1 = new ArrayList<>();
            for(IUnitInfo u1: subunits){
                List<IUnitInfo> subunits2 = fetchSubUnits(allunits,u1.getUnitCode());
                if(subunits2!=null)
                    subunits1.addAll(subunits2);
            }
            subunits = subunits1;
        }
        CollectionsOpt.sortAsTree(units,
            (p,c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));
        return units;
    }

    public static Set<String> listUnitAllUsers(String unitCode, boolean includeSubUnit) {
        List<? extends IUserUnit> userUnits = CodeRepositoryUtil.listUnitUsers(unitCode);

        Set<String> users = new HashSet<>(100);
        if(userUnits!=null) {
            for (IUserUnit user : userUnits) {
                users.add(user.getUserCode());
            }
        }
        if(includeSubUnit) {
            List<IUnitInfo> unitInfos = CodeRepositoryUtil.getAllSubUnits(unitCode);
            if(unitInfos != null){
                for (IUnitInfo unit : unitInfos) {
                    userUnits = CodeRepositoryUtil.listUnitUsers(unit.getUnitCode());
                    if(userUnits!=null) {
                        for (IUserUnit user : userUnits) {
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
     * @param IOptMethod IOptMethod
     * @return 操作方法的权限
     */
    public static boolean checkUserOptPower(String optId, String IOptMethod) {
        Object userDetails = WebOptUtils.getLoginUser(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).checkOptPower(optId, IOptMethod);
        }
        return false;
    }

    public static boolean checkUserOptPower(String optId, String IOptMethod,HttpServletRequest request) {
        Object userDetails = WebOptUtils.getLoginUser(request);
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).checkOptPower(optId, IOptMethod);
        }
        return false;
    }

    /**
     * 获取用户所有的 操作方法
     * @return 返回一个map，key为optid+‘-’+method value 为 'T'
     */
    public static Map<String,String> getUserAllOptPowers() {
        Object userDetails = WebOptUtils.getLoginUser(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).getUserOptList();
        }
        return null;
    }
    public static Map<String,String> getUserAllOptPowers(HttpServletRequest request) {
        Object userDetails = WebOptUtils.getLoginUser(request);
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).getUserOptList();
        }
        return null;
    }
    /**
     * 获取用户参数设置
     *
     * @param paramCode 参数代码
     * @return 用户参数设置
     */
    public static String getUserSettingValue(String paramCode) {
        Object userDetails = WebOptUtils.getLoginUser(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).getUserSettingValue(paramCode);
        }
        return null;
    }

    /**
     * 获取用户所有参数设置
     * @return 返回 key - value 对（map）
     */
    public static Map<String, String> getUserAllSettings() {
        Object userDetails = WebOptUtils.getLoginUser(
            RequestThreadLocal.getLocalThreadWrapperRequest());
        if (userDetails instanceof CentitUserDetails) {
            return ((CentitUserDetails)userDetails).getUserSettings();
        }
        return null;
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

    public static List<String> listAllUserRoles(String sUserCode){
        List<String> userRoles = new ArrayList<>(20);
        userRoles.add(SecurityContextUtils.PUBLIC_ROLE_CODE);
        List<? extends IUserRole> urs = CodeRepositoryCache.userRolesRepo.getCachedValue(sUserCode);
        if(urs!=null && urs.size()>0) {
            for (IUserRole ur : urs) {
                userRoles.add(ur.getRoleCode());
            }
        }
        List<? extends IUserUnit> uus = CodeRepositoryCache.userUnitsMap.getCachedValue(sUserCode);
        if(uus!=null && uus.size()>0) {
            for (IUserUnit uu : uus) {
                List<? extends IUnitRole> uurs = CodeRepositoryCache.unitRolesRepo.getCachedValue(uu.getUnitCode());
                if (uurs != null && uurs.size() > 0) {
                    for (IUnitRole ur : uurs) {
                        userRoles.add(ur.getRoleCode());
                    }
                }
            }
        }
        return userRoles;
    }

    public static List<String> listUserDataFiltersByOptIdAndMethod(String sUserCode, String sOptId, String sOptMethod) {
        Map<String, List<IOptDataScope>> optDataScope = CodeRepositoryCache.optDataScopeRepo.getCachedTarget();
        if(optDataScope==null){
            return null;
        }
        List<IOptDataScope> odss = optDataScope.get(sOptId);
        if(odss == null || odss.size()<1){
            return null;
        }

        Set<String> dataScopes = new HashSet<>();
        List<String> allUserRole = listAllUserRoles(sUserCode);

        for (String rolecode : allUserRole) {
            IRoleInfo ri = CodeRepositoryCache.codeToRoleMap.getCachedTarget().get(rolecode);
            if (ri != null) {
                for (IRolePower rp : CodeRepositoryCache.rolePowerMap.getCachedTarget().get(ri.getRoleCode())) {
                    // 需要过滤掉 不是 sOptId 下面的方式（不过滤也不会影响结果）; 但是这个过滤可能并不能提高效率
                    IOptMethod om = CodeRepositoryCache.codeToMethodMap.getFreshTarget().get(rp.getOptCode());
                    if(StringUtils.equals(sOptId, om.getOptId()) && StringUtils.equals(om.getOptMethod(),sOptMethod)) {
                        String[] oscs = rp.getOptScopeCodeSet();
                        if (oscs != null) {
                            Collections.addAll(dataScopes, oscs);
                        }
                    }
                }
            }
        }

        List<String> filters = new ArrayList<>();
        for(IOptDataScope ods : odss){
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
    public static IOsInfo getOsInfo(String osId){
        List<? extends IOsInfo> osInfos = listOsInfo();
        if(osInfos!=null) {
            for (IOsInfo osInfo : osInfos) {
                if (StringUtils.equals(osId, osInfo.getOsId())) {
                    return osInfo;
                }
            }
        }
        return null;
    }

}
