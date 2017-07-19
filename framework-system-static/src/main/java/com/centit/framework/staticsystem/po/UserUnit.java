package com.centit.framework.staticsystem.po;

import java.util.Date;

import com.centit.framework.model.basedata.IUserUnit;

/**
 * FUserunit entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 用户所属机构表
public class UserUnit implements IUserUnit, java.io.Serializable {

    // Fields
    private static final long serialVersionUID = 1L;

 
    private String userUnitId;

    private String unitCode; // 机构代码


    private String userCode; // 用户代码

    /*@Transient
    private String userName;*/

    private String userStation; // 岗位

 
    private String userRank; // 职务 


    private String rankMemo; // 备注

 
    private String isPrimary; // 是否为主


    private Long userOrder;    //用户排序号
    

    protected Date createDate;

  
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
     * @param id String
     * @param isprimary String
     */
    public UserUnit(String id, String isprimary) {
        this.userUnitId = id;
        this.isPrimary = isprimary;
    }

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
     * @return T:主机构 F：辅机构
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
        this.isPrimary = other.getIsPrimary();
        this.createDate = other.getCreateDate();
        this.rankMemo = other.getRankMemo();
        this.userRank = other.getUserRank();
        this.userStation = other.getUserStation();
        this.userCode = other.getUserCode();
        this.unitCode = other.getUnitCode();
        this.userOrder = other.getUserOrder();
     }

    public void copyNotNullProperty(UserUnit other) {
   	
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

    }
}
