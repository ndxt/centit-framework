package com.centit.framework.hibernate.dao;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.core.dao.DataPowerFilter;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.common.KeyValuePair;
import com.centit.support.compiler.Formula;
import com.centit.support.compiler.Lexer;
import com.centit.support.database.QueryAndNamedParams;
import com.centit.support.database.QueryUtils;
import com.centit.support.database.QueryUtils.IFilterTranslater;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.web.context.ContextLoaderListener;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HibernatePowerFilter extends DataPowerFilter {

	
	/**
	 * HQL注解中的配置信息
	 */
	private static Map<String,Map<String, String /*KeyValuePair<String,Integer>*/>> hqlMetaData;

	
	public static Map<String,Map<String,String>> getHqlMetaData(){
		if(hqlMetaData==null){
			hqlMetaData = new HashMap<String,Map<String,String>>();
			SessionFactory sessionFactory = 
	                ContextLoaderListener.getCurrentWebApplicationContext().
	                getBean("sessionFactory",  SessionFactory.class);

			Set<EntityType<?>> cm = sessionFactory.getMetamodel().getEntities() ;// .getAllClassMetadata();
	    	for(EntityType ent : cm){
	    		Map<String,String> metaData = new HashMap<String,String>();
	    		//String className = ent.getName();
	    		String entityName = ent.getName();// className.substring(className.lastIndexOf(".")+1);
	    		Class<?> poClass = ent.getJavaType();// ent.getValue().getMappedClass();
	    		Table tableName = poClass.getAnnotation(Table.class);
	    		metaData.put(".talbeName",tableName.name());
	    				//new KeyValuePair<String,Integer>(tableName.name(),4));	
	    		Field [] objFields = poClass.getDeclaredFields();
	    		for(Field field :objFields){
	                if(field.isAnnotationPresent(Column.class)){
	                	Column colMeta = field.getAnnotation(Column.class);
	                	//field.getType()
	                	
	                	metaData.put(field.getName(),colMeta.name());
	                			//new KeyValuePair<String,Integer>(colMeta.name(),mapTypeToInt(field.getType())));
	                }else if(field.isAnnotationPresent(EmbeddedId.class)){
	                	String sId = field.getName()+".";
	                	Class<?> idClass = field.getType();	                	
	                	Field [] idFields = idClass.getDeclaredFields();
	    	    		for(Field idfield :idFields){
	    	                if(idfield.isAnnotationPresent(Column.class)){
	    	                	Column colMeta = idfield.getAnnotation(Column.class);
	    	                	metaData.put(sId + idfield.getName(), colMeta.name());
	                			//new KeyValuePair<String,Integer>(colMeta.name(),mapTypeToInt(idfield.getType())));
	    	                } 
	    	    		}
	                } 
	    		}
	    		Class<?> supClass =  poClass.getSuperclass();
	    		while(!supClass.equals(Object.class)){
	    			Field [] supFields = supClass.getDeclaredFields();
    	    		for(Field supfield :supFields){
    	                if(supfield.isAnnotationPresent(Column.class)){
    	                	Column colMeta = supfield.getAnnotation(Column.class);
    	                	metaData.put(supfield.getName(),colMeta.name());
                					//new KeyValuePair<String,Integer>(colMeta.name(),mapTypeToInt(supfield.getType())));
    	                } 
    	    		}
    	    		supClass =  supClass.getSuperclass();
	    		}
	    		hqlMetaData.put(entityName, metaData);
	    	}
		}
    	return hqlMetaData;
	}

	

	public QueryAndNamedParams makeHQL(Class<?>  poClass,Collection<String> filters,boolean jointSql){
		String shortClassName = poClass.getSimpleName();
		return makeHQL( shortClassName, filters, jointSql);			
	}
	
	public QueryAndNamedParams makeHQL(String shortClassName,Collection<String> filters,boolean jointSql){
	
		QueryAndNamedParams hqlAndParams = new QueryAndNamedParams();
		Map<String, String> tables = new HashMap<String, String>();
		tables.put(shortClassName, "");
		
		DataPowerFilterTranslater translater = new DataPowerFilterTranslater(false, jointSql,this);
		translater.setTableAlias(tables);
		
		StringBuilder hql = new StringBuilder("From ").append(shortClassName).append(" where");
		boolean hasFilter= false;
		if(filters!=null){
			for(String filter : filters){
				QueryAndNamedParams hqlPiece =QueryUtils.translateQueryFilter(filter,translater);
				if(hqlPiece!=null){
					hqlAndParams.addAllParams(hqlPiece.getParams());
					if(hasFilter)
						hql.append(" or");
					else
						hql.append(" (");
					hql.append(" ").append(hqlPiece.getHql());
					hasFilter = true;
				}
			}
		}
		if(hasFilter)
			hql.append(" )");
		else
			hql.append(" 1=1");
		
		hqlAndParams.setHql(hql.toString());
		return hqlAndParams;
	}
	
}
