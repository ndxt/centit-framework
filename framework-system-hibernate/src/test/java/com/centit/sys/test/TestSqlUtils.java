package com.centit.sys.test;

import com.centit.support.database.QueryUtils;

public class TestSqlUtils {

    public static void main(String[] args) {
   
     
        System.out.println(QueryUtils.trimSqlOrderByField(
                ""
                ));    
        System.out.println(QueryUtils.trimSqlOrderByField(
                ",,,,,    ,,   "
                )); 
        System.out.println(QueryUtils.trimSqlOrderByField(
                ",,,,,    ,a,   "
                )); 
        System.out.println(QueryUtils.trimSqlOrderByField(
                ",,, adb desc ,adc asc def,defe,tp,,,"
                ));

        
        /* String sql = "Select a,b,c  d    , count(1) ,(select count(1) from b) from table join (select * from table3) tab2";
        List<String> sqls = QueryUtils.splitSqlByFields(sql);
        List<String> fileds = QueryUtils.getSqlFiledNames(sql);
        
        for(String s:sqls)
            System.out.println(s);
        System.out.println("----------------------");
        for(String s:fileds)
            System.out.println(s);*/
        
    }

}
