package com.centit.framework.core.aop;

import java.lang.annotation.*;

/**
 * @desc 定义一个不重复提交的注解
 */
//@Target({ElementType.PARAMETER, ElementType.METHOD})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatCommit {
    /**
     * 延时时间 在延时多久后可以再次提交
     *
     * @return Time unit is one second
     */
    int delaySeconds() default 20;
}
