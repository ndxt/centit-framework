package com.centit.framework.hibernate.controller;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.util.StringUtils;

import com.centit.framework.hibernate.common.RichText;


public class RichTextPropertiesEditor extends PropertiesEditor {
    
	@Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(trimWhile) {
      		setValue(new RichText(StringUtils.trimWhitespace(text)));
        }else
        	setValue(new RichText(text));
    }

    @Override
    public String getAsText() {
       Object obj = getValue();
       if(obj==null)
    	   return null;
       return obj.toString();
    }

    private boolean trimWhile;

    public void setTrimWhile(boolean trimWhile) {
        this.trimWhile = trimWhile;
    }

    public RichTextPropertiesEditor(boolean trimWhile) {
        this.trimWhile = trimWhile;
    }

    public RichTextPropertiesEditor() {
    	 this.trimWhile = false;
    }
}
