package com.centit.framework.components.impl;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.network.HtmlFormUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectUserUnitVariableTranslate implements UserUnitVariableTranslate {
    protected Object object;
    public static final Logger logger = LoggerFactory.getLogger(ObjectUserUnitVariableTranslate.class);
    private Map<String,Set<String>> requestParams;

    public ObjectUserUnitVariableTranslate(){
        object = null;
        requestParams = null;
    }
    public ObjectUserUnitVariableTranslate(Object o){
        object = o;
        requestParams = null;
    }

    public ObjectUserUnitVariableTranslate(HttpServletRequest request){
        object = null;
        setServletRequest(request);
    }

    public ObjectUserUnitVariableTranslate(Object o, HttpServletRequest request){
        object = o;
        setServletRequest(request);
    }
    public void setModuleObject(Object obj){
        this.object = obj;
    }

    public void setServletRequest(HttpServletRequest request){
        if(request==null) {
            requestParams = null;
            return;
        }
        Map<String,String[]> paramMap = request.getParameterMap();

        requestParams = new HashMap<>();
        for (Map.Entry<String,String[]> ent : paramMap.entrySet() ) {
            requestParams.put(
                    ent.getKey(), CollectionsOpt.createHashSet(ent.getValue()));
        }
    }


    /**
     * 默认返回业务模型对象的属性值 , request 队形的参数
     */
    @Override
    public Object getVarValue(String lableName) {

        if(requestParams!=null){
            Set<String> params = requestParams.get(lableName);
            if(params!=null){
                if(params.size()==1) {
                    return params.iterator().next();
                }else{
                    return params;
                }
            }
        }

        if(object!=null){
            return ReflectionOpt.attainExpressionValue(object, lableName);
        }
        return null;
    }

    /**
     * 返回用户表达式中的自定义变量对应的用户组
     */
    @Override
    public Set<String> getUsersVariable(String varName) {
        if(requestParams!=null){
            Set<String> params = requestParams.get(varName);
            if(params!=null && params.size()>0) {
                return params;
            }
        }

        if(object!=null){
            Object res = ReflectionOpt.attainExpressionValue(object, varName);
            String [] us = HtmlFormUtils.getParameterStringArray(res);
            if(us==null)
                return null;
            Set<String> uSet = new HashSet<>();
            for (String s : us){
                uSet.add(s);
            }
            return uSet;
        }
        return null;
    }
    /**
     * 返回机构表达式中的自定义变量对应的用户组
     */
    @Override
    public Set<String> getUnitsVariable(String varName) {
        return getUsersVariable(varName);
    }

}
