package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IRolePower;
import com.centit.support.algorithm.DatetimeOpt;

/**
 * FRolepower entity.
 *
 * @author MyEclipse Persistence Tools
 */

// 角色操作权限表
@Entity
@Table(name = "F_ROLEPOWER")
public class RolePower implements IRolePower, EntityWithTimestamp, java.io.Serializable{

    // Fields
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RolePowerId id; // 主键id
    
    @Column(name = "ROLECODE")
    @NotNull(message = "字段不能为空")
    private String roleCode; //角色代码

    @Column(name = "OPTCODE")
    @NotNull(message = "字段不能为空")
    private String optCode; //操作代码
    
   
    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @Column(name = "OPTSCOPECODES")
    @Size(max = 1000, message = "字段长度必须小于{max}")
    private String optScopeCodes;// 操作代码
    
    // Constructors

    //创建人、更新人、更新时间
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
	//结束
	
	/**
     * default constructor
     */
    public RolePower() {
    }

    public String getRoleCode() {
    	if(this.id!=null)
    	{
    		return this.id.getRoleCode();	
    	}else
    	{
    		return this.roleCode;	
    	}
    }

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getOptCode() {
		
		
		if(this.id!=null)
    	{
    		return this.id.getOptCode();	
    	}else
    	{
    		return optCode;
    	}
	}

	public void setOptCode(String optCode) {
		this.optCode = optCode;
	}

	/**
     * full constructor
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
    	if(this.id!=null)
    	{
    		return this.id;
    	}else
    	{
    		RolePowerId rolePowerId =new RolePowerId();
        	rolePowerId.setOptCode(this.optCode);
        	rolePowerId.setRoleCode(this.roleCode);
            return rolePowerId;
    	}
    	
    }

    public void setId(RolePowerId id) {
        this.id = id;
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

	public void copyNotNullProperty(RolePower other) {
		if (other.getOptScopeCodes() != null)
			this.optScopeCodes = other.getOptScopeCodes();
		if (other.getCreator() != null)
			this.creator = other.getCreator();
		if (other.getUpdator() != null)
			this.updator = other.getUpdator();
		if (other.getUpdateDate() != null)
			this.updateDate = other.getUpdateDate();
	}
	
	public void copy(RolePower other) {
		this.optScopeCodes = other.getOptScopeCodes(); 
		this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
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