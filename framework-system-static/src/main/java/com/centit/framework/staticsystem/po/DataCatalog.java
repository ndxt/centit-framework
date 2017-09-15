package com.centit.framework.staticsystem.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.HtmlUtils;

import com.centit.framework.model.basedata.IDataCatalog;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */

public class DataCatalog implements IDataCatalog,java.io.Serializable{
    private static final long serialVersionUID = 1L;

 
    private String catalogCode; // 类别代码

  
    private String catalogName;// 类别名称

  
    private String catalogStyle;// 类别状态

    private String catalogType;// 类别形式


    private String catalogDesc;// 类别描述

 
    private String fieldDesc; // 字典字段描述

 
    // 默认值为1如何设置？
    private String needCache; // 是否需要缓存


    private String optId;

 
    private List<DataDictionary> dataDictionaries;

    // Constructors
    public DataCatalog(String catalogcode, String catalogname, String catalogstyle, String catalogtype, String isupload) {

        this.catalogCode = catalogcode;

        this.catalogName = catalogname;
        this.catalogStyle = catalogstyle;
        this.catalogType = catalogtype;
        this.needCache = "1";
    }

    public DataCatalog(String catalogcode, String catalogname, String catalogstyle, String catalogtype,
                       String catalogdesc, String isupload, String needCache, String fielddesc) {

        this.catalogCode = catalogcode;

        this.catalogName = catalogname;
        this.catalogStyle = catalogstyle;
        this.catalogType = catalogtype;
        this.catalogDesc = catalogdesc;
        this.fieldDesc = fielddesc;
        this.needCache = needCache;
    }

 
    public String getCatalogCode() {
        return this.catalogCode;
    }

    public void setCatalogCode(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    // Property accessors

    public String getOptId() {
        return optId;
    }

    public void setOptId(String dictionarytype) {
        this.optId = dictionarytype;
    }

    public String getCatalogName() {
        return this.catalogName;
    }

    public void setCatalogName(String catalogname) {
        this.catalogName = catalogname;
    }

    public String getCatalogStyle() {
        return this.catalogStyle;
    }

    public void setCatalogStyle(String catalogstyle) {
        this.catalogStyle = catalogstyle;
    }

    public String getCatalogType() {
        return this.catalogType;
    }

    public void setCatalogType(String catalogtype) {
        this.catalogType = catalogtype;
    }

    public String getCatalogDesc() {
        return this.catalogDesc;
    }

    public void setCatalogDesc(String catalogdesc) {
        this.catalogDesc = catalogdesc;
    }

    public String getFieldDesc() {
        return this.fieldDesc;
    }

    public void setFieldDesc(String fielddesc) {

        if (StringUtils.isNotBlank(fielddesc)) {
            fielddesc = HtmlUtils.htmlUnescape(fielddesc);
        }

        this.fieldDesc = fielddesc;
    }

    public void setIsUpload(String isupload) {
    }

    /**
     * @return the needCache
     */
    public String getNeedCache() {
        return needCache;
    }

    /**
     * @param needCache the needCache to set
     */
    public void setNeedCache(String needCache) {
        this.needCache = needCache;
    }
  
    //结束
    public void copy(DataCatalog other) {

        this.catalogName = other.getCatalogName();
        this.catalogStyle = other.getCatalogStyle();
        this.catalogType = other.getCatalogType();
        this.catalogDesc = other.getCatalogDesc();
        this.fieldDesc = other.getFieldDesc();
        this.optId = other.getOptId();
        this.needCache = other.getNeedCache();
 
    }

    public void copyNotNullProperty(DataCatalog other) {

        if (other.getCatalogName() != null)
            this.catalogName = other.getCatalogName();
        if (other.getCatalogStyle() != null)
            this.catalogStyle = other.getCatalogStyle();
        if (other.getCatalogType() != null)
            this.catalogType = other.getCatalogType();
        if (other.getCatalogDesc() != null)
            this.catalogDesc = other.getCatalogDesc();
        if (other.getFieldDesc() != null)
            this.fieldDesc = other.getFieldDesc();
        if (other.getOptId() != null) {
            this.optId = other.getOptId();
            this.needCache = other.getOptId() == null ? "1" : other.getNeedCache();
        }
   
    }


    public void addAllDataPiece(List<DataDictionary> dataDictionaries) {
        getDataDictionaries().clear();

        if (CollectionUtils.isEmpty(dataDictionaries)) {
            return;
        }

        for (DataDictionary dataDictionary : dataDictionaries) {
            dataDictionary.setCatalogCode(this.catalogCode);
            getDataDictionaries().add(dataDictionary);
        }
    }

    public DataCatalog addDataDictionary(DataDictionary dd) {
        getDataDictionaries().add(dd);
        return this;
    }
    
    public List<DataDictionary> getDataDictionaries() {
        if (null == dataDictionaries) {
            dataDictionaries = new ArrayList<>();
        }
        return dataDictionaries;
    }

    public void setDataDictionaries(List<DataDictionary> dataDictionaries) {
        this.dataDictionaries = dataDictionaries;
    }

    public DataCatalog() {
    }

}
