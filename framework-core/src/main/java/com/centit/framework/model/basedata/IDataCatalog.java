package com.centit.framework.model.basedata;

import java.util.List;

/**
 * @author codefan@hotmail.com
 */

public interface IDataCatalog{
 
	/**
	 * 类别代码
	 * @return 类别代码
	 */
    public String getCatalogCode();
    /**
     * 类别名称
     * @return 类别名称
     */
    public String getCatalogName();
    /**
     * 所属业务ID
     * @return 所属业务ID
     */
    public String getOptId();

    /**
     * 类别形式  L 列表 T 为 树
     * @return 类别形式 L 列表 T 为 树
     */
    public String getCatalogType();

    /**
     * 字典描述
     * @return 字典描述
     */
    public String getCatalogDesc();
    /**
     * 字典明细字段描述
     * @return 字典明细字段描述
     */
    public String getFieldDesc();
    
    /**
     * 获取字典条目明细
     * @return List 获取字典条目明细
     */
    public List<? extends IDataDictionary> getDataDictionaries();
}
