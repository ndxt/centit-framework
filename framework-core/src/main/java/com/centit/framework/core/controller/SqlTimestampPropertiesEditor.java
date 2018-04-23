package com.centit.framework.core.controller;

import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.PropertiesEditor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

public class SqlTimestampPropertiesEditor extends PropertiesEditor {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isBlank(text)) {
            super.setValue(null);
        } else if(Pattern.matches("\\d+", text)) {
            setValue(new Timestamp(Long.parseLong(text)));
        }else {
            Date dt = DatetimeOpt.smartPraseDate(text);
            if(dt!=null){
                Timestamp value = new Timestamp(dt.getTime() );
                setValue(value);
            }else
                setValue(null);
        }
    }

}
