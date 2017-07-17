package com.centit.framework.mybatis.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.core.dao.DictionaryMapColumn;
import com.centit.framework.core.dao.PageDesc;
import com.centit.support.common.KeyValuePair;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.QueryUtils;

/**
 * 作为DatabaseOptUtils的补充，添加的主要内容是和数据字典管理，包括对 DictionaryMap注解的处理
 * @author codefan
 *
 */
public class SysDaoOptUtils {
    
    private SysDaoOptUtils(){
        
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
                new ArrayList<DictionaryMapColumn>();
        
        for(Field field :objFields){
            if(field.isAnnotationPresent(DictionaryMap.class)){
                DictionaryMap dictionary = (DictionaryMap)
                        field.getAnnotation(DictionaryMap.class);
                Map<String,String> dm = CodeRepositoryUtil.getLabelValueMap(dictionary.value());
                if(dm!=null)
                	fieldDictionaryMaps.add(new DictionaryMapColumn(field.getName(), 
                                dictionary.fieldName(),
                                dm));
            }
        }//end of for
        return fieldDictionaryMaps;        
    }
    
    /**
     * 将一个Po对象转换为JSONObject 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param obj
     * @return
     */
    public final static JSONObject objectToJSON(Object obj){
    	if(obj==null)
    		return null;
    	Object json = JSON.toJSON(obj);
    	if(json instanceof JSONObject){
    		 List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(obj.getClass());
    		 if(fieldDictionaryMaps==null||fieldDictionaryMaps.size()==0)
    			 return (JSONObject)json;
    		 JSONObject jsonObj = (JSONObject)json;
    		 for(DictionaryMapColumn col:fieldDictionaryMaps){
    			 jsonObj.put(col.getMapFieldName(),
    					 col.getDictionaryMap().get(jsonObj.get(col.getFieldName())));
    		 }
    		 return jsonObj;
    	}
    	return null;
    }
    
    /**
     * 将一个Po对象数组转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs
     * @return
     */
    public final static JSONArray objectsToJSONArray(Object[] objs){
    	JSONArray ja = new JSONArray();
    	if(objs==null||objs.length==0||objs[0]==null)
    		return ja;
    	List<DictionaryMapColumn> fieldDictionaryMaps =  getDictionaryMapColumns(objs[0].getClass());
    	for(Object obj : objs){         	
         	Object json = JSON.toJSON(obj);
        	if(json instanceof JSONObject && 
    			 fieldDictionaryMaps!=null &&  fieldDictionaryMaps.size()>=0){
        		 JSONObject jsonObj = (JSONObject)json;
        		 for(DictionaryMapColumn col:fieldDictionaryMaps){
        			 jsonObj.put(col.getMapFieldName(),
        					 col.getDictionaryMap().get(jsonObj.get(col.getFieldName())));
        		 }
        		 ja.add(jsonObj);
        	}else 
        		ja.add(json);  
        }
        return ja;
    }
    
    /**
     * 将一个Po对象列表转换为JSONArray 同时检查对象上面的的属性是否有DictionaryMap注解，如果有转换数据字典
     * @param objs
     * @return
     */
    public final static JSONArray objectsToJSONArray(Collection<? extends Object> objs){
    	JSONArray ja = new JSONArray();
    	if(objs==null||objs.isEmpty())
    		return ja;
    	
    	List<DictionaryMapColumn> fieldDictionaryMaps =  
    			getDictionaryMapColumns(
    					objs.iterator().next().getClass());
    	
        for(Object obj : objs){        	
        	Object json = JSON.toJSON(obj);
        	if(json instanceof JSONObject && 
    			 fieldDictionaryMaps!=null &&  fieldDictionaryMaps.size()>=0){
        		 JSONObject jsonObj = (JSONObject)json;
        		 for(DictionaryMapColumn col:fieldDictionaryMaps){
        			 jsonObj.put(col.getMapFieldName(),
        					 col.getDictionaryMap().get(jsonObj.get(col.getFieldName())));
        		 }
        		 ja.add(jsonObj);
        	}else 
        		ja.add(json);  
        }
        return ja;
    }
    
    /**
     * 将fields字段和类objType中的属性进行一一对应，只有对应上的才会放到hql语句中。
     * 并且检查属性上是否有DictionaryMap注解，如果有则获取对应的数据字典用于后面查询是转换编码
     * @param fields 字段，是po中的熟悉名，不是数据库表中的字段名
     * @param objType po对象类型
     * @return DictionaryMapColumn 字段名包括数据字典相关信息
     */
    public final static List<DictionaryMapColumn> makeQueryColumn
            (String[] fields,Class<?> objType){
        
        Field[] objFields = objType.getDeclaredFields();
        List<DictionaryMapColumn> fieldDictionaryMaps = 
                new ArrayList<DictionaryMapColumn>();
        
        for(Field field :objFields){
            if(field.isAnnotationPresent(Column.class) &&
                (fields==null || fields.length==0 ||
                    ArrayUtils.contains(fields,field.getName()))
              ){
          
                if(field.isAnnotationPresent(DictionaryMap.class)){
                    DictionaryMap dictionary = (DictionaryMap)
                            field.getAnnotation(DictionaryMap.class);
                    fieldDictionaryMaps.add(new DictionaryMapColumn(field.getName(), 
                                    dictionary.fieldName(),
                                    CodeRepositoryUtil.getLabelValueMap(dictionary.value() )));
                }else{
                    fieldDictionaryMaps.add(new DictionaryMapColumn(field.getName(), 
                            null,null));
                }
            }
        }//end of for
        return fieldDictionaryMaps;        
    }
    
    private static Object fetchObjectFromList(List<?> dataList , int row ,int col ,int fieldSize){
    	return fieldSize>1?  ((Object [])dataList.get(row))[col] : dataList.get(row);
    }
            
   
    
   
    /**
     * sql语句直接访问数据库
     * 
     * @param sqlSession 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param sqlSen sql语句，这个语句必须用命名参数
     * @param paramsMap	命名参数对应的变量
     * @param dictionaryMap	这个是需要转换数据字典的字段，map的Key对应的sql语句中的字段名，
     * 				value是一个 KeyValuePair，value的key为新的字段名称可以为任意值但不要和field中的字段重名
     * 									   value的value为数据字典代码
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return JSONArray实现了List<JSONObject>接口，JSONObject实现了
     * 		Map<String, Object>接口。所以可以直接转换为List<Map<String,Object>>
     */
    @Transactional
    public final static JSONArray
    	listObjectsBySqlAsJson(SqlSession sqlSession,
            String sqlSen,  Map<String, Object> paramsMap,
            Map<String,KeyValuePair<String,String>> dictionaryMap,
            PageDesc pageDesc){
    	return listObjectsBySqlAsJson(sqlSession ,
                 sqlSen,  paramsMap,
                null,
                dictionaryMap,
                pageDesc);
    }
      
    @Transactional
    public final static JSONArray
    	listObjectsBySqlAsJson(SqlSession sqlSession,
            String sqlSen,  Map<String, Object> paramsMap,
            String [] fieldNames,
            Map<String,KeyValuePair<String,String>> dictionaryMap,
            PageDesc pageDesc){
    	JSONArray ja = new JSONArray();
    	List<String> fields = QueryUtils.getSqlFiledNames(sqlSen);
    	if(fields==null||fields.size()<1) 
    		return ja;
    	int fns = fieldNames==null?0:fieldNames.length;
    	List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<DictionaryMapColumn>();
    	int fi=0;
    	for(String f:fields){
    		String field =null;
    		if(fi<fns)
    			field = fieldNames[fi];
    		else
    			field = DatabaseAccess.mapColumnNameToField(f);
    		fi++;
    		
    		KeyValuePair<String,String> dict =  null;
    		if(dictionaryMap!=null){
    			dict = dictionaryMap.get(field);
    			if(dict==null)
    				dict = dictionaryMap.get(f);
    		}
    		
    		if(dict!=null){
    			fieldDictionaryMaps.add(new DictionaryMapColumn(field, 
    					dict.getKey(),
                        CodeRepositoryUtil.getLabelValueMap(dict.getValue() )));
    		}else{
    			fieldDictionaryMaps.add(new DictionaryMapColumn(field));
    		}
    	}    	

        List<?> dataList = DatabaseOptUtils.findObjectsBySql(sqlSession,sqlSen,
        		paramsMap, pageDesc);
        
        if(dataList==null || dataList.isEmpty())
            return ja;        
        
        for(int j=0; j<dataList.size();j++ ){                  
            JSONObject jo = new JSONObject();
            for(int i=0;i<fieldDictionaryMaps.size();i++){
                jo.put(fieldDictionaryMaps.get(i).getFieldName(), 
                		DatabaseAccess.fetchLobField(fetchObjectFromList(dataList,j,i,fi),false));//  
                		//DatabaseAccess.fetchLobField(((Object [])dataList.get(j))[i],false));
                if(fieldDictionaryMaps.get(i).getMapFieldName() != null){
                    jo.put(fieldDictionaryMaps.get(i).getMapFieldName(), 
                            fieldDictionaryMaps.get(i).getDictionaryMap().get(
                            		String.valueOf(((Object [])dataList.get(j))[i])));
                }
            }
            ja.add(jo);  
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
    	 * @return
    	 */
    	public DictionaryMapBuilder addDictionaryDesc(
    			String codeField,String valueField,String dictCatalog){
    		dictionaryMap.put(codeField, 
    				new KeyValuePair<String,String>(valueField,dictCatalog));
    		return this;
    	}
    	
    	/**
    	 * 直接返回内置的 Map<String,KeyValuePair<String,String>> 类型变量
    	 * @return
    	 */
    	public  Map<String,KeyValuePair<String,String>> create(){
    		return dictionaryMap;
    	}
    }
    
    /**
     * 创建 DictionaryMap的辅助类，这样就可以用一下代码创建一个 Map<String,KeyValuePair<String,String>>
     * 类型的参数放到  listObjectsBy?qlAsJson 函数的 dictionaryMap 变量中
     * 	 SysDaoOptUtils.createDictionaryMapBuilder("F1", "V1", "D1")
			.addDictionaryDesc("F2", "V2", "D2")
			.addDictionaryDesc("F3", "V3", "D3").create()
     * @param codeField 需要数据字典转换的字段名
     * @param valueField 转换后值存放的字段名
     * @param dictCatalog	数据字典类别代码
     * @return
     */
    public static DictionaryMapBuilder createDictionaryMapBuilder(
			String codeField,String valueField,String dictCatalog){
		DictionaryMapBuilder builder = new DictionaryMapBuilder();
		return builder.addDictionaryDesc(codeField, valueField, dictCatalog);
	}
}
