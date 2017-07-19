package com.centit.framework.system.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * FDatadictionaryId entity.
 *
 * @author MyEclipse Persistence Tools
 * @author codefan
 */
//数据字典的主键
@Embeddable
public class DataDictionaryId implements java.io.Serializable {
    private static final long serialVersionUID = -477457533900099603L;
    // Fields
    @Column(name = "CATALOGCODE")
    @NotNull(message = "字段不能为空")
    private String catalogCode; // 类别代码

    @Column(name = "DATACODE")
    @NotNull(message = "字段不能为空")
    private String dataCode; // 数据代码

    // Constructors

    /**
     * default constructor
     */
    public DataDictionaryId() {
    }

    public DataDictionaryId(String catalogcode, String datacode) {
        this.catalogCode = catalogcode;
        this.dataCode = datacode;
    }

    // Property accessors

    public String getCatalogCode() {
        return this.catalogCode;
    }

    public void setCatalogCode(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    public String getDataCode() {
        return this.dataCode;
    }

    public void setDataCode(String datacode) {
        this.dataCode = datacode;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof DataDictionaryId))
            return false;
        DataDictionaryId castOther = (DataDictionaryId) other;

        return ((this.getCatalogCode() == castOther.getCatalogCode()) || (this
                .getCatalogCode() != null
                && castOther.getCatalogCode() != null && this.getCatalogCode()
                .equals(castOther.getCatalogCode())))
                && ((this.getDataCode() == castOther.getDataCode()) || (this
                .getDataCode() != null
                && castOther.getDataCode() != null && this
                .getDataCode().equals(castOther.getDataCode())));
    }

    public int hashCode() {
        int result = 17;

        result = 37
                * result
                + (getCatalogCode() == null ? 0 : this.getCatalogCode()
                .hashCode());
        result = 37 * result
                + (getDataCode() == null ? 0 : this.getDataCode().hashCode());
        return result;
    }

}