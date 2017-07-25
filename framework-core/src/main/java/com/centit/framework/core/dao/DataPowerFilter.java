package com.centit.framework.core.dao;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	 * 获取表达式敌营的值；这个地方需要根据业务的类型多样性和具体需求不断的完善
	 * @param expression String
	 * @return 表达式对应的值
	 */
	public Object attainExpressionValue(String expression){
		return ReflectionOpt.attainExpressionValue(sourceData, expression);
	}

	
	protected static class DataPowerFilterTranslater implements IFilterTranslater{
    	private Map<String,String> tableAlias;
    	private boolean jointSql;
    	private DataPowerFilter dataPowerFilter;
    	
    	public DataPowerFilterTranslater(boolean toSql,boolean jointSql,DataPowerFilter dataPowerFilter)
    	{
    		this.tableAlias = null;
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
			
			return "".equals(alias)? columnDesc.substring(n+1):alias+'.'+  columnDesc.substring(n+1);
			
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
	
	/**
	 * 
	 * @param queryStatement queryStatement
	 * @param filters Collection filters
	 * @param toSql	是否为sql语句，否：表示hql ，是：表示 sql
	 * @param jointSql 变量内嵌在语句中，不用参数
	 * @param isUnion 多个过滤之间是否是并集 
	 * @return translateQuery
	 */
	public QueryAndNamedParams translateQuery(String queryStatement,Collection<String> filters,
			boolean toSql,boolean jointSql, boolean isUnion){
		
		return QueryUtils.translateQuery(queryStatement,
				filters, isUnion, new DataPowerFilterTranslater(toSql, jointSql,this));
	}
	
	
	/**
	 * 权限查询，不同的条件取并集
	 * @param queryStatement queryStatement
	 * @param filters Collection filters
	 * @return translateSqlQuery
	 */
	public QueryAndNamedParams translateSqlQuery
			(String queryStatement,Collection<String> filters){
		return translateQuery(queryStatement, filters,
				true,false, true);
	}
	
	/**
	 * 视图过滤条件查询，不同的过滤条件取交接
	 * @param queryStatement queryStatement
	 * @param filters Collection filters
	 * @return translateSqlFilterQuery
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
				
				Object fieldValue = ReflectionOpt.attainExpressionValue(obj, columnDesc.substring(n+1));
				
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
