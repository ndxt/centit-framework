package com.centit.framework.system.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IOptInfo;

/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */
// 业务模块表
@Entity
@Table(name = "F_OPTINFO")
public class OptInfo implements IOptInfo, EntityWithTimestamp, java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OPTID")
    @GeneratedValue(generator = "assignedGenerator")
    //@GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    private String optId; // 业务编号
    public String getId() {
        return this.optId;
    }
    
    @Column(name = "PREOPTID")
    @Size(max = 32, message = "字段长度不能大于{max}")
    private String preOptId; // 上级业务模块编号
    public String getPid() {
        return this.preOptId;
    }
    
    @Column(name = "OPTNAME")
    @NotNull(message = "字段不能为空")
    @Size(max = 100, message = "字段长度不能大于{max}")
    private String optName; // 业务名称
    public String getText() {
        return this.optName;
    }
    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     */
    @Column(name = "OPTTYPE")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String optType; // 业务类别

    @Column(name = "FORMCODE")
    @Size(max = 4, message = "字段长度不能大于{max}")
    private String formCode; // 界面代码(C/S)

    /**
     * 系统菜单路由
     * 
     * 与angularjs路由匹配
     */
    @Column(name = "OPTROUTE")
    @Size(max = 256, message = "字段长度不能大于{max}")
    private String optRoute;
    public String getUrl() {
        return this.optRoute;
    }
    
    @Column(name = "OPTURL")
    @Size(max = 256, message = "字段长度不能大于{max}")
    private String optUrl; // 业务url（b/s）

    @Column(name = "MSGNO")
    //@Range(max = 1000000000, message = "字段长度不能大于{max}")
    private Long msgNo; // 消息编号

    @Column(name = "MSGPRM")
    @Size(max = 256, message = "字段长度不能大于{max}")
    private String msgPrm; // 业务参数

    @Column(name = "ISINTOOLBAR")
    private String isInToolbar; // 是否放入工具栏

    @Column(name = "IMGINDEX")
    //@Range(max = 100000, message = "字段长度不能大于{max}")
    private Long imgIndex; // 图标编号

    @Column(name = "TOPOPTID")
    @Size(max = 8, message = "字段长度不能大于{max}")
    private String topOptId; // 顶层业务编号

    @Column(name = "FLOWCODE")
    @Size(max = 8, message = "字段长度不能大于{max}")
    private String flowCode; // 流程代码

    @Column(name = "PAGETYPE")
    @Size(max = 1, message = "字段长度必须为{max}")
    private String pageType; // 页面打开方式 D: DIV I： iFrame
    
    public Map<String, Object> getAttributes() {
        boolean external = true;
        if (StringUtils.equals("D", this.pageType)) {
            external = false;
        };
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("external", external);
        return map;
    }
    
    @Column(name = "ORDERIND")
    //@Range(max = 100000, message = "字段长度不能大于{max}")
    private Long orderInd; // 业务顺序

    @Column(name = "ICON")
    @Size(max = 512, message = "字段长度不能大于{max}")
    private String icon;// 图标

    @Column(name = "HEIGHT")
    //@Range(max = 100000, message = "字段长度不能大于{max}")
    private Long height;// 高度

    @Column(name = "WIDTH")
    //@Range(max = 100000, message = "字段长度不能大于{max}")
    private Long width;// 宽度

    
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
	//结束
	
    @Transient
    private List<OptInfo> children;

    @Transient
    private String state;
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    
    @Transient
    private List<OptMethod> optMethods;    
    
    @Transient
    private List<OptDataScope> dataScopes;

    // Constructors

    public List<OptDataScope> getDataScopes() {
    	 if (null == dataScopes) {
    		 dataScopes = new ArrayList<>();
         }
		return dataScopes;
	}
	public void setDataScopes(List<OptDataScope> dataScopes) {
		this.dataScopes = dataScopes;
	}
	/**
     * default constructor
     */
    public OptInfo() {
    }

    /**
     * minimal constructor
     * @param optid String
     * @param optname String
     */
    public OptInfo(String optid, String optname) {

        this.optId = optid;

        this.optName = optname;
    }

    public OptInfo(String optid, String preoptid, String optname,
            String formcode, String opturl, Long msgno, String msgprm,
            String isintoolbar, Long imgindex, String topoptid, String opttype,
            String wfcode, Long orderind, String pageType, String icon,
            Long height, Long width) {

        this.optId = optid;

        this.preOptId = preoptid;
        this.optName = optname;
        this.formCode = formcode;
        this.optUrl = opturl;
        this.msgNo = msgno;
        this.msgPrm = msgprm;
        this.isInToolbar = isintoolbar;
        this.imgIndex = imgindex;
        this.topOptId = topoptid;
        this.optType = opttype;
        this.flowCode = wfcode;
        this.orderInd = orderind;
        this.pageType = pageType;
        this.icon = icon;
        this.height = height;
        this.width = width;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String optid) {
        this.optId = optid;
    }

    // Property accessors

    public String getPreOptId() {
        return this.preOptId;
    }

    public void setPreOptId(String preoptid) {
        this.preOptId = preoptid;
    }

    public String toString() {
        return this.optName;
    }

    public String getOptName() {
        return this.optName;
    }

    public void setOptName(String optname) {
        this.optName = optname;
    }

    public String getFormCode() {
        return this.formCode;
    }

    public void setFormCode(String formcode) {
        this.formCode = formcode;
    }

    public String getOptUrl() {
        if (this.optUrl == null)
            return "...";
        return this.optUrl;
    }

    public void setOptUrl(String opturl) {
        this.optUrl = opturl;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String wfcode) {
        this.flowCode = wfcode;
    }

    public Long getMsgNo() {
        return this.msgNo;
    }

    public void setMsgNo(Long msgno) {
        this.msgNo = msgno;
    }

    public String getMsgPrm() {
        return this.msgPrm;
    }

    public void setMsgPrm(String msgprm) {
        this.msgPrm = msgprm;
    }

    public String getIsInToolbar() {
        return this.isInToolbar;
    }

    public void setIsInToolbar(String isintoolbar) {
        this.isInToolbar = isintoolbar;
    }

    public Long getImgIndex() {
        return this.imgIndex;
    }

    public void setImgIndex(Long imgindex) {
        this.imgIndex = imgindex;
    }

    public String getTopOptId() {
        return this.topOptId;
    }

    public void setTopOptId(String topoptid) {
        this.topOptId = topoptid;
    }

    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     * @return  S:实施业务, O:普通业务, W:流程业务, I:项目业务
     */
    public String getOptType() {
        return this.optType;
    }

    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     * @param opttype opttype
     */
    public void setOptType(String opttype) {
        this.optType = opttype;
    }

    public Long getOrderInd() {
        return this.orderInd;
    }

    public void setOrderInd(Long orderind) {
        this.orderInd = orderind;
    }

    public String getOptRoute() {
        return optRoute;
    }

    public void setOptRoute(String optRoute) {
        this.optRoute = optRoute;
    }

    /**
     * 页面打开方式 D: DIV I： iFrame
     * 
     * @return 页面打开方式 D: DIV I： iFrame
     */
    public String getPageType() {
        return pageType;
    }

    /**
     * 页面打开方式 D: DIV I： iFrame
     * 
     * @param pageType pageType
     */
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void copy(OptInfo other) {

        this.preOptId = other.getPreOptId();
        this.optName = other.getOptName();
        this.formCode = other.getFormCode();
        this.optUrl = other.getOptUrl();
        this.msgNo = other.getMsgNo();
        this.msgPrm = other.getMsgPrm();
        this.isInToolbar = other.getIsInToolbar();
        this.imgIndex = other.getImgIndex();
        this.topOptId = other.getTopOptId();
        this.optType = other.getOptType();
        this.flowCode = other.getFlowCode();
        this.orderInd = other.getOrderInd();
        this.pageType = other.getPageType();
        this.icon = other.getIcon();
        this.height = other.getHeight();
        this.width = other.getWidth();
        this.optRoute = other.getOptRoute();
        this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
    }

    public void copyNotNullProperty(OptInfo other) {

        if (other.getPreOptId() != null)
            this.preOptId = other.getPreOptId();
        if (other.getOptName() != null)
            this.optName = other.getOptName();
        if (other.getFormCode() != null)
            this.formCode = other.getFormCode();
        if (other.getOptUrl() != null)
            this.optUrl = other.getOptUrl();
        if (other.getMsgNo() != null)
            this.msgNo = other.getMsgNo();
        if (other.getMsgPrm() != null)
            this.msgPrm = other.getMsgPrm();
        if (other.getIsInToolbar() != null)
            this.isInToolbar = other.getIsInToolbar();
        if (other.getImgIndex() != null)
            this.imgIndex = other.getImgIndex();
        if (other.getTopOptId() != null)
            this.topOptId = other.getTopOptId();
        if (other.getOptType() != null)
            this.optType = other.getOptType();
        if (other.getFlowCode() != null)
            this.flowCode = other.getFlowCode();
        if (other.getOrderInd() != null)
            this.orderInd = other.getOrderInd();
        if (other.getPageType() != null)
            this.pageType = other.getPageType();
        if (other.getIcon() != null)
            this.icon = other.getIcon();
        if (other.getHeight() != null)
            this.height = other.getHeight();
        if (other.getWidth() != null)
            this.width = other.getWidth();
        if (null != other.getOptRoute()) {
            this.optRoute = other.getOptRoute();
        }
        if (other.getCreator() != null)
        	this.creator =other.getCreator();
        if (other.getUpdator() != null)
        	this.updator =other.getUpdator();
        if (other.getUpdateDate() != null)
        	this.updateDate =other.getUpdateDate();
    }

    public void clearProperties() {
        this.preOptId = null;
        this.optName = null;
        this.formCode = null;
        this.optUrl = null;
        this.msgNo = null;
        this.msgPrm = null;
        this.isInToolbar = null;
        this.imgIndex = null;
        this.topOptId = null;
        this.optType = null;
        this.flowCode = null;
        this.orderInd = null;
        this.pageType = "I";
        this.icon = null;
        this.height = null;
        this.width = null;
        this.optRoute = null;
    }

    public List<OptInfo> getChildren() {
        return children;
    }
    
    public void  addChild(OptInfo child) {
    	if(children==null)
    		children = new ArrayList<>();
    	children.add(child);
    }

    public void setChildren(List<OptInfo> children) {
        this.children = children;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public List<OptMethod> getOptMethods() {
        if (null == optMethods) {
            optMethods = new ArrayList<>();
        }
        return optMethods;
    }
    
    public void setOptMethods(List<OptMethod> optDefs) {
        this.optMethods = optDefs;
    }

    public OptInfo(String optId) {
        super();
        this.optId = optId;
    }

    public void addOptMethod(OptMethod optDef) {
    	getOptMethods().add(optDef);
    }
    
    public void addAllOptMethods(List<OptMethod> optDefs) {
    	getOptMethods().clear();
        if (CollectionUtils.isEmpty(optDefs)) {
            return;
        }

        for (OptMethod optDef : optDefs) {
            optDef.setOptId(this.optId);
        }

        getOptMethods().addAll(optDefs);
    }

    
	public void addAllDataScopes(List<OptDataScope> dataScopeByOptID) {
		getDataScopes().clear();
        if (CollectionUtils.isEmpty(dataScopeByOptID)) {
            return;
        }

        for (OptDataScope dataScope : dataScopeByOptID) {
        	dataScope.setOptId(this.optId);
        }

        getDataScopes().addAll(dataScopeByOptID);
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
