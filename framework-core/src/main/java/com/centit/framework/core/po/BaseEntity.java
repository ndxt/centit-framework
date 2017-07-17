package com.centit.framework.core.po;

import java.io.Serializable;

public interface BaseEntity{
	/**
	 * 获取主键 //PrimaryKey
	 * @return
	 */
	public Serializable getId();
	/**
	 * 复制另一个对象的属性（一般不包括主键）
	 * @param other
	 * @return
	 */
	public BaseEntity copyProperties(BaseEntity other);
	/**
	 * 获取另一个对象的非空属性（同样一般不包括主键）
	 * @param other
	 * @return
	 */
	public BaseEntity copyNotNullProperties(BaseEntity other);
}
