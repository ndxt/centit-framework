package com.centit.framework.components;

import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author codefan
 * @version 2.0
 * 2012-2-3
 */
public abstract class InnerUserUnitFilterCompileEngine {
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
    public static final String ALL_USER_FILTER_ROLE_RANK = "'D'、'P'、'U'、'GW'、'XZ'、'R'、'DT'、'DL'、'UT'、'UL'";

    private InnerUserUnitFilterCompileEngine()
    {
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(InnerUserUnitFilterCompileEngine.class);

    /**
     * D(机构表达式)
     *
     * @param ecc 运行环境
     * @param gene 过滤条件
     * @return 是否正确运行
     */
    private static boolean calcUnits(UserUnitFilterCalcContext ecc, UserUnitFilterGene gene) {
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
            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUnitTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
            } else if (StringRegularOpt.isString(w)) { // 常量
                gene.addUnitTag(StringRegularOpt.trimString(w));
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Type]; calcUnitTypeFilter label . ");
                return false;
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
            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUnitTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
            } else if (StringRegularOpt.isString(w)) { // 常量
                gene.addUnitTag(StringRegularOpt.trimString(w));
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Tag]; calcUnitTagFilter label . ");
                return false;
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
                ecc.setLastErrMsg(w + " is unexpected, expect ')' or ',' ; calcRoleUsers end .");
                return false;
            }
        }
    }

    /**
     * gw("角色代码常量" [,"角色代码常量"]* )
     *
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
            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUserTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
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

            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUserTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
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

    /**
     * UT("角色代码常量" [,"角色代码常量"]* )
     *
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
            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUserTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
            } else if (StringRegularOpt.isString(w)) { // 常量
                gene.addUserType(StringRegularOpt.trimString(w));
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Type]; calcUserTypeFilter label . ");
                return false;
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

    /**
     * UL("角色代码常量" [,"角色代码常量"]* )
     *
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
            if (ecc.isLabel(w)) { // 变量
                Object obj = ecc.getVarTrans().getGeneralVariable(w);
                gene.addUserTag(obj==null?w:StringBaseOpt.castObjectToString(obj));
            } else if (StringRegularOpt.isString(w)) { // 常量
                gene.addUserTag(StringRegularOpt.trimString(w));
            } else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect label or string [User Tag]; calcUserTagFilter label . ");
                return false;
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
    public static UserUnitFilterGene calcSimpleExp(UserUnitFilterCalcContext ecc) {
        UserUnitFilterGene gene = new UserUnitFilterGene();
        String w = ecc.getAWord();
        while (true) {
            if (w == null || "".equals(w))
                break;
            if (",".equals(w) || ")".equals(w) || "!".equals(w) || "|".equals(w) || "||".equals(w) || "&".equals(w)
                    || "&&".equals(w)) {
                ecc.setPreword(w);
                break;
            }
            /*根据部门过滤*/
            if(USER_FILTER_DEPARTMENT.equalsIgnoreCase(w)){
                if(!calcUnits(ecc,gene))
                    return null;
                gene.setOnlyGetPrimaryUser(false);
            }else /*根据用户类别过滤*/if (USER_FILTER_UNIT_TYPE.equalsIgnoreCase(w)) {
                if (!calcUnitTypeFilter(ecc, gene))
                    return null;
            } else /*根据用户标签过滤*/if (USER_FILTER_UNIT_LABEL.equalsIgnoreCase(w)) {
                if (!calcUnitTagFilter(ecc, gene))
                    return null;
            } else /*仅仅获取主要机构用户*/ if(USER_FILTER_PRIMARYUNIT.equalsIgnoreCase(w)){
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
