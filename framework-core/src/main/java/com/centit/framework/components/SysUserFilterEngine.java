package com.centit.framework.components;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;
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
    public static final String ROLE_TYPE_GW = "GW";
    /**
     * 行政角色类别代码
     */
    public static final String ROLE_TYPE_XZ = "XZ";
    /**
     * 项目（办件）角色类别代码
     */
    public static final String ROLE_TYPE_ITEM = "BJ";
    /**
     * 角色表达式
     */
    public static final String ROLE_TYPE_ENGINE = "EN";

    private SysUserFilterEngine()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysUserFilterEngine.class);

    public static Set<String> getUsersByRoleAndUnit(UserUnitFilterCalcContext ecc,
                                                    String roleType,String roleCode,String unitCode)
    {
        return getUsersByRoleAndUnit(ecc,roleType, roleCode, unitCode, false);
    }

    public static Set<String> getUsersByRoleAndUnit(UserUnitFilterCalcContext ecc,
                                                    String roleType,String roleCode,
                                                    String unitCode, boolean onlyGetPrimary)
    {
        List<IUserUnit> lsUserunit = new LinkedList<>();
        if (unitCode != null && !"".equals(unitCode)) {
            IUnitInfo unit = ecc.getUnitInfoByCode(unitCode);
            if (unit != null){
                if(onlyGetPrimary){
                    for(IUserUnit uu: ecc.listUnitUsers(unitCode)){
                        if("T".equals(uu.getIsPrimary())){
                            lsUserunit.add(uu);
                        }
                    }
                }else {
                    lsUserunit.addAll(ecc.listUnitUsers(unitCode));
                }
            }
        } else {
            lsUserunit.addAll(ecc.listAllUserUnits());
        }

        if (ROLE_TYPE_GW.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的岗位
            lsUserunit.removeIf(uu -> !roleCode.equals(uu.getUserStation()));
        } else if (ROLE_TYPE_XZ.equalsIgnoreCase(roleType)) {
            // 过滤掉不符合要求的职位
            lsUserunit.removeIf(uu -> !roleCode.equals(uu.getUserRank()));
        } else
            lsUserunit.clear();

        // 获取所有 符合条件的用户代码
        Set<String> users = new HashSet<>();
        for (IUserUnit uu : lsUserunit) {
            users.add(uu.getUserCode());
        }
        return users;
    }

    private static Set<String> getUsersByFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene rf) {

        boolean hasUnitFilter = rf.isHasGWFilter() || rf.isHasRankFilter() || rf.isHasXZFilter();

        boolean hasTypeTagFilter = rf.isHasUserTagFilter() || rf.isHasUserTypeFilter();
        //获取机构列表
        SysUnitFilterEngine.getUnitsByFilter(ecc, rf);

        if(hasUnitFilter) {
            // 获取所有候选人的岗位、职务信息
            List<IUserUnit> lsUserunit = new LinkedList<>();
            if (rf.isHasUnitFilter()) {
                for (String unitCode : rf.getUnits()) {
                    if (rf.isOnlyGetPrimaryUser()) {
                        for (IUserUnit uu : ecc.listUnitUsers(unitCode)) {
                            if ("T".equals(uu.getIsPrimary())) {
                                lsUserunit.add(uu);
                            }
                        }
                    } else
                        lsUserunit.addAll(ecc.listUnitUsers(unitCode));
                }
            } else {
                lsUserunit.addAll(ecc.listAllUserUnits());
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
                if (rf.isRankAllSub() || rf.isRankAllTop()) { // 所有下级
                    lsUserunit.removeIf(uu -> !rf.matchRank(ecc.getXzRank(uu.getUserRank())));
                } else {
                    Map<String, Integer> unitRank = new HashMap<String, Integer>();
                    for (IUserUnit uu : lsUserunit) {
                        if (rf.matchRank(ecc.getXzRank(uu.getUserRank()))) {
                            Integer nR = unitRank.get(uu.getUnitCode());
                            if (nR == null) {
                                unitRank.put(uu.getUnitCode(), ecc.getXzRank(uu.getUserRank()));
                            } else {
                                if (rf.isRankPlus() && nR > ecc.getXzRank(uu.getUserRank()))
                                    unitRank.put(uu.getUnitCode(), ecc.getXzRank(uu.getUserRank()));
                                else if (rf.isRankMinus() && nR < ecc.getXzRank(uu.getUserRank()))
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
            if(rf.getUsers() != null && rf.getUsers().size()>0){
                lsUserInfo = new ArrayList<>(rf.getUsers().size()+1);
                for (String userCode  : rf.getUsers()) {
                    lsUserInfo.add(ecc.getUserInfoByCode(userCode));
                }
            }else if(!hasUnitFilter){
                List<? extends IUserInfo> extUserInfo = ecc.listAllUserInfo();
                lsUserInfo = new ArrayList<>(extUserInfo.size()+1);
                lsUserInfo.addAll( extUserInfo);
            }

            if(lsUserInfo!=null) {
                if (rf.isHasUserTypeFilter()) {
                    // 过滤掉不符合要求的岗位
                    lsUserInfo.removeIf(user -> !rf.getUserTypes().contains(user.getUserType()));
                }

                if (rf.isHasUserTagFilter()) {
                    for (Iterator<IUserInfo> it = lsUserInfo.iterator(); it.hasNext(); ) {
                        IUserInfo user = it.next();
                        boolean hasTag = false;
                        if (StringUtils.isNoneBlank(user.getUserTag())) {
                            String tags[] = user.getUserTag().split(",");
                            for (String tag : tags) {
                                if (rf.getUserTags().contains(tag)) {
                                    hasTag = true;
                                    break;
                                }
                            }
                        }
                        if (!hasTag)
                            it.remove();
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
     * D()DT()DL()GW()XZ()R()U()UT()UL()
     * @param ecc UserUnitFilterCalcContext
     * @return calcSimpleExp
     */
    private static Set<String> calcSimpleExp(UserUnitFilterCalcContext ecc) {
        UserUnitFilterGene gene = InnerUserUnitFilterCompileEngine.makeUserUnitFilter(ecc);
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
            ecc.setPreword(w);
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
                ecc.setPreword(w);
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

    public static Set<String> calcSystemOperators(String roleExp, Map<String, Set<String>> unitParams,
                                                Map<String, Set<String>> userParams,
                                                Map<String, Integer> rankParams,
                                                UserUnitVariableTranslate varTrans) {
        if (roleExp == null)
            return null;
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
        if (sUsers == null || ecc.hasError())
            logger.error(ecc.getLastErrMsg());
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
