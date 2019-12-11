package com.centit.framework.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应 http 请求 返回的单个数据
 */
public class ResponseSingleData implements ResponseData{

    /**
     * 返回代码，0 表示正确，其他的为错误代码
     */
    private int code;

    /**
     * 返回消息提示 ，code为0时是提示，其他为 错误提示
     */
    private String message;

    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     */
    protected Object data;

    public ResponseSingleData() {
        code = 0;
        message = "OK";
    }

    public ResponseSingleData(int nCode) {
        code = nCode;
        message = nCode==0?"OK":"ERROR";
    }

    public ResponseSingleData(String message) {
        code = 0;
        this.message = message;
    }

    public ResponseSingleData(int nCode, String message) {
        code = nCode;
        this.message = message;
    }

    public static ResponseSingleData makeResponseData(Object objValue){
        ResponseSingleData resData = new ResponseSingleData();
        resData.setData(objValue);
        return resData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int resCode) {
        this.code = resCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String resMessage) {
        this.message = resMessage;
    }

    public Object getData() {
        return data;
    }

    public Object setData(Object objValue) {
        Object oldObj = this.data;
        this.data = objValue;
        return oldObj;
    }

    public String toJSONString(PropertyPreFilter simplePropertyPreFilter){
        Map<String, Object> param = new HashMap<>();
        param.put(ResponseData.RES_CODE_FILED, code);
        param.put(ResponseData.RES_MSG_FILED, message);
        if(data !=null) {
            param.put(ResponseData.RES_DATA_FILED, data);
            if (simplePropertyPreFilter != null) {
                return JSONObject.toJSONString(param, simplePropertyPreFilter);
            }
        }
        return JSONObject.toJSONString(param);
    }


    @Override
    public String toString(){
        return toJSONString();
    }

}
