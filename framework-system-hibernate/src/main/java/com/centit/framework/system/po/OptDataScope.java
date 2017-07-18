package com.centit.framework.system.po;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import com.centit.framework.model.basedata.IOptDataScope;
import com.centit.framework.model.basedata.IOptMethod;

/**
 * FAddressBook entity.
 *
 * @author codefan@hotmail.com
 */
@Entity
@Table(name = "F_OPTDATASCOPE")
public class OptDataScope implements IOptDataScope,Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OPTSCOPECODE")
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String optScopeCode;// 操作代码

    @Column(name = "SCOPENAME")
    @Length(max = 64, message = "字段长度必须小于{max}")
    private String scopeName; // 操作名称

    @Column(name = "OPTID")
    private String optId; 

    /**
     * 数据范围条件
     */
    @Column(name = "FILTERCONDITION")
    @Length(max = 1000, message = "字段长度必须小于{max}")
    private String filterCondition;

   /*
     * 数据范围条件分组，同一组内的条件用 或，不同组件的条件用与
     *
    @Column(name = "FILTERGROUP")
    @Length(max = 16, message = "字段长度必须小于{max}")
    private String filterGroup;// 操作方法
   */
   	@Column(name = "SCOPEMEMO")
    @Length(max = 1000, message = "字段长度必须小于{max}")
    private String scopeMemo; // 操作说明

 
    // Constructors

    /**
     * default constructor
     */
    public OptDataScope() {
    }

    public OptDataScope(String scopeCode, String optid) {

        this.optScopeCode = scopeCode;
        this.optId = optid;

    }

    public OptDataScope(String scopeCode, String scopeName, String optid, String optmethod, String optdesc) {

        this.optScopeCode = scopeCode;
        this.scopeName = scopeName;
        this.filterCondition = optmethod;
        this.optId = optid;
        this.scopeMemo = optdesc;
    }

  
    public String getOptId() {
        return optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

	public String getOptScopeCode() {
		return optScopeCode;
	}

	public void setOptScopeCode(String optScopeCode) {
		this.optScopeCode = optScopeCode;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	
	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getScopeMemo() {
		return scopeMemo;
	}

	public void setScopeMemo(String scopeMemo) {
		this.scopeMemo = scopeMemo;
	}

    public void copy(OptDataScope other) {

        this.scopeName = other.getScopeName();
        this.optScopeCode = other.getOptScopeCode();
        this.scopeMemo = other.getScopeMemo();
        this.filterCondition = other.getFilterCondition();
        //this.filterGroup = other.getFilterGroup();
        this.optId = other.getOptId();
    }

    public void copyNotNullProperty(OptDataScope other) {
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getScopeName() != null)
            this.scopeName = other.getScopeName();
        if (other.getFilterCondition() != null)
            this.filterCondition = other.getFilterCondition();
        if (other.getScopeMemo() != null)
            this.scopeMemo = other.getScopeMemo();
        if (other.getOptScopeCode() != null)
            this.optScopeCode = other.getOptScopeCode();
    }

    @Override
  	public boolean equals(Object obj){
  		if(obj==null)
  			return false;
  		if(this==obj)
  			return true;
  		
  		if(obj instanceof IOptDataScope){
  			return StringUtils.equals(optScopeCode , ((IOptDataScope)obj).getOptScopeCode());
  		}
  		
  		if(obj instanceof IOptMethod){
  			return StringUtils.equals(optScopeCode , ((IOptMethod)obj).getOptCode());
  		}
  		if(obj instanceof String){
  			return StringUtils.equals(optScopeCode , (String)obj);
  		}  		
  		return false;
  	}
    
    @Override
  	public int hashCode(){
		return optScopeCode==null?0:optScopeCode.hashCode();
    }
}
