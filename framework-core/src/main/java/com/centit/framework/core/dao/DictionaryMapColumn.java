package com.centit.framework.core.dao;

import java.io.Serializable;
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

    public DictionaryMapColumn(String fn, DictionaryMap dictionaryMap){
        fieldName = fn;
        mapFieldName = dictionaryMap.fieldName();
        dictCatalog = dictionaryMap.value();
        isExpression = dictionaryMap.isExpression();
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
