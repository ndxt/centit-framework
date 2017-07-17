package com.centit.framework.system.po;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IDataDictionary;


/**
 * FDatadictionary entity.
 *
 * @author MyEclipse Persistence Tools
 * @author codefan
 */
//数据字典表
@Entity
@Table(name = "F_DATADICTIONARY")
public class DataDictionary implements IDataDictionary,EntityWithTimestamp, java.io.Serializable {

    // Fields
    private static final long serialVersionUID = -4063651885248484498L;
    @EmbeddedId
    private DataDictionaryId id; // 主键id

    @Column(name = "EXTRACODE")
    @Length(max = 16, message = "字段长度不能大于{max}")
    private String extraCode; // 附加代码1

    @Column(name = "EXTRACODE2")
    @Length(max = 16, message = "字段长度不能大于{max}")
    private String extraCode2; // 附加代码2

    @Column(name = "DATATAG")
    @NotBlank(message = "字段不能为空")
    @Length(max = 1, message = "字段长度必须为{max}")
    private String dataTag; // 标志符

    @Column(name = "DATAVALUE")
    @NotBlank(message = "字段不能为空")
    @Length(max = 2048, message = "字段长度不能大于{max}")
    private String dataValue; // 数据值

    @Transient
    private JSONObject jsonData;
    
    @Column(name = "DATASTYLE")
    @NotNull(message = "字段不能为空")
    @Length(max = 1, message = "字段长度必须为{max}")
    @Pattern(regexp = "[SUF]", message = "字段只能填写F,S,U")
    private String dataStyle; // 属性

    @Column(name = "DATAORDER")
    //@Pattern(regexp = "\\d{0,6}", message = "字段只能填写数字")
    private Integer dataOrder;

    @Column(name = "DATADESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String dataDesc; // 备注
    
    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @Column(name = "LASTMODIFYDATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModifyDate;

    /**
     * default constructor
     */
    public DataDictionary() {
        this.id = new DataDictionaryId();
        dataTag = "N";
    }

    /**
     * minimal constructor
     */
    public DataDictionary(DataDictionaryId id) {
        this.id = id;
        dataTag = "N";
    }

    /**
     * full constructor
     */
    public DataDictionary(DataDictionaryId id, String extracode,
                          String extracode2, String datatag, String datavalue,
                          String datastyle, String datadesc) {
        this.id = id;
        this.extraCode = extracode;
        this.extraCode2 = extracode2;
        this.dataTag = datatag;
        this.dataValue = datavalue;
        this.dataStyle = datastyle;
        this.dataDesc = datadesc;
    }

    // Property accessors

    public DataDictionaryId getId() {
        return this.id;
    }

    public void setId(DataDictionaryId id) {
        this.id = id;
    }

    public String getDataCode() {
        if (id == null)
            return null;
        return this.id.getDataCode();
    }

    public void setDataCode(String datacode) {
        if (id == null)
            id = new DataDictionaryId();
        this.id.setDataCode(datacode);
    }

    public String getCatalogCode() {
        if (id == null)
            return null;
        return this.id.getCatalogCode();
    }

    public void setCatalogCode(String catalogcode) {
        if (id == null)
            id = new DataDictionaryId();
        this.id.setCatalogCode(catalogcode);
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

     public String getLocalDataValue(String lang) {
    	if(dataValue==null)
    		return null;
    	if(jsonData==null){
    		if(this.dataValue.startsWith("{") && this.dataValue.endsWith("}"))
    			jsonData=JSON.parseObject(dataValue);
    		else
    			return dataValue;
    	}
    	String sRet=null;
    	if(jsonData!=null){
    		if(lang==null)
    			sRet =jsonData.getString("zh_CN");
    		else{
	    		sRet =jsonData.getString(lang);
		    	if(sRet==null)
		    		sRet =jsonData.getString("zh_CN");
    		}
    	}
    	return sRet;
    }
      
    public String getFullKey() {
        return this.id.getCatalogCode() + "." + this.id.getDataCode();
    }

    public String toString() {
        return this.dataValue;
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
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	@Override
    public Date getLastModifyDate() {
        return lastModifyDate;
    }

	@Override
    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataDictionary that = (DataDictionary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}