package com.centit.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.KeyValuePair;
import com.centit.support.database.QueryAndNamedParams;
import com.centit.support.database.QueryUtils;

public class TestQueryUtils {

	public static void printQueryAndNamedParams(QueryAndNamedParams qp) {
		System.out.println(qp.getQuery());
		for(Map.Entry<String, Object>ent : qp.getParams().entrySet()){
			System.out.print(ent.getKey());
			System.out.print("----");
			System.out.println(String.valueOf(ent.getValue()));
		}
	}
	
	public static void printDictionaryMap(Map<String,KeyValuePair<String,String>> m) {

		for(Map.Entry<String, KeyValuePair<String,String>>ent : m.entrySet()){
			System.out.print(ent.getKey());
			System.out.print("----");
			System.out.print(String.valueOf(ent.getValue().getKey()));
			System.out.print("----");
			System.out.println(String.valueOf(ent.getValue().getValue()));
		}
	}
	

	public static void testCreateDictionaryMap() {
		 Map<String,KeyValuePair<String,String>> m = 
				 DictionaryMapUtils.createDictionaryMapBuilder("F1", "V1", "D1")
			.addDictionaryDesc("F2", "V2", "D2")
			.addDictionaryDesc("F3", "V3", "D3").create();
		 printDictionaryMap(m);
	}
	
	public static void testGetParams() {
		
		String queryStatement = "select [(${p1.1}>2 && p2>2)|t1.a,] t2.b,t3.c "+
				"from [(${p1.1}>2  && p2>2)| table1 t1,] table2 t2,table3 t3 "+
				"where 1=1 [(${p1.1}>2  && p2>2)(p1.1:ps)| and t1.a=:ps][(isNotEmpty(${p1.1})&&isNotEmpty(p2)&&isNotEmpty(p3))(p2,p3:px)"
				+ "| and (t2.b> :p2 or t3.c >:px)] order by 1,2";
		System.out.println(QueryUtils.getSqlNamedParameters(queryStatement));
	}
	public static void testTranslateQuery() {
		List<String> filters = new ArrayList<String> ();
		filters.add("[table1.c] like {p1.1:ps}");
		filters.add("[table1.b] = {p5}");
		filters.add("[table4.b] = {p4}");	
		filters.add("([table2.f]={p2} and [table3.f]={p3})");

		Map<String,Object> paramsMap = new HashMap<String,Object>();		
		paramsMap.put("p1.1", "1");
		paramsMap.put("p2", "3");
		
		String queryStatement = "select t1.a,t2.b,t3.c "+
			"from table1 t1,table2 t2,table3 t3 "+
			"where 1=1 {table1:t1} {不认识} [也不认识] order by 1,2";
	
		printQueryAndNamedParams(QueryUtils.translateQuery(
				 queryStatement, filters,
				  paramsMap, true));
		
		queryStatement = "select t1.a,t2.b,t3.c "+
					"from table1 t1,table2 t2,table3 t3 "+
					"where 1=1 {table1:t1}{table9:t1}{table2:t2,table3:t3,table4:t1} order by 1,2";
		paramsMap.put("p3", "5");
		paramsMap.put("p4", "7");
		printQueryAndNamedParams(QueryUtils.translateQuery(
				 queryStatement, filters,
				  paramsMap, true));
		
		queryStatement = "select [(${p1.1}>2 && p2>2)|t1.a,] t2.b,t3.c "+
				"from [(${p1.1}>2  && p2>2)| table1 t1,] table2 t2,table3 t3 "+
				"where 1=1 [(${p1.1}>2  && p2>2)(p1.1:ps)| and t1.a=:ps][(isNotEmpty(${p1.1})&&isNotEmpty(p2)&&isNotEmpty(p3))(p2,p3:px)"
				+ "| and (t2.b> :p2 or t3.c >:px)] order by 1,2";

		printQueryAndNamedParams(QueryUtils.translateQuery(
				 queryStatement, filters,
				  paramsMap, true));
		
		paramsMap.put("p1.1", "5");
		queryStatement = "select [(${p1.1}>2 && p2>2)|t1.a,] t2.b,t3.c "+
				"from [(${p1.1}>2 && p2>2)| table1 t1,] table2 t2,table3 t3 "+
				"where 1=1 [(${p1.1}>2 && p2>2)(p1.1:ps)| and t1.a=:ps][p1.1,:p2,p3:px| and (t2.b> :p2 or t3.c >:px)] order by 1,2";
		printQueryAndNamedParams(QueryUtils.translateQuery(
				 queryStatement, filters,
				  paramsMap, true));
	}
	
	public static void testTemplate() {
		String queryStatement = "select [(${我是中国人@SINA}>2 && p2>2 )|t1.a,] t2.b,t3.c "+
				"from [(${p1.1}>2 && p2>2)| table1 t1,] table2 t2,table3 t3 "+
				"where t2.usercode = :userName  [(${p1.1}>2 && p2>2)(p5,:p9)| and t1.a=:ps][p3:px| and (t2.b> :p2 or t3.c >:px)] order by 1,2";
		System.out.println(StringBaseOpt.objectToString(
				QueryUtils.getSqlTemplateFiledNames(queryStatement)));
	}
	public static void main(String[] args) {
		testGetParams();//testTemplate();
		//CodeRepositoryUtil.loadExtendedSqlMap("D:/Projects/framework2.1/framework-sys-module2.1/src/main/resources/ExtendedSqlMap.xml");
		//System.out.println(CodeRepositoryUtil.getExtendedSql("QUERY_ID_1"));
	}
}
