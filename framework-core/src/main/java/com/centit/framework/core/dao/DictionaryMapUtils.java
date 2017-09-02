package com.centit.framework.core.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.support.common.KeyValuePair;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 作为DatabaseOptUtils的补充，添加的主要内容是和数据字典管理，包括对 DictionaryMap注解的处理
 * @author codefan
 *
 */
@SuppressWarnings("unused")
public class DictionaryMapUtils {
    
    private DictionaryMapUtils(){
        
    }

    private static DictionaryMapColumn makeDictionaryMapColumn(DictionaryMap dictionary,String fieldName ){
		Map<String,String> dm = CodeRepositoryUtil.getLabelValueMap(dictionary.value());
		if(dm!=null)
			return new DictionaryMapColumn(fieldName,
					dictionary.fieldName(),
					dm);
		return null;
	}

	private static DictionaryMapColumn makeDictionaryMapColumn(String dictionaryFieldName  , String dataCatalog, String fieldName ){
		Map<String,String> dm = CodeRepositoryUtil.getLabelValueMap(dataCatalog);
		if(dm!=null)
			return new DictionaryMapColumn(fieldName,
					dictionaryFieldName,
					dm);
		return null;
	}
    /**
     * 检查objType属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
     * @param objType po对象类型
     * @return DictionaryMapColumn 字段名包括数据字典相关信息
     */
    public final static List<DictionaryMapColumn> getDictionaryMapColumns
            (Class<?> objType){
        
        Field[] objFields = objType.getDeclaredFields();
        List<DictionaryMapColumn> fieldDictionaryMaps = 
                new ArrayList<>();
        
        for(Field field :objFields){
            if(field.isAnnotationPresent(DictionaryMap.class)){
				DictionaryMapColumn dictionaryMapColumn = makeDictionaryMapColumn(
						field.getAnnotation(DictionaryMap.class),field.getName());

                if(dictionaryMapColumn != null)
                	fieldDictionaryMaps.add(dictionaryMapColumn);
            }
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
	public final static List<DictionaryMapColumn> getDictionaryMapColumns
	(String[] fields,Class<?> objType){

		Field[] objFields = objType.getDeclaredFields();
		List<DictionaryMapColumn> fieldDictionaryMaps =
				new ArrayList<>();

		for(Field field :objFields){
			if(fields==null || fields.length==0 ||
					ArrayUtils.contains(fields,field.getName())) {

				if (field.isAnnotationPresent(DictionaryMap.class)) {

					DictionaryMapColumn dictionaryMapColumn = makeDictionaryMapColumn(
							field.getAnnotation(DictionaryMap.class),field.getName());

					if(dictionaryMapColumn != null)
						fieldDictionaryMaps.add(dictionaryMapColumn);
				}
			}

		}//end of for
		return fieldDictionaryMaps;
	}

	/**
	 * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
	 * @param obj Object
	 * @param fieldDictionaryMaps 数据字典映射表
	 * @param fields 过滤相关字段
	 * @return Po对象转换为JSONObject
	 */
	public final static Object objectToJSON(Object obj, String[] fields, List<DictionaryMapColumn> fieldDictionaryMaps ){
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
			if(fieldDictionaryMaps==null||fieldDictionaryMaps.size()==0)
				return newJsonObj;

			for(DictionaryMapColumn col:fieldDictionaryMaps){
				newJsonObj.put(col.getMapFieldName(),
						col.getDictionaryMap().get(jsonObj.get(col.getFieldName())));
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
    public final static Object objectToJSON(Object obj, String[] fields){
    	if(obj==null)
    		return null;
    	List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(obj.getClass());
    	return objectToJSON(obj, fields, fieldDictionaryMaps);
    }


	/**
	 * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
	 * @param obj Object
	 * @param fieldDictionaryMaps 数据字典映射表
	 * @return Po对象转换为JSONObject
	 */
	private final static Object objectToJSON(Object obj , List<DictionaryMapColumn> fieldDictionaryMaps ){
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
							col.getDictionaryMap().get(jsonObj.get(col.getFieldName())));
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
	public final static Object objectToJSON(Object obj){
		if(obj==null)
			return null;
		List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(obj.getClass());
		return objectToJSON(obj,fieldDictionaryMaps);
	}

    /**
     * 将一个Po对象数组转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs Object[]
	 * @param fields 过滤相关字段
	 * @return  Po对象数组转换为JSONArray
     */
    public final static JSONArray objectsToJSONArray(Object[] objs, String[] fields){
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
    public final static JSONArray objectsToJSONArray(Collection<? extends Object> objs, String[] fields){
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
	 * 将一个Po对象数组转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
	 * @param objs Object[]
	 * @return  Po对象数组转换为JSONArray
	 */
	public final static JSONArray objectsToJSONArray(Object[] objs){
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
    	private Map<String,KeyValuePair<String,String>> dictionaryMap;
    	private DictionaryMapBuilder(){
    		dictionaryMap = new HashMap<String,KeyValuePair<String,String>>();
    	}
    	
    	/**
    	 * 
    	 * @param codeField 需要数据字典转换的字段名
    	 * @param valueField 转换后值存放的字段名
    	 * @param dictCatalog	数据字典类别代码
    	 * @return  DictionaryMapBuilder
    	 */
    	public DictionaryMapBuilder addDictionaryDesc(
    			String codeField,String valueField,String dictCatalog){
    		dictionaryMap.put(codeField, 
    				new KeyValuePair<>(valueField,dictCatalog));
    		return this;
    	}
    	
    	/**
    	 * 直接返回内置的 Map类型变量
    	 * @return  Map
    	 */
    	public  Map<String,KeyValuePair<String,String>> create(){
    		return dictionaryMap;
    	}
    }
    
    /**
     * 创建 DictionaryMap的辅助类，这样就可以用一下代码创建一个 Map
     * 类型的参数放到  listObjectsBy?qlAsJson 函数的 dictionaryMap 变量中
     * 	 SysDaoOptUtils.createDictionaryMapBuilder("F1", "V1", "D1")
			.addDictionaryDesc("F2", "V2", "D2")
			.addDictionaryDesc("F3", "V3", "D3").create()
     * @param codeField 需要数据字典转换的字段名
     * @param valueField 转换后值存放的字段名
     * @param dictCatalog	数据字典类别代码
     * @return  DictionaryMapBuilder
     */
    public static DictionaryMapBuilder createDictionaryMapBuilder(
			String codeField,String valueField,String dictCatalog){
		DictionaryMapBuilder builder = new DictionaryMapBuilder();
		return builder.addDictionaryDesc(codeField, valueField, dictCatalog);
	}


	/**
	 * 检查objType属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
	 * @param mapInfo po对象类型
	 * @return DictionaryMapColumn 字段名包括数据字典相关信息
	 */
	private final static List<DictionaryMapColumn> getDictionaryMapColumns
	(Map<String,KeyValuePair<String,String>> mapInfo){
		List<DictionaryMapColumn> fieldDictionaryMaps =
				new ArrayList<>();

		for(Map.Entry<String,KeyValuePair<String,String>> ent : mapInfo.entrySet()){
			DictionaryMapColumn dictionaryMapColumn = makeDictionaryMapColumn(
					ent.getValue().getLeft(),ent.getValue().getRight() , ent.getKey());

			if(dictionaryMapColumn != null)
				fieldDictionaryMaps.add(dictionaryMapColumn);

		}//end of for
		return fieldDictionaryMaps;
	}

	public final static Map<String,Object>  mapJsonObject(Map<String,Object> obj,Class<?> objType ) {
		if (obj == null)
			return null;
		List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(obj.getClass());
		return ( Map<String,Object>) objectToJSON( obj , fieldDictionaryMaps);
	}

	public final static Map<String,Object>  mapJsonObject(Map<String,Object> obj,
														  Map<String,KeyValuePair<String,String>> mapInfo) {
		List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(mapInfo);
		return ( Map<String,Object>) objectToJSON( obj , fieldDictionaryMaps);
	}


	private final static List<Map<String,Object>>
		mapJsonArray(List<Map<String,Object>> objs, List<DictionaryMapColumn> fieldDictionaryMaps  ) {
		if(fieldDictionaryMaps==null | fieldDictionaryMaps.size()<1)
			return objs;
		for(Map<String,Object> obj : objs){

			for(DictionaryMapColumn col:fieldDictionaryMaps){
				if( obj.get(col.getFieldName()) !=null) {
					obj.put(col.getMapFieldName(),
							col.getDictionaryMap().get(obj.get(col.getFieldName())));
				}
			}
		}
		return objs;

	}


	public final static List<Map<String,Object>>  mapJsonArray(List<Map<String,Object>> objs,Class<?> objType ) {
		if (objs == null)
			return null;
		List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(objType);
		return mapJsonArray( objs, fieldDictionaryMaps);

	}

	public final static List<Map<String,Object>>  mapJsonArray(List<Map<String,Object>> objs,
															   Map<String,KeyValuePair<String,String>> mapInfo ) {
		if (objs == null)
			return null;
		List<DictionaryMapColumn> fieldDictionaryMaps = getDictionaryMapColumns(mapInfo);
		return mapJsonArray( objs, fieldDictionaryMaps);
	}
}
