package com.centit.framework.core.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应 http 请求 返回的单个数据
 */
public class ResponseSingleData {

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
    private Object resData;

    public ResponseSingleData() {
        resCode = 0;
        resMessage = "OK";
    }

    public ResponseSingleData(int nCode) {
        resCode = nCode;
        resMessage = nCode==0?"OK":"ERROR";
    }

    public ResponseSingleData(String message) {
        resCode = 0;
        resMessage = message;
    }

    public ResponseSingleData(int nCode, String message) {
        resCode = nCode;
        resMessage = message;
    }

    public static ResponseSingleData makeResponseData(Object objValue){
        ResponseSingleData resData = new ResponseSingleData();
        resData.setData(objValue);
        return resData;
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

	public void setMessage(String resMessage) {
		this.resMessage = resMessage;
	}

    public Object getData() {
        return resData;
    }

    public void setData(Object objValue) {
		this.resData = objValue;
    }

    public String toJSONString(PropertyPreFilter simplePropertyPreFilter){
        Map<String, Object> param = new HashMap<>();
        param.put(ResponseData.RES_CODE_FILED, resCode );
        param.put(ResponseData.RES_MSG_FILED,  resMessage );
        if(resData!=null)
            param.put(ResponseData.RES_DATA_FILED, resData);
		if(simplePropertyPreFilter==null)
			return JSONObject.toJSONString(param);
        return JSONObject.toJSONString(param,simplePropertyPreFilter);
    }

	public String toJSONString(){
		return toJSONString(null);
	}

	@Override
	public String toString(){
		return toJSONString();
	}
}
