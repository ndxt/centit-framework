package com.centit.framework.components;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.StringRegularOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @author codefan
 * @version 2.0
 * 2012-2-3
 */
public abstract class SysUserFilterEngine implements Serializable {
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
    /**
     * 按部门过滤
     */
    public static final String USER_FILTER_DEPARTMENT = "D";
    /**
     * 按主机构过滤
     */
    public static final String USER_FILTER_PRIMARYUNIT = "P";
    /**
     * 按照用户代码过滤
     */
    public static final String USER_FILTER_USERCODE = "U";
    /**
     * 按照 岗位角色过滤
     */
    public static final String USER_FILTER_ROLE_TYPE_GW = "GW";
    /**
     * 按照行政角色过滤
     */
    public static final String USER_FILTER_ROLE_TYPE_XZ = "XZ";
    /**
     * 按照行政角色等级过滤
     */
    public static final String USER_FILTER_ROLE_RANK = "R";
    /**
     * 所有过滤方式
     */
    public static final String ALL_USER_FILTER_ROLE_RANK = "'D'、'P'、'U'、'GW'、'XZ'、'R'";

    private SysUserFilterEngine()
    {
        throw new IllegalAccessError("Utility class");
    }
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SysUserFilterEngine.class);

    /**
     * 每个RoleFilterGene类对应一个个权限表达式
     *
     * @author codefan
     * @version 2.0
     */
    private static class RoleFilterGene {
        private boolean hasUnitFilter;
        private boolean hasUserFilter;
        private boolean hasGWFilter;
        private boolean hasXZFilter;
        private boolean hasRankFilter;
        private boolean onlyGetPrimaryUser;

        private Set<String> units;
        private Set<String> users;
        private Set<String> xzRoles;
        private Set<String> gwRoles;

        private int xzRank;
        private boolean rankPlus;
        private boolean rankMinus;
        private boolean rankAllTop;
        private boolean rankAllSub;

        public Set<String> getUnits() {
            return units;
        }

        public Set<String> getUsers() {
            // if(users==null)
            // users = new HashSet<String>();
            return users;
        }

        public Set<String> getXzRoles() {
            return xzRoles;
        }

        public Set<String> getGwRoles() {
            return gwRoles;
        }

        public void setXzRank(int r) {
            xzRank = r;
            hasRankFilter = true;
        }

        public boolean isHasUnitFilter() {
            return hasUnitFilter;
        }

        public boolean isHasUserFilter() {
            return hasUserFilter;
        }

        public boolean isHasGWFilter() {
            return hasGWFilter;
        }

        public boolean isHasXZFilter() {
            return hasXZFilter;
        }

        public boolean isHasRankFilter() {
            return hasRankFilter;
        }

        public boolean isRankMinus() {
            return rankMinus;
        }

        public void setRankMinus() {
            this.rankMinus = true;
            this.rankPlus = false;
        }

        public boolean isRankPlus() {
            return rankPlus;
        }

        public void setRankPlus() {
            this.rankPlus = true;
            this.rankMinus = false;
        }

        public boolean isRankAllTop() {
            return rankAllTop;
        }
        
        public boolean isOnlyGetPrimaryUser() {
            return onlyGetPrimaryUser;
        }
        
        public void setOnlyGetPrimaryUser(boolean onlyGetPrimaryUser) {
            this.onlyGetPrimaryUser = onlyGetPrimaryUser;
        }
        
        public void setRankAllTop() {
            this.rankAllTop = true;
            this.rankAllSub = false;
            xzRank--;
            setRankMinus();
        }

        public boolean isRankAllSub() {
            return rankAllSub;
        }

        public void setRankAllSub() {
            this.rankAllTop = false;
            this.rankAllSub = true;
            xzRank++;
            setRankPlus();
        }

        public RoleFilterGene() {
            hasUnitFilter = hasUserFilter = hasGWFilter = hasXZFilter = hasRankFilter = rankPlus = rankMinus = rankAllTop = rankAllSub = false;
            units = new HashSet<>();
            users = new HashSet<>();
            xzRoles = new HashSet<>();
            gwRoles = new HashSet<>();
            xzRank = 0;
        }

        public void addUnits(Set<String> sucs) {
            if (sucs != null && sucs.size() > 0) {
                units.addAll(sucs);
                hasUnitFilter = true;
            }
        }

        public void addUser(String suc) {
            users.add(suc);
            hasUserFilter = true;
        }

        public void addUsers(Set<String> sucs) {
            if (sucs != null && sucs.size() > 0) {
                users.addAll(sucs);
                hasUserFilter = true;
            }
        }

        public void addXzRole(String rc) {
            xzRoles.add(rc);
            hasXZFilter = true;
        }

        public void addGwRole(String rc) {
            gwRoles.add(rc);
            hasGWFilter = true;
        }

        public boolean matchRank(int nR) {
            return rankPlus ? (nR >= xzRank) : (rankMinus ? nR <= xzRank : nR == xzRank);
        }

    }// end of class RoleFilterGene

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
            for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                IUserUnit uu = it.next();
                // 过滤掉不符合要求的岗位
                if (!roleCode.equals(uu.getUserStation()))
                    it.remove();
            }
        } else if (ROLE_TYPE_XZ.equalsIgnoreCase(roleType)) {
            for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                IUserUnit uu = it.next();
                // 过滤掉不符合要求的职位
                if (!roleCode.equals(uu.getUserRank()))
                    it.remove();
            }
        } else
            lsUserunit.clear();

        // 获取所有 符合条件的用户代码
        Set<String> users = new HashSet<>();
        for (IUserUnit uu : lsUserunit) {
            users.add(uu.getUserCode());
        }
        return users;
    }



    private static Set<String> getUsersByFilter(UserUnitFilterCalcContext ecc, RoleFilterGene rf) {
        boolean hasFilter = rf.isHasGWFilter() || rf.isHasRankFilter() || rf.isHasUnitFilter() || rf.isHasUserFilter()
                || rf.isHasXZFilter();

        if (!hasFilter)
            return new HashSet<>();
        if (rf.isHasUserFilter())
            return rf.getUsers();
        /**
         * 这个地方有一个逻辑错误
         *
         */
        // 获取所有候选人的岗位、职务信息
        List<IUserUnit> lsUserunit = new LinkedList<>();
        if (rf.isHasUnitFilter()) {
            for (String unitCode : rf.getUnits()) {          
                if(rf.isOnlyGetPrimaryUser()){
                    for(IUserUnit uu: ecc.listUnitUsers(unitCode )){
                        if("T".equals(uu.getIsPrimary())){
                            lsUserunit.add(uu); 
                        }
                    }
                }else
                    lsUserunit.addAll(ecc.listUnitUsers(unitCode ) );
            }
        } else {
            lsUserunit.addAll(ecc.listAllUserUnits());
        }

        if (rf.isHasGWFilter()) {
            for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                IUserUnit uu = it.next();
                // 过滤掉不符合要求的岗位
                if (!rf.getGwRoles().contains(uu.getUserStation()))
                    it.remove();
            }
        }

        if (rf.isHasXZFilter()) {
            for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                IUserUnit uu = it.next();
                // 过滤掉不符合要求的职位
                if (!rf.getXzRoles().contains(uu.getUserRank()))
                    it.remove();
            }
        }

        if (rf.isHasRankFilter()) {
            if (rf.isRankAllSub() || rf.isRankAllTop()) { // 所有下级
                for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                    IUserUnit uu = it.next();
                    if (!rf.matchRank(ecc.getXzRank(uu.getUserRank())))
                        it.remove();
                }
            } else {
                Map<String, Integer> unitRank = new HashMap<String, Integer>();
                for (IUserUnit uu : lsUserunit) {
                    if (rf.matchRank(ecc.getXzRank(uu.getUserRank()))) {
                        Integer nR = unitRank.get(uu.getUnitCode());
                        if (nR == null) {
                            unitRank.put(uu.getUnitCode(),ecc.getXzRank(uu.getUserRank()));
                        } else {
                            if (rf.isRankPlus() && nR.intValue() > ecc.getXzRank(uu.getUserRank()))
                                unitRank.put(uu.getUnitCode(),ecc.getXzRank(uu.getUserRank()));
                            else if (rf.isRankMinus() && nR.intValue() < ecc.getXzRank(uu.getUserRank()))
                                unitRank.put(uu.getUnitCode(),ecc.getXzRank(uu.getUserRank()));
                        }
                    }
                }

                for (Iterator<IUserUnit> it = lsUserunit.iterator(); it.hasNext(); ) {
                    IUserUnit uu = it.next();
                    // 过滤掉不符合要求的职位
                    Integer nR = unitRank.get(uu.getUnitCode());
                    if (nR == null || nR.intValue() != ecc.getXzRank(uu.getUserRank()))
                        it.remove();
                }
            }

        }

        // 获取所有 符合条件的用户代码
        rf.getUsers().clear();
        for (IUserUnit uu : lsUserunit) {
            rf.addUser(uu.getUserCode());
        }

        return rf.getUsers();
    }

    /**
     * D(机构表达式)
     *
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcUnits(UserUnitFilterCalcContext ecc, RoleFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcRoleUnits begin .");
            return false;
        }
        Set<String> units = SysUnitFilterEngine.calcUnitsExp(ecc);
        if (units == null)
            return false;
        gene.addUnits(units);
        w = ecc.getAWord();
        if (")".equals(w))
            return true;
        else
            return false;
    }

    /**
     * U(用户变量|"用户代码常量" [,用户变量|"用户代码常量]* )
     *
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcUsers(UserUnitFilterCalcContext ecc, RoleFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcRoleUsers begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                return false;
            }

            if (")".equals(w)) {
                return true;
            }
            if (ecc.isLabel(w)) { // 变量
                Set<String> users = ecc.getUserCode(w);
                if (users != null)
                    gene.addUsers(users);
                else{//作为常量
                    String userCode = StringRegularOpt.trimString(w);
                    if (ecc.getUserInfoByCode(userCode) != null)
                        gene.addUser(userCode);
                }
            } else {//作为常量
                String userCode = StringRegularOpt.trimString(w);
                if (ecc.getUserInfoByCode(userCode) != null)
                    gene.addUser(userCode);
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcRoleUsers end .");
                return false;
            }
        }
    }

    /**
     * gw("角色代码常量" [,"角色代码常量"]* )
     *
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcGwRoles(UserUnitFilterCalcContext ecc, RoleFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcGwRoles begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcGwRoles end .");
                return false;
            }

            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }
            if (ecc.isLabel(w)) { // 变量
                gene.addGwRole(w);
            } else if (StringRegularOpt.isString(w)) { // 常量
                String roleCode = StringRegularOpt.trimString(w);
                gene.addGwRole(roleCode);
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [rolecode]; calcGwRoles label . ");
                return false;
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcGwRoles , .");
                return false;
            }
        }
    }

    /**
     * xz("角色代码常量" [,"角色代码常量"]* )
     *
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcXzRoles(UserUnitFilterCalcContext ecc, RoleFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcXzRoles begin . ");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')'  ; calcXzRoles end .");
                return false;
            }

            if (")".equals(w)) {// 逗号后没有变量 或略这个错误
                return true;
            }

            if (ecc.isLabel(w)) { // 变量
                gene.addXzRole(w);
            } else if (StringRegularOpt.isString(w)) { // 常量
                String roleCode = StringRegularOpt.trimString(w);
                gene.addXzRole(roleCode);
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [rolecode]; calcXzRoles label . ");
                return false;
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcXzRoles , .");
                return false;
            }
        }
    }

    private static boolean calcXzRank(UserUnitFilterCalcContext ecc, RoleFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcXzRank begin . ");
            return false;
        }

        w = ecc.getAWord();
        if (w == null || "".equals(w)) {
            ecc.setLastErrMsg("endoffile is unexpected, expect ')' ; calcXzRank empty . ");
            return false;
        }
        if (")".equals(w)) { // 空过滤器忽略
            return true;
        }
        // -1 表示错误，没有等级，0 表示虚拟的最高值  1 等级最高 越大等级越低
        int rank = -1 ;

        if (ecc.isLabel(w)) { // 变量
            rank = ecc.getRank(w);
        } else if (StringRegularOpt.isNumber(w)) { // 常量
            rank = ecc.stringToRank(w);
        } else { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect label or number [rolerank]; calcXzRank label . ");
            return false;
        }
        w = ecc.getAWord();
        if ("+".equals(w)) {
            w = ecc.getAWord();
            gene.setRankPlus();
            if (!StringRegularOpt.isNumber(w)) {
                if (!")".equals(w)) {
                    ecc.setLastErrMsg(w + " is unexpected, expect number or ')' ; calcXzRank +  .");
                    return false;
                } else
                    ecc.setPreword(w);
            } else {
                if(rank>=0)
                    rank += Integer.valueOf(StringRegularOpt.trimString(w));
                w = ecc.getAWord();
            }

        } else if ("-".equals(w)) {
            w = ecc.getAWord();
            gene.setRankMinus();
            if (!StringRegularOpt.isNumber(w)) {
                if (!")".equals(w)) {
                    ecc.setLastErrMsg(w + " is unexpected, expect number or ')' ; calcXzRank -  .");
                    return false;
                } else
                    ecc.setPreword(w);
            } else {
                if(rank>=0)
                    rank -= Integer.valueOf(StringRegularOpt.trimString(w));
                w = ecc.getAWord();
            }
        } else if ("++".equals(w)) {// 所有的下级
            gene.setRankAllSub();
        } else if ("--".equals(w)) {// 所有的上级
            gene.setRankAllTop();
        }
        if (rank >= 0)
            gene.setXzRank(rank);

        if (")".equals(w)) {
            return true;
        } else {
            ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcXzRank end  .");
            return false;
        }
    }

    /**
     * D()GW()XZ()R()U()
     * @return
     */
    private static Set<String> calcSimpleExp(UserUnitFilterCalcContext ecc) {
        RoleFilterGene gene = new RoleFilterGene();
        String w = ecc.getAWord();
        while (true) {
            if (w == null || "".equals(w))
                break;
            if (",".equals(w) || ")".equals(w) || "!".equals(w) || "|".equals(w) || "||".equals(w) || "&".equals(w)
                    || "&&".equals(w)) {
                ecc.setPreword(w);
                break;
            }
            //根据部门过滤
            if(USER_FILTER_DEPARTMENT.equalsIgnoreCase(w)){
                if(!calcUnits(ecc,gene))
                    return null;
                gene.setOnlyGetPrimaryUser(false);                
            }else /*仅仅获取主要机构用户*/ if(USER_FILTER_PRIMARYUNIT.equalsIgnoreCase(w)){
                if(!calcUnits(ecc,gene))
                    return null;
                gene.setOnlyGetPrimaryUser(true);
            }else /*根据岗位角色过滤*/if (USER_FILTER_ROLE_TYPE_GW.equalsIgnoreCase(w)) {
                if (!calcGwRoles(ecc, gene))
                    return null;
            } else /*根据行政角色过滤*/if (USER_FILTER_ROLE_TYPE_XZ.equalsIgnoreCase(w)) {
                if (!calcXzRoles(ecc, gene))
                    return null;
            } else /*根据行政角色等级过滤*/if (USER_FILTER_ROLE_RANK.equalsIgnoreCase(w)) {
                if (!calcXzRank(ecc, gene))
                    return null;
            } else /*根据用户代码过滤*/ if (USER_FILTER_USERCODE.equalsIgnoreCase(w)) {
                if (!calcUsers(ecc, gene))
                    return null;
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect "+ ALL_USER_FILTER_ROLE_RANK+" ; calcSimpleRole ");
                return null;
            }
            w = ecc.getAWord();
        }

        return getUsersByFilter(ecc,gene);
    }

    /**
     * S(rolesExp[,rolesExp]* )
     *
     * @param ecc
     * @return
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
        // if(lastSameNodeUnit!=null)
        ecc.addAllUnitParam(unitParams);
        ecc.addAllUserParam(userParams);
        ecc.addAllRankParam(rankParams);

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

}
