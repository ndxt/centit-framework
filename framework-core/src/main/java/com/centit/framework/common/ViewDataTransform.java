package com.centit.framework.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.algorithm.ListOpt;
import com.centit.support.algorithm.ReflectionOpt;
import org.apache.commons.lang3.StringUtils;

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
        void appendExtendedAttributes(JSONObject jsonObject , T obj);
    }

    /**
     * 将对象数组 转换为前端树形结构需要的字段，字段和元对象属性的对应关系 有 propertyMap 来设置
     * @param treaData 源数据对象数组
     * @param propertyMap 属性对应关系
     * @param attributesSetter 额外的属性设置接口
     * @param <T> 数据类型
     * @return 返回 jsonArray 给前端展示
     */
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
            attributesSetter.appendExtendedAttributes(map, obj);
            //map.put("external", !("D".equals(optInfo.getPageType())));
            jsonObject.put("attributes", map);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 在已经生成好的jsonArray中添加 tree需要展示的额外的字段
     * @param treaJson 已经转换好的jsonAarry
     * @param propertyMap 需要添加的字段
     * @param attributesSetter 额外的 属性设置接口
     * @return 返回添加相关字段的 jsonArray 供前端展示
     */
    public static JSONArray appendTreeViewJson(JSONArray treaJson,
                                                 Map<String, String> propertyMap ,
                                             TreeViewExteralAttributes<JSONObject> attributesSetter){
        if(treaJson == null)
            return null;

        for( Object obj :  treaJson) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)obj;
                for (Map.Entry<String, String> ent : propertyMap.entrySet()) {
                    if (!"children".equals(ent.getKey())) {
                        //JSONOpt.setAttribute(jsonObject, ent.getKey(),
                        jsonObject.put(ent.getKey(), ReflectionOpt.attainExpressionValue(obj, ent.getValue()));
                    }
                }
                String childrenKey = propertyMap.get("children");
                Object childrenData = jsonObject.get("children");
                if(childrenData == null && StringUtils.isNoneBlank(childrenKey)) {
                    childrenData = ReflectionOpt.attainExpressionValue( obj ,childrenKey);
                    jsonObject.put("children",childrenData);
                }

                if(childrenData != null && childrenData instanceof JSONArray){
                    appendTreeViewJson((JSONArray)childrenData, propertyMap, attributesSetter);
                }

                JSONObject map = new JSONObject(2);
                attributesSetter.appendExtendedAttributes(map, jsonObject);
                //map.put("external", !("D".equals(optInfo.getPageType())));
                jsonObject.put("attributes", map);
                //jsonArray.add(jsonObject);
            }
        }
        return treaJson;
    }
}
