package com.centit.framework.model.basedata;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OptDataScope entity.
 *
 * @author codefan@codefan.com
 */
@Entity
@Table(name = "F_OPTDATASCOPE")
public class OptDataScope implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OPT_SCOPE_CODE")
    //@GeneratedValue(generator = "assignedGenerator")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String optScopeCode;// 操作代码

    @Column(name = "SCOPE_NAME")
    @Length(max = 64)
    private String scopeName; // 操作名称

    @Column(name = "OPT_ID")
    private String optId;

    /**
     * 数据范围条件
     */
    @Column(name = "FILTER_CONDITION")
    @Length(max = 1000)
    private String filterCondition;

   /*
     * 数据范围条件分组，同一组内的条件用 或，不同组件的条件用与
     *
    @Column(name = "FILTERGROUP")
    @Length(max = 16)
    private String filterGroup;// 操作方法
   */
       @Column(name = "SCOPE_MEMO")
    @Length(max = 1000)
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

    @Override
      public boolean equals(Object obj){
          if(obj==null)
              return false;
          if(this==obj)
              return true;

          if(obj instanceof OptDataScope){
              return StringUtils.equals(optScopeCode , ((OptDataScope)obj).getOptScopeCode());
          }

          if(obj instanceof OptMethod){
              return StringUtils.equals(optScopeCode , ((OptMethod)obj).getOptCode());
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
