package com.centit.framework.operationlog;

import com.centit.framework.model.basedata.OperationLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordOperationLog {
    /**
     *
     * @return 记录操作内容表达式，理论上这个不能为空
     */
    String content() default "";

    /**
     *
     * @return 日志等级
     */
    String level() default OperationLog.LEVEL_INFO;

    /**
     *
     * @return 业务编码
     */
    String operation() default "";

    /**
     *
     * @return 操作方法
     */
    String method() default "";
    /**
     *
     * @return 操作对象主键 表达式
     */
    String tag() default "";

    /**
     * 优先级高于 appendRequest
     * @return 更改后对象
     */
    String newValue() default "";
    /**
     * 优先级高于 returnValueAsOld
     * @return 更改前对象
     */
    String oldValue() default "";

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
