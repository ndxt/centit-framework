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
     * 是否为默认(主)机构 T:主机构 F：辅机构
     * @return 是否为默认(主)机构 T:主机构 F：辅机构
     */
    String getIsPrimary();
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
     * 用户在本单位的排序号
     * @return 排序号
     */
    Long getUserOrder();
}
