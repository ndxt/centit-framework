package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IUserUnit;

/**
 * FUserunit entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 用户所属机构表
@Entity
@Table(name = "F_USERUNIT")
public class UserUnit implements IUserUnit, EntityWithTimestamp, java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USERUNITID")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    @Length(max = 64)
    private String userUnitId;
    
    @Column(name = "UNITCODE")
    @DictionaryMap(fieldName="unitName",value="unitCode")
    private String unitCode; // 机构代码

    @Column(name = "USERCODE")
    @DictionaryMap(fieldName="userName",value="userCode")
    private String userCode; // 用户代码

    @Column(name = "USERSTATION")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="userStationText",value="StationType")
    private String userStation; // 岗位

    @Column(name = "USERRANK")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="userRankText",value="RankType")
    private String userRank; // 职务 

    @Column(name = "RANKMEMO")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String rankMemo; // 备注

    @Column(name = "ISPRIMARY")
    @NotBlank(message = "字段不能为空")
    @Length(max = 1, message = "字段长度必须为{max}")
    private String isPrimary; // 是否为主

    @Column(name = "USERORDER")
    private Long userOrder;    //用户排序号
    
    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @Transient
    private String unitName; // 机构名称     
    
    //创建人、更新人、更新时间
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
	@Column(name = "UPDATEDATE")
	private Date  updateDate;
	//结束
	
	
    /**
     * 仅在系统缓存中使用
     */
    @Transient
    private int xzRank;

    // Constructors

    /**
     * default constructor
     */
    public UserUnit() {
    }

    public String getUserUnitId() {
        return userUnitId;
    }

    public void setUserUnitId(String userunitid) {
        this.userUnitId = userunitid;
    }
    
    /**
     * minimal constructor
     */
    public UserUnit(String id, String isprimary) {
        this.userUnitId = id;
        this.isPrimary = isprimary;
    }

    /**
     * full constructor
     */
    public UserUnit(String id, String userstation, String userrank,
                    String isprimary) {
        this.userUnitId = id;
        this.userStation = userstation;
        this.userRank = userrank;
        this.isPrimary = isprimary;
    }

    // Property accessors


    public String getUserStation() {
        return this.userStation;
    }

    public void setUserStation(String userstation) {
        this.userStation = userstation;
    }

    public String getUserRank() {
        return this.userRank;
    }

    public void setUserRank(String userrank) {
        this.userRank = userrank;
    }

    // Property accessors

    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitCode(String unitcode) {
        this.unitCode = unitcode;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    
    public String getRankMemo() {
        return this.rankMemo;
    }

    public void setRankMemo(String rankmemo) {
        this.rankMemo = rankmemo;
    }

    /**
     * T:主机构 F：辅机构
     * @return
     */
    public String getIsPrimary() {
        return this.isPrimary;
    }
    /**
     * @param isprimary T:主机构 F：辅机构
     */
    public void setIsPrimary(String isprimary) {
        this.isPrimary = isprimary;
    }

   
    public void setUnitName(String unitname) {
        this.unitName = unitname;
    }

    public int getXzRank() {
        return xzRank;
    }

    public void setXzRank(int xzRank) {
        this.xzRank = xzRank;
    }

    public Long getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Long userorder) {
        this.userOrder = userorder;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void copy(UserUnit other) {
    	this.userUnitId  = other.getUserUnitId();
    	this.xzRank = other.getXzRank();
        this.isPrimary = other.getIsPrimary();
        this.createDate = other.getCreateDate();
        this.rankMemo = other.getRankMemo();
        this.userRank = other.getUserRank();
        this.userStation = other.getUserStation();
        this.userCode = other.getUserCode();
        this.unitCode = other.getUnitCode();
        this.userOrder = other.getUserOrder();
        this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
     }

    public void copyNotNullProperty(UserUnit other) {

    	this.xzRank = other.getXzRank();
    	
        if (null != other.getIsPrimary()) {
            this.isPrimary = other.getIsPrimary();
        }
        if (null != other.getCreateDate()) {
            this.createDate = other.getCreateDate();
        }
        if (null != other.getRankMemo()) {
            this.rankMemo = other.getRankMemo();
        }
        if (null != other.getUserRank()) {
            this.userRank = other.getUserRank();
        }
        if (null != other.getUserStation()) {
            this.userStation = other.getUserStation();
        }
        if (null != other.getUserCode()) {
            this.userCode = other.getUserCode();
        }
        if (null != other.getUnitCode()) {
            this.unitCode = other.getUnitCode();
        }
        if (null != other.getUserOrder()) {
            this.userOrder = other.getUserOrder();
        }
        if (other.getCreator() != null)
        	this.creator =other.getCreator();
        if (other.getUpdator() != null)
        	this.updator =other.getUpdator();
        if (other.getUpdateDate() != null)
        	this.updateDate =other.getUpdateDate();
    }
    
    //创建人、更新人、更新时间
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

  	@Override
      public Date getLastModifyDate() {
          return updateDate;
    }

  	@Override
      public void setLastModifyDate(Date lastModifyDate) {
          this.updateDate = lastModifyDate;
    }
    //结束
}
