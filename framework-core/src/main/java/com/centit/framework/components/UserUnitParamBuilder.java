package com.centit.framework.components;

import java.util.*;

/**
 * Created by codefan on 17-6-14.
 */
public abstract class UserUnitParamBuilder {
    public static Map<String, Set<String>> createEmptyParamMap(){
        return new HashMap<>();
    }

    public static void setParamToParamMap(Map<String, Set<String>> paramMap, String paramName, String paramValue){
        if (paramValue != null) {
            Set<String> uSet = new HashSet<>();
            uSet.add(paramValue);
            paramMap.put(paramName, uSet);
        }
    }

    public static void setParamsToParamMap(Map<String, Set<String>> paramMap, String paramName, String [] paramValues){
        if (paramValues != null && paramValues.length>0) {
            Set<String> uSet = new HashSet<>();
            for(String paramValue :paramValues)
                uSet.add(paramValue);
            paramMap.put(paramName, uSet);
        }
    }

    public static void setParamsToParamMap(Map<String, Set<String>> paramMap, String paramName, Collection<String> paramValues){
        if (paramValues != null && paramValues.size()>0) {
            paramMap.put(paramName, new HashSet<>(paramValues));
        }
    }

    public static void addParamToParamMap(Map<String, Set<String>> paramMap, String paramName, String paramValue){
        if (paramValue != null) {
            Set<String> uSet = paramMap.get(paramName);
            if(uSet==null) {
                uSet = new HashSet<>();
            }
            uSet.add(paramValue);
            paramMap.put(paramName, uSet);
        }
    }

    public static void addParamsToParamMap(Map<String, Set<String>> paramMap, String paramName, String [] paramValues){
        if (paramValues != null && paramValues.length>0) {
            Set<String> uSet = paramMap.get(paramName);
            if(uSet==null) {
                uSet = new HashSet<>();
            }
            for(String paramValue :paramValues)
                uSet.add(paramValue);
            paramMap.put(paramName, uSet);
        }
    }

    public static void addParamsToParamMap(Map<String, Set<String>> paramMap, String paramName, Collection<String> paramValues){
        if (paramValues != null && paramValues.size()>0) {
            Set<String> uSet = paramMap.get(paramName);
            if(uSet==null) {
                uSet = new HashSet<>();
            }
            uSet.addAll(paramValues);
            paramMap.put(paramName, uSet);
        }
    }
}
