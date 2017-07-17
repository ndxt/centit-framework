package com.centit.framework.system.po;

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
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.CollectionUtils;

import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IRoleInfo;

/**
 * FRoleinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 角色信息表
@Entity
@Table(name = "F_ROLEINFO")
public class RoleInfo implements IRoleInfo,EntityWithTimestamp, java.io.Serializable{

    // Fields
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ROLECODE")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String roleCode; // 角色代码

    @Column(name = "ROLENAME")
    @Length(max = 300, message = "字段长度不能大于{max}")
    private String roleName; // 角色名称

    @Column(name = "ISVALID")
    @NotBlank(message = "字段不能为空")
    @Length(max = 1, message = "字段长度必须为{max}")
    @Pattern(regexp = "[TFA]", message = "字段值必须是T或F,A为新建可以删除")
    private String isValid; // 是否生效

    /**
     * S为系统角色  I为项目角色  W 工作流角色  
     */
    @Column(name = "ROLETYPE")
    @Length(max = 1, message = "字段长度必须为{max}")
    private String roleType; // 角色类别

    @Column(name = "UNITCODE")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String unitCode; // 角色所属机构

    
    @Column(name = "ROLEDESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String roleDesc; // 角色描述
    
    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
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
	@Column(name = "UPDATEDATE")
	private Date  updateDate;

    @Transient
    private List<RolePower> rolePowers;

    /**
     * default constructor
     */
    public RoleInfo() {
    	roleType = "S";
    }

    /**
     * minimal constructor
     */
    public RoleInfo(String rolecode, String isvalid) {
        this.roleCode = rolecode;
        this.isValid = isvalid;
        this.roleType = "S";
    }

    /**
     * full constructor
     */
    public RoleInfo(String rolecode, String rolename,String roleType,
    		String unitCode,String isvalid, String roledesc) {
        this.roleCode = rolecode;
        this.roleName = rolename;
        this.roleType = roleType;
        this.unitCode = unitCode;
        this.isValid = isvalid;
        this.roleDesc = roledesc;
    }

    // Property accessors
    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public String toString() {
        return this.roleName;
    }

    public void setRoleName(String rolename) {
        this.roleName = rolename;
    }

    public String getIsValid() {
        return this.isValid;
    }

    public void setIsValid(String isvalid) {
        this.isValid = isvalid;
    }

    public String getRoleDesc() {
        return this.roleDesc;
    }

    public void setRoleDesc(String roledesc) {
        this.roleDesc = roledesc;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * S为系统角色 I为项目角色 W工作量角色
     */
	public String getRoleType() {
		return roleType;
	}

	/**
     * S为系统角色 I为项目角色  D 部门权限  W工作量角色
     */
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	@Override
    public Date getLastModifyDate() {
        return updateDate;
    }

	@Override
    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }

    public void copyNotNullProperty(RoleInfo other) {
        // this.rolecode = other.getRolecode();
        if (other.getRoleName() != null)
            this.roleName = other.getRoleName();
        if (other.getIsValid() != null)
            this.isValid = other.getIsValid();
        if (other.getRoleDesc() != null)
            this.roleDesc = other.getRoleDesc();
        if (other.getRoleType() != null)
        	this.roleType = other.getRoleType();
        if (other.getUnitCode() != null)
        	this.unitCode = other.getUnitCode();
        if (other.getCreator() != null)
        	this.creator =other.getCreator();
        if (other.getUpdator() != null)
        	this.updator =other.getUpdator();
        if (other.getUpdateDate() != null)
        	this.updateDate =other.getUpdateDate();
    }

    public void copy(RoleInfo other) {
        this.roleCode = other.getRoleCode();
        this.roleName = other.getRoleName();
        this.isValid = other.getIsValid();
        this.roleDesc = other.getRoleDesc();
        this.roleType = other.getRoleType();
        this.unitCode = other.getUnitCode();
        this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
    }

   
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
	//@JSONField(serialize=true,deserialize=false,label="rolePowers")
	public List<RolePower> getRolePowers() {
        if (null == rolePowers) {
            rolePowers = new ArrayList<>();
        }
        return rolePowers;
    }

    public void setRolePowers(List<RolePower> rolePowers) {
        this.rolePowers = rolePowers;
    }


    public void addAllRolePowers(List<RolePower> rolePowers) {
    	getRolePowers().clear();
        if(CollectionUtils.isEmpty(rolePowers)) {
            return;
        }
        getRolePowers().addAll(rolePowers);
    }
}