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

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.framework.model.basedata.OperationLog;

/**
 * 系统操作日志
 */
@Entity
@Table(name = "F_OPT_LOG")
public class OptLog implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOGID")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private Long logId;

    /**
     * 日志级别
     * 使用常量LEVEL_INFO和LEVEL_ERROR表示
     * 默认级别为LEVEL_INFO
     */
    @Column(name = "LOGLEVEL")
    @NotNull(message = "字段不能为空")
    @Size(max = 2, message = "字段长度不能大于{max}")
    private String logLevel = OperationLog.LEVEL_INFO;


    @Column(name = "USERCODE")
    @NotNull(message = "字段不能为空")
    @Size(max = 8, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="userName",value="userCode")
    private String userCode;

    @Column(name = "OPTTIME")
    @NotNull(message = "字段不能为空")
    @Temporal(TemporalType.TIMESTAMP)
    private Date optTime;

    /**
     * 业务操作ID，如记录的是用户管理模块，optId=F_OPT_INFO表中操作用户管理模块业务的主键
     */
    @Column(name = "OPTID")
    @NotNull(message = "字段不能为空")
    @Size(max = 64, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName="optIdText",value="optId")
    private String optId;

    /**
     * 操作业务标记
     * 一般用于关联到业务主体
     */
    @Column(name = "OPTTAG")
    @Size(max = 200, message = "字段长度不能大于{max}")
    private String optTag;

    /**
     * 操作方法
     * 方法，或者字段
     * <p/>
     * 方法使用 P_OPT_LOG_METHOD... 常量表示
     */
    @Column(name = "OPTMETHOD")
    @Size(max = 64, message = "字段长度不能大于{max}")
    private String optMethod;

    /**
     * 操作内容描述
     */
    @Column(name = "OPTCONTENT")
    @NotNull(message = "字段不能为空")
    private String optContent;

    /**
     * 新值
     */
    @Column(name = "NEWVALUE")
    private String newValue;
    /**
     * 原值
     */
    @Column(name = "OLDVALUE")
    private String oldValue;

    public String getOptTag() {
        return optTag;
    }

    public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public void setOptTag(String optTag) {
        this.optTag = optTag;
    }


    // Constructors

    /**
     * default constructor
     */
    public OptLog() {
    }

    public OptLog(String userCode, String optId, String optTag, String optmethod, String oldvalue, String optcontent) {
        this.userCode = userCode;
        // this.userCode = ((FUserDetail)
        // (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUsercode();

        this.optId = optId;
        this.optTag = optTag;
        this.optMethod = optmethod;
        this.optContent = optcontent;
        this.oldValue = oldvalue;
    }

    /**
     * full constructor
     */
    public OptLog(Long logid, String loglevel, String userCode, Date opttime, String optid, String optmethod,
                  String optcontent, String oldvalue) {

        this.logId = logid;

        this.logLevel = loglevel;
        this.userCode = userCode;
        this.optTime = opttime;
        this.optId = optid;
        this.optMethod = optmethod;
        this.optContent = optcontent;
        this.oldValue = oldvalue;
    }

    public Long getLogId() {
        return this.logId;
    }

    public void setLogId(Long logid) {
        this.logId = logid;
    }

    // Property accessors

    public String getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(String loglevel) {
        this.logLevel = loglevel;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getOptTime() {
        return this.optTime;
    }

    public void setOptTime(Date opttime) {
        this.optTime = opttime;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String optid) {
        this.optId = optid;
    }

    public String getOptMethod() {
        return this.optMethod;
    }

    public void setOptMethod(String optmethod) {
        this.optMethod = optmethod;
    }

    public String getOptContent() {
        return this.optContent;
    }

    public void setOptContent(String optcontent) {
        this.optContent = optcontent;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldvalue) {
        this.oldValue = oldvalue;
    }
    
    public void copy(OperationLog other) {
        this.logLevel = other.getLogLevel();
        this.userCode = other.getUserCode();
        this.optTime = other.getOptTime();
        this.optId = other.getOptId();
        this.optTag = other.getOptTag();
        this.optMethod = other.getOptMethod();
        this.optContent = other.getOptContent();
        this.newValue = other.getNewValue();
        this.oldValue = other.getOldValue();
    }


    public void copy(OptLog other) {

        this.setLogId(other.getLogId());

        this.logLevel = other.getLogLevel();
        this.userCode = other.getUserCode();
        this.optTime = other.getOptTime();
        this.optId = other.getOptId();
        this.optTag = other.getOptTag();
        this.optMethod = other.getOptMethod();
        this.optContent = other.getOptContent();
        this.oldValue = other.getOldValue();
        this.newValue = other.getNewValue();
        
      //  this.optTag = other.getoptTag();
        
        this.optTag = other.getOptTag();
    }

    public void copyNotNullProperty(OptLog other) {

        if (other.getLogId() != null)
            this.setLogId(other.getLogId());

        if (other.getLogLevel() != null)
            this.logLevel = other.getLogLevel();
        if (other.getUserCode() != null)
            this.userCode = other.getUserCode();
        if (other.getOptTime() != null)
            this.optTime = other.getOptTime();
        if (other.getOptId() != null)
            this.optId = other.getOptId();
        if (other.getOptTag() != null)
        	this.optTag = other.getOptTag();
        if (other.getOptMethod() != null)
            this.optMethod = other.getOptMethod();
        if (other.getOptContent() != null)
            this.optContent = other.getOptContent();
        if (other.getNewValue() != null)
            this.newValue = other.getNewValue();
        if (other.getOldValue() != null)
            this.oldValue = other.getOldValue();
    }

    public void clearProperties() {

        this.logLevel = null;
        this.userCode = null;
        this.optTime = null;
        this.optId = null;
        this.optMethod = null;
        this.optContent = null;
        this.newValue = null;
        this.oldValue = null;
        this.optTag=null;
    }

//    public String getoptTag() {
//        return optTag;
//    }
//
//    public void setoptTag(String optTag) {
//        this.optTag = optTag;
//    }

    public String getOptMethodText() {
        if (OperationLog.P_OPT_LOG_METHOD_C.equals(getOptMethod())) {
            return "新增";
        }
        if (OperationLog.P_OPT_LOG_METHOD_D.equals(getOptMethod())) {
            return "删除";
        }
        if (OperationLog.P_OPT_LOG_METHOD_U.equals(getOptMethod())) {
            return "更新";
        }

        return this.optMethod;
    }
}
