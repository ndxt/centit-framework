package com.centit.framework.operationlog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordOperationLog {
	String optContent() default "";

	/**
	 * 是否记录操作时间
	 * @return boolean
	 */
	boolean recordTime() default false;
}
