package com.centit.framework.core.service;

import com.centit.framework.core.dao.PageDesc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseEntityManager<T extends Serializable, PK extends Serializable> {
    /**
     * 查找表中的所有 isValid = 'T' 的记录 必需isValid这个字段才可以使用，或者重载这个函数
     *
     * @return 表中的所有 isValid = 'T' 的记录
     */
    List<T> listValidObjects();

    /**
     * 查找表中的所有记录， 包括禁用的 isValid = 'F' 的记录, 如果没有isValid这个字段也可以使用
     *
     * @return 表中的所有记录， 包括禁用的 isValid = 'F' 的记录, 如果没有isValid这个字段也可以使用
     */
    List<T> listObjects();

    /*
     * getObjects 为一组查找 T 数组的函数 根据表单中的过滤条件查找符合条件的对象集合
     *
     * @param filterDesc
     *            过滤条件
     * @return
     */
    /**
     * 根据对象的主键 获得数据库中对应的对象信息
     *
     * @param id PK
     * @return 数据库中对应的对象信息
     */
    T getObjectById(PK id);

    /**
     * 保存泛型参数对象
     *
     * @param o T
     */
    void saveObject(T o);

    
    /**
     * 保存泛型参数对象
     *
     * @param o T
     * @return Serializable
     */
    Serializable saveNewObject(T o);
    
    /**
     * 更新泛型参数对象
     *
     * @param o T
     */
    public void updateObject(T o);
    
    /**
     * 保存泛型参数对象
     *
     * @param o T
     */
    void mergeObject(T o);
    
    
    /**
     * 修改之前check一下版本号，不一致抛异常
     * @param o T
     */
    void updateObjectCheckTimestamp(T o);

    /**
     * 删除泛型参数对象
     *
     * @param o T
     */
    void deleteObject(T o);

    /**
     * 根据主键删除泛型参数对象
     *
     * @param id PK
     */
    void deleteObjectById(PK id);

    /**
     * @param shql sql,hql语句
     * @param filterMap 过滤条件
     * @return listObjects
     */
    List<T> listObjects(String shql, Map<String, Object> filterMap);

    /**
     * @param filterMap 过滤条件
     * @return listObjects
     */
    List<T> listObjects(Map<String, Object> filterMap);

    /**
     * 配合 EC Table 设计的一个查询语句
     *
     * @param shql sql,hql语句
     * @param filterMap 过滤条件
     * @param pageDesc  分页属性
     * @return listObjects
     */
    List<T> listObjects(String shql, Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 配合 EC Table 设计的一个查询语句，将 filterMap 组装成对应的Hql语句 调用对应的 getObjects
     *
     * @param filterMap 过滤条件
     * @param pageDesc  分页属性
     * @return listObjects
     */
    List<T> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 根据唯一属性值返回对象
     *
     * @param propertyName 字段名
     * @param propertyValue 值
     * @return 唯一属性值返回对象
     */
    T getObjectByProperty(final String propertyName, final Object propertyValue);

    /**
     * 根据多个属性返回唯一对象
     *
     * @param properties map 字段
     * @return 多个属性返回唯一对象
     */
    T getObjectByProperties(Map<String, Object> properties);

    /**
     * 通过反射调用泛型对象 des 的 copyNotNullProperty("F") 函数，参数为 sou， 要求对象必须有
     * copyNotNullProperty这个函数，脚手架的反向工程自动生成这个函数 子类可以重写这个函数
     *
     * @param des  T
     * @param sou  T
     */
    void copyObjectNotNullProperty(T des, T sou);

    /**
     * 通过反射调用泛型对象 des 的 copy("F") 函数，参数为 sou， 要求对象必须有
     * copyNotNullProperty这个函数，脚手架的反向工程自动生成这个函数 子类可以重写这个函数
     *
     * @param des  T
     * @param sou  T
     */
    void copyObject(T des, T sou);

    /**
     * 通过反射调用泛型对象 clearProperties 函数，清楚对象的所有非主键属性的值， 要求对象必须有
     * clearProperties这个函数，脚手架的反向工程自动生成这个函数 子类可以重写这个函数
     *
     * @param des  T
     */
    void clearObjectProperties(T des);

}
