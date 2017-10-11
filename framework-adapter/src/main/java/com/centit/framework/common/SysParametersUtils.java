package com.centit.framework.common;

import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 系统外置目录路径工具类
 *
 * @author sx
 * date 2012-12-7
 */
public class SysParametersUtils {

    private static final Logger logger = LoggerFactory.getLogger(SysParametersUtils.class);

    private static Properties prop;

    /**
     * 常用参数在此添加对应枚举，并在下面添加对应静态方法
     *
     * @author sx
     * date 2012-12-7
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
     * @return String 根目录
     */
    public static String getAppHome() {
        return getParameters(Parameter.APP_HOME) ;
    }

    
    /**
     *  获取应用的临时目录
     *
     * @return String 应用的临时目录
     */
    public static String getTempHome() {
        return getParameters(Parameter.APP_HOME)
                 + getParameters(Parameter.TEMP) ;
    }

    /**
     * 获取上传文件临时目录
     *
     * @return String 上传文件临时目录
     */
    public static String getUploadTempHome() {
        return getTempHome();
    }

    /**
     * 获取配置目录
     *
     * @return String 配置目录
     */
    public static String getConfigHome() {
        return getParameters(Parameter.APP_HOME) +"/config";
    }
    
    /**
     * 获取日志目录
     *
     * @return String 日志目录
     */
    public static String getLogHome() {
        return getParameters(Parameter.APP_HOME) + getParameters(Parameter.LOG_HOME);
    }

    /**
     * 获取上传文件目录
     *
     * @return String 上传文件目录
     */
    public static String getUploadHome() {
        return getParameters(Parameter.APP_HOME) + getParameters(Parameter.UPLOAD_HOME);
    }

    /**
     * 获取索引文件目录
     *
     * @return String 索引文件目录
     */
    public static String getIndexHome() {
        return getParameters(Parameter.APP_HOME) + getParameters(Parameter.INDEX_HOME);
    }

    /**
     * 公共文件夹
     *
     * @return String 公共文件夹
     */
    public static String getPublicFileHome() {
        return getUploadHome() + getParameters(Parameter.PUBLIC_FILE);
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
           return NumberBaseOpt.parseInteger(s,defaultValue);
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

    /**
     *读取配置文件信息
     * @param forceReload 是否强制加载
     * @return Properties 配置文件信息
     */
    public static Properties loadProperties(boolean forceReload) {
        if (forceReload || null == prop) {
            prop = new Properties();
            try {
                InputStream resource = SysParametersUtils
                        .class.getResourceAsStream("/system.properties");
                //new ClassPathResource("system.properties").getInputStream();
                if(resource==null)
                    resource = ClassLoader.getSystemResourceAsStream("/system.properties");
                prop.load(resource);
            } catch (IOException e) {
                logger.error("获取系统参数出错！",e);
                //e.printStackTrace();
            }
        }

        return prop;
    }

}
