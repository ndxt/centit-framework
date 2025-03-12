package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
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
@Table(name = "F_USERROLE")
@ApiModel(value="系统用户角色关联信息对象",description="系统用户角色关联信息对象 UserInfo")
public class UserRole implements java.io.Serializable {
    // Fields
    // public final SimpleDateFormat sdfDate = new
    // SimpleDateFormat("yyyy-MM-dd");

    private static final long serialVersionUID = 8079422314053320707L;
    @JSONField(serialize = false)
    public static final String OS_MEMBER="osmember";
    @JSONField(serialize = false)
    public static final String PLAT_ADMIN="platadmin";
    @JSONField(serialize = false)
    public static final String TENANT_ADMIN="tenantadmin";

    @JSONField(serialize = false, deserialize = false)
    @EmbeddedId
    private UserRoleId id; // 主键

    @Column(name = "OBTAIN_DATE")
    @Temporal(TemporalType.DATE)
    private Date obtainDate; // 获得角色时间

    @Column(name = "SECEDE_DATE")
    @Temporal(TemporalType.DATE)
    private Date secedeDate;

    @Column(name = "CHANGE_DESC")
    @Length(max = 256)
    private String changeDesc; // 说明

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    //创建人、更新人、更新时间
    /**
     * CREATOR(创建人) 创建人
     */
    @Column(name = "CREATOR")
    @Length(max = 32)
    private String  creator;
       /**
     * UPDATOR(更新人) 更新人
     */
    @Column(name = "UPDATOR")
    @Length(max = 32)
    private String  updator;
    /**
     * UPDATEDATE(更新时间) 更新时间
     */
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date  updateDate;
    //结束

    /**
     * 获得类型 从哪获得角色
     */
    @Transient
    private String obtainType;

    @Transient
    private String inheritedFrom;

    public String getObtainType() {
      return obtainType;
    }

    public void setObtainType(String obtainType) {
      this.obtainType = obtainType;
    }

    public String getInheritedFrom() {
      return inheritedFrom;
    }

    public void setInheritedFrom(String inheritedFrom) {
      this.inheritedFrom = inheritedFrom;
    }
    // Constructors

    /**
     * default constructor
     */
    public UserRole() {
        obtainType = "D";
        this.id=new UserRoleId();
    }

    /**
     * minimal constructor
     * @param id UserRoleId
     */
    public UserRole(UserRoleId id) {
        obtainType = "D";
        this.id = id;
    }

    /**
     * full constructor
     *
     * @param id UserRoleId
     * @param obtainDate Date
     * @param changedesc String
     */
    public UserRole(UserRoleId id, Date obtainDate, String changedesc) {
        obtainType = "D";
        this.id = id;
        this.obtainDate = obtainDate;
        this.changeDesc = changedesc;
    }

// Property accessors

    public UserRoleId getId() {
        return this.id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public String getUserCode() {
        if (this.id == null)
            this.id = new UserRoleId();
        return this.id.getUserCode();
    }

    public void setUserCode(String userCode) {
        if (this.id == null)
            this.id = new UserRoleId();
        this.id.setUserCode(userCode);
    }

    public String getRoleCode() {
        if (this.id == null)
            this.id = new UserRoleId();
        return this.id.getRoleCode();
    }

    public void setRoleCode(String rolecode) {
        if (this.id == null)
            this.id = new UserRoleId();
        this.id.setRoleCode(rolecode);
    }

    public Date getObtainDate() {
        return this.obtainDate;
    }

    public void setObtainDate(Date obtaindate) {

        this.obtainDate =obtaindate;
    }

    public Date getSecedeDate() {
        return secedeDate;
    }

    public void setSecedeDate(Date secedeDate) {
        this.secedeDate = secedeDate;
    }
    /*
     * public void setSecededate(String ssecededate) { try { this.secededate =
     * sdfDate.parse(ssecededate); } catch (ParseException e) {
     * e.printStackTrace(); } }
     */
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
    //结束
}
