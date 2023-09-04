package com.centit.framework.model.basedata;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@Table(name = "F_OS_INFO")
@ApiModel(value="应用系统对象",description="应用系统对象 OsInfo")
public class OsInfo implements  java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OS_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @ApiModelProperty(value = "应用系统ID",name = "osId",required = true)
    private String osId;

    @Column(name = "OS_NAME")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "应用名称",name = "osName")
    private String osName;

    public static final String OSTYPE_PLATFORM = "P";
    public static final String OSTYPE_LOCODE = "L";

    @Column(name = "OS_TYPE")
    @Length(max = 16, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "应用类型 P(PaaS平台业务） L(基于Locde研发的业务) I(基于框架研发的业务） T(第三方研发的业务） O（外部业务）", name = "osName")
    private String osType;
    /**
     * 业务系统后台url
     */
    @Column(name = "OS_URL")
    @Length(max = 200, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "应用后台url",name = "osUrl")
    private String osUrl;

    @Column(name = "TOP_UNIT")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "所属租户",name = "osUrl")
    private String topUnit;
    /**
     * 业务系统首页
     */
    @Column(name = "OS_HOME_PAGE")
    @Length(max = 300, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "应用系统首页",name = "osHomePage")
    private String osHomePage;

    @Column(name = "OAUTH_USER")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "oauth2 登录用户名",name = "oauthUser")
    private String oauthUser;

    @Column(name = "OAUTH_PASSWORD")
    @Length(max = 128, message = "字段长度不能大于{max}")
    @ApiModelProperty(value = "oauth2 登录密码 密文",name = "oauthPassword")
    private String oauthPassword;

    /**
     * 关联的顶层菜单ID
     * 这个现在默认为 os_id
     */
    @Column(name = "REL_OPT_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    // 顶层业务编号
    private String relOptId;

    @Column(name = "CREATED")
    @Length(max = 8, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName = "createUserName", value = "userCode")
    private String created;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()", condition = GeneratorCondition.ALWAYS )
    @Column(name = "LAST_MODIFY_DATE")
    private Date lastModifyDate;

    @ValueGenerator( strategy= GeneratorType.FUNCTION, value = "today()")
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "应用流程设置")
    @Column(name = "PAGE_FLOW")
    @Basic(fetch = FetchType.LAZY)
    private String  pageFlow;

    @Column(name = "IS_DELETE")
    @NotBlank(message = "字段不能为空[T/F]")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private boolean deleted;

    @ApiModelProperty(value = "图片id")
    @Column(name = "PIC_ID")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String  picId;

    @ApiModelProperty(value = "默认数据库,用于后台根据表单自动创建表")
    @Column(name = "DEFAULT_DATABASE")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String  defaultDatabase;

    @ApiModelProperty(value = "网页logo图片主键")
    @Column(name = "LOGO_FILE_ID")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String  logoFileId;
    @ApiModelProperty(value = "应用描述")
    @Column(name = "OS_DESC")
    @Length(max = 1023, message = "字段长度不能大于{max}")
    private String  osDesc;
    @ApiModelProperty(value = "群聊id")
    @Column(name = "GROUP_ID")
    private Long  groupId;



    public void copyNotNull(OsInfo osInfo){
        deleted = osInfo.isDeleted();
        if(osInfo.getCreated()!=null){
            created = osInfo.getCreated();
        }
        if(osInfo.getOsId()!=null){
            osId = osInfo.getOsId();
        }
        if(osInfo.getOsName()!=null){
            osName = osInfo.getOsName();
        }
        if(osInfo.getOsType()!=null){
            osType = osInfo.getOsType();
        }
        if(osInfo.getTopUnit()!=null){
            topUnit = osInfo.getTopUnit();
        }
        if(osInfo.getCreateTime()!=null){
            createTime = osInfo.getCreateTime();
        }
        if(osInfo.getLastModifyDate()!=null){
            lastModifyDate = osInfo.getLastModifyDate();
        }
        if(osInfo.getOauthPassword()!=null){
            oauthPassword = osInfo.getOauthPassword();
        }
        if(osInfo.getOauthUser()!=null){
            oauthUser = osInfo.getOauthUser();
        }
        if(osInfo.getOsHomePage()!=null){
            osHomePage = osInfo.getOsHomePage();
        }
        if(osInfo.getOsUrl()!=null){
            osUrl = osInfo.getOsUrl();
        }
        if(osInfo.getPageFlow()!=null){
            pageFlow = osInfo.getPageFlow();
        }
        if(osInfo.getPicId()!=null){
            picId = osInfo.getPicId();
        }
        if(osInfo.getRelOptId()!=null){
            relOptId = osInfo.getRelOptId();
        }
        if(osInfo.getLogoFileId()!=null){
            logoFileId = osInfo.getLogoFileId();
        }
        if(osInfo.getOsDesc()!=null){
            logoFileId = osInfo.getOsDesc();
        }
    }
}
