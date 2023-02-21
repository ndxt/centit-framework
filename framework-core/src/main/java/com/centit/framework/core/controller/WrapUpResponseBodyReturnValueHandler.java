package com.centit.framework.core.controller;


import com.alibaba.fastjson.JSON;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ToResponseData;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
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
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
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

    protected void writeWithMessageConverters(Object value,
                                                  MethodParameter returnType,
                                                  ServletServerHttpResponse httpResponse)
        throws IOException, HttpMessageNotWritableException {

        WrapUpResponseBody wrap = returnType.getMethodAnnotation(WrapUpResponseBody.class);
        WrapUpContentType wrapUpType = wrap != null ? wrap.contentType()
                            : WrapUpContentType.DATA;

        switch (wrapUpType) {
            case RAW:
                JsonResultUtils.writeOriginalObject(value, httpResponse.getServletResponse());
                return;
            case JAVASCRIPT:
                {
                    String scriptValue;
                    if (ReflectionOpt.isScalarType(value.getClass())) {
                        scriptValue = StringBaseOpt.objectToString(value);
                    } else {
                        scriptValue = JSON.toJSONString(value);
                    }
                    JsonResultUtils.writeJavaScript(scriptValue, httpResponse.getServletResponse());
                }
                return;
            case IMAGE:
                if (value instanceof RenderedImage) {
                    JsonResultUtils.writeOriginalImage((RenderedImage) value,
                        httpResponse.getServletResponse());
                } else {
                    throw new ObjectException(500, "需要image/gif格式的RenderedImage对象。");
                }
                return;
            case XML:
                {
                    String xmlValue;
                    if (ReflectionOpt.isScalarType(value.getClass())) {
                        xmlValue = StringBaseOpt.objectToString(value);
                    } else {
                        xmlValue = XMLObject.objectToXMLString("response", value);
                    }
                    JsonResultUtils.writeOriginalXml(xmlValue, httpResponse.getServletResponse());
                }
                return;
            case HTML:
                JsonResultUtils.writeOriginalHtml(value, httpResponse.getServletResponse());
                return;
            case FILE:
                if (value instanceof File) {
                    JsonResultUtils.writeOriginalFile((File) value,
                        httpResponse.getServletResponse());
                } else if (value instanceof InputStream) {
                    JsonResultUtils.writeOriginalFile((InputStream) value, "未命名文件",
                        httpResponse.getServletResponse());
                } else {
                    throw new ObjectException(500, "需要File对象。");
                }
                return;
            default:
                break;
        }

        ResponseData outputValue;
        if(value == null) {
            outputValue = ResponseData.successResponse;
            messageConverter.write(
                outputValue, MediaType.APPLICATION_JSON, httpResponse);
            return;
        }

        if (value instanceof ToResponseData){
            outputValue = ((ToResponseData)value).toResponseData();
        } else if (value instanceof ResponseData) {
            outputValue = (ResponseData) value;
        } else {
            outputValue = ResponseData.makeResponseData(value);
        }

        switch (wrapUpType) {
            case MAP_DICT:{
                Object data = outputValue.getData();
                if (data instanceof Collection) {
                    outputValue = ResponseData.makeErrorMessageWithData(
                        DictionaryMapUtils.objectsToJSONArray((Collection<? extends Object>)data),
                        outputValue.getCode(), outputValue.getMessage());
                } else {
                    outputValue = ResponseData.makeErrorMessageWithData(
                        DictionaryMapUtils.objectToJSON(data),
                        outputValue.getCode(), outputValue.getMessage());

                }
            }
            break;
            case BASE64: {
                outputValue = ResponseData.makeErrorMessageWithData(
                    Base64.encodeBase64String(JSON.toJSONString(outputValue.getData()).getBytes(StandardCharsets.UTF_8)),
                    outputValue.getCode(), outputValue.getMessage());
            }
            break;
            case DATA:
            default:
            break;
        }
        messageConverter.write(
            outputValue, MediaType.APPLICATION_JSON, httpResponse);
    }

}
