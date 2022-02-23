package com.centit.framework.common;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 响应 http 请求 返回的数据，可以用Map返回多个数据
 */
@SuppressWarnings("unused")
public class ResponseMapData extends ResponseSingleData {

    private static final long serialVersionUID = 1L;

    public ResponseMapData() {
        super();
    }

    public ResponseMapData(int nCode) {
        super(nCode);
    }

    public ResponseMapData(String message) {
        super(message);
    }

    public ResponseMapData(int nCode, String message) {
        super(nCode, message);
    }

    public static ResponseMapData makeResponseData(Map<String, Object> resMapData){
        ResponseMapData resData = new ResponseMapData();
        resData.setData(resMapData);
        return resData;
    }

    public Map<String, Object> getData() {
        return (Map<String, Object>) data;
    }

    @Deprecated
    public Object setData(Object objValue) {
        if(! (objValue instanceof Map)){
            throw new RuntimeException("参数必须式 Map<String, Object> 类型");
        }
        Object oldObj = this.data;
        this.data = objValue;
        return oldObj;
    }

    public Map<String, Object> setData(Map<String, Object> objValue) {
        Object oldObj = data;
        this.data = objValue;
        return (Map<String, Object>)oldObj;
    }

    public void addResponseData(String sKey, Object objValue) {
        if(data == null ){
            data = new LinkedHashMap<>();
        }
        ((Map<String, Object>) data).put(sKey, objValue);
    }

    public Object getResponseData(String sKey) {
        if( this.data == null){
            return null;
        }
        return ((Map<String, Object>) data).get(sKey);
    }
}
