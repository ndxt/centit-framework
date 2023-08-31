package com.centit.framework.model.basedata;

import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

/**
 * FRolepower entity.
 *
 * @author MyEclipse Persistence Tools
 */

// 角色操作权限表
@Entity
@Table(name = "F_ROLEPOWER")
@ApiModel(value="系统角色操作权限对象",description="系统角色操作权限对象 RolePower")
public class RolePower implements java.io.Serializable{

    // Fields
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RolePowerId id; // 主键id

    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    protected Date createDate;

    @Column(name = "OPT_SCOPE_CODES")
    @Length(max = 1000, message = "字段长度必须小于{max}")
    private String optScopeCodes;// 操作代码
    // Constructors

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

    private String topUnit;

    public String getTopUnit() {
        return topUnit;
    }

    public void setTopUnit(String topUnit) {
        this.topUnit = topUnit;
    }

    /**
     * default constructor
     */
    public RolePower() {
    }

    /**
     * full constructor
     * @param id RolePowerId
     */
    public RolePower(RolePowerId id) {
        this.id = id;
        this.createDate = DatetimeOpt.currentUtilDate();
    }

    public RolePower(RolePowerId id,String optScopeCodes) {
        this.id = id;
        this.optScopeCodes = optScopeCodes;
        this.createDate = DatetimeOpt.currentUtilDate();
    }

    // Property accessors
    public RolePowerId getId() {
        return this.id;
    }

    public void setId(RolePowerId id) {
        this.id = id;
    }

    public String getRoleCode() {
        if(this.id==null)
            this.id = new RolePowerId();
        return this.id.getRoleCode();
    }

    public void setRoleCode(String rolecode) {
        if(this.id==null)
            this.id = new RolePowerId();
        this.id.setRoleCode(rolecode);
    }

    public String getOptCode() {
        if(this.id==null)
            this.id = new RolePowerId();
        return this.id.getOptCode();
    }

    public void setOptCode(String optcode) {
        if(this.id==null)
            this.id = new RolePowerId();
        this.id.setOptCode(optcode);
    }

    public String getOptScopeCodes() {
        return optScopeCodes;
    }

    public String[] getOptScopeCodeSet() {
        if(optScopeCodes==null)
            return null;
        return optScopeCodes.split(",");
    }

    public void setOptScopeCodes(String optScopeCodes) {
        this.optScopeCodes = optScopeCodes;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || !(obj instanceof RolePower)) {
            return false;
        }
        return getId().equals(((RolePower)obj).getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
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
