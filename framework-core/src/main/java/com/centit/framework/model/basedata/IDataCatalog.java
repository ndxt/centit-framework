package com.centit.framework.model.basedata;

import java.util.List;

/**
 * @author codefan@hotmail.com
 */

public interface IDataCatalog{
 
	/**
	 * 类别代码
	 * @return
	 */
    public String getCatalogCode();
    /**
     * 类别名称
     * @return
     */
    public String getCatalogName();
    /**
     * 所属业务ID
     * @return
     */
    public String getOptId();

    /**
     * 类别形式  L 列表 T 为 树
     * @return
     */
    public String getCatalogType();

    /**
     * 字典描述
     * @return
     */
    public String getCatalogDesc();
    /**
     * 字典明细字段描述
     * @return
     */
    public String getFieldDesc();
    
    /**
     * 获取字典条目明细
     * @return
     */
    public List<? extends IDataDictionary> getDataDictionaries();
}
