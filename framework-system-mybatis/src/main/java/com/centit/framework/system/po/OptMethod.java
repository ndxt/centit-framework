package com.centit.framework.system.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IOptDataScope;
import com.centit.framework.model.basedata.IOptMethod;

/**
 * OptMethod entity.
 *
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "F_OPTDEF")
public class OptMethod implements IOptMethod,EntityWithTimestamp, java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OPTCODE")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String optCode;// 操作代码

    @Column(name = "OPTNAME")
    @Size(max = 100, message = "字段长度必须小于{max}")
    private String optName; // 操作名称

    @Column(name = "OPTID")
    private String optId; 
    
    @Column(name = "OPTMETHOD")
    @Size(max = 50, message = "字段长度必须小于{max}")
    private String optMethod;// 操作方法

    @Column(name = "OPTDESC")
    @Size(max = 256, message = "字段长度必须小于{max}")
    private String optDesc; // 操作说明

    @Column(name = "ISINWORKFLOW")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String isInWorkflow;// 是否是流程操作

    @Column(name = "OPTURL")
    @Size(max = 256, message = "字段长度必须小于{max}")
    private String optUrl;

    @Column(name = "OPTREQ")
    @Size(max = 20, message = "字段长度必须小于{max}")
    private String optReq;


    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

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
	
	/**
	 * 方法排序号
	 */
	@Column(name = "OPTORDER")
	private Integer optOrder;// 操作方法排序
	//结束
	
    // Constructors

    /**
     * default constructor
     */
    public OptMethod() {
    }

    /**
     * minimal constructor
     */
    public OptMethod(String optcode, String optid) {

        this.optCode = optcode;
        this.optId = optid;

    }

    /**
     * full constructor
     */
    public OptMethod(String optcode, String optname, String optid, String optmethod, String optdesc) {

        this.optCode = optcode;
        this.optName = optname;
        this.optMethod = optmethod;
        this.optId = optid;
        this.optDesc = optdesc;
    }

    public OptMethod(String optcode, String optname, String optid, String optmethod, String optdesc, String isinworkflow) {
        this.optCode = optcode;
        this.optName = optname;
        this.optMethod = optmethod;
        this.optId = optid;
        this.optDesc = optdesc;
        this.isInWorkflow = isinworkflow;
    }

    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptCode() {
        return this.optCode;
    }

    public void setOptCode(String optcode) {
        this.optCode = optcode;
    }

    public String toString() {
        return this.optName;
    }

    // Property accessors

    public String getOptName() {
        return this.optName;
    }

    public void setOptName(String optname) {
        this.optName = optname;
    }

    public String getOptMethod() {
        return this.optMethod;
    }

    public void setOptMethod(String optmethod) {
        this.optMethod = optmethod;
    }

    public String getOptDesc() {
        return this.optDesc;
    }

    public void setOptDesc(String optdesc) {
        this.optDesc = optdesc;
    }

    public void setIsInWorkflow(String isinworkflow) {
        this.isInWorkflow = isinworkflow;
    }

    public String getIsInWorkflow() {
        return isInWorkflow;
    }

    public String getOptUrl() {
        return optUrl;
    }

    public void setOptUrl(String optUrl) {
        this.optUrl = optUrl;
    }

    public String getOptReq() {
        return optReq;
    }

    public void setOptReq(String optReq) {
        this.optReq = optReq;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setOptOrder(Integer optOrder) {
		this.optOrder = optOrder;
	}
	@Override
	public Integer getOptOrder() {
		return optOrder;
	}
	
	
    public void copy(OptMethod other) {
    	this.optCode = other.getOptCode();
        this.optName = other.getOptName();
        this.optMethod = other.getOptMethod();
        this.optDesc = other.getOptDesc();
        this.isInWorkflow = other.getIsInWorkflow();
        this.optId = other.getOptId();
        this.optOrder = other.getOptOrder();
        this.optReq = other.getOptReq();
        this.optUrl = other.getOptUrl();
        
        this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
    }

    public void copyNotNullProperty(OptMethod other) {
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getOptName() != null)
            this.optName = other.getOptName();
        if (other.getOptMethod() != null)
            this.optMethod = other.getOptMethod();
        if (other.getOptDesc() != null)
            this.optDesc = other.getOptDesc();
        if (other.getIsInWorkflow() != null)
            this.isInWorkflow = other.getIsInWorkflow();
        if (other.getCreator() != null)        	
        	this.creator =other.getCreator();
        
        if (other.getOptOrder() != null)
        	this.optOrder = other.getOptOrder();
        if (other.getOptReq() != null)
        	this.optReq = other.getOptReq();
        if (other.getOptUrl() != null)
        	this.optUrl = other.getOptUrl();
        
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
  	@Override
  	public boolean equals(Object obj){
  		if(obj==null)
  			return false;
  		if(this==obj)
  			return true;
  		
  		if(obj instanceof IOptMethod){
  			return StringUtils.equals(optCode , ((IOptMethod)obj).getOptCode());
  		}
  		
  		if(obj instanceof IOptDataScope){
  			return StringUtils.equals(optCode , ((IOptDataScope)obj).getOptScopeCode());
  		}
  		if(obj instanceof String){
  			return StringUtils.equals(optCode , (String)obj);
  		}  		
  		return false;
  	}
  	
  	@Override
  	public int hashCode(){
		return optCode==null?0:optCode.hashCode();
    }
}
