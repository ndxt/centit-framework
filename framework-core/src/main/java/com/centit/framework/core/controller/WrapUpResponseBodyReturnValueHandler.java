package com.centit.framework.core.controller;


import com.alibaba.fastjson.JSON;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ObjectException;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ToResponseData;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.xml.XMLObject;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
 */
public class WrapUpResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {

    protected final HttpMessageConverter<Object> messageConverter;
    /**
     * Basic constructor with converters only. Suitable for resolving
     * {@code @RequestBody}. For handling {@code @WrapUpResponseBody} consider also
     * providing a {@code ContentNegotiationManager}.
     * @param converter 编码转换器，默认使用fastjson转换器
     */
    public WrapUpResponseBodyReturnValueHandler(HttpMessageConverter<Object> converter) {
        this.messageConverter = converter;
    }


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), WrapUpResponseBody.class) ||
            returnType.hasMethodAnnotation(WrapUpResponseBody.class) ); /*||
            returnType.getMethodAnnotation(WrapUpResponseBody.class) != null)/*/
    }


    /**
     * Create a new {@link HttpInputMessage} from the given {@link NativeWebRequest}.
     * @param webRequest the web request to create an input message from
     * @return the input message
     */
    protected ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    /**
     * Creates a new {@link HttpOutputMessage} from the given {@link NativeWebRequest}.
     * @param webRequest the web request to create an output message from
     * @return the output message
     */
    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(response);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
        throws IOException, HttpMessageNotWritableException {

        mavContainer.setRequestHandled(true);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

        // Try even with null return value. WrapUpResponseBodyAdvice could get involved.
        writeWithMessageConverters(returnValue, returnType, outputMessage);
    }

    protected <T> void writeWithMessageConverters(T value,
                                                  MethodParameter returnType,
                                                  ServletServerHttpResponse outputMessage)
        throws IOException, HttpMessageNotWritableException {

        WrapUpResponseBody wrap = returnType.getMethodAnnotation(WrapUpResponseBody.class);
        WrapUpContentType wrapUpType =  wrap != null ? wrap.contentType()
                            : WrapUpContentType.DATA;
        switch (wrapUpType) {
            case RAW:
                JsonResultUtils.writeOriginalObject(value,outputMessage.getServletResponse());
                break;
            case JAVASCRIPT:
                String scriptValue;
                if(ReflectionOpt.isScalarType(value.getClass())){
                    scriptValue = StringBaseOpt.objectToString(value);
                }else {
                    scriptValue = JSON.toJSONString(value);
                }
                JsonResultUtils.writeJavaScript(scriptValue,outputMessage.getServletResponse());
                break;
            case IMAGE:
                if(value instanceof RenderedImage){
                    JsonResultUtils.writeOriginalImage((RenderedImage)value,
                        outputMessage.getServletResponse());
                }else{
                    throw new ObjectException(500, "需要image/gif格式的RenderedImage对象。");
                }
                break;
            case XML: {
                    String xmlValue;
                    if (ReflectionOpt.isScalarType(value.getClass())) {
                        xmlValue = StringBaseOpt.objectToString(value);
                    } else {
                        xmlValue = XMLObject.objectToXMLString("response", value);
                    }
                    JsonResultUtils.writeOriginalXml(xmlValue, outputMessage.getServletResponse());
                }
                break;
            case HTML:
                JsonResultUtils.writeOriginalHtml(value,outputMessage.getServletResponse());
                break;
            case FILE:
                if(value instanceof File){
                    JsonResultUtils.writeOriginalFile((File) value,
                        outputMessage.getServletResponse());
                } else if(value instanceof InputStream){
                    JsonResultUtils.writeOriginalFile((InputStream) value, "未命名文件",
                        outputMessage.getServletResponse());
                } else {
                    throw new ObjectException(500, "需要File对象。");
                }
                break;
            case MAP_DICT:{
                    Object outputValue;
                    if (value instanceof Collection) {
                        outputValue =
                            DictionaryMapUtils.objectsToJSONArray((Collection<? extends Object>)value);
                    } else {
                        outputValue = DictionaryMapUtils.objectToJSON(value);
                    }
                    messageConverter.write(
                        ResponseData.makeResponseData(outputValue), MediaType.APPLICATION_JSON_UTF8, outputMessage);
                }
                break;
            default: {
                    ResponseData outputValue;
                    if(value == null) {
                        outputValue = ResponseData.makeSuccessResponse();
                    } else if (value instanceof ToResponseData){
                        outputValue = ((ToResponseData)value).toResponseData();
                    } else if (value instanceof ResponseData) {
                        outputValue = (ResponseData)value;
                    } else {
                        outputValue = ResponseData.makeResponseData(value);
                    }
                    messageConverter.write(
                        outputValue, MediaType.APPLICATION_JSON_UTF8, outputMessage);
                }
                break;
        }
    }

}
