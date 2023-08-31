package com.centit.framework.model.basedata;

import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

/**
 * FUserrole entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 用户角色设定
@Entity
@Table(name = "F_UNITROLE")
@ApiModel(value="系统机构角色对象",description="系统机构角色对象 UnitRole")
public class UnitRole implements java.io.Serializable {

    // Fields
    // public final SimpleDateFormat sdfDate = new
    // SimpleDateFormat("yyyy-MM-dd");

    private static final long serialVersionUID = 8079422314053320707L;

    @EmbeddedId
    private UnitRoleId id; // 主键

    @Column(name = "OBTAIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date obtainDate; // 获得角色时间

    @Column(name = "SECEDE_DATE")
    @Temporal(TemporalType.DATE)
    private Date secedeDate;

    @Column(name = "CHANGE_DESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String changeDesc; // 说明

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

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
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date  updateDate;
    //结束


    // Constructors

    /**
     * default constructor
     */
    public UnitRole() {
        this.id = new UnitRoleId();
    }

    /**
     * minimal constructor
     * @param id UserRoleId
     */
    public UnitRole(UnitRoleId id) {
        this.id = id;
    }

    /**
     * full constructor
     *
     * @param id UserRoleId
     * @param obtainDate Date
     * @param changedesc String
     */
    public UnitRole(UnitRoleId id, Date obtainDate, String changedesc) {
        this.id = id;
        this.obtainDate = obtainDate;
        this.changeDesc = changedesc;
    }

    // Property accessors

    public UnitRoleId getId() {
        return this.id;
    }

    public void setId(UnitRoleId id) {
        this.id = id;
    }

    public String getUnitCode() {
        if (this.id == null)
            this.id = new UnitRoleId();
        return this.id.getUnitCode();
    }

    public void setUnitCode(String userCode) {
        if (this.id == null)
            this.id = new UnitRoleId();
        this.id.setUnitCode(userCode);
    }

    public String getRoleCode() {
        if (this.id == null)
            this.id = new UnitRoleId();
        return this.id.getRoleCode();
    }

    public void setRoleCode(String rolecode) {
        if (this.id == null)
            this.id = new UnitRoleId();
        this.id.setRoleCode(rolecode);
    }

    public Date getObtainDate() {
        return this.obtainDate;
    }

    public void setObtainDate(Date obtaindate) {

        this.obtainDate =obtaindate;
    }

    public String getChangeDesc() {
        return this.changeDesc;
    }

    public void setChangeDesc(String changedesc) {
        this.changeDesc = changedesc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getSecedeDate() {
        return secedeDate;
    }

    public void setSecedeDate(Date secedeDate) {
        this.secedeDate = secedeDate;
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

    public Date getLastModifyDate() {
        return updateDate;
  }

    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
  }

}
