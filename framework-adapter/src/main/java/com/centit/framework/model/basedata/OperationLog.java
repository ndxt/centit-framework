package com.centit.framework.model.basedata;

import com.alibaba.fastjson.JSON;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
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

    /**
     * 调试信息
     */
    public static final String LEVEL_DEBUG = "3";

    /**
     * 只能同一机构查看的日志
     */
    public static final String LEVEL_SECURITY_UNIT = "4";

    /**
     * 只能自己查看的日志
     */
    public static final String LEVEL_SECURITY_USER = "5";

    private static Logger logger = LoggerFactory.getLogger(OperationLog.class);
    /**
     * 系统日志操作
     */
    public static final String P_OPT_LOG_METHOD = "P_OPT_LOG_METHOD";
    public static final String P_OPT_LOG_METHOD_C = "create";
    public static final String P_OPT_LOG_METHOD_U = "update";
    public static final String P_OPT_LOG_METHOD_D = "delete";

    /*日志主键*/
    private String logId;

    /**
     * 日志级别，用户可以自己解释这个属性
     */
    private String logLevel;

    /**
     * 操作用户
     */
    @DictionaryMap(value = "userCode", fieldName = "userName")
    private String userCode;

    /**
     * 操作用户所属机构
     */
    @DictionaryMap(value = "unitCode", fieldName = "unitName")
    private String unitCode;
    /**
     * 同一个请求同一个 协作号，主要用于调试和跟踪
     */
    private String correlationId;
    /**
     * 操作时间
     */
    private Date optTime;
    /**
     * 所属租户
     */
    private String topUnit;
    /**
     * 所属业务id
     */
    private String osId;//application id
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
     * 日志内容描述; 也可以是json
     */
    private String optContent;
    /**
     * 更新前旧值，json格式，这个字段不是必须的
     */
    private Object newValue;
    /**
     * 更新后新值，json格式，这个字段不是必须的
     */
    private Object oldValue;
    private String loginIp;

    public OperationLog() {
        this.logLevel = LEVEL_INFO;
        this.optTime = DatetimeOpt.currentSqlDate();
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

    public OperationLog unit(String unitCode){
        this.unitCode = unitCode;
        return this;
    }

    public OperationLog correlation(String correlationId){
        this.correlationId = correlationId;
        return this;
    }

    public OperationLog topUnit(String topUnit){
        this.topUnit = topUnit;
        return this;
    }

    public OperationLog application(String osId){
        this.osId = osId;
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
        this.newValue = obj;//castObjectToJsonStr(obj);
        return this;
    }

    public OperationLog oldObject(Object obj){
        this.oldValue = obj;//castObjectToJsonStr(obj);
        return this;
    }

    /**
     * 更新后新值，json格式，这个字段不是必须的
     */
    public OperationLog loginIp(String loginIp){
        this.loginIp = loginIp;
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

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
