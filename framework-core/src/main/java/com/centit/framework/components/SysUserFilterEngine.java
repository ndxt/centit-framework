package com.centit.framework.components;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserRole;
import com.centit.framework.model.basedata.IUserUnit;
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
                    String roleType,String roleCode,
                    String unitCode, boolean onlyGetPrimary) {
        List<IUserUnit> lsUserunit = new LinkedList<>();
        if (unitCode != null && !"".equals(unitCode)) {
            IUnitInfo unit = ecc.getUnitInfoByCode(unitCode);
            if (unit != null){
                if(onlyGetPrimary){
                    for(IUserUnit uu: ecc.listUnitUsers(unitCode)){
                        if("T".equals(uu.getRelType())){
                            lsUserunit.add(uu);
                        }
                    }
                }else {
                    if(ecc.listUnitUsers(unitCode)!=null) {
                        lsUserunit.addAll(ecc.listUnitUsers(unitCode));
                    }
                }
            }
        } else {
            if(ecc.listAllUserUnits()!=null) {
                lsUserunit.addAll(ecc.listAllUserUnits());
            }
        }

        if (ROLE_TYPE_GW.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的岗位
            lsUserunit.removeIf(uu -> !roleCode.equals(uu.getUserStation()));
        } else if (ROLE_TYPE_XZ.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的职位
            lsUserunit.removeIf(uu -> !roleCode.equals(uu.getUserRank()));
        } else if(ROLE_TYPE_SYSTEM.equalsIgnoreCase(roleType)) {
            // 获取系统角色用户
            List<? extends IUserRole> userRoles = ecc.listRoleUsers(roleCode);
            Set<String> users = new HashSet<>();
            for(IUserRole ur : userRoles){
                for(IUserUnit uu : lsUserunit){
                    if(StringUtils.equals( uu.getUserCode(), ur.getUserCode())){
                        users.add(uu.getUserCode());
                        break;
                    }
                }
            }
            return users;
        } else {
            lsUserunit.clear();
        }
        // 获取所有 符合条件的用户代码
        Set<String> users = new HashSet<>();
        for (IUserUnit uu : lsUserunit) {
            users.add(uu.getUserCode());
        }
        return users;
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
            List<IUserUnit> lsUserunit = new LinkedList<>();
            if (rf.isHasUnitFilter()) {
                for (String unitCode : rf.getUnits()) {
                    List<? extends IUserUnit> uus = ecc.listUnitUsers(unitCode);
                    if(uus != null || uus.size()>0) {
                        lsUserunit.addAll(uus);
                    }
                }
            } else {
                List<? extends IUserUnit> uus = ecc.listAllUserUnits();
                if(uus!=null) {
                    lsUserunit.addAll(uus);
                }
            }
            if (rf.isHasRelationFilter()) {
                // 过滤掉关联关系不一致的用户
                // 这个地方 暂时没有考虑 类别嵌套（默认类别）的情况
                lsUserunit.removeIf(uu -> !rf.getUserUnitRelTypes().contains(uu.getRelType()));
            }
            if (rf.isHasGWFilter()) {
                // 过滤掉不符合要求的岗位
                lsUserunit.removeIf(uu -> !rf.getGwRoles().contains(uu.getUserStation()));
            }

            if (rf.isHasXZFilter()) {
                // 过滤掉不符合要求的职位
                lsUserunit.removeIf(uu -> !rf.getXzRoles().contains(uu.getUserRank()));
            }

            if (rf.isHasRankFilter()) {
                //如果是 所有上下级，直接过滤
                if (rf.isRankAllSub() || rf.isRankAllTop()) { // 所有下级
                    lsUserunit.removeIf(uu -> !rf.matchRank(ecc.getXzRank(uu.getUserRank())));
                } else {
                    // 针对不同的部门，分别找出这个部门对应的等级
                    Map<String, Integer> unitRank = new HashMap<>();
                    for (IUserUnit uu : lsUserunit) {
                        if (rf.matchRank(ecc.getXzRank(uu.getUserRank()))) {
                            Integer nR = unitRank.get(uu.getUnitCode());
                            if (nR == null) {
                                unitRank.put(uu.getUnitCode(), ecc.getXzRank(uu.getUserRank()));
                            } else {
                                if(  (rf.isRankPlus() && nR > ecc.getXzRank(uu.getUserRank()))
                                   ||(rf.isRankMinus() && nR < ecc.getXzRank(uu.getUserRank())) )
                                    unitRank.put(uu.getUnitCode(), ecc.getXzRank(uu.getUserRank()));
                            }
                        }
                    }

                    for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                        IUserUnit uu = it.next();
                        // 过滤掉不符合要求的职位
                        Integer nR = unitRank.get(uu.getUnitCode());
                        if (nR == null || nR != ecc.getXzRank(uu.getUserRank()))
                            it.remove();
                    }
                }
            }
            // 获取所有 符合条件的用户代码
            rf.getUsers().clear();
            for (IUserUnit uu : lsUserunit) {
                rf.addUser(uu.getUserCode());
            }
        }

        if(hasTypeTagFilter) {
            List<IUserInfo> lsUserInfo = null;
            if (rf.getUsers() != null && rf.getUsers().size() > 0) {
                lsUserInfo = new ArrayList<>(rf.getUsers().size() + 1);
                for (String userCode : rf.getUsers()) {
                    lsUserInfo.add(ecc.getUserInfoByCode(userCode));
                }
            } else if (!hasUnitFilter) {
                List<? extends IUserInfo> extUserInfo = ecc.listAllUserInfo();
                lsUserInfo = new ArrayList<>(extUserInfo.size() + 1);
                lsUserInfo.addAll(extUserInfo);
            }

            if (lsUserInfo != null) {
                if (rf.isHasUserTypeFilter()) {
                    // 过滤掉不符合要求的岗位
                    lsUserInfo.removeIf(user -> !rf.getUserTypes().contains(user.getUserType()));
                }

                if (rf.isHasUserTagFilter()) {
                    for (Iterator<IUserInfo> it = lsUserInfo.iterator(); it.hasNext(); ) {
                        IUserInfo user = it.next();
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
                        Map<String, IUserInfo> tmpUserInfo = new HashMap<>(nUserSize + 1);
                        for (String roleCode :rf.getOptRoles()) {
                            List<? extends IUserRole> userRoles = ecc.listRoleUsers(roleCode);
                            for(IUserRole ur : userRoles){
                                for(IUserInfo userInfo : lsUserInfo){
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
                        for (Iterator<IUserInfo> it = lsUserInfo.iterator(); it.hasNext(); ) {
                            IUserInfo user = it.next();
                            boolean hasRole = false;
                            List<? extends IUserRole> userRoles = ecc.listUserRoles(user.getUserCode());
                            if (userRoles != null) {
                                for (IUserRole ur : userRoles) {
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

                for (IUserInfo user : lsUserInfo) {
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

    public static Set<String> calcSystemOperators(String roleExp,
                                                Map<String, Set<String>> unitParams,
                                                Map<String, Set<String>> userParams,
                                                Map<String, Integer> rankParams,
                                                UserUnitVariableTranslate varTrans) {
        if (StringUtils.isBlank(roleExp)) {
            return null;
        }
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
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
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
        ecc.setFormula(rolesExp);
        calcRolesExp(ecc);
        if (ecc.hasError())
            return ecc.getLastErrMsg();
        return "T";
    }

    public static Set<String> calcAllLeaderByUser(String userCode){
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
        IUserInfo userInfo = ecc.getUserInfoByCode(userCode);
        if(userInfo==null){
            logger.error("系统中无此用户！");
            return null;
        }
        ecc.addUserParam("self",userCode);
        ecc.addUserParam("unit",userInfo.getPrimaryUnit());
        ecc.addRankParam("userRank",
                ecc.getUserUnitRank( userCode,userInfo.getPrimaryUnit()) );
        ecc.setFormula("D(unit--)R(userRank--)");
        Set<String> sUsers = calcRolesExp(ecc);
        if (sUsers == null || ecc.hasError())
            logger.error(ecc.getLastErrMsg());
        return sUsers;
    }

    public static Set<String> calcUnitLeaderByUser(String userCode, String unitCode){
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
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
