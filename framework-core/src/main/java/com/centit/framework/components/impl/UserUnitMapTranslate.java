package com.centit.framework.components.impl;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.network.HtmlFormUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserUnitMapTranslate implements UserUnitVariableTranslate {

    private Map<String,Object> varMap;
    //public
    public UserUnitMapTranslate(){
        varMap = null;
    }

    public UserUnitMapTranslate(Map<String,Object> varMap) {
        this.varMap = varMap;
    }

    /**
     * 返回权限表达式中的自定义变量对应的用户组
     *
     * @param varName 自定义变量
     * @return 权限表达式中的自定义变量对应的用户组
     */
    public Set<String> getUsersVariable(String varName){
        if(varMap==null)
            return null;

        Object res = ReflectionOpt.attainExpressionValue(varMap, varName);
        String [] us = HtmlFormUtils.getParameterStringArray(res);
        if(us==null)
            return null;
        Set<String> uSet = new HashSet<>();
        for (String s : us){
            uSet.add(s);
        }
        return uSet;
    }

    /**
     * 返回机构表达式中的自定义变量对应的机构组
     *
     * @param varName 自定义变量
     * @return 机构表达式中的自定义变量对应的机构组
     */
    public Set<String> getUnitsVariable(String varName){
        return getUsersVariable(varName);
    }

    /**
     * 变量名--变量值的转变
     * 变量 是用 ${变量名}
     * 如果这个变量不存在，返回空字符串 "''"
     *
     * @param varName varName
     * @return  变量值的转变
     */
    @Override
    public Object getVarValue(String varName) {
        if(varMap==null)
            return null;
        return ReflectionOpt.attainExpressionValue(varMap, varName);
        //return StringBaseOpt.objectToString(res);
    }

    public Map<String,Object> getVarMap() {
        return varMap;
    }

    public void setVarMap(Map<String,Object> varMap) {
        this.varMap = varMap;
    }
}
