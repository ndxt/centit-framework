package com.centit.framework.core.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法 HibernateOptUtils.listObjectsAsJson 配合这个查询工作
 * 
 * @author codefan
 * @create 2015年12月16日
 * @version
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DictionaryMap {
    /**
     * (Must be assigned) The catalog code of the dictionary .
     * <p/>
     * Defaults to the default catalog code.
     * 对应的数据字典的目录代码。
     */
    public String value() default "";
    
    /**
     * (Must be assigned) The name of the Field Name.
     * <p/>
     * Defaults to the field name.
     * 数字字典值对应的字段名
     */
    public String fieldName() default "";
}
