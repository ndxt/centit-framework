package com.centit.framework.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.algorithm.ListOpt;
import com.centit.support.algorithm.ReflectionOpt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by codefan on 17-10-27.
 * @author codefan
 */
public abstract class ViewDataTransform {

    private ViewDataTransform() {
        throw new IllegalAccessError("Utility class");
    }

    public static Map<String,String> createStringHashMap(String... objs){
        if(objs==null || objs.length<2)
            return null;
        Map<String,String> paramsMap = new HashMap<>(objs.length);
        for(int i=0;i<objs.length / 2;i++){
            paramsMap.put(String.valueOf(objs[i*2]), objs[i*2+1]);
        }
        return paramsMap;
    }


    public interface TreeViewExteralAttributes<T>{
        void setAttributes(JSONObject jsonObject , T obj);
    }

    public static <T> JSONArray makeTreeViewJson(Collection<T> treaData,
                                             Map<String, String> propertyMap , TreeViewExteralAttributes<T> attributesSetter){
        if(treaData == null)
            return null;
        JSONArray jsonArray = new JSONArray(treaData.size());
        for(T obj :  treaData){
            JSONObject jsonObject = new JSONObject();
            for(Map.Entry<String, String> ent : propertyMap.entrySet()){
                if("children".equals(ent.getKey())){
                    Object children = ReflectionOpt.attainExpressionValue( obj , ent.getValue());
                    if(children==null)
                        continue;

                    Collection<T> childrenData = null;
                    if(children instanceof Collection){
                        childrenData = (Collection<T>) children;
                    }else if(children  instanceof Object[]) {
                        childrenData = ListOpt.arrayToList(( T[]) children);
                    }

                    if(childrenData!=null) {
                        jsonObject.put(ent.getKey(),
                                makeTreeViewJson(childrenData, propertyMap, attributesSetter));
                    }
                }else {
                    //JSONOpt.setAttribute(jsonObject, ent.getKey(),
                    jsonObject.put(ent.getKey(),
                            ReflectionOpt.attainExpressionValue(obj, ent.getValue()));
                }
            }
            JSONObject map = new JSONObject(2);
            attributesSetter.setAttributes(map, obj);
            //map.put("external", !("D".equals(optInfo.getPageType())));
            jsonObject.put("attributes", map);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
