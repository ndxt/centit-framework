package com.centit.framework.hibernate.dao;

import com.centit.framework.core.dao.PageDesc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BaseDao<T extends Serializable, PK extends Serializable>
{
    void deleteObject(T o);

    void deleteObjectForce(T o);

    void deleteObjectForceById(PK id);

    void deleteObjectById(PK id);

    void saveNewObject(T o);

    void mergeObject(T o) ;

    void saveRawObject(T o);

    void saveObject(T o);

    T getObjectById(PK id) ;

    List<PK> saveNewObjects(Collection<T> os);

    List<PK> saveNewObjects(T[] os);

    void updateRawObject(T o);

    void updateObject(T o);

    void updateObjectProperties(T o,String... properties ) throws NoSuchFieldException ;

    /**
    * 修改之前check一下版本号，不一致抛异常
    * @param o T
    */
    void updateObjectCheckTimestamp(T o) ;

    /**
    * 只更改部分属性，根据对象的主键来更新,同时检验 时间戳
    * @param o 这对对象中除了要有对应的属性值 ，还必须有主键值
    * @param properties 需要更的属性
    * @throws NoSuchFieldException 如果属性名输入的不对会报错
    */
    void updateObjectPropertiesCheckTimestamp(T o,String... properties )
    throws NoSuchFieldException ;


    /**
    * 批量删除记录
    * @param dbObjects 数据库中旧的对象列表
    */
    @Transactional(propagation=Propagation.MANDATORY)
    void deleteObjectsAsTabulation(Collection<T> dbObjects);

    /**
    *  批量删除记录 根据单独外键查询
    * @param propertyName 外键关联字段
    * @param propertyValue 外键值（主表的主键）
    */
    void deleteObjectsAsTabulation(
    final String propertyName,
    final Object propertyValue );

    /**
    *  批量删除记录 根据复合外键查询
    * @param properties 复合外键字段属性 和对应额值
    */
    void deleteObjectsAsTabulation(Map<String, Object> properties);


    /**
    * 用新的子表对象列表替换旧的子表对象列表
    * @param dbObjects 数据库中旧的对象列表
    * @param newObjects 新的对象列表
    * @param checkTimestamp 更新的记录是否检查更改时间戳
    * @return  新的子表对象
    */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.MANDATORY)
    List<PK> replaceObjectsAsTabulation(List<T> dbObjects,Collection<T> newObjects,
    boolean checkTimestamp);

    /**
    *  用新的子表对象列表替换旧的子表对象列表
    *  通过单主键查询数据库中的旧的类别
    * @param newObjects 新的对象列表
    * @param propertyName 外键关联字段
    * @param propertyValue 外键值（主表的主键）
    * @return  新的子表对象
    */
    @Transactional(propagation=Propagation.MANDATORY)
    List<PK> replaceObjectsAsTabulation(Collection<T> newObjects,
    final String propertyName,
    final Object propertyValue );

    /**
    *  用新的子表对象列表替换旧的子表对象列表
    *  通过复合主键查询数据库中的旧的类别
    * @param newObjects 新的对象列表
    * @param properties 复合外键字段属性 和对应额值
    * @return  新的子表对象
    */
    @Transactional(propagation=Propagation.MANDATORY)
    List<PK> replaceObjectsAsTabulation(Collection<T> newObjects,
    Map<String, Object> properties);

    /**
    *  用新的子表对象列表替换旧的子表对象列表 ,更新的记录需要检查更改时间戳
    *  通过单主键查询数据库中的旧的类别
    * @param newObjects 新的对象列表
    * @param propertyName 外键关联字段
    * @param propertyValue 外键值（主表的主键）
    * @return  新的子表对象
    */
    List<PK> replaceObjectsAsTabulationCheckTimestamp(Collection<T> newObjects,
    final String propertyName,
    final Object propertyValue );
    /**
    *  用新的子表对象列表替换旧的子表对象列表 ,更新的记录需要检查更改时间戳
    *  通过复合主键查询数据库中的旧的类别
    * @param newObjects 新的对象列表
    * @param properties 复合外键字段属性 和对应额值
    * @return  新的子表对象
    */
    List<PK> replaceObjectsAsTabulationCheckTimestamp(Collection<T> newObjects,
    Map<String, Object> properties);



    List<T> listObjectsAll();

    List<T> listObjects();

    List<T> listValidObjects();

    List<T> listObjects(String hql) ;

    List<T> listObjects(String shql, Object[] values, PageDesc pageDesc);

    List<T> listObjects(String shql, Object value, PageDesc pageDesc);

    List<T> listObjects(String shql, PageDesc pageDesc);

    List<T> listObjects(String shql, Object[] values);

    List<T> listObjects(String shql, Object value) ;

    List<T> listObjects(String shql, Map<String, Object> filterDesc);

    List<T> listObjects(Map<String, Object> filterDesc);

    List<T> listObjectsByNamedHql(String shql,
    Map<String, Object> params, int startPos, int maxSize) ;

    List<T> listObjects(String shql, Map<String, Object> filterDesc,
    PageDesc pageDesc);

    /**
    * 为了和 myBatis 兼容
    * @param sHql
    * @param filterMap
    * @return
    */
    int pageCount(String sHql , Map<String, Object> filterMap);

    int pageCount(Map<String, Object> filterMap) ;

    List<T> pageQuery(String sHql , Map<String, Object> filterMap);

    List<T> pageQuery(Map<String, Object> filterMap) ;

    List<T> listObjects(Map<String, Object> filterMap, PageDesc pageDesc);


    List<T> listObjectByProperty(final String propertyName,
    final Object propertyValue) ;


    List<T> listObjectByProperties(Map<String, Object> properties);

    T getObjectByProperty(final String propertyName,
    final Object propertyValue) ;

    T getObjectByProperties(Map<String, Object> properties);
}
