package com.centit.framework.model.basedata;

/**
 * @author codefan@hotmail.com
 */

public interface IDataCatalog{

    /**
     * 类别代码
     * @return 类别代码
     */
    String getCatalogCode();

    /**
     * 类别名称
     * @return 类别名称
     */
    String getCatalogName();

    /**
     * 所属租户ID
     * @return 所属租户ID
     */
    String getTopUnit();

    /**
     * 所属系统ID
     * @return 所属系统ID
     */
    String getOsId();

    /**
     * 所属业务ID
     * @return 所属业务ID
     */
    String getOptId();

    /**
     * 类别形式  L 列表 T 为 树
     * @return 类别形式 L 列表 T 为 树
     */
    String getCatalogType();

    /**
     * 字典描述
     * @return 字典描述
     */
    String getCatalogDesc();

    /**
     * 字典明细字段描述
     * @return 字典明细字段描述
     */
    String getFieldDesc();
}
