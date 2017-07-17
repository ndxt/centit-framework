package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 
  用户自定义过滤条件表null   
*/
@Entity
@Table(name = "F_USER_QUERY_FILTER")
public class UserQueryFilter implements java.io.Serializable {
	private static final long serialVersionUID =  1L;



	/**
	 * 过滤条件序号 null 
	 */
	@Id
	@Column(name = "FILTER_NO")
	@GeneratedValue(generator = "assignedGenerator")
	//@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
	private Long filterNo;

	@Column(name = "USERCODE")
    @NotNull(message = "字段不能为空")
    private String userCode;// 用户代码

	/**
	 * 所属模块编码 开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法 
	 */
	@Column(name = "MODLE_CODE")
	@NotNull(message = "字段不能为空")
	@Size(max = 64, message = "字段长度不能大于{max}")
	private String  modleCode;
	/**
	 * 筛选器名称 用户自行定义的名称 
	 */
	@Column(name = "FILTER_NAME")
	@NotNull(message = "字段不能为空")
	@Size(max = 64, message = "字段长度不能大于{max}")
	private String  filterName;
	/**
	 * 条件变量名 变量值，json格式，对应一个map 
	 */
	@Column(name = "FILTER_VALUE")
	@NotNull(message = "字段不能为空")
	@Size(max = 3200, message = "字段长度不能大于{max}")
	private String  filterValue;
	
	@Column(name = "IS_DEFAULT")
	@NotNull(message = "字段不能为空")
	@Size(max = 1, message = "字段长度不能大于{max}")
	private String  isDefault;
	 
	@Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
	
	// Constructors
	/** default constructor */
	public UserQueryFilter() {
	}
	/** minimal constructor */
	public UserQueryFilter(
		Long filterNo	,String  userCode	
		,String  modleCode,String  filterName,String  filterValue) {
	
	
		this.filterNo = filterNo;		
		this.userCode= userCode; 
		this.modleCode= modleCode; 
		this.filterName= filterName; 
		this.filterValue= filterValue; 		
	}
  
	public Long getFilterNo() {
		return this.filterNo;
	}

	public void setFilterNo(Long filterNo) {
		this.filterNo = filterNo;
	}
	// Property accessors
  
	public String getModleCode() {
		return this.modleCode;
	}
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public void setModleCode(String modleCode) {
		this.modleCode = modleCode;
	}
  
	public String getFilterName() {
		return this.filterName;
	}
	
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
  
	public String getFilterValue() {
		return this.filterValue;
	}
	
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	/**
	 * T : 最新查看， F： 其他
	 * @return
	 */
	public String getIsDefault() {
		return this.isDefault;
	}
	/**
	 * T : 最新查看， F： 其他
	 * @param isDefault
	 */
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


	public UserQueryFilter copy(UserQueryFilter other){
  
		this.setFilterNo(other.getFilterNo());
		this.userCode= other.getUserCode();  
		this.modleCode= other.getModleCode();  
		this.filterName= other.getFilterName();  
		this.filterValue= other.getFilterValue();
		this.isDefault= other.getIsDefault();
		this.createDate= other.getCreateDate();

		return this;
	}
	
	public UserQueryFilter copyNotNullProperty(UserQueryFilter other){
  
	if( other.getFilterNo() != null)
		this.setFilterNo(other.getFilterNo());
		if( other.getUserCode() != null)
			this.userCode= other.getUserCode();  
		if( other.getModleCode() != null)
			this.modleCode= other.getModleCode();  
		if( other.getFilterName() != null)
			this.filterName= other.getFilterName();  
		if( other.getFilterValue() != null)
			this.filterValue= other.getFilterValue();		
		if( other.getIsDefault() != null)
			this.isDefault= other.getIsDefault();
		if( other.getCreateDate() != null)
			this.createDate= other.getCreateDate();
		return this;
	}

	public UserQueryFilter clearProperties(){
		this.userCode= null;
		this.modleCode= null;  
		this.filterName= null;  
		this.filterValue= null;
		this.isDefault=null;
		this.createDate=null;
		return this;
	}
}
