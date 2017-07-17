package com.centit.framework.core.controller;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;
/**
 * 调用htmlEscape 转换属性中的  > < " ' 等等html 标签 防止XSS攻击
 * @author codefan
 *
 */
public class StringPropertiesEditor extends PropertiesEditor {
    
	@Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(trimWhile) {
            String noSpaceText = StringUtils.trimWhitespace(text);
            setValue(StringUtils.hasText(noSpaceText) ? 
            		HtmlUtils.htmlEscape(noSpaceText) : noSpaceText);
        }else
        	setValue(StringUtils.hasText(text) ? HtmlUtils.htmlEscape(text) : text);
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

    public StringPropertiesEditor(boolean trimWhile) {
        this.trimWhile = trimWhile;
    }

    public StringPropertiesEditor() {
    	 this.trimWhile = false;
    }
}
