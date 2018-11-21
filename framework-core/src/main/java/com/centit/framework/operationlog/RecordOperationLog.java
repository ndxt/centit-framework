package com.centit.framework.operationlog;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordOperationLog {
    /**
     *
     * @return 记录操作内容，理论上这个不能为空
     */
    @AliasFor("value")
    String content() default "";

    /**
     * 是否记录操作时间
     * @return boolean
     */
    boolean timing() default false;

    /**
     * 是否将HttpRequest中的参数放入newValue中，
     * 一般查询都需要将这个 参数设置为true
     * @return boolean
     */
    boolean appendRequest() default false;

    /**
     * 将方法的返回值放到 old 字段
     * @return boolean
     */
    boolean returnValueAsOld() default false;
}
