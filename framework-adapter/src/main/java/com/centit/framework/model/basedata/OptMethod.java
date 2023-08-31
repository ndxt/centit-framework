package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

/**
 * OptMethod entity.
 *
 * @author codefan@codefan.com
 */
@Data
@Entity
@Table(name = "F_OPTDEF")
@ApiModel(value="操作方法对象",description="操作方法对象 OptMethod")
public class OptMethod implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OPT_CODE")
    //@GeneratedValue(generator = "assignedGenerator")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @ApiModelProperty(value = "操作代码",name = "optCode")
    private String optCode;// 操作代码

    @Column(name = "OPT_NAME")
    @Length(max = 100, message = "字段长度必须小于{max}")
    @ApiModelProperty(value = "操作名称",name = "optName")
    private String optName; // 操作名称

    @Column(name = "OPT_ID")
    private String optId;

    @Column(name = "OPT_METHOD")
    @Length(max = 50, message = "字段长度必须小于{max}")
    @ApiModelProperty(value = "操作方法",name = "optMethod")
    private String optMethod;// 操作方法

    @Column(name = "OPT_DESC")
    @Length(max = 256, message = "字段长度必须小于{max}")
    @ApiModelProperty(value = "操作说明",name = "optDesc")
    private String optDesc; // 操作说明

    @Column(name = "IS_IN_WORKFLOW")
    @Length(max = 1, message = "字段长度必须为{max}")
    private String isInWorkflow;// 是否是流程操作

    @Column(name = "OPT_URL")
    @Length(max = 256, message = "字段长度必须小于{max}")
    private String optUrl;

    @Column(name = "OPT_REQ")
    @Length(max = 20, message = "字段长度必须小于{max}")
    private String optReq;


    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    //创建人、更新人、更新时间
    /**
     * CREATOR(创建人) 创建人
     */
    @Column(name = "CREATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String  creator;
       /**
     * UPDATOR(更新人) 更新人
     */
    @Column(name = "UPDATOR")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String  updator;
    /**
     * UPDATEDATE(更新时间) 更新时间
     */
    @Column(name = "UPDATE_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE,
        condition = GeneratorCondition.ALWAYS, value="today()" )
    private Date  updateDate;

    /**
     * 方法排序号
     */
    @Column(name = "OPT_ORDER")
    private Integer optOrder;
    @Column(name = "OPT_TYPE")
    @ApiModelProperty(value = "业务类别 A:api网关B:元数据C:http调用", name = "optType")
    private String optType;
    @Column(name = "API_ID")
    private String apiId;
    @Column(name = "SOURCE_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @JSONField(serialize = false)
    private String sourceId;
    @Transient
    private String userCode;
    @Transient
    private String topOptId;
    //结束

    // Constructors

    /**
     * default constructor
     */
    public OptMethod() {
    }

    /**
     * minimal constructor
     *
     * @param optcode String
     * @param optid String
     */
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

    public String toString() {
        return this.optName;
    }

    //结束
      @Override
      public boolean equals(Object obj){
          if(obj==null)
              return false;
          if(this==obj)
              return true;

          if(obj instanceof OptMethod){
              return StringUtils.equals(optCode , ((OptMethod)obj).getOptCode());
          }

          if(obj instanceof OptDataScope){
              return StringUtils.equals(optCode , ((OptDataScope)obj).getOptScopeCode());
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
