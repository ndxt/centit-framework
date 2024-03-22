package com.centit.framework.model.basedata;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * FVUserOptMoudleListId entity.
 *
 * @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "F_V_USEROPTMOUDLELIST")
public class FVUserOptMoudleList implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    // Fields
    @Id
    @Column(name = "OPT_ID")
    //@GeneratedValue(generator = "assignedGenerator")
    private String optId;           //业务模块类

    @Column(name = "USER_CODE")
    @Length(max = 8)
    private String userCode;        //用户代码

    @Column(name = "OPT_NAME")
    @Length(max = 32)
    private String optName;

    @Column(name = "PRE_OPT_ID")
    @Length(max = 8)
    private String preOptId;

    @Column(name = "FORM_CODE")
    @Length(max = 4)
    private String formCode;

    @Column(name = "OPT_URL")
    @Length(max = 256)
    private String optUrl;

    /**
     * 系统菜单路由
     *
     * 与angularjs路由匹配
     */
    @Column(name = "OPT_ROUTE")
    @Length(max = 256)
    private String optRoute;

    @Column(name = "OPT_TYPE")
    @Length(max = 1)
    private String optType;

    @Column(name = "MSG_NO")
    @Length(max = 10)
    private Long msgNo;

    @Column(name = "MSG_PRM")
    @Length(max = 256)
    private String msgPrm;

    @Column(name = "IS_IN_TOOLBAR")
    @Length(max = 1)
    private String isInToolbar;

    @Column(name = "IMG_INDEX")
    @Length(max = 10)
    private Long imgIndex;

    @Column(name = "TOP_OPT_ID")
    @Length(max = 8)
    private String topOptId;

    @Column(name = "ORDER_IND")
    @Length(max = 4)
    private Long orderInd;

    @Column(name = "PAGE_TYPE")
    @Length(max = 1)
    private String pageType;  //页面打开方式 D: DIV I： iFrame

    // Constructors

    /**
     * default constructor
     */
    public FVUserOptMoudleList() {
    }

    /**
     * minimal constructor
     *
     * @param userCode String
     * @param optid String
     * @param optname String
     */
    public FVUserOptMoudleList(String userCode, String optid, String optname) {
        this.userCode = userCode;
        this.optId = optid;
        this.optName = optname;
    }


    public FVUserOptMoudleList(String userCode, String optid, String optname,
                               String preoptid, String formcode, String opturl, Long msgno,
                               String msgprm, String isintoolbar, Long imgindex, String topoptid,
                               String opttype,
                               Long orderind, String pageType) {
        this.userCode = userCode;
        this.optId = optid;
        this.optName = optname;
        this.preOptId = preoptid;
        this.formCode = formcode;
        this.optUrl = opturl;
        this.msgNo = msgno;
        this.msgPrm = msgprm;
        this.isInToolbar = isintoolbar;
        this.imgIndex = imgindex;
        this.topOptId = topoptid;
        this.orderInd = orderind;
        this.pageType = pageType;
    }

    // Property accessors

    public String getUsercode() {
        return getUserCode();
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOptId() {
        return this.optId;
    }

    public void setOptId(String optId) {
        this.optId = optId;
    }

    public String getOptName() {
        return this.optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getPreOptId() {
        return this.preOptId;
    }

    public void setPreOptId(String preOptId) {
        this.preOptId = preOptId;
    }

    public String getFormCode() {
        return this.formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getOptUrl() {
        return this.optUrl;
    }

    public void setOptUrl(String opturl) {
        this.optUrl = opturl;
    }

    public Long getMsgNo() {
        return this.msgNo;
    }

    public void setMsgNo(Long msgNo) {
        this.msgNo = msgNo;
    }

    public String getMsgPrm() {
        return this.msgPrm;
    }

    public void setMsgPrm(String msgPrm) {
        this.msgPrm = msgPrm;
    }

    public String getIsInToolbar() {
        return this.isInToolbar;
    }

    public void setIsInToolbar(String isInToolbar) {
        this.isInToolbar = isInToolbar;
    }

    public Long getImgIndex() {
        return this.imgIndex;
    }

    public void setImgIndex(Long imgIndex) {
        this.imgIndex = imgIndex;
    }

    public String getTopOptId() {
        return this.topOptId;
    }

    public void setTopOptId(String topOptId) {
        this.topOptId = topOptId;
    }

    public Long getOrderInd() {
        return this.orderInd;
    }

    public void setOrderInd(Long orderInd) {
        this.orderInd = orderInd;
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
     * @return PageType
     */
    public String getPageType() {
        return pageType;
    }

    /**
     * 页面打开方式 D: DIV I： iFrame
     *
     * @param pageType String
     */
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof FVUserOptMoudleList))
            return false;
        FVUserOptMoudleList castOther = (FVUserOptMoudleList) other;

        return ((this.getUserCode() == castOther.getUserCode()) || (this
                .getUserCode() != null
                && castOther.getUserCode() != null && this.getUserCode()
                .equals(castOther.getUserCode())))
                && ((this.getOptId() == castOther.getOptId()) || (this
                .getOptId() != null
                && castOther.getOptId() != null && this.getOptId()
                .equals(castOther.getOptId())))
                && ((this.getOptName() == castOther.getOptName()) || (this
                .getOptName() != null
                && castOther.getOptName() != null && this.getOptName()
                .equals(castOther.getOptName())))
                && ((this.getPreOptId() == castOther.getPreOptId()) || (this
                .getPreOptId() != null
                && castOther.getPreOptId() != null && this
                .getPreOptId().equals(castOther.getPreOptId())))
                && ((this.getFormCode() == castOther.getFormCode()) || (this
                .getFormCode() != null
                && castOther.getFormCode() != null && this
                .getFormCode().equals(castOther.getFormCode())))
                && ((this.getOptUrl() == castOther.getOptUrl()) || (this
                .getOptUrl() != null
                && castOther.getOptUrl() != null && this.getOptUrl()
                .equals(castOther.getOptUrl())))
                && ((this.getMsgNo() == castOther.getMsgNo()) || (this
                .getMsgNo() != null
                && castOther.getMsgNo() != null && this.getMsgNo()
                .equals(castOther.getMsgNo())))
                && ((this.getMsgPrm() == castOther.getMsgPrm()) || (this
                .getMsgPrm() != null
                && castOther.getMsgPrm() != null && this.getMsgPrm()
                .equals(castOther.getMsgPrm())))
                && ((this.getIsInToolbar() == castOther.getIsInToolbar()) || (this
                .getIsInToolbar() != null
                && castOther.getIsInToolbar() != null && this
                .getIsInToolbar().equals(castOther.getIsInToolbar())))
                && ((this.getImgIndex() == castOther.getImgIndex()) || (this
                .getImgIndex() != null
                && castOther.getImgIndex() != null && this
                .getImgIndex().equals(castOther.getImgIndex())))
                && ((this.getTopOptId() == castOther.getTopOptId()) || (this
                .getTopOptId() != null
                && castOther.getTopOptId() != null && this
                .getTopOptId().equals(castOther.getTopOptId())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result
                + (getUserCode() == null ? 0 : this.getUserCode().hashCode());
        result = 37 * result
                + (getOptId() == null ? 0 : this.getOptId().hashCode());
        result = 37 * result
                + (getOptName() == null ? 0 : this.getOptName().hashCode());
        result = 37 * result
                + (getPreOptId() == null ? 0 : this.getPreOptId().hashCode());
        result = 37 * result
                + (getFormCode() == null ? 0 : this.getFormCode().hashCode());
        result = 37 * result
                + (getOptUrl() == null ? 0 : this.getOptUrl().hashCode());
        result = 37 * result
                + (getMsgNo() == null ? 0 : this.getMsgNo().hashCode());
        result = 37 * result
                + (getMsgPrm() == null ? 0 : this.getMsgPrm().hashCode());
        result = 37
                * result
                + (getIsInToolbar() == null ? 0 : this.getIsInToolbar()
                .hashCode());
        result = 37 * result
                + (getImgIndex() == null ? 0 : this.getImgIndex().hashCode());
        result = 37 * result
                + (getTopOptId() == null ? 0 : this.getTopOptId().hashCode());
        return result;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }
}
