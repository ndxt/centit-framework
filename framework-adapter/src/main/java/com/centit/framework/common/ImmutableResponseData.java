package com.centit.framework.common;

public class ImmutableResponseData  implements ResponseData{

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

    public ImmutableResponseData() {
        this.code = RESULT_OK;
        this.message = "OK";
    }

    public ImmutableResponseData(int nCode, String message) {
        this.code = nCode;
        this.message = message;
    }

    public ImmutableResponseData(int nCode, String message, Object data) {
        this.code = nCode;
        this.message = message;
        this.data = data;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public String toString(){
        return toJSONString();
    }

}
