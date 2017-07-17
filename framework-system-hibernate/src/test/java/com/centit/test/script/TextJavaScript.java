package com.centit.test.script;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
/**
 * 测试javascript的引擎，jdk 1.6 引入 Rhino 引擎。
 * 
 * @author codefan
 * @create 2015年10月9日
 * @version
 */
public class TextJavaScript {
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        // we are using the rhino javascript engine
        ScriptEngine engine = mgr.getEngineByName("javascript");

        // pass a Java collection to javascript 
        List <String> list1 = Arrays.asList
          ("Homer", "Bart", "Marge", "Maggie", "Lisa");
        engine.put("list1", list1);
        
        String jsCode = 
          "var index; " 
          + "var values =list1.toArray();"
          + "println('*** Java object to Javascript');"
          + "for(index in values) {" 
          + "  println(values[index]);"
          + "}";
        try {
          engine.eval(jsCode);
        }
        catch (ScriptException se) {
          se.printStackTrace();
        }

        // pass a collection from javascript to java
        jsCode = 
            "importPackage(java.util);"
          + "var list2 = Arrays.asList(['Moe', 'Barney', 'Ned']); ";
        try {
          engine.eval(jsCode);
        }
        catch (ScriptException se) {
          se.printStackTrace();
        }
        List <String> list2 = (List<String>) engine.get("list2");
        System.out.println("*** Javascript object to Java");
        for (String val : list2) {
          System.out.println(val);
        }
    }
}
/*
 输出 :
    *** Java object to Javascript
    Homer
    Bart
    Marge
    Maggie
    Lisa
    *** Javascript object to Java
    Moe
    Barney
    Ned
*/
