package com.centit.framework.core.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.LeftRightPair;
import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.EmbeddedId;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作为DatabaseOptUtils的补充，添加的主要内容是和数据字典管理，包括对 DictionaryMap注解的处理
 * @author codefan
 *
 */
@SuppressWarnings({"unused","unchecked"})
public abstract class DictionaryMapUtils {

    private static final ConcurrentHashMap<String , List<DictionaryMapColumn>> DICTIONARY_MAP_METADATA_CLASSPATH =
        new ConcurrentHashMap<>(100);

    private DictionaryMapUtils(){

    }

    private static DictionaryMapColumn makeDictionaryMapColumn(DictionaryMap dictionary, String fieldName ){
        Map<String,String> dm = CodeRepositoryUtil.getAllLabelValueMap(dictionary.value());
        return new DictionaryMapColumn(fieldName,dictionary);
    }

    public static void mergeDictionaryMapColumn(List<DictionaryMapColumn> des, Class<?> objType){
        if( objType !=null
            && ! objType.equals(Object.class)
            && ! objType.equals(Serializable.class)) {
            List<DictionaryMapColumn> mapCols = getDictionaryMapColumns(objType);
            if (mapCols != null) {
                for (DictionaryMapColumn mc : mapCols) {
                    if (!des.contains(mc)) {
                        des.add(mc);
                    }
                }
            }
        }
    }
    public static List<DictionaryMapColumn> innerFetchDictionaryMapColumns(Class<?> objType){
        List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<>(10);
        Field[] objFields = objType.getDeclaredFields();
        for(Field field :objFields){
            if (field.isAnnotationPresent(DictionaryMap.class)) {
                DictionaryMapColumn dictionaryMapColumn = makeDictionaryMapColumn(
                    field.getAnnotation(DictionaryMap.class),field.getName());
                fieldDictionaryMaps.add(dictionaryMapColumn);
            } else if (field.isAnnotationPresent(EmbeddedId.class)) {
                fieldDictionaryMaps.addAll(
                    innerFetchDictionaryMapColumns(field.getType()));
            }
        }
        List<Method> getter = ReflectionOpt.getAllGetterMethod(objType);
        for(Method method : getter){
            if (method.isAnnotationPresent(DictionaryMap.class)) {
                DictionaryMapColumn dictionaryMapColumn = makeDictionaryMapColumn(
                    method.getAnnotation(DictionaryMap.class),ReflectionOpt.mapGetter2Field(method));
                //FIXED 不能简单的 add 需要去重
                if(!fieldDictionaryMaps.contains(dictionaryMapColumn)) {
                    fieldDictionaryMaps.add(dictionaryMapColumn);
                }
            }
        }

        Class<?> superClass = objType.getSuperclass();
        mergeDictionaryMapColumn(fieldDictionaryMaps, superClass);
        Class<?> interfaces[] = objType.getInterfaces();
        if(interfaces !=null) {
            for (Class<?> intf : interfaces){
                mergeDictionaryMapColumn(fieldDictionaryMaps, intf);
            }
        }

        return fieldDictionaryMaps;
    }

    /**
     * 检查objType属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
     * @param objType po对象类型
     * @return DictionaryMapColumn 字段名包括数据字典相关信息
     */
    public static List<DictionaryMapColumn> getDictionaryMapColumns
            (Class<?> objType){
        String className = objType.getName();
        List<DictionaryMapColumn> fieldDictionaryMaps = DICTIONARY_MAP_METADATA_CLASSPATH.get(className);
        if(fieldDictionaryMaps == null){
            fieldDictionaryMaps = innerFetchDictionaryMapColumns(objType);
            DICTIONARY_MAP_METADATA_CLASSPATH.put(className, fieldDictionaryMaps);
        }//end of for
        return fieldDictionaryMaps;
    }

    /**
     * 将fields字段和类objType中的属性进行一一对应，只有对应上的才会放到hql语句中。
     * 并且检查属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
     * @param fields 字段，是po中的熟悉名，不是数据库表中的字段名
     * @param objType po对象类型
     * @return DictionaryMapColumn 字段名包括数据字典相关信息
     */
    public static List<DictionaryMapColumn> getDictionaryMapColumns
            (String[] fields,Class<?> objType){

        Field[] objFields = objType.getDeclaredFields();
        List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(objType);
        if(fields==null || fields.length==0 || fieldDictionaryMaps.size()<1){
            return fieldDictionaryMaps;
        }

        List<DictionaryMapColumn> tempDictionaryMaps = new ArrayList<>(10);

        for(DictionaryMapColumn field : fieldDictionaryMaps){
            if( ArrayUtils.contains(fields,field.getFieldName())){
                tempDictionaryMaps.add(field);
            }
        }//end of for
        return tempDictionaryMaps;
    }

    private final static String mapDictinaryValue(DictionaryMapColumn col, Object obj){
        return col.isExpression()?
            CodeRepositoryUtil.transExpression( col.getDictCatalog(),
                StringBaseOpt.objectToString(obj)) :
            CodeRepositoryUtil.getValue( col.getDictCatalog(),
                StringBaseOpt.objectToString(obj));
    }
    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param obj Object
     * @param fieldDictionaryMaps 数据字典映射表
     * @param fields 过滤相关字段
     * @return Po对象转换为JSONObject
     */
    public static Object objectToJSON(Object obj, String[] fields, List<DictionaryMapColumn> fieldDictionaryMaps ){
        if(obj==null)
            return null;
        if(fields==null || fields.length==0){
            return objectToJSON(obj,fieldDictionaryMaps);
        }
        Object json = JSON.toJSON(obj);
        if(json instanceof JSONObject){
            JSONObject newJsonObj = new JSONObject();
            JSONObject jsonObj = (JSONObject)json;
            for(String fieldName : fields ){
                newJsonObj.put(fieldName, jsonObj.get(fieldName));
            }
            if(fieldDictionaryMaps==null || fieldDictionaryMaps.size()==0)
                return newJsonObj;

            for(DictionaryMapColumn col:fieldDictionaryMaps){
                newJsonObj.put(col.getMapFieldName(),
                    mapDictinaryValue(col, jsonObj.get(col.getFieldName())));
            }
            return newJsonObj;
        }
        return json;
    }

    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param obj Object
     * @param fields 过滤相关字段
     * @return Po对象转换为JSONObject
     */
    public static Object objectToJSON(Object obj, String[] fields){
        if(obj==null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(obj.getClass());
        return objectToJSON(obj, fields, fieldDictionaryMaps);
    }

    /**
     * 将一个Po对象转换为JSONObject 不检查对象上面的的属性是否有DictionaryMap注解，只做fields的过滤
     * @param obj Object
     * @param fields 过滤相关字段
     * @return Po对象转换为JSONObject
     */
    public static Object objectToJSONNotMapDict(Object obj, String[] fields){
        if(obj==null)
            return null;
        return objectToJSON(obj, fields, null);
    }

    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param obj Object
     * @param fieldDictionaryMaps 数据字典映射表
     * @return Po对象转换为JSONObject
     */
    private static Object objectToJSON(Object obj , List<DictionaryMapColumn> fieldDictionaryMaps ){
        if(obj==null)
            return null;
        Object jsonObject= ( obj instanceof  Map) ? obj : JSON.toJSON(obj);
        if(jsonObject instanceof Map){
            if(fieldDictionaryMaps==null||fieldDictionaryMaps.size()==0)
                return jsonObject;
            Map<String,Object> jsonObj = ( Map<String,Object>) jsonObject;
            for(DictionaryMapColumn col: fieldDictionaryMaps){
                if( jsonObj.get(col.getFieldName()) !=null) {
                    jsonObj.put(col.getMapFieldName(),
                        mapDictinaryValue(col, jsonObj.get(col.getFieldName())));
                }
            }
            return jsonObj;
        }
        return jsonObject;
    }

    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param obj Object
     * @return Po对象转换为JSONObject
     */
    public static Object objectToJSON(Object obj){
        if(obj==null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(obj.getClass());
        return objectToJSON(obj,fieldDictionaryMaps);

    }


    private static Map<String,Object>  mapJsonObjectCascade(Map<String,Object> jsonObj, Object object) {
        Map<String,Object> jsonObject = mapJsonObject(jsonObj, object.getClass() );

        for(Map.Entry<String, Object> entry : jsonObject.entrySet()){
            if(entry.getValue() instanceof JSONObject){
                Object fieldValue = ReflectionOpt.getFieldValue(object, entry.getKey());
                if(fieldValue != null){
                    mapJsonObjectCascade((JSONObject)entry.getValue(),fieldValue);
                }
            }else if(entry.getValue() instanceof JSONArray){
                Object fieldValue = ReflectionOpt.getFieldValue(object, entry.getKey());
                if(fieldValue instanceof List){
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    for(int i=0; i< jsonArray.size(); i++ ){
                        if(jsonArray.get(i) instanceof  JSONObject) {
                            mapJsonObjectCascade((JSONObject) jsonArray.get(i),
                                    ((List) fieldValue).get(i));
                        }
                    }
                }else if(fieldValue instanceof Set){
                    // Set 因为是无序的所有只能情况重新转换，这个应该会影响性能
                    JSONArray jsonArray = (JSONArray) entry.getValue();
                    jsonArray.clear();
                    for(Object listItem : (Set)fieldValue ){
                        jsonArray.add(objectToJSONCascade(listItem));
                    }
                }
            }
        }
        return jsonObject;
    }

    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * 并且检查他的属性是否也对象 或者是 Set List 如果是 同时将子表也 进行数据字典映射
     * 只能转换 List &lt; Object &gt; 不能转换 List &lt; List &lt; Object &gt; &gt; 就是不能转换二维数据
     * 转换  Set 属性是 性能 有问题
     * @param obj Object Object不能是 Collection 对象
     * @return Po对象转换为JSONObject
     */
    public static Object objectToJSONCascade(Object obj){
        if(obj==null)
            return null;
        Object json = JSON.toJSON(obj);
        if(json instanceof JSONObject) {
            return mapJsonObjectCascade((JSONObject)json, obj);
        }
        return json;
    }

    /**
     * 将一个Po对象数组转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs Object[]
     * @param fields 过滤相关字段
     * @return  Po对象数组转换为JSONArray
     */
    public static JSONArray objectsToJSONArray(Object[] objs, String[] fields){
        JSONArray ja = new JSONArray();
        if(objs==null||objs.length==0||objs[0]==null)
            return ja;
        List<DictionaryMapColumn> fieldDictionaryMaps =
                getDictionaryMapColumns(fields, objs[0].getClass());
        for(Object obj : objs){
            ja.add(objectToJSON(obj, fields, fieldDictionaryMaps));
        }
        return ja;
    }

    /**
     * 将一个Po对象列表转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs Collection Object
     * @param fields 过滤相关字段
     * @return Po对象列表转换为JSONArray
     */
    public static JSONArray objectsToJSONArray(Collection<? extends Object> objs, String[] fields){
        JSONArray ja = new JSONArray();
        if(objs==null||objs.isEmpty())
            return ja;

        List<DictionaryMapColumn> fieldDictionaryMaps =
                getDictionaryMapColumns(fields,
                        objs.iterator().next().getClass());

        for(Object obj : objs){
            ja.add(objectToJSON(obj, fields, fieldDictionaryMaps));
        }
        return ja;
    }

    /**
     * 将一个Po对象列表转换为JSONArray 不检查对象上面的的属性是否有DictionaryMap注解，只做fields的过滤
     * @param objs Collection Object
     * @param fields 过滤相关字段
     * @return Po对象列表转换为JSONArray
     */
    public static JSONArray objectsToJSONArrayNotMapDict(Collection<? extends Object> objs, String[] fields){
        JSONArray ja = new JSONArray();
        if(objs==null||objs.isEmpty())
            return ja;
        for(Object obj : objs){
            ja.add(objectToJSON(obj, fields, null));
        }
        return ja;
    }

    /**
     * 将一个Po对象数组转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs Object[]
     * @return  Po对象数组转换为JSONArray
     */
    public static JSONArray objectsToJSONArray(Object[] objs){
        JSONArray ja = new JSONArray();
        if(objs==null||objs.length==0||objs[0]==null)
            return ja;

        List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(objs[0].getClass());
        for(Object obj : objs){
            ja.add(objectToJSON(obj,fieldDictionaryMaps));
        }
        return ja;
    }

    /**
     * 将一个Po对象列表转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs Collection Object
     * @return Po对象列表转换为JSONArray
     */
    public final static JSONArray objectsToJSONArray(Collection<? extends Object> objs){
        JSONArray ja = new JSONArray();
        if(objs==null||objs.isEmpty())
            return ja;

        List<DictionaryMapColumn> fieldDictionaryMaps =
                getDictionaryMapColumns(
                        objs.iterator().next().getClass());

        for(Object obj : objs){
            ja.add(objectToJSON(obj,fieldDictionaryMaps));
        }
        return ja;
    }

    /**
     * 内置的类不需要直接实例化，通过 SysDaoOptUtils.createDictionaryMapBuilder创建
     * @author codefan
     *
     */
    static public class DictionaryMapBuilder{
        private Map<String,LeftRightPair<String,String>> dictionaryMap;
        private DictionaryMapBuilder(){
            dictionaryMap = new HashMap<>();
        }

        /**
         * fieldName ， newFieldName， catalog
         * @param codeField 需要数据字典转换的字段名
         * @param valueField 转换后值存放的字段名
         * @param dictCatalog    数据字典类别代码
         * @return  DictionaryMapBuilder
         */
        public DictionaryMapBuilder addDictionaryDesc(
                String codeField,String valueField,String dictCatalog){
            dictionaryMap.put(codeField, new LeftRightPair<>(valueField,dictCatalog));
            return this;
        }

        /**
         * 直接返回内置的 Map类型变量
         * @return  Map
         */
        public List<DictionaryMapColumn> build(){
            return DictionaryMapUtils.createDictionaryMapColumns(dictionaryMap);
        }
    }

    /**
     * 创建 DictionaryMap的辅助类，这样就可以用一下代码创建一个 Map
     * 类型的参数放到  listObjectsBy?qlAsJson 函数的 dictionaryMap 变量中
     *      SysDaoOptUtils.createDictionaryMapBuilder("F1", "V1", "D1")
            .addDictionaryDesc("F2", "V2", "D2")
            .addDictionaryDesc("F3", "V3", "D3").create()
     * @param codeField 需要数据字典转换的字段名
     * @param valueField 转换后值存放的字段名
     * @param dictCatalog    数据字典类别代码
     * @return  DictionaryMapBuilder
     */
    public static DictionaryMapBuilder createDictionaryMapBuilder(
            String codeField,String valueField,String dictCatalog){
        DictionaryMapBuilder builder = new DictionaryMapBuilder();
        return builder.addDictionaryDesc(codeField, valueField, dictCatalog);
    }


    /*private static List<DictionaryMapColumn> createDictionaryMapColumns
            (List<Triple<String,String,String>> mapInfo){
        List<DictionaryMapColumn> fieldDictionaryMaps =
                new ArrayList<>();

        for(Triple<String,String,String> ent : mapInfo){
            DictionaryMapColumn dictionaryMapColumn = new DictionaryMapColumn(
                    ent.getLeft(), ent.getMiddle(), ent.getRight());

            fieldDictionaryMaps.add(dictionaryMapColumn);

        }//end of for
        return fieldDictionaryMaps;
    }*/

    /**
     * 检查objType属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
     * @param mapInfo po对象类型 fieldName ， newFieldName， catalog
     * @return DictionaryMapColumn 字段名包括数据字典相关信息
     */
    private static List<DictionaryMapColumn> createDictionaryMapColumns
        (Map<String,LeftRightPair<String,String>> mapInfo){
        List<DictionaryMapColumn> fieldDictionaryMaps =
            new ArrayList<>();

        for(Map.Entry<String,LeftRightPair<String,String>> ent : mapInfo.entrySet()){
            DictionaryMapColumn dictionaryMapColumn = new DictionaryMapColumn(
                ent.getKey(), ent.getValue().getLeft(), ent.getValue().getRight());
            fieldDictionaryMaps.add(dictionaryMapColumn);
        }//end of for
        return fieldDictionaryMaps;
    }

    public static Map<String,Object> mapJsonObject(Map<String,Object> obj, Class<?>... objTypes) {
        if (obj == null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<>();
        for(Class<?> objType : objTypes) {
            fieldDictionaryMaps.addAll(DictionaryMapUtils.getDictionaryMapColumns(objType));
        }
        return ( Map<String,Object>) objectToJSON( obj , fieldDictionaryMaps);
    }

    public static Map<String,Object>  mapJsonObject(Map<String,Object> obj,
                                                          Map<String,LeftRightPair<String,String>> mapInfo) {
        List<DictionaryMapColumn> fieldDictionaryMaps = createDictionaryMapColumns(mapInfo);
        return ( Map<String,Object>) objectToJSON( obj , fieldDictionaryMaps);
    }

    public static List<Map<String,Object>>
        mapJsonArray(List<Map<String,Object>> objs, List<DictionaryMapColumn> fieldDictionaryMaps  ) {
        if(fieldDictionaryMaps==null | fieldDictionaryMaps.size()<1)
            return objs;
        for(Map<String,Object> obj : objs){
            for(DictionaryMapColumn col:fieldDictionaryMaps){
                if( obj.get(col.getFieldName()) !=null) {
                    obj.put(col.getMapFieldName(),
                        mapDictinaryValue(col, obj.get(col.getFieldName())));
                }
            }
        }
        return objs;
    }


    public static List<Map<String,Object>>  mapJsonArray(List<Map<String,Object>> objs,Class<?>... objTypes) {
        if (objs == null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<>();
        for(Class<?> objType : objTypes) {
            fieldDictionaryMaps.addAll(DictionaryMapUtils.getDictionaryMapColumns(objType));
        }
        return mapJsonArray( objs, fieldDictionaryMaps);

    }

    public static List<Map<String,Object>>  mapJsonArray(List<Map<String,Object>> objs,
                                                         Map<String,LeftRightPair<String,String>> mapInfo ) {
        if (objs == null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = createDictionaryMapColumns(mapInfo);
        return mapJsonArray( objs, fieldDictionaryMaps);
    }

    public static JSONArray
        mapJsonArray(JSONArray objs, List<DictionaryMapColumn> fieldDictionaryMaps) {
        if(fieldDictionaryMaps==null | fieldDictionaryMaps.size()<1)
            return objs;
        for(Object obj : objs){
            if(obj instanceof Map) {
                Map<String,Object> jsonObj = (Map<String,Object>)obj;
                for (DictionaryMapColumn col : fieldDictionaryMaps) {
                    if (jsonObj.get(col.getFieldName()) != null) {
                        jsonObj.put(col.getMapFieldName(),
                            mapDictinaryValue(col, jsonObj.get(col.getFieldName())));
                    }
                }
            }
        }
        return objs;
    }

    public static JSONArray mapJsonArray(JSONArray objs, Class<?> ...objTypes) {
        if (objs == null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<>();
        for(Class<?> objType : objTypes) {
            fieldDictionaryMaps.addAll(DictionaryMapUtils.getDictionaryMapColumns(objType));
        }
        return mapJsonArray( objs, fieldDictionaryMaps);
    }

    /**
     *
     * @param objs 转换前的对象
     * @param mapInfo Map&lt;String,LeftRightPair&lt;String,String&gt;&gt;  fieldName ， newFieldName， catalog
     * @return 转换后的对象
     */
    public static JSONArray mapJsonArray(JSONArray objs,
              Map<String,LeftRightPair<String,String>> mapInfo ) {
        if (objs == null)
            return null;
        List<DictionaryMapColumn> fieldDictionaryMaps = createDictionaryMapColumns(mapInfo);
        return mapJsonArray( objs, fieldDictionaryMaps);
    }

}
