package com.centit.framework.core.controller;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class UtilDateDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {

    public final static UtilDateDeserializer instance = new UtilDateDeserializer();

    @SuppressWarnings("unchecked")
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {

        if (val == null) {
            return null;
        }

        if (val instanceof java.util.Date) {
            return (T) val;
        } else if (val instanceof Number) {
            return (T) new java.util.Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (StringUtils.isBlank(strVal)) {
                return null;
            } else {
               return (T) ( DatetimeOpt.smartPraseDate(strVal));
            }
        }
        return null;
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}

