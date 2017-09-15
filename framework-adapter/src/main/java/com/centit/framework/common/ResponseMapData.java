package com.centit.framework.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.*;

/**
 * 响应 http 请求 返回的数据，可以用Map返回多个数据
 */
@SuppressWarnings("unused")
public class ResponseMapData implements ResponseData {
    /**
     * 返回代码，0 表示正确，其他的为错误代码
     */
    private int resCode;

    /**
     * 返回消息提示 ，code为0时是提示，其他为 错误提示
     */
    private String resMessage;

    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     */
    private Map<String, Object> resMapData;

    public ResponseMapData() {
        resCode = 0;
        resMessage = "OK";
        resMapData = new LinkedHashMap<String, Object>();
    }

    public ResponseMapData(int nCode) {
        resCode = nCode;
        resMessage = nCode==0?"OK":"ERROR";
        resMapData = new LinkedHashMap<String, Object>();
    }

    public ResponseMapData(String message) {
        resCode = 0;
        resMessage = message;
        resMapData = new LinkedHashMap<String, Object>();
    }

    public ResponseMapData(int nCode, String message) {
        resCode = nCode;
        resMessage = message;
        resMapData = new LinkedHashMap<String, Object>();
    }

    public int getCode() {
        return resCode;
    }

    public void setCode(int resCode) {
        this.resCode = resCode;
    }

    public String getMessage() {
        return resMessage;
    }

    /*public String getResponseMessage() {
        return resMessage;
    }
    */
    public void setMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public void setResponseMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public Map<String, Object> getData() {
        return resMapData;
    }

    public Map<String, Object> setData(Map<String, Object> objValue) {
        Map<String, Object> oldObj = this.resMapData;
        this.resMapData = objValue;
        return oldObj;
    }

    public void addResponseData(String sKey, Object objValue) {
        resMapData.put(sKey, objValue);
    }
    
    public Object getResponseData(String sKey) {
        return resMapData.get(sKey);
    }

    public String toJSONString(PropertyPreFilter simplePropertyPreFilter){
        Map<String, Object> param = new HashMap<>();
        param.put(RES_CODE_FILED, resCode );
        param.put(RES_MSG_FILED,  resMessage );
        if(resMapData!=null)
            param.put(RES_DATA_FILED, resMapData);
        if(simplePropertyPreFilter==null)
            return JSONObject.toJSONString(param);
        return JSONObject.toJSONString(param,simplePropertyPreFilter);
    }

    @Override
    public String toString(){
        return toJSONString();
    }

}
