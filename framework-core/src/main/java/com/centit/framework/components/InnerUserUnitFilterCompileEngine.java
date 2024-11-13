package com.centit.framework.components;

import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.compiler.VariableFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author codefan
 * @version 2.0
 * 2012-2-3
 */
public abstract class InnerUserUnitFilterCompileEngine {
    /**
     * 按部门过滤
     */
    public static final String USER_FILTER_DEPARTMENT = "D";
    /**
     * 按主机构过滤
     */
    public static final String USER_FILTER_RELTYPE = "rel";
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
     * 按照系统角色过滤
     */
    public static final String USER_FILTER_SYSTEM_ROLE = "RO";
    /**
     * 按照行政角色等级过滤
     */
    public static final String USER_FILTER_ROLE_RANK = "R";

    /**
     * 根据机构类别过滤
     */
    public static final String USER_FILTER_UNIT_TYPE = "DT";

    /**
     * 根据结构tag标记过滤 label
     */
    public static final String USER_FILTER_UNIT_LABEL = "DL";
    /**
     * 根据用户类别过滤
     */
    public static final String USER_FILTER_USER_TYPE = "UT";

    /**
     * 根据用户tag标记过滤 label
     */
    public static final String USER_FILTER_USER_LABEL = "UL";
    /**
     * 所有过滤方式
     */
    public static final String ALL_USER_FILTER_ROLE_RANK = "'D'、'P'、'U'、'GW'、'XZ'、'R'、'DT'、'DL'、'UT'、'UL'、'RO'";

    private InnerUserUnitFilterCompileEngine()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(InnerUserUnitFilterCompileEngine.class);

    private static Object mapVariable(UserUnitFilterCalcContext ecc, String w){
        if(StringRegularOpt.isNumber(w) ){
            return Integer.valueOf(StringRegularOpt.trimString(w));
        }
        if(ecc.isLabel(w)){
            UserUnitVariableTranslate variableTranslate = ecc.getVarTrans();
            if(variableTranslate != null){
                Object obj = ecc.getVarTrans().getVarValue(w);
                if(obj != null){
                    if(obj instanceof Collection){
                        Collection<?> objects = (Collection<?>) obj;
                        if(objects.isEmpty()){
                            return null;
                        }
                        if(objects.size() == 1){
                            return objects.iterator().next();
                        }
                    }
                    return obj;
                }
            }
            return w;
        }
        if(w.charAt(0)=='"' || w.charAt(0)=='\'') {
            return StringRegularOpt.trimString(w);
        }
        return null;
    }

    /**
     * D(null) =>null D(all) => D1,D2,D11,D12,D111,D112,D1111,D1112 D("D12")
     * =>D12 D(null+1) =>D1,D2 D(all+1) => D11,D12,D111,D112,D1111,D1112 D(A) =>
     * D111 D(U+1) => D1111,D1112 D(U-1) => D11 D(P-1+1) => D111,D112 D(W*1) =>
     * D1
     * @param ecc UserUnitFilterCalcContext
     * @return makeUserUnitFilter
     */
    private static Set<String> calcSimpleUnitExp(UserUnitFilterCalcContext ecc) {
        Set<String> units = new HashSet<>();
        String w = ecc.getAWord();
        if (ecc.isLabel(w)) { // 变量
            if ("all".equalsIgnoreCase(w)) {
                for (UnitInfo unitEnt: ecc.listAllUnitInfo()) {
                    units.add(unitEnt.getUnitCode());
                }
            } else if ("empty".equalsIgnoreCase(w)) {
                w = ecc.getAWord();
                if ("-".equals(w)) { // 获取所有 叶子 机构
                    units = SysUnitFilterEngine.nullParentUnits(ecc,1);
                } else if ("+".equals(w)) {  // 获取所有 根 机构
                    units = SysUnitFilterEngine.nullSubUnits(ecc,1);
                }
                ecc.writeBackAWord(w);
            } else {
                Set<String> us = ecc.getUnitCode(w);
                if (us != null)
                    units.addAll(us);
                else{
                    if(ecc.getUnitInfoByCode(w) !=null){
                        units.add(w);
                    }
                }
            }
        } else if (!VariableFormula.isKeyWord(w)) { // 常量
            String unitCode = StringRegularOpt.trimString(w);
            if (ecc.getUnitInfoByCode(unitCode) != null)
                units.add(unitCode);
        } else { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect label or string [unitcode]; calcSimpleUnit label . ");
            return null;
        }
        // --------------------------------------------------------------//
        w = ecc.getAWord();
        while(w != null && ! "".equals(w) && ! ")".equals(w) && ! ",".equals(w)) {
            if ("-".equals(w)) {
                ecc.setCanAcceptOpt(true);
                w = ecc.getAWord();
                Object objTiers = mapVariable(ecc, w);
                if (objTiers instanceof Integer) {
                    units = SysUnitFilterEngine.parentUnits(ecc, units, (Integer) objTiers);
                } else if (objTiers instanceof String) {
                    units = SysUnitFilterEngine.parentUnits(ecc, units, (String) objTiers);
                } else {
                    ecc.setLastErrMsg(w + " is unexpected, expect number ; calcSimpleUnit - . ");
                    return null;
                }
            }else if ("+".equals(w)) {
                w = ecc.getAWord();
                Object objTiers = mapVariable(ecc, w);
                if (objTiers instanceof Integer) {
                    units = SysUnitFilterEngine.subUnits(ecc, units, (Integer) objTiers);
                } else if (objTiers instanceof String) {
                    units = SysUnitFilterEngine.subUnits(ecc, units, (String) objTiers);
                } else {
                    ecc.setLastErrMsg(w + " is unexpected, expect number; calcSimpleUnit + . ");
                    return null;
                }
            } else if ("*".equals(w)) {
                ecc.setCanAcceptOpt(true);
                w = ecc.getAWord();
                if ("+".equals(w)) {// 所有同一系列 同一层节点
                    w = ecc.getAWord();
                    Object objTiers = mapVariable(ecc, w);
                    if (objTiers instanceof Integer) {
                        units = SysUnitFilterEngine.topUnits(ecc, units, (Integer) objTiers);
                    } else {
                        ecc.setLastErrMsg(w + " is unexpected, expect number ; calcSimpleUnit *+.");
                        return null;
                    }

                } else if ("-".equals(w)) {// 所有节点的上层节点中， 指定层次的节点
                    w = ecc.getAWord();
                    Object objTiers = mapVariable(ecc, w);
                    if (objTiers instanceof Integer) {
                        Set<String> parUnits = SysUnitFilterEngine.allParentUnits(ecc, units);
                        units = SysUnitFilterEngine.topUnits(ecc, units, (Integer) objTiers);
                        units.retainAll(parUnits);
                    } else {
                        ecc.setLastErrMsg(w + " is unexpected, expect number ; calcSimpleUnit *-.");
                        return null;
                    }
                } else {  //所有同一系列最上面几层节点
                    Object objTiers = mapVariable(ecc, w);
                    if (objTiers instanceof Integer) {
                        units = SysUnitFilterEngine.seriesUnits(ecc, units, (Integer) objTiers);
                    } else {
                        ecc.setLastErrMsg(w + " is unexpected, expect number ; calcSimpleUnit *.");
                        return null;
                    }
                }
            } else if ("++".equals(w)) {// 所有的下层节点
                units = SysUnitFilterEngine.allSubUnits(ecc, units);
            } else if ("--".equals(w)) {// 所有的上层节点
                units = SysUnitFilterEngine.allParentUnits(ecc, units);
            } else if ("**".equals(w)) {// 所有同一系列节点
                units = SysUnitFilterEngine.allSeriesUnits(ecc, units);
            }
            w = ecc.getAWord();
        }
        ecc.writeBackAWord(w);
        // w = ecc.getAWord();
        // if(")".equals(w)){ //语句结束
        return units;
    }


    /**
     * D(机构表达式,......)
     *
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUnits(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUnits begin.");
            return false;
        }
        while(true) {
            Set<String> units = calcSimpleUnitExp(ecc);
            if (units != null) {
                gene.addUnits(units);
            }
            w = ecc.getAWord();
            if (")".equals(w))
                return true;
            if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcUnits end.");
                return false;
            }
        }
    }

    /**
     * DT("角色代码常量" [,"角色代码常量"]* )
     *
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUnitTypeFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUnitTypeFilter begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcUnitTypeFilter end .");
                return false;
            }
            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String) { // 变量
                gene.addUnitType((String)obj);
            } else {
                List<String> tags = StringBaseOpt.objectToStringList(obj);
                if(tags == null || tags.isEmpty()) { // 语法错误
                    ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Type]; calcUnitTypeFilter label . ");
                    return false;
                }
                for(String tag : tags) {
                    gene.addUnitType(tag);
                }
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcUnitTypeFilter , .");
                return false;
            }
        }
    }

    /**
     * DL("角色代码常量" [,"角色代码常量"]* )
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUnitTagFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUnitTagFilter begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcUnitTagFilter end .");
                return false;
            }

            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addUnitTag((String)obj);
            } else {
                List<String> tags = StringBaseOpt.objectToStringList(obj);
                if(tags == null || tags.isEmpty()) { // 语法错误
                    ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Tag]; calcUnitTagFilter label . ");
                    return false;
                }
                for(String tag : tags) {
                    gene.addUnitTag(tag);
                }
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcUnitTagFilter , .");
                return false;
            }
        }
    }

    /**
     * U(用户变量|"用户代码常量" [,用户变量|"用户代码常量]* )
     *
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUsers(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
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
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcUsers end .");
                return false;
            }
        }
    }

    /**
     * gw("角色代码常量" [,"角色代码常量"]* )
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcGwRoles(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
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
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addGwRole((String)obj);
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
     * ro("系统角色代码常量" [,"系统角色代码常量"]* )
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcOptRoles(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcOptRoles begin . ");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')'  ; calcOptRoles end .");
                return false;
            }
            if (")".equals(w)) {// 逗号后没有变量 或略这个错误
                return true;
            }
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addOptRole((String)obj);
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [rolecode]; calcOptRoles label . ");
                return false;
            }
            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcOptRoles , .");
                return false;
            }
        }
    }
    /**
     * xz("角色代码常量" [,"角色代码常量"]* )
     * @param ecc
     * @param gene
     * @return
     */
    private static boolean calcXzRoles(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
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
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addXzRole((String)obj);
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

    /**
     * UT("用户类型常量" [,"用户类型常量"]* )
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUserTypeFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUserTypeFilter begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcUserTypeFilter end .");
                return false;
            }

            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addUserType((String)obj);
            } else {
                List<String> tags = StringBaseOpt.objectToStringList(obj);
                if(tags == null || tags.isEmpty()) { // 语法错误
                    ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Type]; calcUserTypeFilter label . ");
                    return false;
                }
                for(String tag : tags) {
                    gene.addUserType(tag);
                }
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcUserTypeFilter , .");
                return false;
            }
        }
    }

    private static boolean calcUserUnitRelType(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUserUnitRelType begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcUserUnitRelType end .");
                return false;
            }

            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }

            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addUserUnitRelType((String)obj);
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [UserUnitRelType]; calcUserUnitRelType label . ");
                return false;
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcUserUnitRelType , .");
                return false;
            }
        }
    }
    /**
     * UL("用户标记常量" [,"用户标记常量"]* )
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUserTagFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcUserTagFilter begin .");
            return false;
        }
        while (true) {
            w = ecc.getAWord();
            if (w == null || "".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcUserTagFilter end .");
                return false;
            }

            if (")".equals(w)) { // 逗号后没有变量 或略这个错误
                return true;
            }
            Object obj = mapVariable(ecc,w);
            if (obj instanceof String){ // 变量
                gene.addUserTag((String)obj);
            } else {
                List<String> tags = StringBaseOpt.objectToStringList(obj);
                if(tags == null || tags.isEmpty()) { // 语法错误
                    ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Tag]; calcUserTagFilter label . ");
                    return false;
                }
                for(String tag : tags) {
                    gene.addUserTag(tag);
                }
            }

            w = ecc.getAWord();
            if (")".equals(w)) {
                return true;
            } else if (!",".equals(w)) {
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ','  ; calcUserTagFilter , .");
                return false;
            }
        }
    }
    //R(U) / R(U-) / R(U-1) / R(U--) /R(U-1--)
    //一共5种情况，上面的U为一个变量，也可以是一个 数字 常量
    private static boolean calcXzRank(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
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
        String rank =  ecc.getRank(w);
        w = ecc.getAWord();
        if ("+".equals(w)) {
            gene.setRankPlus();
            w = ecc.getAWord();
            if (!StringRegularOpt.isNumber(w)) {
                if (!")".equals(w)) {
                    ecc.setLastErrMsg(w + " is unexpected, expect number or ')' ; calcXzRank +  .");
                    return false;
                } /*else
                    ecc.writeBackAWord(w);*/
            } else {
                int n = Integer.valueOf(StringRegularOpt.trimString(w), 0);
                for(int i=0; i<n; i++){
                    rank = StringBaseOpt.nextCode(rank);
                }
                w = ecc.getAWord();
            }
        } else if ("-".equals(w)) {
            w = ecc.getAWord();
            gene.setRankMinus();
            if (!StringRegularOpt.isNumber(w)) {
                if (!")".equals(w)) {
                    ecc.setLastErrMsg(w + " is unexpected, expect number or ')' ; calcXzRank -  .");
                    return false;
                } /*else
                    ecc.writeBackAWord(w);*/
            } else {
                int n = Integer.valueOf(StringRegularOpt.trimString(w), 0);
                for(int i=0; i<n; i++){
                    rank = StringBaseOpt.prevCode(rank);
                }
                w = ecc.getAWord();
            }
        }
        // 判断是否需要所有的
        if ("++".equals(w)) {// 所有的下级
            gene.setRankAllSub();
            w = ecc.getAWord();
        } else if ("--".equals(w)) {// 所有的上级
            gene.setRankAllTop();
            w = ecc.getAWord();
        }

        gene.setXzRank(rank);

        if (")".equals(w)) {
            return true;
        } else {
            ecc.setLastErrMsg(w + " is unexpected, expect ')' ; calcXzRank end  .");
            return false;
        }
    }

    /**
     * D()DT()DL()GW()XZ()R()UT()UL()U()RO()
     * @param ecc 表达式运行上下文环境
     * @return 过滤条件因素，这个可以认为是一个分析好的 filter 语句
     */
    public static UserUnitFilterGene makeUserUnitFilter(UserUnitFilterCalcContext ecc) {
        UserUnitFilterGene gene = new UserUnitFilterGene();
        String w = ecc.getAWord();
        while (true) {
            if (w == null || "".equals(w))
                break;
            if (",".equals(w) || ")".equals(w) || "!".equals(w) || "|".equals(w) || "||".equals(w) || "&".equals(w)
                || "&&".equals(w)) {
                ecc.writeBackAWord(w);
                break;
            }
            /*根据部门过滤*/
            if(USER_FILTER_DEPARTMENT.equalsIgnoreCase(w)){
                if(!calcUnits(ecc,gene))
                    return null;
            }else /*根据用户类别过滤*/if (USER_FILTER_UNIT_TYPE.equalsIgnoreCase(w)) {
                if (!calcUnitTypeFilter(ecc, gene))
                    return null;
            } else /*根据用户标签过滤*/if (USER_FILTER_UNIT_LABEL.equalsIgnoreCase(w)) {
                if (!calcUnitTagFilter(ecc, gene))
                    return null;
            } else /*过滤用户机构关联关系*/ if(USER_FILTER_RELTYPE.equalsIgnoreCase(w)){
                if(!calcUserUnitRelType(ecc,gene))
                    return null;

            }else /*根据岗位角色过滤*/if (USER_FILTER_ROLE_TYPE_GW.equalsIgnoreCase(w)) {
                if (!calcGwRoles(ecc, gene))
                    return null;
            } else /*根据行政角色过滤*/if (USER_FILTER_ROLE_TYPE_XZ.equalsIgnoreCase(w)) {
                if (!calcXzRoles(ecc, gene))
                    return null;
            } else /*根据行政角色过滤*/if (USER_FILTER_SYSTEM_ROLE.equalsIgnoreCase(w)) {
                if (!calcOptRoles(ecc, gene))
                    return null;
            } else /*根据行政角色等级过滤*/if (USER_FILTER_ROLE_RANK.equalsIgnoreCase(w)) {
                if (!calcXzRank(ecc, gene))
                    return null;
            } else /*根据用户类别过滤*/if (USER_FILTER_USER_TYPE.equalsIgnoreCase(w)) {
                if (!calcUserTypeFilter(ecc, gene))
                    return null;
            } else /*根据用户标签过滤*/if (USER_FILTER_USER_LABEL.equalsIgnoreCase(w)) {
                if (!calcUserTagFilter(ecc, gene))
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

        return gene;
    }
}
