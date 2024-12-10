package com.centit.framework.components;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.basedata.UserRole;
import com.centit.framework.model.basedata.UserUnit;
import com.centit.support.common.ObjectException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author codefan
 * @version 2.0
 * 2012-2-3
 */
public abstract class SysUserFilterEngine {
    /**
     * 岗位角色类别代码
     */
    public static final String ROLE_TYPE_GW = InnerUserUnitFilterCompileEngine.USER_FILTER_ROLE_TYPE_GW;// "GW";
    /**
     * 行政角色类别代码
     */
    public static final String ROLE_TYPE_XZ = InnerUserUnitFilterCompileEngine.USER_FILTER_ROLE_TYPE_XZ; // "XZ";

    /**
     * 项目（办件）角色类别代码
     */
    public static final String ROLE_TYPE_ITEM = "BJ";

    /**
     * 系统功能权限角色
     */
    public static final String ROLE_TYPE_SYSTEM = InnerUserUnitFilterCompileEngine.USER_FILTER_SYSTEM_ROLE;// "RO";

    /**
     * 角色表达式
     */
    public static final String ROLE_TYPE_ENGINE = "EN";
    /**
     * 预存表达式， 工作流中特有的 saved formula
     */
    public static final String ROLE_TYPE_ENGINE_FORMULA = "SF";

    private SysUserFilterEngine()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysUserFilterEngine.class);

    public static Set<String> getUsersByRoleAndUnit(UserUnitFilterCalcContext ecc,
         String roleType, String roleCode, String unitCode){
        return getUsersByRoleAndUnit(ecc,roleType, roleCode, unitCode, false);
    }

    public static Set<String> getUsersByRoleAndUnit(UserUnitFilterCalcContext ecc,
                    String roleType, String roleCode,
                    String unitCode, boolean onlyGetPrimary) {
        boolean filterByUnitCode = StringUtils.isNotBlank(unitCode);
        List<UserUnit> lsUserunit = new ArrayList<>();
        if (filterByUnitCode) {
            UnitInfo unit = ecc.getUnitInfoByCode(unitCode);
            if (unit != null){
                if(onlyGetPrimary){
                    for(UserUnit uu: ecc.listUnitUsers(unitCode)){
                        if("T".equals(uu.getRelType())){
                            lsUserunit.add(uu);
                        }
                    }
                }else {
                    List<UserUnit> userUnits = ecc.listUnitUsers(unitCode);
                    if(userUnits!=null) {
                        lsUserunit.addAll(userUnits);
                    }
                }
            }
        }

        if(ROLE_TYPE_SYSTEM.equalsIgnoreCase(roleType)) {
            // 获取系统角色用户
            List<UserRole> userRoles = ecc.listRoleUsers(roleCode);
            Set<String> users = new HashSet<>();
            for (UserRole ur : userRoles) {
                if(filterByUnitCode) {
                    for (UserUnit uu : lsUserunit) {
                        if (StringUtils.equals(uu.getUserCode(), ur.getUserCode())) {
                            users.add(uu.getUserCode());
                            break;
                        }
                    }
                } else {
                    users.add(ur.getUserCode());
                }
            }
            return users;
        }

        if (!filterByUnitCode){
            List<UserUnit> userlist = ecc.listAllUserUnits();
            if(userlist!=null && !userlist.isEmpty()) {
                lsUserunit = userlist;
            }
        }

        Set<String> users = new HashSet<>();
        if (ROLE_TYPE_GW.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的岗位
            for(UserUnit uu: lsUserunit){
                if(roleCode.equals(uu.getUserStation())) {
                    users.add(uu.getUserCode());
                }
            }
        } else if (ROLE_TYPE_XZ.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的职位
            for(UserUnit uu: lsUserunit){
                if(roleCode.equals(uu.getUserRank())) {
                    users.add(uu.getUserCode());
                }
            }
        }
        return users;
    }

    private static boolean matchFilter(UserUnitFilterGene rf, UserUnit uu){
        if (rf.isHasRelationFilter()) {
            // 过滤掉关联关系不一致的用户
            // 这个地方 暂时没有考虑 类别嵌套（默认类别）的情况
            if(!rf.getUserUnitRelTypes().contains(uu.getRelType())){
                return false;
            }
        }
        if (rf.isHasGWFilter()) {
            // 过滤掉不符合要求的岗位
            if(!rf.getGwRoles().contains(uu.getUserStation())){
                return false;
            }
        }

        if (rf.isHasXZFilter()) {
            // 过滤掉不符合要求的职位
            if(!rf.getXzRoles().contains(uu.getUserRank())){
                return false;
            }
        }

        if (rf.isHasRankFilter() && (rf.isRankAllSub() || rf.isRankAllTop()) ) { // 所有下级
            return rf.matchRank(uu.getPostRank());
        }
        return true;
    }

    private static Set<String> getUsersByFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene rf) {

        boolean hasUnitFilter = rf.isHasGWFilter() || rf.isHasRankFilter()
            || rf.isHasXZFilter() || rf.isHasUnitFilter() || rf.isHasRelationFilter() ;

        boolean hasTypeTagFilter =
            rf.isHasUserTagFilter() || rf.isHasUserTypeFilter() || rf.isHasRoleFilter();
        //获取机构列表
        SysUnitFilterEngine.getUnitsByFilter(ecc, rf);

        if(hasUnitFilter && (!rf.isHasUserFilter())) {
            // 获取所有候选人的岗位、职务信息
            List<UserUnit> lsUserUnits = new LinkedList<>();

            if (rf.isHasUnitFilter()) {
                for (String unitCode : rf.getUnits()) {
                    List<UserUnit> uus = ecc.listUnitUsers(unitCode);
                    if(uus != null && !uus.isEmpty()) {
                        for(UserUnit uu: uus) {
                            if(matchFilter(rf, uu)) {
                                lsUserUnits.add(uu);
                            }
                        }
                    }
                }
            } else {
                List<UserUnit> uus = ecc.listAllUserUnits();
                if(uus!=null) {
                    for(UserUnit uu: uus) {
                        if(matchFilter(rf, uu)) {
                        lsUserUnits.add(uu);
                        }
                    }
                }
            }

            if (rf.isHasRankFilter() && !rf.isRankAllSub() && !rf.isRankAllTop()){
                // 针对不同的部门，分别找出这个部门对应的等级
                Map<String, String> unitRank = new HashMap<>();
                for (UserUnit uu : lsUserUnits) {
                    if (rf.matchRank(uu.getPostRank())) {
                        String nR = unitRank.get(uu.getUnitCode());
                        if (nR == null) {
                            unitRank.put(uu.getUnitCode(), uu.getPostRank());
                        } else {
                            if( (rf.isRankPlus()  && StringUtils.compare(nR, uu.getPostRank())>0)
                               || (rf.isRankMinus() && StringUtils.compare(nR, uu.getPostRank())<0) )
                                unitRank.put(uu.getUnitCode(), uu.getPostRank());
                        }
                    }
                }
                //TODO 这个地方需要重新 实现，不是精确匹配，是匹配最接近的
                for (Iterator<UserUnit> it = lsUserUnits.iterator(); it.hasNext(); ) {
                    UserUnit uu = it.next();
                    // 过滤掉不符合要求的职位
                    String nR = unitRank.get(uu.getUnitCode());
                    if (nR == null || !nR.equals(uu.getPostRank()))
                        it.remove();
                }
            }

            // 获取所有 符合条件的用户代码
            rf.getUsers().clear();
            for (UserUnit uu : lsUserUnits) {
                rf.addUser(uu.getUserCode());
            }
        }

        if(hasTypeTagFilter) {
            List<UserInfo> lsUserInfo = null;
            if (rf.getUsers() != null && rf.getUsers().size() > 0) {
                lsUserInfo = new ArrayList<>(rf.getUsers().size() + 1);
                for (String userCode : rf.getUsers()) {
                    UserInfo userInfo = ecc.getUserInfoByCode(userCode);
                    if (null != userInfo) {
                        lsUserInfo.add(userInfo);
                    }
                }
            } else if (!hasUnitFilter) {
                // 剔除禁用用户
                List<UserInfo> extUserInfo = ecc.listAllUserInfo();
                lsUserInfo = new ArrayList<>(extUserInfo.size() + 1);
                for(UserInfo ui : extUserInfo) {
                    if("T".equals(ui.getIsValid())) {
                        lsUserInfo.add(ui);
                    }
                }
            }

            if (lsUserInfo != null) {
                if (rf.isHasUserTypeFilter()) {
                    // 过滤掉不符合要求的岗位
                    lsUserInfo.removeIf(user -> !rf.getUserTypes().contains(user.getUserType()));
                }

                if (rf.isHasUserTagFilter()) {
                    for (Iterator<UserInfo> it = lsUserInfo.iterator(); it.hasNext(); ) {
                        UserInfo user = it.next();
                        boolean hasTag = false;
                        if (StringUtils.isNotBlank(user.getUserTag())) {
                            String tags[] = user.getUserTag().split(",");
                            for (String tag : tags) {
                                if (rf.getUserTags().contains(tag)) {
                                    hasTag = true;
                                    break;
                                }
                            }
                        }
                        if (!hasTag) {
                            it.remove();
                        }
                    }
                }

                if (rf.isHasRoleFilter()) {
                    int nUserSize = lsUserInfo.size();
                    // 优化计算 效率
                    if(nUserSize > rf.getOptRoles().size() ){
                        //将 lsUserInfo 转换为map 效率可能更好
                        Map<String, UserInfo> tmpUserInfo = new HashMap<>(nUserSize + 1);
                        for (String roleCode :rf.getOptRoles()) {
                            List<UserRole> userRoles = ecc.listRoleUsers(roleCode);
                            for(UserRole ur : userRoles){
                                for(UserInfo userInfo : lsUserInfo){
                                    if(StringUtils.equals( userInfo.getUserCode(), ur.getUserCode())){
                                        tmpUserInfo.put(userInfo.getUserCode(), userInfo);
                                        break;
                                    }
                                }
                            }
                        }
                        lsUserInfo.clear();
                        lsUserInfo.addAll(tmpUserInfo.values());
                    } else { // 和上面的 效果应该是等价的，主要是考虑性能问题
                        for (Iterator<UserInfo> it = lsUserInfo.iterator(); it.hasNext(); ) {
                            UserInfo user = it.next();
                            boolean hasRole = false;
                            List<UserRole> userRoles = ecc.listUserRoles(user.getUserCode());
                            if (userRoles != null) {
                                for (UserRole ur : userRoles) {
                                    if (rf.getOptRoles().contains(ur.getRoleCode())) {
                                        hasRole = true;
                                        break;
                                    }
                                }
                            }
                            if (!hasRole) {
                                it.remove();
                            }
                        }
                    }
                }
                rf.getUsers().clear();

                for (UserInfo user : lsUserInfo) {
                    rf.addUser(user.getUserCode());
                }
            }
        }
        return rf.getUsers();
    }

    /**
     * D(机构过滤)DT(机构类别过滤)DL(机构标签过滤)
     * GW(岗位过滤)XZ(行政职务过滤)R(行政等级过滤)
     * U(用户过滤)UT(用户类别过滤)UL(用户标签过滤)RO(用户角色过滤)
     * D()DT()DL()GW()XZ()R()U()UT()UL()RO()
     * D 根据 机构代码 过滤
     * DT 根据 机构类型 过滤
     * DL 根据 机构标签 过滤
     * GW 根据 岗位过滤
     * XZ 根据 行政职务过滤
     * R 根据 行政职务等级 过滤
     * U 根据 用户代码过滤
     * UT 根据 用户类型过滤
     * UL 根据 用户标签 过滤
     * RO 根据 用户角色 过滤
     * @param ecc UserUnitFilterCalcContext
     * @return calcSimpleExp
     */
    private static Set<String> calcSimpleExp(UserUnitFilterCalcContext ecc) {
        UserUnitFilterGene gene = InnerUserUnitFilterCompileEngine.makeUserUnitFilter(ecc);
        if(gene==null){
            return null;
        }
        return getUsersByFilter(ecc,gene);
    }

    /**
     * S(rolesExp[,rolesExp]* )
     *
     * @param ecc UserUnitFilterCalcContext
     * @return calcSingleExp
     */
    private static Set<String> calcSingleExp(UserUnitFilterCalcContext ecc) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) // 语法错误
            return null;
        Set<String> users = calcRolesExp(ecc);
        if (users == null)// 语法错误
            return null;
        while (users.size() == 0) {
            w = ecc.getAWord();
            if (")".equals(w))
                return users;
            if (",".equals(w))
                users = calcRolesExp(ecc);
            else {
                ecc.setLastErrMsg(w + " is unexpected, expect ',' or ')' ; calcSingleRoleExp");
                return null;
            }
        }
        ecc.seekToRightBracket();

        return users;
    }

    /**
     * (rolesExp) | S(singleExp) | SimpleExp
     *
     * @param ecc
     * @return
     */
    private static Set<String> calcItemExp(UserUnitFilterCalcContext ecc) {
        String w = ecc.getAWord();
        if (w == null || "".equals(w)) { // 语法错误
            ecc.setLastErrMsg("End of file is unexpected; calcItemRole begin ");
            return null;
        }
        if ("(".equals(w)) {
            Set<String> users = calcRolesExp(ecc);
            w = ecc.getAWord();
            if (")".equals(w))
                return users;
            else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcItemRole .");
                return null;
            }
        } else if ("S".equalsIgnoreCase(w)) {
            return calcSingleExp(ecc);
        } else {
            ecc.writeBackAWord(w);
            return calcSimpleExp(ecc);
        }
    }

    /**
     * itemExp ([或  itemExp][与 itemExp][非 itemExp])*
     *
     * @param ecc UserUnitFilterCalcContext
     * @return calcRolesExp
     */
    public static Set<String> calcRolesExp(UserUnitFilterCalcContext ecc) {
        Set<String> users = calcItemExp(ecc);
        if (users == null)
            return null;
        while (true) {
            String w = ecc.getAWord();
            if (w == null || "".equals(w))
                return users;

            if (",".equals(w) || ")".equals(w)) {
                ecc.writeBackAWord(w);
                return users;
            }

            if ("|".equals(w) || "||".equals(w)) { // 并
                Set<String> users2 = calcItemExp(ecc);
                if (users2 == null)
                    return null;
                users.addAll(users2);
            } else if ("&".equals(w) || "&&".equals(w)) { // 交
                Set<String> users2 = calcItemExp(ecc);
                if (users2 == null)
                    return null;
                users.retainAll(users2);
            } else if ("!".equals(w)) {// 差
                Set<String> users2 = calcItemExp(ecc);
                if (users2 == null)
                    return null;
                users.removeAll(users2);
            } else {// 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect '||','&&','!',',' or ')' ; calcRoles .");
                return null;
            }
        }
    }

    public static Set<String> calcSystemOperators(String roleExp, String topUnit,
                                                Map<String, Set<String>> unitParams,
                                                Map<String, Set<String>> userParams,
                                                Map<String, String> rankParams,
                                                UserUnitVariableTranslate varTrans) {
        if (StringUtils.isBlank(roleExp)) {
            return null;
        }
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext(topUnit);
        ecc.setFormula(roleExp);
        ecc.setVarTrans(varTrans);
        if(unitParams!=null) {
            ecc.addAllUnitParam(unitParams);
        }
        if(userParams!=null) {
            ecc.addAllUserParam(userParams);
        }
        if(rankParams!=null) {
            ecc.addAllRankParam(rankParams);
        }

        Set<String> sUsers = calcRolesExp(ecc);
        //if (sUsers == null || ecc.hasError())
        if (ecc.hasError()) {
            logger.error(ecc.getLastErrMsg());
            throw new ObjectException(ecc, ObjectException.FORMULA_GRAMMAR_ERROE,
                roleExp +":"+ ecc.getLastErrMsg());
        }
        return sUsers;
    }

    public static String validateRolesExp(String rolesExp) {
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext("system");
        ecc.setFormula(rolesExp);
        calcRolesExp(ecc);
        if (ecc.hasError())
            return ecc.getLastErrMsg();
        return "T";
    }

    public static Set<String> calcAllLeaderByUser(String userCode, String topUnit){
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext(topUnit);
        UserInfo userInfo = ecc.getUserInfoByCode(userCode);
        if(userInfo==null){
            logger.error("系统中无此用户！");
            return null;
        }
        ecc.addUserParam("self",userCode);
        ecc.addUserParam("unit",userInfo.getPrimaryUnit());
        ecc.addRankParam("userRank",
                ecc.getUserUnitRank( userCode, userInfo.getPrimaryUnit()) );
        ecc.setFormula("D(unit--)R(userRank--)");
        Set<String> sUsers = calcRolesExp(ecc);
        if (sUsers == null || ecc.hasError())
            logger.error(ecc.getLastErrMsg());
        return sUsers;
    }

    public static Set<String> calcUnitLeaderByUser(String userCode, String topUnit, String unitCode){
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext(topUnit);
        ecc.addUserParam("self",userCode);
        ecc.addUserParam("unit",unitCode);
        ecc.addRankParam("userRank",
                ecc.getUserUnitRank( userCode,unitCode) );
        ecc.setFormula("D(unit)R(userRank--)");
        Set<String> sUsers = calcRolesExp(ecc);
        if (sUsers == null || ecc.hasError())
            logger.error(ecc.getLastErrMsg());
        return sUsers;
    }
}
