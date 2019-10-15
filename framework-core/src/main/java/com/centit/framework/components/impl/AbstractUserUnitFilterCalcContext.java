package com.centit.framework.components.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.compiler.Lexer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractUserUnitFilterCalcContext implements UserUnitFilterCalcContext {
    protected Lexer lexer;
    protected Map<String, Set<String>> unitParams;
    protected Map<String, Set<String>> userParams;
    protected Map<String, Integer> rankParams;
    protected boolean hasError;
    protected String lastErrMsg;
    protected UserUnitVariableTranslate varTrans;

    public AbstractUserUnitFilterCalcContext() {
        lexer = new Lexer();
        unitParams = new HashMap<>();
        userParams = new HashMap<>();
        rankParams = new HashMap<>();
        hasError = false;
        varTrans = null;
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
    public void addRankParam(String paramName, int r) {
        rankParams.put(paramName, r);
    }

    @Override
    public void addAllRankParam(Map<String, Integer> rankParam) {
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
    public int stringToRank(String srank) {
        if (StringUtils.isBlank(srank))
            return -1;
        if (!StringRegularOpt.isNumber(srank))
            return -1;
        return Integer.valueOf(StringRegularOpt.trimString(srank));
    }

    @Override
    public int getRank(String paramName) {
        Integer rank = rankParams.get(paramName);
        if (rank != null)
            return rank;
        String srank = StringBaseOpt.objectToString(
            varTrans.getVarValue(paramName));
        return stringToRank(srank);
    }

    @Override
    public int getUserRank(String userCode) {
        List<? extends IUserUnit> uus = listUserUnits(userCode);
        int nRank = CodeRepositoryUtil.MAXXZRANK;
        if (uus != null) {
            for (IUserUnit uu : uus) {
                int nr = getXzRank(uu.getUserRank());
                if (nr < nRank) {
                    nRank = nr;
                }
            }
        }
        return nRank;
    }

    @Override
    public int getUserUnitRank(String userCode, String unitCode) {
        List<? extends IUserUnit> uus = listUserUnits(userCode);
        int nRank = CodeRepositoryUtil.MAXXZRANK;
        if (uus != null) {
            for (IUserUnit uu : uus) {
                if (StringUtils.equals(uu.getUnitCode(), unitCode)) {
                    int nr = getXzRank(uu.getUserRank());
                    if (nr < nRank) {
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
    public void setPreword(String preWord) {
        lexer.setPreword(preWord);
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
