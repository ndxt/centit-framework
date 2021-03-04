package com.centit.framework.model.basedata;

/**
 * @author codefan
 */
//数据字典表
public interface IDataDictionary{

    /**
     * 类别代码
     * @return 类别代码
     */
     String getCatalogCode();
    /**
     * 数据字典代码
     * @return 数据字典代码
     */
     String getDataCode();

    /**
     * 在字典行政职务 extraCode 作为行政等级，
     *      在不同的字典中这个字段可以执行解释
     *      但在树形数据字典中这个字段统一作为父类代码
     * 数据字典扩展代码，在树形数据字典中位父类代码
     * @return 数据字典扩展代码，在树形数据字典中为父类代码
     */
     String getExtraCode();
    /**
     * 在多租户系统中、系统维护中的 岗位、行政职务 字典中这个字段对应住户代码topUnit
     * 数据字典扩展代码2, 业务自行解释
     * @return 数据字典扩展代码
     */
     String getExtraCode2();

    /**
     * 字典内容，如果支持国际化这个将是一个json key为语言类别代码
     * @return 字典内容
     */
     String getDataValue();

    /**
     * 获得某个自定语言的描述
     * @param lang 语言类别代码
     * @return 某个自定语言的描述
     */
     String getLocalDataValue(String lang);
    /**
     * 数据字典标记，用于分类只有一个字节，比如 D 为已删除 ，其他应用业务执行解释
     * @return 数据字典标记
     */
     String getDataTag();


    /**
     * 字典条目描述
     * @return 字典条目描述
     */
     String getDataDesc();

    /**
     * 字典排序号
     * @return 字典排序号
     */
     Integer getDataOrder();


}
