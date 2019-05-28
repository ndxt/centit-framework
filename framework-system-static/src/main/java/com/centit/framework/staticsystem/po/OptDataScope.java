package com.centit.framework.staticsystem.po;

import com.centit.framework.model.basedata.IOptDataScope;
import com.centit.framework.model.basedata.IOptMethod;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * OptDataScope entity.
 *
 * @author codefan@codefan.com
 */
public class OptDataScope implements IOptDataScope, Serializable {
    private static final long serialVersionUID = 1L;

    private String optScopeCode;// 操作代码

    private String scopeName; // 操作名称

    private String optId;

    private String filterCondition;

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
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (obj instanceof IOptDataScope) {
            return StringUtils.equals(optScopeCode, ((IOptDataScope) obj).getOptScopeCode());
        }

        if (obj instanceof IOptMethod) {
            return StringUtils.equals(optScopeCode, ((IOptMethod) obj).getOptCode());
        }
        if (obj instanceof String) {
            return StringUtils.equals(optScopeCode, (String) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return optScopeCode == null ? 0 : optScopeCode.hashCode();
    }
}
