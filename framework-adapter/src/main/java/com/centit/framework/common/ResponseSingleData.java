package com.centit.framework.common;

/**
 * 响应 http 请求 返回的单个数据
 */
public class ResponseSingleData implements ResponseData{

    private static final long serialVersionUID = 1L;
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
        this.code = RESULT_OK;
        this.message = "OK";
    }

    public ResponseSingleData(int nCode) {
        this.code = nCode;
        this.message = nCode==RESULT_OK ? "OK" : "ERROR";
    }

    public ResponseSingleData(String message) {
        this.code = RESULT_OK;
        this.message = message;
    }

    public ResponseSingleData(int nCode, String message) {
        this.code = nCode;
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

    @Override
    public String toString(){
        return toJSONString();
    }

}
