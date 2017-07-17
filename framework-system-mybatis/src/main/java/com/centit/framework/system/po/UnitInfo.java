package com.centit.framework.system.po;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IUnitInfo;

/**
 * FUnitinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 机构信息表
@Entity
@Table(name = "F_UNITINFO")
public class UnitInfo implements IUnitInfo,EntityWithTimestamp, java.io.Serializable{
    // Fields
    private static final long serialVersionUID = -2538006375160615889L;

    @Id
    @Column(name = "UNITCODE")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String unitCode; // 机构代码

    @Column(name = "PARENTUNIT")
    @Size(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="parentUnitName",value="unitCode")
    private String parentUnit; // 上级机构代码

    @Column(name = "UNITTYPE")
    @Size(max = 1, message = "字段长度必须为{max}")
    @DictionaryMap(fieldName="unitTypeText",value="UnitType")
    private String unitType; // 机构类别

    @Column(name = "ISVALID")
    @NotNull(message = "字段不能为空")
    @Pattern(regexp = "[TFA]", message = "字段值必须是T或F,A为新建可以删除")
    private String isValid; // 状态

    @Column(name = "UNITNAME")
    @NotNull(message = "字段不能为空")
    @Size(max = 300, message = "字段长度不能大于{max}")
    private String unitName;// 机构名称

    @Column(name = "ENGLISHNAME")
    @Size(max = 300, message = "字段长度不能大于{max}")
    private String englishName;// 机构英文名称      
    
    @Column(name = "UNITSHORTNAME")
    @Size(max = 32, message = "字段长度不能大于{max}")
    private String unitShortName;

    @Column(name = "UNITWORD")
    @Size(max = 100, message = "字段长度不能大于{max}")
    private String unitWord;//机构自定义编码

    @Column(name = "UNITTAG")
    @Size(max = 100, message = "字段长度不能大于{max}")
    private String unitTag;//机构标识用于第三方系统关联
    
    @Column(name = "UNITDESC")
    @Size(max = 256, message = "字段长度不能大于{max}")
    private String unitDesc; // 机构描述

    @Column(name = "ADDRBOOKID")
    //@Range(max = 999999999, message = "通讯主键不能大于{max}")
    private Long addrbookId; // 通讯主体id

    @Column(name = "UNITORDER")
    //@Range(max = 9999, message = "排序号不能大于{max}")
    private Long unitOrder; // 机构排序

    @Column(name = "UNITGRADE")
    //@Range(max = 9999, message = "等级不能大于{max}")
    private Long unitGrade;//机构等级

    @Column(name = "DEPNO")// 机构编码
    @Size(max = 100, message = "字段长度不能大于{max}")
    private String depNo;
        
    @Column(name = "UNITPATH")// 机构编码
    @Size(max = 1000, message = "字段长度不能大于{max}")
    private String unitPath;
    
    @Transient
    private String state;
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
	@Column(name = "UNITMANAGER")
    @Size(max = 32, message = "字段长度不能大于{max}")
    private String unitManager; // 部门负责人
	
	public String getUnitManager() {
		return unitManager;
	}

	public void setUnitManager(String unitManager) {
		this.unitManager = unitManager;
	}

	@Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
    
    /**
	 * CREATOR(创建人) 创建人 
	 */
	@Column(name = "CREATOR")
	@Size(max = 32, message = "字段长度不能大于{max}")
	private String  creator;
	   /**
	 * UPDATOR(更新人) 更新人 
	 */
	@Column(name = "UPDATOR")
	@Size(max = 32, message = "字段长度不能大于{max}")
	private String  updator;
	/**
	 * UPDATEDATE(更新时间) 更新时间 
	 */
	@Column(name = "UPDATEDATE")
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
        unitUsers = null;
    }

    /**
     * minimal constructor
     */
    public UnitInfo(String unitcode, String unitstate, String unitname) {
        this.unitCode = unitcode;
        this.isValid = unitstate;
        this.unitName = unitname;
        unitUsers = null;
    }

    public UnitInfo(String unitcode, String parentunit, String unittype,
                    String isvalid, String unitname, String unitshortname, String unitword,
                    String unitdesc, Long addrbookid, Long grade, Long unitorder, String depno,
                    Date createDate, Date lastModifyDate) {
        super();
        this.unitCode = unitcode;
        this.parentUnit = parentunit;
        this.unitType = unittype;
        this.isValid = isvalid;
        this.unitName = unitname;
        this.unitShortName = unitshortname;
        this.unitWord = unitword;
        this.unitDesc = unitdesc;
        this.addrbookId = addrbookid;
        this.unitGrade = grade;
        this.unitOrder = unitorder;
        this.depNo = depno;
    }

    /**
     * full constructor
     */
    public UnitInfo(String unitcode, String parentunit, String unittype,
                    String unitstate, String unitname, String unitdesc,
                    Long addrbookid, String unitshortname, String depno, 
                    String unittag, String englishname,String unitword, Long unitgrade) {
        this.unitCode = unitcode;
        this.parentUnit = parentunit;
        this.unitType = unittype;
        this.isValid = unitstate;
        this.unitName = unitname;
        this.unitDesc = unitdesc;
        this.addrbookId = addrbookid;
        this.unitShortName = unitshortname;
        this.depNo = depno;
        this.unitWord = unitword;
        this.unitGrade = unitgrade;
        this.unitTag = unittag;
        this.englishName = englishname; 
        unitUsers = null;
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

    public Long getAddrbookId() {
        return addrbookId;
    }

    public void setAddrbookId(Long addrbookid) {
        this.addrbookId = addrbookid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	@Override
    public Date getLastModifyDate() {
        return updateDate;
    }

	@Override
    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }
	
	
    //Json格式化时循环引用，此方法返回值，不序列化
    @JSONField(serialize = false)
    public List<UserUnit> getUnitUsers() {
        if (unitUsers == null)
            unitUsers = new ArrayList<UserUnit>();
        return unitUsers;
    }

    public void setSubUserUnits(List<UserUnit> SUs) {
        this.unitUsers = SUs;
    }


    public void copy(UnitInfo other) {
        this.parentUnit = other.getParentUnit();
        this.unitType = other.getUnitType();
        this.isValid = other.getIsValid();
        this.unitName = other.getUnitName();
        this.unitShortName = other.getUnitShortName();
        this.unitDesc = other.getUnitDesc();
        this.addrbookId = other.getAddrbookId();
        this.unitOrder = other.getUnitOrder();
        this.depNo = other.getDepNo();
        this.unitWord = other.getUnitWord();
        this.unitGrade = other.getUnitGrade();
        this.unitTag = other.getUnitTag();
        this.englishName =other.getEnglishName();
        this.creator=other.getCreator();
        this.updator=other.getUpdator();
        this.updateDate=other.getUpdateDate();
        this.unitPath =other.getUnitPath();
        this.unitManager = other.getUnitManager();
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
        if (other.getAddrbookId() != null)
            this.addrbookId = other.getAddrbookId();
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
        if (other.getCreator() != null)
        	this.creator =other.getCreator();
        if (other.getUpdator() != null)
        	this.updator =other.getUpdator();
        if (other.getUpdateDate() != null)
        	this.updateDate =other.getUpdateDate();
        if (other.getUnitPath() != null)
        	this.unitPath =other.getUnitPath();
        if (other.getUnitManager() != null)
        	this.unitManager = other.getUnitManager();
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

    @JSONField(name="children")
    public List<UnitInfo> getSubUnits() {
    	if(subUnits==null)
    		subUnits = new ArrayList<>();
        return subUnits;
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
