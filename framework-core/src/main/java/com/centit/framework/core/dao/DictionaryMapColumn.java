package com.centit.framework.core.dao;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class DictionaryMapColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String mapFieldName;

    private Map<String,String> dictionaryMap;

    public DictionaryMapColumn(){
        fieldName = null;
        dictionaryMap = null;
        mapFieldName = null;
    }

    public DictionaryMapColumn(String fn){
        fieldName = fn;
        dictionaryMap = null;
        mapFieldName = null;
    }

    public DictionaryMapColumn(String fn,String mfn,Map<String,String> dm){
        fieldName = fn;
        dictionaryMap = dm;
        mapFieldName = mfn;
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

    public String mapDictionaryValue(String key) {
        if(dictionaryMap==null){
            return key;
        }
        String value = dictionaryMap.get(key);
        return value==null?key:value;
    }

    public void setDictionaryMap(Map<String, String> dictionaryMap) {
        this.dictionaryMap = dictionaryMap;
    }

}
