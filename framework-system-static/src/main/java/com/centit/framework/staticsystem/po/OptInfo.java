package com.centit.framework.staticsystem.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.centit.framework.model.basedata.IOptInfo;

/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */
// 业务模块表
public class OptInfo implements IOptInfo, java.io.Serializable{
    private static final long serialVersionUID = 1L;

 
    private String optId; // 业务编号
    public String getId() {
        return this.optId;
    }
    
  
    private String preOptId; // 上级业务模块编号
    public String getPid() {
        return this.preOptId;
    }
    
    private String optName; // 业务名称
    public String getText() {
        return this.optName;
    }
    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     */
    private String optType; // 业务类别


    private String formCode; // 界面代码(C/S)

    /**
     * 系统菜单路由
     * 
     * 与angularjs路由匹配
     */

    private String optRoute;
    public String getUrl() {
        return this.optRoute;
    }
    

    private String optUrl; // 业务url（b/s）


    private Long msgNo; // 消息编号


    private String msgPrm; // 业务参数


    private String isInToolbar; // 是否放入工具栏


    private Long imgIndex; // 图标编号


    private String topOptId; // 顶层业务编号


    private String flowCode; // 流程代码

 
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
    
 
    private Long orderInd; // 业务顺序

 
    private String icon;// 图标

 
    private Long height;// 高度

 
    private Long width;// 宽度


	/**
     * default constructor
     */
    public OptInfo() {
    }

    /**
     * minimal constructor
     */
    public OptInfo(String optid, String optname) {

        this.optId = optid;

        this.optName = optname;
    }

    /**
     * full constructor
     */
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

    private List<OptInfo> children;

    public List<OptInfo> getChildren() {
        return children;
    }
    
    public void addChild(OptInfo child) {
    	if(children==null)
    		children = new ArrayList<OptInfo>();
        this.children.add(child);
    }   
	

    public void setChildren(List<OptInfo> children) {
        this.children = children;
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
     * @return
     */
    public String getOptType() {
        return this.optType;
    }

    /**
     * S:实施业务, O:普通业务, W:流程业务, I:项目业务
     * @param opttype
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
     * @return
     */
    public String getPageType() {
        return pageType;
    }

    /**
     * 页面打开方式 D: DIV I： iFrame
     * 
     * @param pageType
     */
    public void setPageType(String pageType) {
        this.pageType = pageType;
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


    public OptInfo(String optId) {
        super();
        this.optId = optId;
    }    
}
