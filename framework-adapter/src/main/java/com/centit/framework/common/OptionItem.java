package com.centit.framework.common;

import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class OptionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private Serializable value;
    private Serializable group;

    public OptionItem(){

    }
    public OptionItem(String name){
        this.name = name;
        this.value = name;
    }

    public OptionItem(String name,Serializable value){
        this.name = name;
        this.value = value;
    }

    public OptionItem(String name,Serializable value,Serializable group){
        this.name = name;
        this.value = value;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public Serializable getGroup() {
        return group;
    }

    public void setGroup(Serializable group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        if(name==null)
            return 0;
        return name.hashCode();  
    }  
      
    @Override  
    public boolean equals(Object obj) {
        if(this==obj || value==obj)
            return true;
        if(obj==null)
            return false;

        if(obj instanceof String){
            return StringUtils.equals(StringBaseOpt.objectToString(value),
                    (String)obj);
        }

        if(obj instanceof OptionItem){
            return StringUtils.equals(StringBaseOpt.objectToString(value),
                    StringBaseOpt.objectToString(((OptionItem)obj).getValue()));
        }

        return StringUtils.equals(StringBaseOpt.objectToString(value),
                StringBaseOpt.objectToString(obj));
    }
    
    
}
