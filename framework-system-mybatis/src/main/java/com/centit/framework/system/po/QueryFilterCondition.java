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
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 
  系统内置查询方式null   
*/
@Entity
@Table(name = "F_QUERY_FILTER_CONDITION")
public class QueryFilterCondition implements java.io.Serializable {
	private static final long serialVersionUID =  1L;



	/**
	 * 过滤条件序号 null 
	 */
	@Id
	@Column(name = "CONDITION_NO")
	@GeneratedValue(generator = "assignedGenerator")
	//@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
	private Long conditionNo;

	/**
	 * 数据库表代码 数据库表代码或者po的类名 
	 */
	@Column(name = "TABLE_CLASS_NAME")
	@NotNull(message = "字段不能为空")
	@Size(max = 64, message = "字段长度不能大于{max}")
	private String  tableClassName;
	/**
	 * 参数名 参数名 
	 */
	@Column(name = "PARAM_NAME")
	@NotNull(message = "字段不能为空")
	@Size(max = 64, message = "字段长度不能大于{max}")
	private String  paramName;
	/**
	 * 参数提示 参数输入框提示 
	 */
	@Column(name = "PARAM_LABEL")
	@NotNull(message = "字段不能为空")
	@Size(max = 120, message = "字段长度不能大于{max}")
	private String  paramLabel;
	/**
	 * 参数类型 参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月 
	 */
	@Column(name = "PARAM_TYPE")
	@Size(max = 8, message = "字段长度不能大于{max}")
	private String  paramType;
	/**
	 * 参数默认值值 null 
	 */
	@Column(name = "DEFAULT_VALUE")
	@Size(max = 100, message = "字段长度不能大于{max}")
	private String  defaultValue;
	/**
	 * 添加语句 过滤语句，将会拼装到sql语句中 
	 */
	@Column(name = "FILTER_SQL")
	@Size(max = 200, message = "字段长度不能大于{max}")
	private String  filterSql;
	/**
	 * 参考类型 数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取
 
	 */
	@Column(name = "SELECT_DATA_TYPE")
	@NotNull(message = "字段不能为空")
	@Size(max = 0, message = "字段长度不能大于{max}")
	private String  selectDataType;
	/**
	 * 数据字典类别 数据字典 
	 */
	@Column(name = "SELECT_DATA_CATALOG")
	@Size(max = 64, message = "字段长度不能大于{max}")
	private String  selectDataCatalog;
	/**
	 * SQL语句 有两个返回字段的sql语句 
	 */
	@Column(name = "SELECT_SQL")
	@Size(max = 1000, message = "字段长度不能大于{max}")
	private String  selectSql;
	/**
	 * 枚举类型 KEY,Value数值对，JSON格式 
	 */
	@Column(name = "SELECT_JSON")
	@Size(max = 2000, message = "字段长度不能大于{max}")
	private String  selectJson;

	@Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;
	// Constructors
	/** default constructor */
	public QueryFilterCondition() {
	}
	/** minimal constructor */
	public QueryFilterCondition(
		Long conditionNo		
		,String  tableClassName,String  paramName,String  paramLabel,String  selectDataType) {
	
	
		this.conditionNo = conditionNo;		
	
		this.tableClassName= tableClassName; 
		this.paramName= paramName; 
		this.paramLabel= paramLabel; 
		this.selectDataType= selectDataType; 		
	}

/** full constructor */
	public QueryFilterCondition(
	 Long conditionNo		
	,String  tableClassName,String  paramName,String  paramLabel,String  paramType,String  defaultValue,String  filterSql,String  selectDataType,String  selectDataCatalog,String  selectSql,String  selectJson) {
	
	
		this.conditionNo = conditionNo;		
	
		this.tableClassName= tableClassName;
		this.paramName= paramName;
		this.paramLabel= paramLabel;
		this.paramType= paramType;
		this.defaultValue= defaultValue;
		this.filterSql= filterSql;
		this.selectDataType= selectDataType;
		this.selectDataCatalog= selectDataCatalog;
		this.selectSql= selectSql;
		this.selectJson= selectJson;		
	}
	

  
	public Long getConditionNo() {
		return this.conditionNo;
	}

	public void setConditionNo(Long conditionNo) {
		this.conditionNo = conditionNo;
	}
	// Property accessors
  
	public String getTableClassName() {
		return this.tableClassName;
	}
	
	public void setTableClassName(String tableClassName) {
		this.tableClassName = tableClassName;
	}
  
	public String getParamName() {
		return this.paramName;
	}
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
  
	public String getParamLabel() {
		return this.paramLabel;
	}
	
	public void setParamLabel(String paramLabel) {
		this.paramLabel = paramLabel;
	}
  
	public String getParamType() {
		return this.paramType;
	}
	
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
  
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
  
	public String getFilterSql() {
		return this.filterSql;
	}
	
	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}
  
	public String getSelectDataType() {
		return this.selectDataType;
	}
	
	public void setSelectDataType(String selectDataType) {
		this.selectDataType = selectDataType;
	}
  
	public String getSelectDataCatalog() {
		return this.selectDataCatalog;
	}
	
	public void setSelectDataCatalog(String selectDataCatalog) {
		this.selectDataCatalog = selectDataCatalog;
	}
  
	public String getSelectSql() {
		return this.selectSql;
	}
	
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}
  
	public String getSelectJson() {
		return this.selectJson;
	}
	
	public void setSelectJson(String selectJson) {
		this.selectJson = selectJson;
	}

	public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	public QueryFilterCondition copy(QueryFilterCondition other){
  
		this.setConditionNo(other.getConditionNo());
  
		this.tableClassName= other.getTableClassName();  
		this.paramName= other.getParamName();  
		this.paramLabel= other.getParamLabel();  
		this.paramType= other.getParamType();  
		this.defaultValue= other.getDefaultValue();  
		this.filterSql= other.getFilterSql();  
		this.selectDataType= other.getSelectDataType();  
		this.selectDataCatalog= other.getSelectDataCatalog();  
		this.selectSql= other.getSelectSql();  
		this.selectJson= other.getSelectJson();
		this.createDate= other.getCreateDate();
		return this;
	}
	
	public QueryFilterCondition copyNotNullProperty(QueryFilterCondition other) {

		if (other.getConditionNo() != null)
			this.setConditionNo(other.getConditionNo());

		if (other.getTableClassName() != null)
			this.tableClassName = other.getTableClassName();
		if (other.getParamName() != null)
			this.paramName = other.getParamName();
		if (other.getParamLabel() != null)
			this.paramLabel = other.getParamLabel();
		if (other.getParamType() != null)
			this.paramType = other.getParamType();
		if (other.getDefaultValue() != null)
			this.defaultValue = other.getDefaultValue();
		if (other.getFilterSql() != null)
			this.filterSql = other.getFilterSql();
		if (other.getSelectDataType() != null)
			this.selectDataType = other.getSelectDataType();
		if (other.getSelectDataCatalog() != null)
			this.selectDataCatalog = other.getSelectDataCatalog();
		if (other.getSelectSql() != null)
			this.selectSql = other.getSelectSql();
		if (other.getSelectJson() != null)
			this.selectJson = other.getSelectJson();
		if (other.getCreateDate() != null)
			this.createDate = other.getCreateDate();
		return this;
	}

	public QueryFilterCondition clearProperties(){
  
		this.tableClassName= null;  
		this.paramName= null;  
		this.paramLabel= null;  
		this.paramType= null;  
		this.defaultValue= null;  
		this.filterSql= null;  
		this.selectDataType= null;  
		this.selectDataCatalog= null;  
		this.selectSql= null;  
		this.selectJson= null;
		this.createDate= null;
		return this;
	}
}
