package com.centit.framework.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import com.centit.support.algorithm.DatetimeOpt;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatetimeSerializer implements ObjectSerializer {

    public final static DatetimeSerializer instance = new DatetimeSerializer();
    
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            out.writeNull();
            return;
        }
        
        Date date;
        if (object instanceof Date) {
            date = (Date) object;
        } else {
            date = TypeUtils.castToDate(object);
        }
        
        if (out.isEnabled(SerializerFeature.WriteDateUseDateFormat)) {
            DateFormat format = serializer.getDateFormat();
            if (format == null) {
                format = new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT);
            }
            String text = format.format(date);
            out.writeString(text);
            return;
        }
        
        if (out.isEnabled(SerializerFeature.WriteClassName)) {
            if (object.getClass() != fieldType) {
                if (object.getClass() == java.util.Date.class) {
                    out.write("new Date(");
                    out.writeLong(((Date) object).getTime());
                    out.write(')');
                } else {
                    out.write('{');
                    out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
                    serializer.write(object.getClass().getName());
                    out.writeFieldValue(',', "val", ((Date) object).getTime());
                    out.write('}');
                }
                return;
            }
        }

        long time = date.getTime();
        if (serializer.isEnabled(SerializerFeature.UseISO8601DateFormat)) {
            if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.append('\'');
            } else {
                out.append('\"');
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int millis = calendar.get(Calendar.MILLISECOND);

            char[] buf;
            if (millis != 0) {
                buf = "0000-00-00T00:00:00.000".toCharArray();
                IOUtils.getChars(millis, 23, buf);
                IOUtils.getChars(second, 19, buf);
                IOUtils.getChars(minute, 16, buf);
                IOUtils.getChars(hour, 13, buf);
                IOUtils.getChars(day, 10, buf);
                IOUtils.getChars(month, 7, buf);
                IOUtils.getChars(year, 4, buf);

            } else {
                if (second == 0 && minute == 0 && hour == 0) {
                    buf = "0000-00-00".toCharArray();
                    IOUtils.getChars(day, 10, buf);
                    IOUtils.getChars(month, 7, buf);
                    IOUtils.getChars(year, 4, buf);
                } else {
                    buf = "0000-00-00T00:00:00".toCharArray();
                    IOUtils.getChars(second, 19, buf);
                    IOUtils.getChars(minute, 16, buf);
                    IOUtils.getChars(hour, 13, buf);
                    IOUtils.getChars(day, 10, buf);
                    IOUtils.getChars(month, 7, buf);
                    IOUtils.getChars(year, 4, buf);
                }
            }
            
            out.write(buf);
            
            int timeZone = calendar.getTimeZone().getRawOffset()/(3600*1000);
            if (timeZone == 0) {
                out.append("Z");
            } else if (timeZone > 0) {
                out.append("+").append(String.format("%02d", timeZone)).append(":00");
            } else {
                out.append("-").append(String.format("%02d", -timeZone)).append(":00");
            }

            if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.append('\'');
            } else {
                out.append('\"');
            }
        } else {
            out.writeString(DatetimeOpt.convertDatetimeToString(date));
        }
    }
}
