package com.centit.framework.model.basedata;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * FDatadictionary entity.
 *
 * @author MyEclipse Persistence Tools
 * @author codefan
 */
//数据字典表
@Entity
@Table(name = "F_DATADICTIONARY")
@ApiModel(value="数据字典对象",description="数据字典类别对象DataDictionary")
public class DataDictionary implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = -4063651885248484498L;

    @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    private DataDictionaryId id; // 主键id

    @Column(name = "EXTRA_CODE")
    @Length(max = 16)
    private String extraCode; // 附加代码1

    @Column(name = "EXTRA_CODE2")
    @Length(max = 16)
    private String extraCode2; // 附加代码2

    @Column(name = "DATA_TAG")
    @ValueGenerator(strategy = GeneratorType.CONSTANT, occasion = GeneratorTime.NEW, value = "T")
    @Length(max = 1)
    @ApiModelProperty(value = "标志符字段不能为空，长度为1",name = "dataTag",required = true)
    private String dataTag; // 标志符

    @Column(name = "DATA_VALUE")
    @NotBlank()
    @Length(max = 2048)
    @ApiModelProperty(value = "数据值字段不能为空，字段长度不能大于2048",name = "dataValue",required = true)
    private String dataValue; // 数据值

    @Transient
    private JSONObject jsonData;

    @Column(name = "DATA_STYLE")
    @ValueGenerator(strategy = GeneratorType.CONSTANT, occasion = GeneratorTime.NEW, value = "U")
    @Length(max = 1)
    @Pattern(regexp = "[SUF]")
    @ApiModelProperty(value = "数据值字段不能为空，字段长度不能大于1，字段只能填写F,S,U",name = "dataStyle",required = true)
    @DictionaryMap(fieldName = "dataStyle", value = "CatalogStyleText")
    private String dataStyle; // 属性

    @OrderBy
    @Column(name = "DATA_ORDER")
    //@Pattern(regexp = "\\d{0,6}", message = "字段只能填写数字")
    private Integer dataOrder;

    @Column(name = "DATA_DESC")
    @Length(max = 256)
    private String dataDesc; // 备注

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW,
        condition = GeneratorCondition.IFNULL, value="today()" )
    protected Date createDate;

    @Column(name = "LAST_MODIFY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
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
     *
     * @param id DataDictionaryId
     */
    public DataDictionary(DataDictionaryId id) {
        this.id = id;
        dataTag = "N";
    }

    /**
     * full constructor
     * @param id DataDictionaryId
     * @param extracode String
     * @param extracode2  String
     * @param datatag String
     * @param datavalue String
     * @param datastyle String
     * @param datadesc String
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
            if(lang==null) {
                sRet = jsonData.getString("zh_CN");
            } else {
                sRet =jsonData.getString(lang);
                if(sRet==null) {
                    sRet = jsonData.getString("zh_CN");
                }
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

    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
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
