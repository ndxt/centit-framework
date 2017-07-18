package com.centit.framework.hibernate.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.core.dao.DictionaryMapColumn;
import com.centit.framework.core.dao.PageDesc;
import com.centit.support.common.KeyValuePair;
import com.centit.support.database.DatabaseAccess;
import com.centit.support.database.QueryAndNamedParams;
import com.centit.support.database.QueryUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.*;

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
     * @param obj Object
     * @return Po对象转换为JSONObject
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
     * @param objs Object[]
     * @return  Po对象数组转换为JSONArray
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
     * 这个方法是配合 DictionaryMap 注解来执行查询的 
     * @param baseDao 这个Dao和DatabaseOpt中的不一样这个必须和下面的Po对象对应（objType），
     * 			因为他需要获得对应的过滤条件 baseDao.getFilterField() 
     * @param fields fields 字段，是po中的熟悉名，不是数据库表中的字段名
     * @param objType po对象类型，和上面的Dao对应
     * @param hqlFromPiece 原始的过滤查询HQL，包括语句和参数列表(Map)
     * 				 查询语句可以是通过数据权限过滤自动生成的语句，或者是开发人员自己编写的内置语句
     * @param filterMap 查询变量 和baseDao中的listObject是中的查询变量一样
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return JSONArray实现了List接口，JSONObject实现了
     * 		Map接口。所以可以直接转换为List
     */
    @Transactional
    public final static JSONArray listObjectsAsJson(BaseDaoImpl<?, ?> baseDao ,
            String[] fields,Class<?> objType ,QueryAndNamedParams hqlFromPiece, 
            Map<String, Object> filterMap, PageDesc pageDesc){
    	
    	JSONArray ja = new JSONArray();
        List<DictionaryMapColumn> fieldDictionaryMaps = 
                makeQueryColumn(fields, objType);
        if(fieldDictionaryMaps.size()<1)
            return ja;
       
        int fieldCount = 0;
        StringBuilder hqlBuilder = new StringBuilder("Select ");
        
        for(DictionaryMapColumn field :fieldDictionaryMaps){
            if(fieldCount>0)
                hqlBuilder.append(",");
            hqlBuilder.append(field.getFieldName());
            fieldCount++;
        }
        
        hqlBuilder.append(" ").append(hqlFromPiece.getHql());
        
        //hqlBuilder.append(" from ").append(objType.getSimpleName()).append(" where 1=1 ");        
        
        QueryAndNamedParams hql = BaseDaoImpl.builderHqlAndNamedParams(hqlBuilder.toString(), 
                filterMap,baseDao.getFilterField());
        
        hql.addAllParams(hqlFromPiece.getParams());

        List<?> dataList = DatabaseOptUtils.findObjectsByHql(baseDao,hql.getHql(),
                hql.getParams(), pageDesc);
        
        if(dataList==null || dataList.isEmpty())
            return ja;        
        
        for(int j=0; j<dataList.size();j++ ){                  
            JSONObject jo = new JSONObject();
            for(int i=0;i<fieldDictionaryMaps.size();i++){                
                jo.put(fieldDictionaryMaps.get(i).getFieldName(),  
                		fetchObjectFromList(dataList,j,i,fieldCount));//   ((Object [])dataList.get(j))[i]); //JSON.toJSON
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
     * 这个方法是配合 DictionaryMap 注解来执行查询的 
     * @param baseDao 这个Dao和DatabaseOpt中的不一样这个必须和下面的Po对象对应（objType），
     * 			因为他需要获得对应的过滤条件 baseDao.getFilterField() 
     * @param fields fields 字段，是po中的熟悉名，不是数据库表中的字段名
     * @param objType po对象类型，和上面的Dao对应
     * @param filterMap 查询变量 和baseDao中的listObject是中的查询变量一样
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return JSONArray实现了List接口，JSONObject实现了
     * 		Map接口。所以可以直接转换为List
     */
    @Transactional
    public final static JSONArray listObjectsAsJson(BaseDaoImpl<?, ?> baseDao ,
            String[] fields,Class<?> objType, 
            Map<String, Object> filterMap, PageDesc pageDesc){
    	QueryAndNamedParams hqlFromPiece = new QueryAndNamedParams("from "+objType.getSimpleName()+" where 1=1");
        return listObjectsAsJson(baseDao , fields,objType, 
        		hqlFromPiece, filterMap, pageDesc);
    }
    

    /**
     * 获取一条记录的json格式数据， 这个函数的参数和Dao中的参数兼容
     * @param baseDao 这个Dao和DatabaseOpt中的不一样这个必须和下面的Po对象对应（objType），
     * 			因为他需要获得对应的过滤条件 baseDao.getFilterField() 
     * @param fields fields 字段，是po中的熟悉名，不是数据库表中的字段名
     * @param objType po对象类型，和上面的Dao对应
     * @param filterMap 查询变量 和baseDao中的listObject是中的查询变量一样
     * @return JSONObject实现了Map接口。
     * 			所以可以直接转换为Map
     */
    @Transactional
    public final static JSONObject getObjectAsJson(BaseDaoImpl<?, ?> baseDao ,
            String[] fields,Class<?> objType, 
            Map<String, Object> filterMap){
    	
        List<DictionaryMapColumn> fieldDictionaryMaps = 
                makeQueryColumn(fields, objType);
        
        if(fieldDictionaryMaps.size()<1)
            return null;
       
        int fieldCount = 0;
        StringBuilder hqlBuilder = new StringBuilder("Select ");
        
        for(DictionaryMapColumn field :fieldDictionaryMaps){
            if(fieldCount>0)
                hqlBuilder.append(",");
            hqlBuilder.append(field.getFieldName());
            fieldCount++;
        }
        
        hqlBuilder.append(" from ").append(objType.getSimpleName()).append(" where 1=1 ");        
        
        QueryAndNamedParams hql = BaseDaoImpl.builderHqlAndNamedParams(hqlBuilder.toString(), 
                filterMap,baseDao.getFilterField());

        List<?> dataList = DatabaseOptUtils.findObjectsByHql(baseDao,hql.getHql(),
                hql.getParams());
        
        if(dataList==null || dataList.isEmpty())
            return null;

        JSONObject jo = new JSONObject();            
        for(int i=0;i<fieldDictionaryMaps.size();i++){                
            jo.put(fieldDictionaryMaps.get(i).getFieldName(), 
            		fetchObjectFromList(dataList,0,i,fieldCount));//  ((Object [])dataList.get(0))[i]);
            if(fieldDictionaryMaps.get(i).getMapFieldName() != null){
                jo.put(fieldDictionaryMaps.get(i).getMapFieldName(), 
                        fieldDictionaryMaps.get(i).getDictionaryMap().get(
                        		String.valueOf(((Object [])dataList.get(0))[i])));
            }
        }
        return jo;
    }
    
    /**
     * sql语句直接访问数据库
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param sqlSen sql语句，这个语句必须用命名参数
     * @param paramsMap	命名参数对应的变量
     * @param dictionaryMap	这个是需要转换数据字典的字段，map的Key对应的sql语句中的字段名，
     * 				value是一个 KeyValuePair，value的key为新的字段名称可以为任意值但不要和field中的字段重名
     * 									   value的value为数据字典代码
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return JSONArray实现了List接口，JSONObject实现了
     * 		Map接口。所以可以直接转换为List
     */
    @Transactional
    public final static JSONArray
    	listObjectsBySqlAsJson(BaseDaoImpl<?, ?> baseDao ,
            String sqlSen,  Map<String, Object> paramsMap,
            Map<String,KeyValuePair<String,String>> dictionaryMap,
            PageDesc pageDesc){
    	return listObjectsBySqlAsJson(baseDao ,
                 sqlSen,  paramsMap,
                null,
                dictionaryMap,
                pageDesc);
    }
      
    @Transactional
    public final static JSONArray
    	listObjectsBySqlAsJson(BaseDaoImpl<?, ?> baseDao ,
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

        List<?> dataList = DatabaseOptUtils.findObjectsBySql(baseDao,sqlSen,
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
     * Hql语句访问数据库
     * 
     * @param baseDao 这个Dao和sqlSen语句中的对象没有任何关系，这个只用了获取session来访问数据
     * @param hqlSen hql语句，这个语句必须用命名参数
     * @param paramsMap	命名参数对应的变量
     * @param dictionaryMap	这个是需要转换数据字典的字段，map的Key对应的sql语句中的字段名，
     * 				value是一个 KeyValuePair，value的key为新的字段名称可以为任意值但不要和field中的字段重名
     * 									   value的value为数据字典代码
     * @param pageDesc 分页信息，其中的totalRows属性为输出信息，表示总共的记录条数
     * @return JSONArray实现了List接口，JSONObject实现了
     * 		Map接口。所以可以直接转换为List
     */
    @Transactional
    public final static JSONArray
    	listObjectsByHqlAsJson(BaseDaoImpl<?, ?> baseDao ,
            String hqlSen,  Map<String, Object> paramsMap,
            Map<String,KeyValuePair<String,String>> dictionaryMap,
            PageDesc pageDesc){
    	
    	JSONArray ja = new JSONArray();
    	List<String> fields = QueryUtils.getSqlFiledNames(hqlSen);
    	if(fields==null||fields.size()<1) 
    		return ja;
    	
    	List<DictionaryMapColumn> fieldDictionaryMaps = new ArrayList<DictionaryMapColumn>();
    	for(String field:fields){

    		KeyValuePair<String,String> dict = dictionaryMap==null? null : dictionaryMap.get(field);
    		if(dict!=null){
    			fieldDictionaryMaps.add(new DictionaryMapColumn(field, 
    					dict.getKey(),
                        CodeRepositoryUtil.getLabelValueMap(dict.getValue() )));
    		}else{
    			fieldDictionaryMaps.add(new DictionaryMapColumn(field));
    		}
    	}    	
    	int fi=fields.size();
        List<?> dataList = DatabaseOptUtils.findObjectsByHql(baseDao,hqlSen,
        		paramsMap, pageDesc);
        
        if(dataList==null || dataList.isEmpty())
            return ja;       
        
        for(int j=0; j<dataList.size();j++ ){                  
            JSONObject jo = new JSONObject();
            for(int i=0;i<fieldDictionaryMaps.size();i++){
            	jo.put(fieldDictionaryMaps.get(i).getFieldName(), 
            			DatabaseAccess.fetchLobField(fetchObjectFromList(dataList,j,i,fi),false));
            			//  DatabaseAccess.fetchLobField(((Object [])dataList.get(j))[i],false));
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
    	 * @return  DictionaryMapBuilder
    	 */
    	public DictionaryMapBuilder addDictionaryDesc(
    			String codeField,String valueField,String dictCatalog){
    		dictionaryMap.put(codeField, 
    				new KeyValuePair<String,String>(valueField,dictCatalog));
    		return this;
    	}
    	
    	/**
    	 * 直接返回内置的 Map 类型变量
    	 * @return 直接返回内置的 Map 类型变量
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
     * @return DictionaryMapBuilder
     */
    public static DictionaryMapBuilder createDictionaryMapBuilder(
			String codeField,String valueField,String dictCatalog){
		DictionaryMapBuilder builder = new DictionaryMapBuilder();
		return builder.addDictionaryDesc(codeField, valueField, dictCatalog);
	}
}
