package com.centit.framework.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.centit.support.algorithm.StringRegularOpt;

/**
 * 系统外置目录路径工具类
 *
 * @author sx
 * @create 2012-12-7
 */
public class SysParametersUtils {

    private static final Logger logger = LoggerFactory.getLogger(SysParametersUtils.class);

    private static Properties prop;

    /**
     * 常用参数在此添加对应枚举，并在下面添加对应静态方法
     *
     * @author sx
     * @create 2012-12-7
     */
    public static enum Parameter {
        APP_HOME("app.home"), LOG_HOME("dir.log"), 
        UPLOAD_HOME("dir.upload"), PUBLIC_FILE("dir.publicfile"), 
        INDEX_HOME("dir.index"), TEMP("dir.temp");

        private String value;

        private Parameter(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    
    /**
     * 获取应用的根目录
     *
     * @return
     */
    public static String getAppHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME)) ;
    }

    
    /**
     *  获取应用的临时目录
     *
     * @return
     */
    public static String getTempHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) 
        		 + getParameters(Parameter.TEMP)) ;
    }

    /**
     * 获取上传文件临时目录
     *
     * @return
     */
    public static String getUploadTempHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) + getParameters(Parameter.UPLOAD_HOME)
                + getParameters(Parameter.TEMP));
    }

    /**
     * 获取配置目录
     *
     * @return
     */
    public static String getConfigHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) +"/config");
    }
    
    /**
     * 获取日志目录
     *
     * @return
     */
    public static String getLogHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) + getParameters(Parameter.LOG_HOME));
    }

    /**
     * 获取上传文件目录
     *
     * @return
     */
    public static String getUploadHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) + getParameters(Parameter.UPLOAD_HOME));
    }

    /**
     * 获取索引文件目录
     *
     * @return
     */
    public static String getIndexHome() {
        return FilenameUtils.normalize(getParameters(Parameter.APP_HOME) + getParameters(Parameter.INDEX_HOME));
    }

    /**
     * 公共文件夹
     *
     * @return
     */
    public static String getPublicFileHome() {
        return FilenameUtils.normalize(getUploadHome() + getParameters(Parameter.PUBLIC_FILE));
    }

    public static String getParameters(Parameter p) {
        return loadProperties().getProperty(p.getValue());
    }


    public static String getStringValue(String key) {
        return loadProperties().getProperty(key);
    }

    public static String getStringValue(String key,String defaultValue) {
        return loadProperties().getProperty(key,defaultValue);
    }
    
    public static int getIntValue(String key) {
        return Integer.parseInt(loadProperties().getProperty(key));
    }
    
    public static int getIntValue(String key,int defaultValue) {
       String s = loadProperties().getProperty(key);
       if(StringRegularOpt.isNumber(s))
    	   return Integer.parseInt(s);
       return defaultValue;
    }
    
    public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(getStringValue(key));
	}
    
    public static boolean getBoolean(String key,boolean defaultValue) {
    	String sValue = getStringValue(key);
    	return StringUtils.isNotBlank(sValue)?
    		 Boolean.parseBoolean(sValue):defaultValue;
	}
    

    public static Properties loadProperties() {
        return loadProperties(false);
    }

    public static Properties loadProperties(boolean forceReload) {
        if (forceReload || null == prop) {
            prop = new Properties();
            try {
                InputStream resource = new ClassPathResource("system.properties").getInputStream();
                prop.load(resource);
            } catch (IOException e) {
                logger.error("获取系统参数出错！",e);
                //e.printStackTrace();
            }
        }

        return prop;
    }

}
