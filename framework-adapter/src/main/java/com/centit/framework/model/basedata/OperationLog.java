package com.centit.framework.model.basedata;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.centit.support.algorithm.ReflectionOpt;


public class OperationLog implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    /**
     * 操作日志
     */
    public static final String LEVEL_INFO = "0";
    /**
     * 错误提示
     */
    public static final String LEVEL_ERROR = "1";
    private static Logger logger = LoggerFactory.getLogger(OperationLog.class);
    /**
     * 系统日志操作
     */
    public static final String P_OPT_LOG_METHOD = "P_OPT_LOG_METHOD";
    public static final String P_OPT_LOG_METHOD_C = "create";
    public static final String P_OPT_LOG_METHOD_U = "update";
    public static final String P_OPT_LOG_METHOD_D = "delete";

  
    private String logLevel = LEVEL_INFO;
  
    /**
     * 操作用户
     */
    private String userCode;
    /**
     * 操作时间
     */
    private Date optTime;
    /**
     * 操作业务编号
     */
    private String optId;
    /**
     * 业务操作方法
     */
    private String optMethod;
    /**
     * 业务对象组件，复合主键用&连接格式与url参数类似
     */
    private String optTag;
    /**
     * 日志内容描述
     */
    private String optContent;
    /**
     * 更新前旧值，json格式，这个字段不是必须的
     */
    private String newValue; 
    /**
     * 更新后新值，json格式，这个字段不是必须的
     */
    private String oldValue; 


    public OperationLog() {
        this.logLevel = LEVEL_INFO;
    }
    
    public OperationLog(String userCode, String optId, String optTag, String optmethod, String optcontent) {
        this.logLevel = LEVEL_INFO;
        this.userCode = userCode;
        this.optId = optId;
        this.optTag = optTag;
        this.optMethod = optmethod;
        this.optContent = optcontent;
    }
    
    public OperationLog(String userCode, String optId, String optTag, String optmethod, 
    		String optcontent ,String newValue, String oldvalue ) {
        this.logLevel = LEVEL_INFO;
        this.userCode = userCode;
        this.optId = optId;
        this.optTag = optTag;
        this.optMethod = optmethod;
        this.optContent = optcontent;
        this.newValue = newValue;
        this.oldValue = oldvalue;
    }
    

    public OperationLog(String loglevel, String userCode, String optId, String optTag, String optmethod, 
    		String optcontent,String newValue, String oldvalue) {
        this.logLevel = loglevel;
        this.userCode = userCode;
        this.optId = optId;
        this.optTag = optTag;
        this.optMethod = optmethod;
        this.optContent = optcontent;
        this.newValue = newValue;
        this.oldValue = oldvalue;
    }

    public String getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(String loglevel) {
        this.logLevel = loglevel;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    
    public String getOptTag() {
        return optTag;
    }

    public void setOptTag(String optTag) {
        this.optTag = optTag;
    }
    
    public Date getOptTime() {
        return this.optTime;
    }

    public void setOptTime(Date opttime) {
        this.optTime = opttime;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String optid) {
        this.optId = optid;
    }

    public String getOptMethod() {
        return this.optMethod;
    }

    public void setOptMethod(String optmethod) {
        this.optMethod = optmethod;
    }

    public String getOptContent() {
        return this.optContent;
    }
    public void setOptContent(String optcontent) {
        this.optContent = optcontent;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public String getNewValue() {
        return this.newValue;
    }   

    
    public void setOptNewObject(Object obj){
    	this.newValue = JSON.toJSONString(obj);
    }

    public void setOptOldObject(Object obj){
    	this.oldValue = JSON.toJSONString(obj);
    }

    public <T extends Object> void setOptDifference(T newObj,T oldObj){
        if(newObj==null || oldObj==null){
            this.newValue = JSON.toJSONString(newObj);
            this.oldValue = JSON.toJSONString(oldObj);
            return;
        }
    	Field[] fields = ReflectionOpt.getFields(newObj);
    	if(fields==null || fields.length<1)
    		return ;
    	Map<String,Object> newTempValue = new HashMap<>();
    	Map<String,Object> oldTempValue = new HashMap<>();
    	for(Field field : fields){
    		try{
    			Object obj1 = ReflectionOpt.forceGetFieldValue(newObj, field);
    			Object obj2 = ReflectionOpt.forceGetFieldValue(oldObj, field);
    			//method.getName()
    			if(obj1==null){
    				if(obj2!=null){
    					//newValue.put(field.getName(),"");
    					oldTempValue.put(field.getName(),obj2);
    				}
    			}else{
    				if(obj2==null){
    					newTempValue.put(field.getName(),obj1);
    					//oldValue.put(field.getName(),"");
    				}else if(!obj1.equals(obj2)){
    					newTempValue.put(field.getName(),obj1);
    					oldTempValue.put(field.getName(),obj2);
    				}
    			}
    				
    		} catch (Exception e) {
    			logger.error(e.getMessage(),e);
    		}
    	}    	 
    	this.newValue = JSON.toJSONString(newTempValue);
    	this.oldValue = JSON.toJSONString(oldTempValue);
    }
    
    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldvalue) {
        this.oldValue = oldvalue;
    }

     public String getoptTag() {
        return optTag;
    }

    public void setoptTag(String optTag) {
        this.optTag = optTag;
    }
    
    public String  getOptMethodText() {
        if (P_OPT_LOG_METHOD_C.equals(this.optMethod)) {
            return "新增";
        }
        if (P_OPT_LOG_METHOD_D.equals(this.optMethod)) {
            return "删除";
        }
        if (P_OPT_LOG_METHOD_U.equals(this.optMethod)) {
            return "更新";
        }
        return this.optMethod;
    }
}
