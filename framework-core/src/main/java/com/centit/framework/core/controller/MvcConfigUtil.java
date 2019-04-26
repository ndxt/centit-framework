package com.centit.framework.core.controller;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.SqlDateDeserializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class MvcConfigUtil {

    public static FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter =
            new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setFeatures(Feature.AllowArbitraryCommas,Feature.AllowUnQuotedFieldNames,
            Feature.DisableCircularReferenceDetect);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }

    public static void fastjsonGlobalConfig(){
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Timestamp.class, SqlTimestampDeserializer.instance);
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Date.class,  SqlDateDeserializer.instance);
        ParserConfig.getGlobalInstance().putDeserializer(java.util.Date.class, UtilDateDeserializer.instance);

        SerializeConfig.getGlobalInstance().put(java.util.Date.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Date.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Timestamp.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Blob.class, LobSerializer.instance);
        //SerializeConfig.getGlobalInstance().put(oracle.sql.BLOB.class, LobSerializer.instance);
    }

    public static void setApplicationContext(ApplicationContext applicationContext, FastJsonHttpMessageConverter jsonHttpMessageConverter) throws BeansException {

        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
            applicationContext.getBean(RequestMappingHandlerAdapter.class);

        List<HandlerMethodReturnValueHandler> sortedHandlers = new ArrayList<>(20);
        List<HandlerMethodReturnValueHandler> defaultHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 建议都使用框架的这个注解处理，为了提高性能可以放在最前面：
        sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(jsonHttpMessageConverter));
        sortedHandlers.addAll(defaultHandlers);
        // 下面的代码式 放到 Spring 定义的  Annotation-based 组中 排在 sortedHandlers 后面
        /*for(HandlerMethodReturnValueHandler handler : defaultHandlers ){
            sortedHandlers.add(handler);
            if(handler instanceof RequestResponseBodyMethodProcessor){
                sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(jsonHttpMessageConverter));
            }
        }*/
        requestMappingHandlerAdapter.setReturnValueHandlers(sortedHandlers);
    }
}
