package com.centit.framework.filter;

import java.util.regex.Pattern;

/**
 * @author hx
 * 安全过滤配置信息类
 */
public class XSSSecurityConfig {
	
	/**
	 * 配置文件标签 isCheckHeader
	 */
	public static final String IS_CHECK_HEADER = "isCheckHeader";

	/**
	 * 配置文件标签 isCheckParameter
	 */
	public static final String IS_CHECK_PARAMETER = "isCheckParameter";

	/**
	 * 配置文件标签 isLog
	 */
	public static final String IS_LOG = "isLog";

	/**
	 * 配置文件标签 isChain
	 */
	public static final String IS_CHAIN = "isChain";

	/**
	 * 配置文件标签 replace
	 */
	public static final String REPLACE = "replace";

	/**
	 * 配置文件标签 regexList
	 */
	public static final String REGEX_LIST = "regexList";

	/**
	 * 替换非法字符的字符串
	 */
	public static final String REPLACEMENT = "";

	/**
	 * FILTER_ERROR_PAGE:过滤后错误页面
	 * 406   （不接受） 无法使用请求的内容特性响应请求的网页。 
	 */
	public static final String FILTER_ERROR_PAGE = "/system/exception/406";
	
	/**
	 * 配置文件标签refererAllowUrlExtra
	 */
	public static final String REFERER_ALLOW_URL_EXTRA = "refererAllowUrlExtra";
	
	
	private static final XSSSecurityConfig instance= new XSSSecurityConfig();
	
	private XSSSecurityConfig(){
		
	}
	public static XSSSecurityConfig getConfig(){
		return instance;
	}
	/**
	 * CHECK_HEADER：是否开启header校验
	 */
	public boolean isCheckHeader; 
	
	/**
	 * CHECK_PARAMETER：是否开启parameter校验
	 */
	public boolean isCheckParameter;
	
	/**
	 * IS_LOG：是否记录日志
	 */
	public boolean isLog;
	
	/**
	 * IS_LOG：是否中断操作
	 */
	public boolean isChain;
	
	/**
	 * REPLACE：是否开启替换
	 */
	public boolean isReplace;
	
	/**
     * 允许的refererUrl上下文补充
     */
	public String refererAllowUrlExtra;
	
	/**
	 * REGEX：校验正则表达式
	 */
	public String regex;
	
	 /**
     * 特殊字符匹配
     */
	public Pattern xssPattern;

	
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public Pattern getXssPattern() {
		return xssPattern;
	}
	public void setXssPattern(Pattern xssPattern) {
		this.xssPattern = xssPattern;
	}
	public boolean isCheckHeader() {
		return isCheckHeader;
	}

	public void setCheckHeader(boolean isCheckHeader) {
		this.isCheckHeader = isCheckHeader;
	}

	public boolean isCheckParameter() {
		return isCheckParameter;
	}

	public void setCheckParameter(boolean isCheckParameter) {
		this.isCheckParameter = isCheckParameter;
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	public boolean isChain() {
		return isChain;
	}

	public void setChain(boolean isChain) {
		this.isChain = isChain;
	}

	public boolean isReplace() {
		return isReplace;
	}

	public void setReplace(boolean isReplace) {
		this.isReplace = isReplace;
	}

	public String getRefererAllowUrlExtra() {
		return refererAllowUrlExtra;
	}

	public void setRefererAllowUrlExtra(String refererAllowUrlExtra) {
		this.refererAllowUrlExtra = refererAllowUrlExtra;
	}
}
