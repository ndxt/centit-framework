package com.centit.framework.core.dao;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DictionaryMapColumn implements Serializable {
    private static final long serialVersionUID = 1L;
    /**原始数据字段名*/
    private String fieldName;
    /**新增数据字段名*/
    private String mapFieldName;
    /**数据字典类别代码*/
    private String dictCatalog;

    private boolean isExpression;

    public DictionaryMapColumn(){
        fieldName = null;
        mapFieldName = null;
        isExpression = false;
    }

    /**
     * @param fn 原始数据字段名
     * @param mfn 新增数据字段名
     * @param catalog 数据字典类别代码
     */
    public DictionaryMapColumn(String fn, String mfn, String catalog){
        fieldName = fn;
        mapFieldName = mfn;
        dictCatalog = catalog;
        isExpression = false;
    }

    public static List<DictionaryMapColumn> mapAnnotation(String fn, DictionaryMap dictionaryMap){
        if(/*dictionaryMap.value()==null ||*/ dictionaryMap.value().length==0)
            return null;
        List<DictionaryMapColumn> columns = new ArrayList<>(dictionaryMap.value().length);
        String [] fieldNams = dictionaryMap.fieldName();
        int i=0;
        for(String sValue : dictionaryMap.value()){
            DictionaryMapColumn col = new DictionaryMapColumn();
            col.setFieldName(fn);
            col.setDictCatalog(sValue);
            col.setExpression(dictionaryMap.isExpression());
            col.setMapFieldName(
                /*fieldNams !=null &&*/ fieldNams.length>i && StringUtils.isNotBlank(fieldNams[i])?
                    fieldNams[i] : fn+ "Aux"+i);
            i++;
        }
        return columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryMapColumn)) return false;
        DictionaryMapColumn that = (DictionaryMapColumn) o;
        return Objects.equals(getFieldName(), that.getFieldName()) &&
            Objects.equals(getMapFieldName(), that.getMapFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFieldName(), getMapFieldName());
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMapFieldName() {
        return mapFieldName;
    }

    public void setMapFieldName(String mapFieldName) {
        this.mapFieldName = mapFieldName;
    }

    public String getDictCatalog() {
        return dictCatalog;
    }

    public void setDictCatalog(String dictCatalog) {
        this.dictCatalog = dictCatalog;
    }

    public boolean isExpression() {
        return isExpression;
    }

    public void setExpression(boolean expression) {
        isExpression = expression;
    }
}
