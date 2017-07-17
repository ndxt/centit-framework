package com.centit.framework.core.controller;

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.centit.support.algorithm.DatetimeOpt;

public class SmartDateFormat extends SimpleDateFormat{

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(SmartDateFormat.class);
	public SmartDateFormat() {
        super();
    }

    public SmartDateFormat(String pattern)
    {
    	super(pattern);
    }


    public SmartDateFormat(String pattern, Locale locale)
    {
    	super(pattern,locale);
    }

    public SmartDateFormat(String pattern, DateFormatSymbols formatSymbols)
    {
    	super(pattern,formatSymbols);
    }
	
	@Override
	public Date parse(String source, ParsePosition pos){
		if (StringUtils.isBlank(source)) {
            return null;
		}
		
		int start = pos.getIndex();
		int errorIndex =  pos.getErrorIndex();
		Date parseDate = null;
		try{
			parseDate =  super.parse(source, pos);
		}catch(RuntimeException e){
			logger.error(e.getMessage(),e);
			parseDate = null;
		}
		if(parseDate!=null)
			return parseDate;
		
		pos.setErrorIndex(errorIndex);
		
		int nlen = source.length();
        if(Pattern.matches("\\d+", source)) {
        	pos.setIndex(start + nlen);
            return new Date(Long.parseLong(source));
        }else {
        	pos.setIndex(start + nlen);
            return DatetimeOpt.smartPraseDate(source);
        }
	}
}
