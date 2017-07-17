package com.centit.framework.system.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.compiler.Lexer;

public class ExpCalcContext extends Lexer {
    private Map<String, Set<String>> unitParams;
    private Map<String, Set<String>> userParams;
    private Map<String, Integer> rankParams;
    private boolean hasError;
    private String lastErrMsg;
    private UserUnitVariableTranslate varTrans;

    public ExpCalcContext() {
        unitParams = new HashMap<String, Set<String>>();
        userParams = new HashMap<String, Set<String>>();
        rankParams = new HashMap<String, Integer>();
        hasError = false;
        varTrans = null;
    }

    public void setVarTrans(UserUnitVariableTranslate varTrans) {
        this.varTrans = varTrans;
    }

    public boolean hasError() {
        return hasError;
    }

    public void clearError() {
        this.hasError = false;
    }

    public String getLastErrMsg() {
        return lastErrMsg;
    }

    public void setLastErrMsg(String lastErrMsg) {
        this.hasError = true;
        this.lastErrMsg = "Pos " + this.getCurrPos() + " : " + lastErrMsg;
    }

    public void clearParams() {
        unitParams.clear();
        userParams.clear();
        rankParams.clear();
    }

    public void addUnitParam(String paramName, String unitCode) {
        if (unitCode != null) {
            Set<String> uSet = new HashSet<String>();
            uSet.add(unitCode);
            unitParams.put(paramName, uSet);
        }
    }

    public void addUnitParam(String paramName, Set<String> unitCodes) {
        if (unitCodes != null && unitCodes.size() > 0)
            unitParams.put(paramName, unitCodes);
    }

    public void addUserParam(String paramName, String userCode) {
        if (userCode != null) {
            Set<String> uSet = new HashSet<String>();
            uSet.add(userCode);
            userParams.put(paramName, uSet);
        }
    }

    public void addUserParam(String paramName, Set<String> userCodes) {
        if (userCodes != null && userCodes.size() > 0)
            userParams.put(paramName, userCodes);
    }

    public void addRankParam(String paramName, int r) {
        rankParams.put(paramName, r);
    }

    public Set<String> getUnitCode(String paramName) {
        Set<String> uSet = unitParams.get(paramName);
        if (uSet != null) {
            return uSet;
        }
        if (varTrans != null)
            uSet = varTrans.getUnitsVariable(paramName);
        
        if (uSet != null) {
            return uSet;
        }
        else 
            return null;
    }

    public Set<String> getUserCode(String paramName) {
        Set<String> uSet = userParams.get(paramName);
        if (uSet != null) {
            return uSet;
        }
        if (varTrans != null)
            return varTrans.getUsersVariable(paramName);
        else return null;
    }

    public int getRank(String paramName) {
        return rankParams.get(paramName);
    }

}
