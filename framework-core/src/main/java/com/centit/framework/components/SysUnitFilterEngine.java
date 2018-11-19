package com.centit.framework.components;

import com.centit.framework.components.impl.SystemUserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUnitInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 结构引擎，相关说明文档参见 http://192.168.128.8/wiki/pages/viewpage.action?pageId=12746758
 *
 * @author codefan
 */
public abstract class SysUnitFilterEngine {
    private SysUnitFilterEngine(){
        throw new IllegalAccessError("Utility class");
    }

    private static final Logger logger = LoggerFactory.getLogger(SysUnitFilterEngine.class);

    /**
     * D(null+1)    D1,D2
     * @param ecc UserUnitFilterCalcContext
     * @param nTiers int
     * @return Set nullSubUnits
     */
    public static Set<String> nullSubUnits(UserUnitFilterCalcContext ecc, int nTiers) {
        Set<String> units = new HashSet<>();
        if (nTiers < 1)
            return units;
        // 找到所有根机构
        for (IUnitInfo  unitEnt: ecc.listAllUnitInfo() ) {
            String puc = unitEnt.getParentUnit();
            if ((puc == null || "0".equals(puc) || "".equals(puc)) && "T".equals(unitEnt.getIsValid()))
                units.add(unitEnt.getUnitCode());
        }

        return subUnits(ecc, units, nTiers - 1);
    }

    /**
     * D(null-1)   D1111，D1112,D112,D12,D2
     * @param ecc UserUnitFilterCalcContext
     * @param nTiers int
     * @return Set nullSubUnits
     */
    public static Set<String> nullParentUnits(UserUnitFilterCalcContext ecc, int nTiers) {
        Set<String> units = new HashSet<>();
        if (nTiers < 1)
            return units;
        // 找到所有的叶子机构
        for (IUnitInfo  unitEnt: ecc.listAllUnitInfo() ) {
            List<? extends IUnitInfo> subUS = ecc.listSubUnit(unitEnt.getUnitCode());
            if ((subUS == null || subUS.size() == 0) && "T".equals(unitEnt.getIsValid()))
                units.add(unitEnt.getUnitCode());
        }

        return parentUnits(ecc,units, nTiers - 1);
    }

    /**
     * 查找有共同最上层机构的所有子机构
     ** @param ecc UserUnitFilterCalcContext
     * @param units int
     * @return Set allSeriesUnits
     */
    public static Set<String> allSeriesUnits(UserUnitFilterCalcContext ecc, Set<String> units) {
        if (units == null || units.size() == 0)
            return units;

        Set<String> retUnits = new HashSet<>();
        for (String unitCode : units) {
            String tu = topUnit(ecc,unitCode);
            if (tu != null)
                retUnits.add(tu);
        }
        //retUnits.addAll(allSubUnits(ecc,retUnits));
        return allSubUnits(ecc,retUnits);
    }

    /**
     * 查找所有的下层机构
     *@param ecc UserUnitFilterCalcContext
     *@param units Set units
     *@return 所有的下层机构
     */
    public static Set<String> allSubUnits(UserUnitFilterCalcContext ecc, Set<String> units) {
        if (units == null || units.size() == 0)
            return units;
        int preSize = 0;
        Set<String> retUnits = new HashSet<>();
        Set<String> midUnits = units;
        while (midUnits != null && midUnits.size() != 0) {
            retUnits.addAll(midUnits);
            // 排除机构层级设置中的循环问题
            if (preSize == retUnits.size())
                break;
            preSize = retUnits.size();
            midUnits = subUnits(ecc, midUnits, 1);
        }

        return retUnits;
    }

    /**
     * D(U--)
     *
     *@param ecc UserUnitFilterCalcContext
     *@param units Set units
     *@return allParentUnits 所有上层机构
     */
    public static Set<String> allParentUnits(UserUnitFilterCalcContext ecc, Set<String> units) {
        if (units == null || units.size() == 0)
            return units;
        Set<String> midUnits = units;
        Set<String> parUnits = new HashSet<>();
        while(true) {
            Set<String> retUnits = new HashSet<>();
            for (String suc : midUnits) {
                IUnitInfo u = ecc.getUnitInfoByCode(suc);
                String puc = u.getParentUnit();
                if ((puc != null) && (!"0".equals(puc)) && (!"".equals(puc)))
                    retUnits.add(puc);
            }
            if(retUnits.size()<1)
                break;
            parUnits.addAll(midUnits);
            midUnits = retUnits;
        }
        parUnits.addAll(midUnits);
        return parUnits;
    }


    /**
     * D(U+5)
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param nTiers int
     * @return subUnits 下层机构
     */
    public static Set<String> subUnits(UserUnitFilterCalcContext ecc, Set<String> units, int nTiers) {
        if (nTiers < 1 || units == null || units.size() == 0)
            return units;

        Set<String> midUnits = units;
        for (int i = 0; i < nTiers; i++) {
            Set<String> retUnits = new HashSet<>();
            for (String suc : midUnits) {
                //IUnitInfo u = ecc.getUnitInfoByCode(suc);
                for(IUnitInfo ui: ecc.listSubUnit(suc))
                    retUnits.add(ui.getUnitCode());
            }
            midUnits = retUnits;
        }
        return midUnits;
    }

    private static boolean matchUnitTag(String unitTag, String unitTags){
        if(StringUtils.isBlank(unitTags)) {
            return false;
        }
        String [] tags = unitTags.split(",");
        for(String tag : tags){
            if(StringUtils.equals(tag,unitTag)){
                return true;
            }
        }
        return false;
    }
    /**
     * D(U+"tag or type")
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param typeOrTag 类型或者标记
     * @return subUnits 下层机构
     */
    public static Set<String> subUnits(UserUnitFilterCalcContext ecc, Set<String> units, String typeOrTag) {
        if (typeOrTag == null || units == null || units.size() == 0)
            return units;
        Set<String> resUnits =  new HashSet<>();
        for (String suc : units) {
            List<IUnitInfo> midUnits =  new ArrayList<>();
            midUnits.add(ecc.getUnitInfoByCode(suc));
            while(midUnits.size()>0) {
                boolean hasFound = false;
                List<IUnitInfo> tempUnits = new ArrayList<>();
                for (IUnitInfo ui : midUnits) {
                    if(typeOrTag.equals(ui.getUnitType()) || matchUnitTag(typeOrTag, ui.getUnitTag())){
                        resUnits.add(ui.getUnitCode());
                        hasFound = true;
                    }
                    tempUnits.addAll( ecc.listSubUnit(ui.getUnitCode()));
                }
                if(hasFound){
                    break;
                }
                midUnits = tempUnits;
            }
        }
        return resUnits;
    }

    /**
     * D(U-5)
     *
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param nTiers int
     * @return parentUnits Set 上层机构
     */
    public static Set<String> parentUnits(UserUnitFilterCalcContext ecc, Set<String> units, int nTiers) {
        if (nTiers < 1 || units == null || units.size() == 0)
            return units;
        Set<String> midUnits = units;
        for (int i = 0; i < nTiers; i++) {
            Set<String> retUnits = new HashSet<>();
            for (String suc : midUnits) {
                IUnitInfo u = ecc.getUnitInfoByCode(suc);
                String puc = u.getParentUnit();
                if ((puc != null) && (!"0".equals(puc)) && (!"".equals(puc)))
                    retUnits.add(puc);
            }
            midUnits = retUnits;
        }
        return midUnits;
    }

    /**
     * D(U-"tag or type")
     *
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param typeOrTag 类型或者标记
     * @return parentUnits Set 上层机构
     */
    public static Set<String> parentUnits(UserUnitFilterCalcContext ecc, Set<String> units, String typeOrTag) {
        if (typeOrTag == null || units == null || units.size() == 0)
            return units;
        Set<String> midUnits =  new HashSet<>();
        for (String suc : units) {
            IUnitInfo u = ecc.getUnitInfoByCode(suc);
            while(u != null) {
                if(typeOrTag.equals(u.getUnitType()) || matchUnitTag(typeOrTag, u.getUnitTag())){
                    midUnits.add(u.getUnitCode());
                    break;
                }
                String puc = u.getParentUnit();
                if ((puc == null) || ("0".equals(puc)) || ("".equals(puc))) {
                    break;
                }
                u = ecc.getUnitInfoByCode(puc);
            }
        }
        return midUnits;
    }


    /**
     * D(U*1)
     * @param ecc UserUnitFilterCalcContext
     * @param unitCode unitCode
     * @return topUnit 最上层机构
     */
    public static String topUnit(UserUnitFilterCalcContext ecc, String unitCode) {
        String topUnit = null;
        String retUnitCode = unitCode;
        while (true) {
            IUnitInfo u = ecc.getUnitInfoByCode(retUnitCode);
            if (u == null)
                return topUnit;
            topUnit = retUnitCode; // 记录下有小的机构代码
            String puc = u.getParentUnit();
            if (puc == null || "0".equals(puc) || "".equals(puc))
                return retUnitCode;
            retUnitCode = puc;
        }
    }

    /**
     * D(U*5)
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param nTiers int
     * @return Set 最上层机构
     */
    public static Set<String> topUnits(UserUnitFilterCalcContext ecc, Set<String> units, int nTiers) {
        if (nTiers < 1)
            return units;
        Set<String> retUnits = new HashSet<>();
        for (String unitCode : units) {
            String tu = topUnit(ecc,unitCode);
            if (tu != null)
                retUnits.add(tu);
        }
        return subUnits(ecc,retUnits, nTiers);
    }

    /**
     * 所有同一系列最上面几层节点
     * @param ecc UserUnitFilterCalcContext
     * @param units Set units
     * @param nTiers int
     * @return 同一系列最上面几层节点
     */
    public static Set<String> seriesUnits(UserUnitFilterCalcContext ecc, Set<String> units, int nTiers) {

        if ( units == null || units.size() == 0)
            return units;

        Set<String> retUnits = new HashSet<>();
        for (String unitCode : units) {
            String tu = topUnit(ecc,unitCode);
            if (tu != null)
                retUnits.add(tu);
        }
        if(nTiers < 1)
            return retUnits;

        Set<String> midUnits = retUnits;
        Set<String> serUnits = new HashSet<>();
        for (int i = 0; i < nTiers; i++) {
            retUnits = new HashSet<>();
            for (String suc : midUnits) {
                //IUnitInfo u = ecc.getUnitInfoByCode(suc);
                for(IUnitInfo ui: ecc.listSubUnit(suc))
                    retUnits.add(ui.getUnitCode());
            }
            serUnits.addAll(midUnits);
            midUnits = retUnits;
        }
        serUnits.addAll(midUnits);
        return serUnits;
    }


    /**
     * S(unitExp[,unitExp]* )
     *
     * @param ecc UserUnitFilterCalcContext
     * @return Set calcSingleExp
     */
    private static Set<String> calcSingleExp(UserUnitFilterCalcContext ecc) {
        String w = ecc.getAWord();
        if (!"(".equals(w)) { // 语法错误
            ecc.setLastErrMsg(w + " is unexpected, expect '(' ; calcSingleUnit begin .");
            return null;
        }
        Set<String> units = calcUnitsExp(ecc);
        // if(units==null)//语法错误
        // return null;

        while (units == null || units.size() == 0) {
            w = ecc.getAWord();
            if (")".equals(w))
                return units;
            if (",".equals(w))
                units = calcUnitsExp(ecc);
            else {
                ecc.setLastErrMsg(w + " is unexpected, expect ',' or ')' ; calcSingleUnit end .");
                return null;
            }
        }
        ecc.seekToRightBracket();

        return units;
    }

    /**
     * (unitExp) | S(singleExp) | SimpleExp
     *
     * @param ecc UserUnitFilterCalcContext
     * @return Set calcItemExp
     */
    private static Set<String> calcItemExp(UserUnitFilterCalcContext ecc) {
        String w = ecc.getAWord();
        if (w == null || "".equals(w)) { // 语法错误
            ecc.setLastErrMsg("End of file is unexpected; calcItemUnit begin ");
            return null;
        }
        if ("(".equals(w)) {
            Set<String> units = calcUnitsExp(ecc);
            w = ecc.getAWord();
            if (")".equals(w))
                return units;
            else { // 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect ')'; calcItemUnit (unitExp) end ");
                return null;
            }
            // }else if("D".equals(w)){
            // return makeUserUnitFilter(ecc);
        } else if ("S".equalsIgnoreCase(w)) {
            return calcSingleExp(ecc);
        } else {
            ecc.setPreword(w);
            return calcSimpleExp(ecc);
        }
    }

    /**
     * itemExp ([或 itemExp][与 itemExp][非 itemExp])*
     *
     * @param ecc UserUnitFilterCalcContext
     * @return Set calcUnitsExp
     */
    public static Set<String> calcUnitsExp(UserUnitFilterCalcContext ecc) {
        Set<String> units = calcItemExp(ecc);
        if (units == null)
            return null;
        while (true) {
            String w = ecc.getAWord();
            if (w == null || "".equals(w))
                return units;

            if (",".equals(w) || ")".equals(w)) {
                ecc.setPreword(w);
                return units;
            }

            if ("|".equals(w) || "||".equals(w)) { // 并
                Set<String> units2 = calcItemExp(ecc);
                if (units2 == null)
                    return null;
                units.addAll(units2);
            } else if ("&".equals(w) || "&&".equals(w)) { // 交
                Set<String> units2 = calcItemExp(ecc);
                if (units2 == null)
                    return null;
                units.retainAll(units2);
            } else if ("!".equals(w)) {// 差
                Set<String> units2 = calcItemExp(ecc);
                if (units2 == null)
                    return null;
                units.removeAll(units2);
            } else {// 语法错误
                ecc.setLastErrMsg(w + " is unexpected, expect '||','&&','!',',' or ')'; calcUnitsExp end  ");
                return null;
            }
        }
    }


    public static Set<String> getUnitsByFilter(UserUnitFilterCalcContext ecc, UserUnitFilterGene rf) {

        boolean hasUnitFilter =  rf.isHasUnitTagFilter() || rf.isHasUnitTypeFilter();
        if( hasUnitFilter && rf.getUnits() != null && rf.getUnits().size()>0) {
            List<IUnitInfo> lsUnitInfo = new ArrayList<>(rf.getUnits().size()+1);
            for (String unitCode  : rf.getUnits()) {
                lsUnitInfo.add(ecc.getUnitInfoByCode(unitCode));
            }
            if(lsUnitInfo!=null) {
                if (rf.isHasUnitTypeFilter()) {
                    // 过滤掉不符合要求的岗位
                    lsUnitInfo.removeIf(unit -> !rf.getUnitTypes().contains(unit.getUnitType()));
                }

                if (rf.isHasUnitTagFilter()) {
                    for (Iterator<IUnitInfo> it = lsUnitInfo.iterator(); it.hasNext(); ) {
                        IUnitInfo unit = it.next();
                        boolean hasTag = false;
                        if (StringUtils.isNotBlank(unit.getUnitTag())) {
                            String tags[] = unit.getUnitTag().split(",");
                            for (String tag : tags) {
                                if (rf.getUnitTags().contains(tag)) {
                                    hasTag = true;
                                    break;
                                }
                            }
                        }
                        if (!hasTag)
                            it.remove();
                    }
                }

                rf.getUnits().clear();
                for (IUnitInfo unit : lsUnitInfo) {
                    rf.addUnit(unit.getUnitCode());
                }
            }
        }
        return rf.getUnits();
    }
    /**
     * D()DT()DL()
     * @return 机构表达式计算结果
     */
    private static Set<String> calcSimpleExp(UserUnitFilterCalcContext ecc) {
        UserUnitFilterGene gene = InnerUserUnitFilterCompileEngine.makeUserUnitFilter(ecc);
        if(gene==null){
            return null;
        }
        return getUnitsByFilter(ecc,gene);
    }

    /**
     *
     * @param unitExp unitExp
     * @param unitParams Map unitParams
     * @param varTrans UserUnitVariableTranslate
     * @return calcSystemUnitsByExp
     */
    public static Set<String> calcSystemUnitsByExp(String unitExp,
                                             Map<String, Set<String>> unitParams,
                                             UserUnitVariableTranslate varTrans) {
        if (unitExp == null)
            return null;
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
        ecc.setFormula(unitExp);
        ecc.setVarTrans(varTrans);
        ecc.addAllUnitParam(unitParams);
        Set<String> untis = calcUnitsExp(ecc);

        if (ecc.hasError()) {
            logger.error(ecc.getLastErrMsg());
        }

        return untis;
    }

    /**
     *
     * @param ecc UserUnitFilterCalcContext
     * @return calcSingleUnitByExp
     */
    public static String calcSingleUnitByExp(UserUnitFilterCalcContext ecc) {
        Set<String> untis = calcUnitsExp(ecc);
        if (untis == null || untis.size() == 0)
            return null;
        if (ecc.hasError())
            logger.error(ecc.getLastErrMsg());
        return untis.iterator().next();
    }

    public static String calcSingleSystemUnitByExp(String unitExp,Map<String, Set<String>> unitParams , UserUnitVariableTranslate varTrans) {
        if (unitExp == null)
            return null;
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
        ecc.setFormula(unitExp);
        ecc.setVarTrans(varTrans);
        ecc.addAllUnitParam(unitParams);
        return calcSingleUnitByExp(ecc);
    }

    public static String validateUnitsExp(String unitExp) {
        UserUnitFilterCalcContext ecc = new SystemUserUnitFilterCalcContext();
        ecc.setFormula(unitExp);
        calcUnitsExp(ecc);
        if (ecc.hasError())
            return ecc.getLastErrMsg();
        return "T";
    }

}
