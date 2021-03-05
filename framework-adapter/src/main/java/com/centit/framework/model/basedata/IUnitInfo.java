package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;

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
     * 统一社会信用代码
     * @return 统一社会信用代码
     */
    String getSocialCreditCode();

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
     * parentUnit 为 空 可作为租户
     * @return 上级机构代码
     */
     @DictionaryMap(fieldName = "parentUnitName", value = "unitCode")
     String getParentUnit();

    /**
     * 机构类别 T 租户
     * @return 机构类别
     */
     String getUnitType();

    /**
     * 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     * @return 机构是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
     */
     String getIsValid();

    /**
     * 用户顶级机构 用户帐套管理，作为 租户的标识
     * 这个属性在创建时指定，并且不能变
     * 在添加机构的时候，需要校验机构的 unitPath 必须包括这个机构
     * @return 用户顶级机构
     */
    String getTopUnit();

    /**
     * 机构路径，为这个机构所有上级机构的代码 用'/'连接的字符串
     * 通过这个机构可以查找其所有的上级机构代码， 用'/'分割这个字符串就可以
     * 也可以从数据库中查找出所有他的下级机构，只要判断其 unitPath 是否已本机构的unitPath为前缀
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
     * 机构标签
     * @return 机构标签
     */
    String getUnitTag();
    /**
     * 第三方编码
     * @return 机构第三发业务中的主键
     */
    String getUnitWord();
}
