package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;

/**
 * FUserunit entity.
 * 用户机构关联关系
 * @author MyEclipse Persistence Tools
 */
public interface IUserUnit{
    int MAX_XZ_RANK = 100000;
    /**
     * 关联关系主键
     * @return 关联关系主键
     */
    String getUserUnitId();
    /**
     * 用户编码，是用户的主键
     * @return 用户编码，是用户的主键
     */
    @DictionaryMap(fieldName = "userName", value = "userCode")
    String getUserCode();

    /**
     * 机构代码 是机构的主键
     * @return 机构代码 是机构的主键
     */
    @DictionaryMap(fieldName = "unitName", value = "unitCode")
    String getUnitCode();
    /**
     * 关联关系，数据字典
     * 归属部门 T 工作部门 F 借出部门 O 借入部门 I
     * @return 归属部门 T 工作部门 F 借出部门 O 借入部门 I
     */
    @DictionaryMap(fieldName = "userUnitRelType", value = "UserUnitRelType")
    String getRelType();

    /**
     * 用户顶级机构 用户帐套管理，作为 租户的标识
     * 这个属性在创建时指定，并且不能变
     * 在添加机构的时候，需要校验机构的 unitPath 必须包括这个机构
     * @return 用户顶级机构
     */
    String getTopUnit();

    /**
     * 用户在本机构的岗位
     * @return 用户在本机构的岗位
     */
    @DictionaryMap(fieldName = "userStationText", value = "StationType")
    String getUserStation();
    /**
     * 用户在本机构的行政职务
     * @return 用户在本机构的行政职务
     */
    @DictionaryMap(fieldName = "userRankText", value = "RankType")
    String getUserRank();

    /**
     * 用户在本机构的 职级
     * @return 用户在本机构的 职级
     */
    @DictionaryMap(fieldName = "userPostText", value = "PostRank")
    String getPostRank();
    /**
     * 用户在本单位的排序号
     * @return 排序号
     */
    Long getUserOrder();
}
