package com.centit.framework.model.basedata;

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
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FUnitinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 机构信息表
@Entity
@Table(name = "F_UNITINFO")
@ApiModel(value="系统机构信息对象",description="系统机构信息对象 UnitInfo")
public class UnitInfo implements java.io.Serializable{
    // Fields
    private static final long serialVersionUID = -2538006375160615889L;

    @Id
    @Column(name = "UNIT_CODE")
    @ValueGenerator(strategy = GeneratorType.RANDOM_ID, value = "11:D")
    @ApiModelProperty(value = "机构代码",name = "unitCode")
    private String unitCode; // 机构代码

    @Column(name = "PARENT_UNIT")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="parentUnitName",value="unitCode")
    @ApiModelProperty(value = "上级机构代码",name = "parentUnit")
    private String parentUnit; // 上级机构代码

    //机构、租户、虚拟组
    @Column(name = "UNIT_TYPE")
    @Length(max = 1, message = "字段长度必须为{max}")
    @DictionaryMap(fieldName="unitTypeText",value="UnitType")
    @ApiModelProperty(value = "机构类别",name = "unitType")
    private String unitType; // 机构类别

    @Column(name = "IS_VALID")
    @NotBlank(message = "字段不能为空")
    @Pattern(regexp = "[TFA]", message = "字段值必须是T或F,A为新建可以删除")
    @ApiModelProperty(value = "是否生效 T:生效 F:失效 A:新建可以删除",name = "isValid",required = true)
    private String isValid; // 状态

    @Column(name = "UNIT_NAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 300, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构名称",name = "unitName",required = true)
    private String unitName;// 机构名称

    @Column(name = "SOCIAL_CREDIT_CODE")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "统一社会信用代码",name = "socialCreditCode",required = true)
    private String socialCreditCode;

    @Column(name = "ENGLISH_NAME")
    @Length(max = 300, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构英文名称",name = "englishName",required = true)
    private String englishName;// 机构英文名称

    @Column(name = "UNIT_SHORT_NAME")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构简称",name = "unitShortName",required = true)
    private String unitShortName;

    @Column(name = "UNIT_WORD")
    @Length(max = 100, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构自定义编码，用户和第三方对接",name = "unitWord")
    private String unitWord;//机构自定义编码

    @Column(name = "UNIT_TAG")
    @Length(max = 2200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构标签",name = "unitTag",required = true)
    private String unitTag;//机构标签

    @Column(name = "UNIT_DESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "机构描述",name = "unitDesc")
    private String unitDesc; // 机构描述

    @Column(name = "UNIT_ORDER")
    @Range(max = 9999,min=1, message = "排序号不能大于{max}或小于{min}")
    @OrderBy
    private Long unitOrder; // 机构排序

    @Column(name = "UNIT_GRADE")
    @Range(max = 9999, message = "等级不能大于{max}")
    private Long unitGrade;//机构等级

    @Column(name = "DEP_NO")// 机构编码
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String depNo;

    /**
     * 顶级机构，用于帐套、租户管理
     */
    @Column(name = "TOP_UNIT")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String topUnit; // 顶级机构，用于帐套

    @Column(name = "UNIT_PATH")// 机构路径
    @Length(max = 1000, message = "字段长度不能大于{max}")
    private String unitPath;

    @Transient
    private String state;

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    @Column(name = "UNIT_MANAGER")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String unitManager; // 部门负责人；租户所有人

    public String getUnitManager() {
        return unitManager;
    }

    public void setUnitManager(String unitManager) {
        this.unitManager = unitManager;
    }

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy= GeneratorType.FUNCTION, value="today()")
    protected Date createDate;

    /**
     * CREATOR(创建人) 创建人
     */
    @Column(name = "CREATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String  creator;
       /**
     * UPDATOR(更新人) 更新人
     */
    @Column(name = "UPDATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String  updator;
    /**
     * UPDATEDATE(更新时间) 更新时间
     */
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date  updateDate;


    // private Set<String> subUsers; //所有下属用户代码集合
    @Transient
    private List<UserUnit> unitUsers;

    @Transient
    private List<UnitInfo> subUnits;

    // Constructors

    /**
     * default constructor
     */
    public UnitInfo() {
        this.unitUsers = null;
    }

    public UnitInfo(String unitcode, String unitstate, String unitname) {
        this.unitCode = unitcode;
        this.isValid = unitstate;
        this.unitName = unitname;
        this.unitUsers = null;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getUnitTag() {
        return unitTag;
    }

    public void setUnitTag(String unitTag) {
        this.unitTag = unitTag;
    }

    // Property accessors
    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitCode(String unitcode) {
        this.unitCode = unitcode;
    }

    public String getUnitShortName() {
        return unitShortName;
    }

    public void setUnitShortName(String unitshortname) {
        this.unitShortName = unitshortname;
    }

    public String getParentUnit() {
        return this.parentUnit;
    }

    public void setParentUnit(String parentunit) {
        this.parentUnit = parentunit;
    }

    public String getUnitType() {
        return this.unitType;
    }

    public void setUnitType(String unittype) {
        this.unitType = unittype;
    }

    public String getIsValid() {
        return this.isValid;
    }

    public void setIsValid(String unitstate) {
        this.isValid = unitstate;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public String toString() {
        return this.unitName;
    }

    public void setUnitName(String unitname) {
        this.unitName = unitname;
    }

    public String getUnitDesc() {
        return this.unitDesc;
    }

    public void setUnitDesc(String unitdesc) {
        this.unitDesc = unitdesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifyDate() {
        return updateDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }

    public Long getUnitOrder() {
        return unitOrder;
    }

    public void setUnitOrder(Long unitorder) {
        this.unitOrder = unitorder;
    }

    public String getUnitWord() {
        return unitWord;
    }

    public void setUnitWord(String unitWord) {
        this.unitWord = unitWord;
    }

    public Long getUnitGrade() {
        return unitGrade;
    }

    public void setUnitGrade(Long unitGrade) {
        this.unitGrade = unitGrade;
    }

    public String getDepNo() {
        return depNo;
    }

    public void setDepNo(String depNo) {
        this.depNo = depNo;
    }

    @JSONField(name="children")
    public List<UnitInfo> getSubUnits() {
        if(subUnits==null) {
          subUnits = new ArrayList<>();
        }
        return subUnits;
    }

    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }
/*public List<UnitInfo> getChildren() {
        if(subUnits==null)
            subUnits = new ArrayList<>();
        return subUnits;
    }*/

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return this.updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUnitPath() {
        return unitPath;
    }

    public void setUnitPath(String unitPath) {
        this.unitPath = unitPath;
    }
}
