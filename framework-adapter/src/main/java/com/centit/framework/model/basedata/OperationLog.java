package com.centit.framework.model.basedata;

import com.alibaba.fastjson.JSON;
import com.centit.support.algorithm.CollectionsOpt;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
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
    /**
     * 警告信息
     */
    public static final String LEVEL_WARN = "2";

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
     * 业务对象组件，复合主键用&amp;连接格式与url参数类似
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

    public static OperationLog create(){
        return new OperationLog();
    }

    public OperationLog level(String logLevel){
        this.logLevel = logLevel;
        return this;
    }

    public OperationLog user(String userCode){
        this.userCode = userCode;
        return this;
    }

    public OperationLog operation(String soptid){
        this.optId = soptid;
        return this;
    }

    public OperationLog method(String smethod){
        this.optMethod = smethod;
        return this;
    }

    public OperationLog tag(String optTag){
        this.optTag = optTag;
        return this;
    }

    public OperationLog time(Date opttime){
        this.optTime = opttime;
        return this;
    }

    public OperationLog content(String scontent){
        this.optContent = scontent;
        return this;
    }

    private static String castObjectToJsonStr(Object newValue){
        if(newValue == null){
            return null;
        }
        if(newValue instanceof String) {
            return (String) newValue;
        }
        return JSON.toJSONString(newValue);
    }

    public OperationLog newObject(Object obj){
        this.newValue = castObjectToJsonStr(obj);
        return this;
    }

    public OperationLog oldObject(Object obj){
        this.oldValue = castObjectToJsonStr(obj);
        return this;
    }

    public <T extends Object> OperationLog makeDifference(T oldObj, T newObj){
        if(newObj==null || oldObj==null){
            this.newValue = castObjectToJsonStr(newObj);
            this.oldValue = castObjectToJsonStr(oldObj);
            return this;
        }

        Map<String,Object> oldObjMap = CollectionsOpt.objectToMap(oldObj);
        Map<String,Object> newObjMap = CollectionsOpt.objectToMap(newObj);

        Map<String,Object> newTempValue = new HashMap<>();
        Map<String,Object> oldTempValue = new HashMap<>();
        for(Map.Entry<String,Object> ent: newObjMap.entrySet()){
           if(!oldObjMap.containsKey(ent.getKey())){
               newTempValue.put(ent.getKey(), ent.getValue());
           }
        }

        for(Map.Entry<String,Object> ent: oldObjMap.entrySet()){
            Object newP = newObjMap.get(ent.getKey());
            if(newP == null){
                oldTempValue.put(ent.getKey(), ent.getValue());
            } else if(!newP.equals(ent.getValue())){
                oldTempValue.put(ent.getKey(), ent.getValue());
                newTempValue.put(ent.getKey(), newP);
            }
        }

        this.newValue = JSON.toJSONString(newTempValue);
        this.oldValue = JSON.toJSONString(oldTempValue);
        return this;
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
