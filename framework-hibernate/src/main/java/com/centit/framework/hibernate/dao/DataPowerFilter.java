package com.centit.framework.hibernate.dao;

import com.alibaba.fastjson.JSONObject;
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

public class DataPowerFilter {
	/**
	 * 过滤条件中可以应用的数据，至少包括 userinfo 用户信息，unitinfo 用户主机构信息
	 */
	private Map<String,Object> sourceData;
	

	public void setSourceDatas(Map<String,Object> sourceData) {
		this.sourceData = sourceData;
	}
	
	public void setSourceFromJson(JSONObject sourceJson) {
		this.sourceData = sourceJson;
	}
	
	public void addSourceData(String name,Object data) {
		if(data==null)
			return;
		if(sourceData == null)
			sourceData = new HashMap<String,Object>();
		sourceData.put(name,data);
	}
	
	public void addSourceData(Object data) {
		if(data==null)
			return;
		if(sourceData == null)
			sourceData = new HashMap<String,Object>();
		sourceData.put(data.getClass().getSimpleName(),data);
	}
	
	public void addSourceDatas(Collection<Object> sourceData) {
		if(this.sourceData == null)
			this.sourceData = new HashMap<String,Object>();
		for(Object obj:sourceData){
			if(obj!=null)
				this.sourceData.put(obj.getClass().getSimpleName(), obj);
		}
	}
	
	public void addSourceDatas(Map<String,Object> paramMap) {
		if(this.sourceData == null)
			this.sourceData = new HashMap<String,Object>();
		this.sourceData.putAll(paramMap);
	}
	
	public void addSourceDatas(Object[] sourceData) {
		if(this.sourceData == null)
			this.sourceData = new HashMap<String,Object>();
		for(Object obj:sourceData){
			if(obj!=null)
				this.sourceData.put(obj.getClass().getSimpleName(), obj);
		}
	}
	
	public void setSourceDatas(Collection<Object> sourceData) {
		this.sourceData = new HashMap<String,Object>();
		for(Object obj:sourceData)
			this.sourceData.put(obj.getClass().getSimpleName(), obj);
	}
	
	public void setSourceDatas(Object[] sourceData) {
		this.sourceData = new HashMap<String,Object>();
		for(Object obj:sourceData)
			this.sourceData.put(obj.getClass().getSimpleName(), obj);
	}
	
	
	/**
	 * HQL注解中的配置信息
	 */
	private static Map<String,Map<String, String /*KeyValuePair<String,Integer>*/>> hqlMetaData;
	
	/*public static int mapTypeToInt(Class<?> type){
		if(type.getSuperclass().equals(Number.class) || type.equals(Number.class))
			return 1;
		if(type.equals(java.util.Date.class) || type.equals(java.sql.Date.class))
			return 2;
		return 3;
	}*/
	
	/**
	 * 获取表达式敌营的值；这个地方需要根据业务的类型多样性和具体需求不断的完善
	 * @param expression
	 * @return
	 */
	private Object attainExpressionValue(String expression){
		return ReflectionOpt.attainExpressionValue(sourceData, expression);
	}
	
	private Object getObjectFieldValue(Object obj , String valueField){
		if(obj ==null || valueField==null || "".equals(valueField))
			return null;
		String [] fs = valueField.split("\\.");
		Object retObj = obj;
		
		for(int i=0;i<fs.length;i++){
			if(retObj instanceof Map ){
				@SuppressWarnings("unchecked")
				Map<String ,Object> objMap = (Map<String ,Object>) retObj;
				retObj = objMap.get(fs[i]);
			}else{
				retObj = ReflectionOpt.getFieldValue(retObj, fs[i]);
			}
			
			if(retObj==null)
				return null;
		}		
		return retObj;
	}
	
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
	
	
	private static class DataPowerFilterTranslater implements IFilterTranslater{
    	private Map<String,String> tableAlias;
    	private boolean toSql;
    	private boolean jointSql;
    	private DataPowerFilter dataPowerFilter;
    	
    	public DataPowerFilterTranslater(boolean toSql,boolean jointSql,DataPowerFilter dataPowerFilter)
    	{
    		this.tableAlias = null;
    		this.toSql = toSql;
    		this.jointSql = jointSql;
    		this.dataPowerFilter = dataPowerFilter;
    	}

		@Override
		public void setTableAlias(Map<String, String> tableAlias) {
			this.tableAlias = tableAlias;
		}
		
		@Override
		public String translateColumn(String columnDesc){
			if(tableAlias==null||columnDesc==null||tableAlias.size()==0)
				return null;

			int n = columnDesc.indexOf('.');
			if(n<0){
				return tableAlias.get(columnDesc);
			}
			
			String poClassName = columnDesc.substring(0,n);
			String alias = tableAlias.get(poClassName);
			
			if(alias==null)
				return null;
			if(toSql){
				Map<String,String> poClassMate = DataPowerFilter.getHqlMetaData().get(poClassName);
				if(poClassMate==null)
					return null;
				String fieldName = poClassMate.get(columnDesc.substring(n+1));//.getKey();
				return "".equals(alias)? fieldName:alias+'.'+  fieldName;
			}else{
				return "".equals(alias)? columnDesc.substring(n+1):alias+'.'+  columnDesc.substring(n+1);
			}
		}
		
		@Override
    	public KeyValuePair<String,Object> translateParam(String paramName){
 
			Object obj = dataPowerFilter.attainExpressionValue(paramName);
			
			if(obj==null)
				return null;
			if(obj instanceof String){
				if(StringUtils.isBlank((String)obj))
					return null;
			}
			
			if(jointSql){
				return new KeyValuePair<String,Object>(
						QueryUtils.buildObjectStringForQuery(obj),null);					
			}else{
				return new KeyValuePair<String,Object>(
						paramName,obj);
			}
    	}

		@Override
		public String getVarValue(String varName) {			
			Object res = dataPowerFilter.attainExpressionValue(varName);
			if(res==null)
				return "\"\"";
			return StringRegularOpt.quotedString(StringBaseOpt.objectToString(res));	
		}

		@Override
		public String getLabelValue(String labelName) {
			return getVarValue(labelName);
		}

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
	

	/**
	 * 
	 * @param queryStatement
	 * @param filters
	 * @param toSql	是否为sql语句，否：表示hql ，是：表示 sql
	 * @param jointSql 变量内嵌在语句中，不用参数
	 * @param isUnion 多个过滤之间是否是并集 
	 * @return
	 */
	public QueryAndNamedParams translateQuery(String queryStatement,Collection<String> filters,
			boolean toSql,boolean jointSql, boolean isUnion){
		
		return QueryUtils.translateQuery(queryStatement,
				filters, isUnion, new DataPowerFilterTranslater(toSql, jointSql,this));
	}
	
	/**
	 * 权限查询，不同的条件取并集
	 * @param queryStatement
	 * @param filters
	 * @return
	 */
	public QueryAndNamedParams translateHqlQuery
			(String queryStatement,Collection<String> filters){
		return translateQuery(queryStatement, filters,
				false,false, true);
	}
	
	/**
	 * 权限查询，不同的条件取并集
	 * @param queryStatement
	 * @param filters
	 * @return
	 */
	public QueryAndNamedParams translateSqlQuery
			(String queryStatement,Collection<String> filters){
		return translateQuery(queryStatement, filters,
				true,false, true);
	}
	
	/**
	 * 视图过滤条件查询，不同的过滤条件取交接
	 * @param queryStatement
	 * @param filters
	 * @return
	 */
	public QueryAndNamedParams translateSqlFilterQuery
			(String queryStatement,Collection<String> filters){
		return translateQuery(queryStatement, filters,
				true,false, false);
	}
	
	public int checkObjectFilter(Object obj,String filter){
		String poClassName = obj.getClass().getSimpleName();
		Lexer varMorp = new Lexer();
		varMorp.setFormula(filter);
		StringBuilder checkStatement= new StringBuilder();
		String sWord = varMorp.getAWord();
		int prePos = 0;
		while( sWord!=null && ! sWord.equals("") ){
			if( sWord.equals("[")){
				int curPos = varMorp.getCurrPos();
				if(curPos-1>prePos)
					checkStatement.append( filter.substring(prePos, curPos-1));					
				varMorp.seekTo(']');
				prePos = varMorp.getCurrPos();				
				String columnDesc =  filter.substring(curPos,prePos-1).trim();
				int n = columnDesc.indexOf('.');
				if(n<0)
					return 0;
				
				String tempClassName = columnDesc.substring(0,n);
				if(!poClassName.equals(tempClassName))
					return 0;
				
				Object fieldValue = getObjectFieldValue(obj, columnDesc.substring(n+1));				
				
				checkStatement.append(QueryUtils.buildObjectStringForQuery(fieldValue));
			
			}else if( sWord.equals("{")){
				int curPos = varMorp.getCurrPos();
				if(curPos-1>prePos)
					checkStatement.append( filter.substring(prePos, curPos-1));				
				varMorp.seekTo('}');
				prePos = varMorp.getCurrPos();
				String valueDesc =  filter.substring(curPos,prePos-1).trim();
				Object fieldValue = attainExpressionValue(valueDesc);
				
				checkStatement.append(QueryUtils.buildObjectStringForQuery(fieldValue));
			}			
			sWord = varMorp.getAWord();
		}
		checkStatement.append(filter.substring(prePos));
		
		Formula fCalcCond = new Formula();
        return StringRegularOpt.isTrue(fCalcCond.calculate(checkStatement.toString()))?1:-1;
	}
	
	public boolean checkObject(Object obj,Collection<String> filters){		
		if(filters==null) return true;
		
		int nFalse=0;
		for(String filter : filters){
			int nRes =  checkObjectFilter(obj,filter);
			if(nRes==1)
				return true;
			else if(nRes==-1)
				nFalse++;
		}
		return nFalse>0?false:true;
	}
	
}
