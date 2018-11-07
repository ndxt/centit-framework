package com.centit.framework.core.controller;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.common.ResponseData;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * 这个是对返回的 数据进行处理，不过这个会和swagger 冲突
 * 这类可以用来 压缩 传输的数据
 */
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {
    public JsonHttpMessageConverter() {
        super();
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 判断返回的 对象是否为 ResponseData
        if(object instanceof ResponseData){
            super.writeInternal(object,outputMessage);
        }else{ // 如果不是 ResponseData 则封装为 ResponseData
            super.writeInternal(ResponseData.makeResponseData(object),outputMessage);
        }
    }
}
