package com.centit.framework.core.controller;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WrapUpResponseBody  {
    /**
     * 用来指定 封装类别
     * @return String  contentType
     */
    WrapUpContentType contentType() default WrapUpContentType.DATA;
}
