package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IOptMethod;

/**
 * OptMethod entity.
 * @author codefan@hotmail.com
 */

public class OptMethod implements IOptMethod, java.io.Serializable{
    private static final long serialVersionUID = 1L;


    private String optCode;// 操作代码


    private String optName; // 操作名称


    private String optId;

    private String optMethod;// 操作方法


    private String optDesc; // 操作说明


    private String isInWorkflow;// 是否是流程操作


    private String optUrl;


    private String optReq;//http 请求方法

    private Integer optOrder;// 操作方法排序


    // Constructors

    /**
     * default constructor
     */
    public OptMethod() {
    }

    public OptMethod(String optcode, String optid) {

        this.optCode = optcode;
        this.optId = optid;

    }

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
        if (other.getOptOrder() != null)
            this.optOrder = other.getOptOrder();
        if (other.getOptReq() != null)
            this.optReq = other.getOptReq();
        if (other.getOptUrl() != null)
            this.optUrl = other.getOptUrl();
    }
}
