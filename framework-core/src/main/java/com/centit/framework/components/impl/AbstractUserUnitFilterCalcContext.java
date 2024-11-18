package com.centit.framework.components.impl;

import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.UserUnit;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.compiler.Lexer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractUserUnitFilterCalcContext implements UserUnitFilterCalcContext {

    protected Lexer lexer;
    protected Map<String, Set<String>> unitParams;
    protected Map<String, Set<String>> userParams;
    protected Map<String, String> rankParams;
    protected boolean hasError;
    protected String lastErrMsg;
    protected UserUnitVariableTranslate varTrans;
    protected String topUnit;

    public AbstractUserUnitFilterCalcContext(String topUnit) {
        this.lexer = new Lexer();
        this.unitParams = new HashMap<>();
        this.userParams = new HashMap<>();
        this.rankParams = new HashMap<>();
        this.hasError = false;
        this.varTrans = null;
        this.topUnit = topUnit;
    }

    @Override
    public void setVarTrans(UserUnitVariableTranslate varTrans) {
        this.varTrans = varTrans;
    }

    @Override
    public UserUnitVariableTranslate getVarTrans() {
        return this.varTrans;
    }

    @Override
    public Object getVarValue(String labelName){
        return this.varTrans.getVarValue(labelName);
    }

    @Override
    public boolean hasError() {
        return hasError;
    }

    @Override
    public void clearError() {
        this.hasError = false;
    }

    @Override
    public String getLastErrMsg() {
        return lastErrMsg;
    }

    @Override
    public String getTopUnit() {
        if(StringUtils.isBlank(topUnit)){
            return GlobalConstValue.NO_TENANT_TOP_UNIT;
        }
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }

    @Override
    public void setLastErrMsg(String lastErrMsg) {
        this.hasError = true;
        this.lastErrMsg = "Pos " + lexer.getCurrPos() + " : " + lastErrMsg;
    }

    @Override
    public void clearParams() {
        unitParams.clear();
        userParams.clear();
        rankParams.clear();
    }

    @Override
    public void addUnitParam(String paramName, String unitCode) {
        if (unitCode != null) {
            Set<String> uSet = new HashSet<String>();
            uSet.add(unitCode);
            unitParams.put(paramName, uSet);
        }
    }

    @Override
    public void addAllUnitParam(Map<String, Set<String>> unitParam) {
        this.unitParams.putAll(unitParam);
    }

    @Override
    public void addUnitParam(String paramName, Set<String> unitCodes) {
        if (unitCodes != null && unitCodes.size() > 0)
            unitParams.put(paramName, unitCodes);
    }

    @Override
    public void addUserParam(String paramName, String userCode) {
        if (userCode != null) {
            Set<String> uSet = new HashSet<String>();
            uSet.add(userCode);
            userParams.put(paramName, uSet);
        }
    }

    @Override
    public void addAllUserParam(Map<String, Set<String>> userParam) {
        this.userParams.putAll(userParam);
    }

    @Override
    public void addUserParam(String paramName, Set<String> userCodes) {
        if (userCodes != null && userCodes.size() > 0)
            userParams.put(paramName, userCodes);
    }

    @Override
    public void addRankParam(String paramName, String r) {
        rankParams.put(paramName, r);
    }

    @Override
    public void addAllRankParam(Map<String, String> rankParam) {
        this.rankParams.putAll(rankParam);
    }

    @Override
    public Set<String> getUnitCode(String paramName) {
        Set<String> uSet = unitParams.get(paramName);
        if (uSet != null) {
            return uSet;
        }
        if (varTrans != null)
            uSet = varTrans.getUnitsVariable(paramName);

        if (uSet != null) {
            return uSet;
        } else
            return null;
    }

    @Override
    public Set<String> getUserCode(String paramName) {
        Set<String> uSet = userParams.get(paramName);
        if (uSet != null) {
            return uSet;
        }
        if (varTrans != null)
            return varTrans.getUsersVariable(paramName);
        else return null;
    }


    @Override
    public String getRank(String paramName) {
        String rank = rankParams.get(paramName);
        if (rank != null)
            return rank;
        rank = StringBaseOpt.objectToString(
            varTrans.getVarValue(paramName));
        if (rank != null)
            return rank;
        return paramName;
    }

    @Override
    public String getUserRank(String userCode) {
        List<UserUnit> uus = listUserUnits(userCode);
        String nRank = UserUnit.MAX_XZ_RANK;
        if (uus != null) {
            for (UserUnit uu : uus) {
                String nr = uu.getPostRank();
                if (StringUtils.compare(nr , nRank) < 0){
                    nRank = nr;
                }
            }
        }
        return nRank;
    }

    @Override
    public String getUserUnitRank(String userCode, String unitCode) {
        List<UserUnit> uus = listUserUnits(userCode);
        String nRank = UserUnit.MAX_XZ_RANK;
        if (uus != null) {
            for (UserUnit uu : uus) {
                if (StringUtils.equals(uu.getUnitCode(), unitCode)) {
                    String nr = uu.getPostRank();
                    if (StringUtils.compare(nr , nRank) < 0){
                        nRank = nr;
                    }
                }
            }
        }
        return nRank;
    }

    @Override
    public void setFormula(String sFormula) {
        lexer.setFormula(sFormula);
    }

    @Override
    public void writeBackAWord(String preWord) {
        lexer.writeBackAWord(preWord);
    }

    @Override
    public void setCanAcceptOpt(boolean canAcceptOpt) {
        lexer.setCanAcceptOpt(canAcceptOpt);
    }

    @Override
    public boolean isLabel(String sWord) {
        return lexer.isLabel(sWord);
    }

    @Override
    public String getAWord() {
        return lexer.getAWord();
    }

    @Override
    public boolean seekToRightBracket() {
        return lexer.seekToRightBracket();
    }

}
