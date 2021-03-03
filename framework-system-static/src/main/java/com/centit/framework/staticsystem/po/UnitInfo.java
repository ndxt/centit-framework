package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.support.algorithm.DatetimeOpt;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * FUnitinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 机构信息表
public class UnitInfo implements IUnitInfo, java.io.Serializable{
    // Fields
    private static final long serialVersionUID = -2538006375160615889L;


    private String unitCode; // 机构代码


    private String parentUnit; // 上级机构代码


    private String unitType; // 机构类别


    private String isValid; // 状态

    private String unitName;// 机构名称


    private String englishName;// 机构英文名称


    private String unitShortName;


    private String unitWord;//机构自定义编码


    private String unitTag;//机构标识用于第三方系统关联


    private String unitDesc; // 机构描述


    private Long unitOrder; // 机构排序


    private Long unitGrade;//机构等级


    private String depNo;

    private String topUnit;

    private String socialCreditCode;

    private String unitPath;

    private String unitManager;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updator;

    // Constructors
    /**
     * default constructor
     */
    public UnitInfo() {

    }

    /**
     * minimal constructor
     * @param unitcode String
     * @param unitstate String
     * @param unitname String
     */
    public UnitInfo(String unitcode, String unitstate, String unitname) {
        this.unitCode = unitcode;
        this.isValid = unitstate;
        this.unitName = unitname;

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

    @Override
    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }

    @Override
    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */

    public Date getCreateDate() {
        return DatetimeOpt.currentUtilDate();
    }

    /**
     * 获取最后更新时间戳
     *
     * @return 最后更新时间戳
     */

    public Date getLastModifyDate() {
        return DatetimeOpt.currentUtilDate();
    }

    /**
     * 获取创建人
     * @return 创建人Code
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 获取更新人
     * @return 更新人Code
     */

    public String getUpdator() {
        return updator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public void copy(UnitInfo other) {
        this.parentUnit = other.getParentUnit();
        this.unitType = other.getUnitType();
        this.isValid = other.getIsValid();
        this.unitName = other.getUnitName();
        this.unitShortName = other.getUnitShortName();
        this.unitDesc = other.getUnitDesc();
        this.unitOrder = other.getUnitOrder();
        this.depNo = other.getDepNo();
        this.unitWord = other.getUnitWord();
        this.unitGrade = other.getUnitGrade();
        this.unitTag = other.getUnitTag();
        this.englishName =other.getEnglishName();
        this.unitPath =other.getUnitPath();
        this.topUnit =other.getTopUnit();
        this.socialCreditCode =other.getSocialCreditCode();
    }

    public void copyNotNullProperty(UnitInfo other) {

        if (other.getUnitCode() != null)
            this.unitCode = other.getUnitCode();
        if (other.getParentUnit() != null)
            this.parentUnit = other.getParentUnit();
        if (other.getUnitType() != null)
            this.unitType = other.getUnitType();
        if (other.getIsValid() != null)
            this.isValid = other.getIsValid();
        if (other.getUnitName() != null)
            this.unitName = other.getUnitName();
        if (other.getUnitDesc() != null)
            this.unitDesc = other.getUnitDesc();
        if (other.getUnitShortName() != null)
            this.unitShortName = other.getUnitShortName();
        if (other.getUnitOrder() != null)
            this.unitOrder = other.getUnitOrder();
        if (other.getUnitWord() != null)
            this.unitWord = other.getUnitWord();
        if (other.getUnitGrade() != null)
            this.unitGrade = other.getUnitGrade();
        if (other.getDepNo() != null)
            this.depNo = other.getDepNo();
        if (other.getUnitTag() != null)
            this.unitTag = other.getUnitTag();
        if (other.getEnglishName() != null)
            this.englishName =other.getEnglishName();
        if (other.getUnitPath() != null)
            this.unitPath =other.getUnitPath();
        if (other.getTopUnit() != null)
            this.topUnit =other.getTopUnit();
        if (other.getSocialCreditCode() != null)
            this.socialCreditCode =other.getSocialCreditCode();
    }

    public static String[] field2Name(Field[] f) {
        String[] name = new String[f.length];
        for (int i = 0; i < f.length; i++) {
            name[i] = f[i].getName();
        }
        return name;
    }

    public static Object[] field2Value(Field[] f, UserInfo o)
            throws IllegalArgumentException, IllegalAccessException {
        Object[] value = new Object[f.length];
        for (int i = 0; i < f.length; i++) {
            value[i] = f[i].get(o);
        }
        return value;
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


    public String getUnitPath() {
        return unitPath;
    }

    public void setUnitPath(String unitPath) {
        this.unitPath = unitPath;
    }

    public String getUnitManager() {
        return unitManager;
    }

    public void setUnitManager(String unitManager) {
        this.unitManager = unitManager;
    }
}
