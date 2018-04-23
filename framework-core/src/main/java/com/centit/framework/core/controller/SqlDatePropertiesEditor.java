package com.centit.framework.core.controller;

import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.PropertiesEditor;

import java.sql.Date;
import java.util.regex.Pattern;

public class SqlDatePropertiesEditor extends PropertiesEditor {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isBlank(text)) {
            super.setValue(null);
        } else if(Pattern.matches("\\d+", text)) {
            setValue(new Date(Long.parseLong(text)));
        }else {
            Date value = DatetimeOpt.convertToSqlDate(
                    DatetimeOpt.smartPraseDate(text));
            setValue(value);
        }
    }

}
