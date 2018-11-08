package com.centit.framework.core.controller;


import com.centit.framework.common.ResponseData;
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
import java.io.IOException;

/**
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
 */
public class WrapUpResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {

    protected final HttpMessageConverter<Object> messageConverter;
    /**
     * Basic constructor with converters only. Suitable for resolving
     * {@code @RequestBody}. For handling {@code @WrapUpResponseBody} consider also
     * providing a {@code ContentNegotiationManager}.
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

    protected <T> void writeWithMessageConverters(T value, MethodParameter returnType, ServletServerHttpResponse outputMessage)
        throws IOException, HttpMessageNotWritableException {

        Object outputValue = value;
        if(!(outputValue instanceof ResponseData)){
            outputValue = ResponseData.makeResponseData(outputValue);
        }
        messageConverter.write(
            outputValue, MediaType.APPLICATION_JSON_UTF8, outputMessage);
    }

}
