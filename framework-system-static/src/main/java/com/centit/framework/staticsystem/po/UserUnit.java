package com.centit.framework.staticsystem.po;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.model.basedata.IUserUnit;

import java.util.Date;

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

    @DictionaryMap(fieldName="unitName",value="unitCode")
    private String unitCode; // 机构代码

    @DictionaryMap(fieldName="userName",value="userCode")
    private String userCode; // 用户代码

    /*@Transient
    private String userName;*/
    @DictionaryMap(fieldName="userStationText",value="StationType")
    private String userStation; // 岗位

    @DictionaryMap(fieldName="userRankText",value="RankType")
    private String userRank; // 职务

    private String postRank;
    private String rankMemo; // 备注
    private String relType; // 是否为主
    private Long userOrder;    //用户排序号

    /**
     * 用户顶级机构 用户帐套管理，作为 租户的标识
     * 这个属性在创建时指定，并且不能变
     * 在添加机构的时候，需要校验机构的 unitPath 必须包括这个机构
     */
    private String topUnit;

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
        this.relType = isprimary;
    }

    public UserUnit(String id, String userstation, String userrank,
                    String isprimary) {
        this.userUnitId = id;
        this.userStation = userstation;
        this.userRank = userrank;
        this.relType = isprimary;
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

    public void setPostRank(String postRank) {
        this.postRank = postRank;
    }

    /**
     * 用户在本机构的 职级
     *
     * @return 用户在本机构的 职级
     */
    @Override
    public String getPostRank() {
        return this.postRank;
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

    @Override
    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
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
    public String getRelType() {
        return this.relType;
    }
    /**
     * @param isprimary T:主机构 F：辅机构
     */
    public void setRelType(String isprimary) {
        this.relType = isprimary;
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
        this.relType = other.getRelType();
        this.createDate = other.getCreateDate();
        this.rankMemo = other.getRankMemo();
        this.userRank = other.getUserRank();
        this.userStation = other.getUserStation();
        this.userCode = other.getUserCode();
        this.unitCode = other.getUnitCode();
        this.userOrder = other.getUserOrder();
        this.topUnit = other.getTopUnit();
     }

    public void copyNotNullProperty(UserUnit other) {

        if (null != other.getRelType()) {
            this.relType = other.getRelType();
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
        if (other.getTopUnit() != null) {
            this.topUnit = other.getTopUnit();
        }
    }
}
