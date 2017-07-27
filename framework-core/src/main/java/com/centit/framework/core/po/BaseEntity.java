package com.centit.framework.core.po;

import java.io.Serializable;

public interface BaseEntity{
	/**
	 * 获取主键 //PrimaryKey
	 * @return PrimaryKey
	 */
	Serializable getId();
	/**
	 * 复制另一个对象的属性（一般不包括主键）
	 * @param other BaseEntity
	 * @return 另一个对象的属性（一般不包括主键）
	 */
	BaseEntity copyProperties(BaseEntity other);
	/**
	 * 获取另一个对象的非空属性（同样一般不包括主键）
	 * @param other BaseEntity
	 * @return 另一个对象的非空属性
	 */
	BaseEntity copyNotNullProperties(BaseEntity other);
}
