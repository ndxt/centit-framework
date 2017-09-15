package com.centit.framework.staticsystem.po;


import com.centit.framework.model.basedata.IDataDictionary;


/**
 * FDatadictionary entity.
 *
 * @author MyEclipse Persistence Tools
 * @author codefan
 */
//数据字典表
public class DataDictionary implements IDataDictionary, java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;

    private String catalogCode; // 类别代码

    private String dataCode; // 数据代码
    
    private String extraCode; // 附加代码1


    private String extraCode2; // 附加代码2


    private String dataTag; // 标志符

    private String dataValue; // 数据值

 
    private String dataStyle; // 属性

  
    private Integer dataOrder;


    private String dataDesc; // 备注
    

    /**
     * default constructor
     */
    public DataDictionary() {       
        dataTag = "N";
    }

  
    public DataDictionary(String catalogcode, String datacode, String extracode,
                          String extracode2, String datatag, String datavalue,
                          String datastyle, String datadesc) {
         this.catalogCode = catalogcode;
         this.dataCode = datacode;
        this.extraCode = extracode;
        this.extraCode2 = extracode2;
        this.dataTag = datatag;
        this.dataValue = datavalue;
        this.dataStyle = datastyle;
        this.dataDesc = datadesc;
    }

    // Property accessors

  

    public String getDataCode() {

        return this.dataCode;
    }

    public void setDataCode(String datacode) {
        this.dataCode= datacode;
    }

    public String getCatalogCode() {
        return this.catalogCode;
    }

    public void setCatalogCode(String catalogcode) {
        this.catalogCode=catalogcode;
    }

    public String getExtraCode() {
        return this.extraCode;
    }

    public void setExtraCode(String extracode) {
        this.extraCode = extracode;
    }

    public String getExtraCode2() {
        return this.extraCode2;
    }

    public void setExtraCode2(String extracode2) {
        this.extraCode2 = extracode2;
    }

    public String getDataTag() {
        return this.dataTag;
    }

    public String getState() {
        if(this.dataTag == null)
            this.dataTag = "N";
        return this.dataTag;
    }

    public void setDataTag(String datastate) {
        this.dataTag = datastate;
    }

    public String getDataValue() {
        return this.dataValue;
    }
    
    @Override
    public String getLocalDataValue(String lang) {
         return this.dataValue;
    }
 
    public String getFullKey() {
        return this.catalogCode + "." + this.dataCode;
    }

    public void setDataValue(String datavalue) {
        this.dataValue = datavalue;
    }

    public String getDataStyle() {
        return this.dataStyle;
    }

    public void setDataStyle(String datastyle) {
        this.dataStyle = datastyle;
    }

    public String getDataDesc() {
        return this.dataDesc;
    }

    public void setDataDesc(String datadesc) {
        this.dataDesc = datadesc;
    }
    
  

    public void copy(DataDictionary other) {

        this.extraCode = other.getExtraCode();
        this.extraCode2 = other.getExtraCode2();
        this.dataTag = other.getDataTag();
        this.dataValue = other.getDataValue();
        this.dataStyle = other.getDataStyle();
        this.dataDesc = other.getDataDesc();
    }

    public void copyNotNullProperty(DataDictionary other) {

        if (other.getExtraCode() != null)
            this.extraCode = other.getExtraCode();
        if (other.getExtraCode2() != null)
            this.extraCode2 = other.getExtraCode2();
        if (other.getDataTag() != null)
            this.dataTag = other.getDataTag();
        if (other.getDataValue() != null)
            this.dataValue = other.getDataValue();
        if (other.getDataStyle() != null)
            this.dataStyle = other.getDataStyle();
        if (other.getDataDesc() != null)
            this.dataDesc = other.getDataDesc();
    }

    public String display() {
        return "字典明细信息 [" + "标记=" + dataTag
                + ", 标志符=" + dataValue + ", 类型=" + dataStyle
                + ", 数据描述=" + dataDesc + "]";
    }

    public Integer getDataOrder() {
        return dataOrder;
    }

    public void setDataOrder(Integer dataOrder) {
        this.dataOrder = dataOrder;
    }



  }