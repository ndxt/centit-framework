package com.centit.framework.model.basedata;

/**
 * FUnitinfo entity.
 * 机构信息
 * @author MyEclipse Persistence Tools
 */
public interface IUnitInfo{
    /**
     * 机构代码 是机构的主键
     * @return 机构代码 是机构的主键
     */
     String getUnitCode();


    /**
     * 机构自编代码
     * @return 机构自编代码
     */
     String getDepNo();

    /**
     * 机构名称
     * @return 机构名称
     */
     String getUnitName();

    /**
     * 机构简称
     * @return 机构简称
     */
    String getUnitShortName();
    /**
     * 上级机构代码
     * @return 上级机构代码
     */
     String getParentUnit();
    /**
     * 机构类别
     * @return 机构类别
     */
     String getUnitType();
    /**
     * 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     * @return 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     */
     String getIsValid();
    /**
     * 机构路径
     * @return 机构路径
     */
     String getUnitPath();
    /**
     * 机构排序
     * @return 机构排序
     */
     Long getUnitOrder();

    /**
     * 分管领导（机构管理员）
     * @return 分管领导（机构管理员）
     */
     String getUnitManager();

    /**
     * 获取和第三方对接数据，一般为第三方业务数据组件
     * @return 机构第三发业务中的主键
     */
    String getUnitTag();
}
