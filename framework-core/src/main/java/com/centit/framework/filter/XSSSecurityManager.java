/**
 * 
 */
package com.centit.framework.filter;

import java.util.Iterator;
import java.util.regex.Pattern;

import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author hx
 * 安全过滤配置管理类，由XSSSecurityManger修改
 */
public class XSSSecurityManager {
	
	private static Logger logger = LoggerFactory.getLogger(XSSSecurityManager.class);
	
		
    private XSSSecurityManager(){
        //不可被实例化
    }
    
    public static void init(FilterConfig config){
    	logger.info("XSSSecurityManager init(FilterConfig config) begin");
    	//初始化过滤配置文件
        String xssPath = config.getServletContext().getRealPath("/")
        		+ config.getInitParameter("securityconfig");
        
        // 初始化安全过滤配置
        try {
			initConfig(xssPath);
		} catch (DocumentException e) {
			logger.error("安全过滤配置文件xss_security_config.xml加载异常",e);
		}
		logger.info("XSSSecurityManager init(FilterConfig config) end");
    }
    
    /**
     * 读取安全审核配置文件xss_security_config.xml
     * 设置XSSSecurityConfig配置信息
     * @param path 配置文件地址 eg C:/apache-tomcat-6.0.33/webapps/security_filter/WebRoot/config/xss/xss_security_config.xml
     * @return initConfig
     * @throws DocumentException DocumentException
     */
	@SuppressWarnings("unchecked")
	public static boolean initConfig(String path) throws DocumentException {
		logger.info("XSSSecurityManager.initConfig(String path) begin");
		Element superElement = new SAXReader().read(path).getRootElement();
		XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
		cfg.setCheckHeader(new Boolean(getEleValue(superElement,XSSSecurityConfig.IS_CHECK_HEADER)));
		cfg.setCheckParameter(new Boolean(getEleValue(superElement,XSSSecurityConfig.IS_CHECK_PARAMETER)));
		cfg.setLog(new Boolean(getEleValue(superElement,XSSSecurityConfig.IS_LOG)));
		cfg.setChain(new Boolean(getEleValue(superElement,XSSSecurityConfig.IS_CHAIN)));
		cfg.setReplace(new Boolean(getEleValue(superElement,XSSSecurityConfig.REPLACE)));
		cfg.setRefererAllowUrlExtra(getEleValue(superElement,XSSSecurityConfig.REFERER_ALLOW_URL_EXTRA));
		
		Element regexEle = superElement.element(XSSSecurityConfig.REGEX_LIST);
		
		if(regexEle != null){
			Iterator<Element> regexIt = regexEle.elementIterator();
			StringBuffer tempStr = new StringBuffer("^");
			//xml的cdata标签传输数据时，会默认在\前加\，需要将\\替换为\
			while(regexIt.hasNext()){
				Element regex = (Element)regexIt.next();
				String tmp = regex.getText();
				tmp = tmp.replaceAll("\\\\\\\\", "\\\\");
	        	tempStr.append(tmp);
	        	tempStr.append("|");
			}
	        if(tempStr.charAt(tempStr.length()-1)=='|'){
	        	cfg.setRegex( tempStr.substring(0, tempStr.length()-1)+"$");
	        	cfg.setXssPattern(Pattern.compile(cfg.getRegex()));
	        	logger.info("安全匹配规则"+cfg.getRegex());
	        }else{
	        	logger.error("安全过滤配置文件加载失败:正则表达式异常 "+tempStr.toString());
	        	return false;
	        }
		}else{
			logger.error("安全过滤配置文件中没有 "+XSSSecurityConfig.REGEX_LIST+" 属性");
			return false;
		}		
		
		logger.info("XSSSecurityManager.initConfig(String path) end");
		return true;

	}
    
	/**
	 * 从目标element中获取指定标签信息，若找不到该标签，记录错误日志
	 * @param element 目标节点
	 * @param tagName 制定标签
	 * @return 
	 */
	private static String getEleValue(Element element, String tagName){
		if (isNullStr(element.elementText(tagName))){
			logger.error("安全过滤配置文件中没有 "+XSSSecurityConfig.REGEX_LIST+" 属性");
		}
		return element.elementText(tagName);
	}
	
    /**
     * 对非法字符进行替换
     * @param text String
     * @return 对非法字符进行替换
     */
    public static String securityReplace(String text){
    	if(isNullStr(text)){
    		return text;
    	}else{
    		XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
    		return text.replaceAll(cfg.getRegex(), XSSSecurityConfig.REPLACEMENT);
    	}
    }
    
    /**
     * 匹配字符是否含特殊字符
     * @param text String
     * @return 匹配字符是否含特殊字符
     */
    public static boolean matches(String text){
    	if(text==null){
    		return false;
    	}
    	XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
    	return cfg.getXssPattern().matcher(text).matches();
    }
    
    /**
     * 释放关键信息
     */
    public static void destroy(){
    	logger.info("XSSSecurityManager.destroy() begin");
        //XSS_PATTERN = null;
        //REGEX = null;
        logger.info("XSSSecurityManager.destroy() end");
    }
    
    /**
     * 判断是否为空串，建议放到某个工具类中
     * @param value 匹配字符是否含特殊字符
     * @return 判断是否为空串
     */
    public static boolean isNullStr(String value){
    	return value == null || value.trim().equals("");
    }
}
