package com.centit.framework.system.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.model.basedata.IUserInfo;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * FUserinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 系统用户信息表
@Entity
@Table(name = "F_USERINFO")
public class UserInfo implements IUserInfo, EntityWithTimestamp, java.io.Serializable{
    // Fields
    private static final long serialVersionUID = -1753127177790732963L;

    @Id
    @Column(name = "USERCODE")
    @GeneratedValue(generator = "assignedGenerator")
    private String userCode; // 用户代码

    //密码不参与返回序列化
    @JSONField(serialize = false)
    @Column(name = "USERPIN")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String userPin; // 用户密码

    @Column(name = "ISVALID")
    @NotBlank(message = "字段不能为空")
    @Pattern(regexp = "[TFA]", message = "字段值必须是T或F,A为新建可以删除")
    private String isValid; // 状态


    /**
     * 'G发布任务/R接收任务/S系统管理';
     */
    @Column(name = "USERTYPE")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String userType; // 用户类别

    @Column(name = "LOGINNAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String loginName; // 用户登录名

    @Column(name = "USERNAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 300, message = "字段长度不能大于{max}")
    private String userName; // 用户姓名

    @Column(name = "ENGLISHNAME")
    @Length(max = 300, message = "字段长度不能大于{max}")
    private String englishName;// 用户英文姓名

    @Column(name = "USERDESC")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String userDesc; // 用户描述

    @Column(name = "LOGINTIMES")
    private Long loginTimes; // 登录次数

    @Column(name = "ACTIVETIME")
    @Temporal(TemporalType.DATE)
    private Date activeTime; // 最后一次登录时间

    @Column(name = "PWDEXPIREDTIME")
    @Temporal(TemporalType.DATE)
    private Date pwdExpiredTime; // 密码失效时间

    @Column(name = "LOGINIP")
    @Length(max = 16, message = "字段长度不能大于{max}")
    private String loginIp; // 登录地址

    @Column(name = "ADDRBOOKID")
    @Range(min = 1, max = 999999999, message = "字段不能小于{min}大于{max}")
    private Long addrbookId; // 通讯id

    @Column(name = "REGEMAIL")
    @Email(message = "Email格式不正确")
    @Length(max = 60, message = "字段长度不能大于{max}")
    private String regEmail; // 注册email

    @Column(name = "USERPWD")
    @Length(max = 20, message = "字段长度不能大于{max}")
    @JSONField(serialize = false)
    private String userPwd;

    @Column(name = "REGCELLPHONE")
    @Length(max = 15, message = "字段长度不能大于{max}")
    private String regCellPhone;

    @Column(name = "USERWORD")
    @Length(max = 100, message = "字段长度不能}大于{max}")
    private String userWord;

    @Column(name = "USERTAG")
    @Length(max = 100, message = "字段长度不能}大于{max}")
    private String userTag;

    @Column(name = "USERORDER")
    @Range(max = 99999, message = "字段不能大于{max}")
    private Long userOrder; // 用户排序

    @Column(name = "PRIMARYUNIT")
    private String primaryUnit;

    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

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
    @Column(name = "UPDATEDATE")
    private Date  updateDate;


    @Transient
    private String userNamePinyin; //
    // 用户的主机构，只有在数据字典中有效

    @Transient
    @JSONField(serialize=false)
    private List<UserUnit> userUnits;

    @Transient
    @JSONField(serialize=false)
    private List<UserRole> userRoles;


    @JSONField(serialize = false)
    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getRegCellPhone() {
        return regCellPhone;
    }

    public void setRegCellPhone(String regCellPhone) {
        this.regCellPhone = regCellPhone;
    }

    public String getUserWord() {
        return userWord;
    }

    public void setUserWord(String userWord) {
        this.userWord = userWord;
    }

    /**
     * 密码失效时间
     * @return 密码失效时间
     */
    public Date getPwdExpiredTime() {
		return pwdExpiredTime;
	}

	public void setPwdExpiredTime(Date pwdExpiredTime) {
		this.pwdExpiredTime = pwdExpiredTime;
	}

    public UserInfo() {
        userUnits = null;
        primaryUnit = null;
        userRoles = null;
       
        //this.userType = "U";
    }

    public UserInfo(String userCode, String userstate, String loginname,
                    String username) {
        this.userCode = userCode;
        this.isValid = userstate;
        this.userName = username;
        this.setLoginName(loginname);
        this.userUnits = null;
        this.primaryUnit = null;
        //this.userType = "U";
        userRoles = null;
    }

    public UserInfo(String userCode, String userpin,String usertype, String userstate,
            String loginname, String username, String userdesc,
            Long logintimes, Date activeime, String loginip, Long addrbookid) {
			this.userCode = userCode;
			this.userPin = userpin;
			this.isValid = userstate;
			this.userName = username;
			this.userType = usertype;
			this.userDesc = userdesc;
			this.loginTimes = logintimes;
			this.activeTime = activeime;
			this.loginIp = loginip;
			this.loginName = loginname;
			this.addrbookId = addrbookid;
			// userUnits=null;
			primaryUnit = null;
			}

    
    public UserInfo(String userCode, String userpin,String usertype, String userstate,
                    String loginname, String username, String userdesc,
                    String usertag, String englishname,
                    Long logintimes, Date activeime, String loginip, Long addrbookid) {
        this.userCode = userCode;
        this.userPin = userpin;
        this.isValid = userstate;
        this.userName = username;
        this.userType = usertype;
        this.userDesc = userdesc;
        this.loginTimes = logintimes;
        this.activeTime = activeime;
        this.loginIp = loginip;
        this.loginName = loginname;
        this.addrbookId = addrbookid;
        this.userTag = usertag;
        this.englishName = englishname;
        // userUnits=null;
        primaryUnit = null;
    }

    // Property accessors
    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JSONField(serialize = false)
    public String getUserPin() {
        return this.userPin;
    }

    public void setUserPin(String userpin) {
        this.userPin = userpin;
    }

    public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getUserTag() {
		return userTag;
	}

	public void setUserTag(String userTag) {
		this.userTag = userTag;
	}

	/**
     * T:生效 F:无效
     *
     * @return T:生效 F:无效
     */
    public String getIsValid() {
        return this.isValid;
    }

    /**
     * @param userstate T:生效 F:无效
     */
    public void setIsValid(String userstate) {
        this.isValid = userstate;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public void setUserName(String username) {
        this.userName = username;
    }

    public String getUserDesc() {
        return this.userDesc;
    }

    public void setUserDesc(String userdesc) {
        this.userDesc = userdesc;
    }

    public Long getLoginTimes() {
        return this.loginTimes;
    }

    public void setLoginTimes(Long logintimes) {
        this.loginTimes = logintimes;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public void setLoginIp(String loginip) {
        this.loginIp = loginip;
    }

    public boolean isEnabled() {
        return "T".equals(isValid);
    }

    public String getLoginName() {
        if (loginName == null)
            return "";
        return loginName;
    }

    public void setLoginName(String loginname) {
        if(loginname==null)
            return;
        this.loginName = loginname.toLowerCase();
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activetime) {
        this.activeTime = activetime;
    }

    public String getUserNamePinyin() {
        return userNamePinyin;
    }

    public void setUserNamePinyin(String usernamepinyin) {
        this.userNamePinyin = usernamepinyin;
    }

    public Long getAddrbookId() {
        return addrbookId;
    }

    public void setAddrbookId(Long addrbookid) {
        this.addrbookId = addrbookid;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail = regEmail;
    }

    public String getRegEmail() {
        return regEmail;
    }

    public String getPrimaryUnit() {
        return primaryUnit;
    }
      
    public void setPrimaryUnit(String primaryUnit) {
        this.primaryUnit = primaryUnit;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	@Override
    public Date getLastModifyDate() {
        return updateDate;
    }

	@Override
    public void setLastModifyDate(Date lastModifyDate) {
        this.updateDate = lastModifyDate;
    }
	
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

	public void copy(UserInfo other) {
        this.userCode = other.getUserCode();
        this.userPin = other.getUserPin();
        this.isValid = other.getIsValid();
        this.loginName = other.getLoginName();
        this.userName = other.getUserName();
        this.userDesc = other.getUserDesc();
        this.primaryUnit=other.getPrimaryUnit();
        this.loginTimes = other.getLoginTimes();
        this.activeTime = other.getActiveTime();
        this.loginIp = other.getLoginIp();
        this.addrbookId = other.getAddrbookId();
        this.regEmail = other.getRegEmail();
        this.regCellPhone = other.getRegCellPhone();
        this.userOrder = other.getUserOrder();
        this.userType = other.getUserType();
        this.userTag = other.getUserTag();
        this.englishName =other.getEnglishName();
        this.replaceUserRoles(other.listUserRoles());
        this.replaceUserUnits(other.getUserUnits());
        this.creator=other.creator;
        this.updator=other.updator;
        this.updateDate=other.updateDate;
    }

    public void copyNotNullProperty(UserInfo other) {
        /*if (other.getUserCode() != null)
            this.userCode = other.getUserCode();
        if (other.getUserPin() != null)
            this.userPin = other.getUserPin();*/
        if (other.getIsValid() != null)
            this.isValid = other.getIsValid();
        if (other.getUserType() != null)
            this.userType = other.getUserType();
        if(other.getPrimaryUnit()!=null)
            this.primaryUnit=other.getPrimaryUnit();
        if (other.getLoginName() != null)
            this.loginName = other.getLoginName();
        if (other.getUserName() != null)
            this.userName = other.getUserName();
        if (other.getUserDesc() != null)
            this.userDesc = other.getUserDesc();
        if (other.getLoginTimes() != null)
            this.loginTimes = other.getLoginTimes();
        if (other.getActiveTime() != null)
            this.activeTime = other.getActiveTime();
        if (other.getLoginIp() != null)
            this.loginIp = other.getLoginIp();
        if (other.getAddrbookId() != null)
            this.addrbookId = other.getAddrbookId();
        if (other.getRegEmail() != null)
            this.regEmail = other.getRegEmail();
        if (other.getUserTag() != null)
        	this.userTag = other.getUserTag();
        if (other.getEnglishName() != null)
        	this.englishName =other.getEnglishName();
        if (other.getUserOrder() != null)
            this.userOrder = other.getUserOrder();
        if (other.regCellPhone != null)
            this.regCellPhone = other.getRegCellPhone();
        if (other.listUserRoles() != null)
            this.replaceUserRoles(other.listUserRoles());
        if(other.getUserUnits()!=null)
            this.replaceUserUnits(other.getUserUnits());
        if (other.getCreator() != null)
        	this.creator =other.getCreator();
        if (other.getUpdator() != null)
        	this.updator =other.getUpdator();
        if (other.getUpdateDate() != null)
        	this.updateDate =other.getUpdateDate();
    }

    public void clearProperties() {
        this.userCode = null;
        this.userPin = null;
        this.isValid = null;
        this.loginName = null;
        this.userName = null;
        this.userDesc = null;
        this.loginTimes = null;
        this.activeTime = null;
        this.loginIp = null;
        this.addrbookId = null;
        this.primaryUnit=null;
        this.regEmail = null;
        this.userType = null;
        this.userOrder = null;
    }

    public Long getUserOrder() {
        if (userOrder == null)
            return 1000l;
        return userOrder;
    }

    public void setUserOrder(Long userorder) {
        this.userOrder = userorder;
    }

    @Override
    @JSONField(serialize=false)
    public List<UserUnit> getUserUnits() {
        if (userUnits == null)
            userUnits = new ArrayList<UserUnit>();
        return userUnits;
    }

    public void setUserUnits(List<UserUnit> userUnits) {
        this.userUnits = userUnits;
    }
    
    public List<UserRole> listUserRoles() {
        if(null==userRoles)
            userRoles=new ArrayList<UserRole>();
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    
    private void addUserUnit(UserUnit userunit) {
        this.getUserUnits().add(userunit);
        
    }

    private void removeUserUnit(UserUnit odt) {
        this.getUserUnits().remove(odt);
        
    }

    private void addUserRole(UserRole userrole) {
        userrole.setUserCode(this.userCode);
        this.listUserRoles().add(userrole);
    }

    private void removeUserRole(UserRole odt) {
        this.listUserRoles().remove(odt);
        
    }
    /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     * @param  userRoles Collection
     */
    public void replaceUserRoles(Collection<UserRole> userRoles) {
        //必须不稳null，如果为null 请直接调用删除
    	if(userRoles==null)
            return;
        List<UserRole> newObjs = new ArrayList<UserRole>();
        for(UserRole p :userRoles){
            if(p==null)
                continue;
            UserRole newdt = new UserRole();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<UserRole> oldObjs = new HashSet<UserRole>();
        oldObjs.addAll(listUserRoles());
        
        for(Iterator<UserRole> it=oldObjs.iterator(); it.hasNext();){
            UserRole odt = it.next();
            found = false;
            for(UserRole newdt :newObjs){
                if(odt.getId().equals( newdt.getId())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeUserRole(odt);
        }
        oldObjs.clear();
        //insert or update
        for(UserRole newdt :newObjs){
            found = false;
            for(Iterator<UserRole> it=listUserRoles().iterator();
             it.hasNext();){
                UserRole odt = it.next();
                if(odt.getId().equals( newdt.getId())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addUserRole(newdt);
        }   
    }
     /**
     * 替换子类对象数组，这个函数主要是考虑hibernate中的对象的状态，以避免对象状态不一致的问题
     * @param  userUnits Collection
     */
    public void replaceUserUnits(Collection<UserUnit> userUnits) {
        if(userUnits==null)
            return;
        List<UserUnit> newObjs = new ArrayList<UserUnit>();
        for(UserUnit p :userUnits){
            if(p==null)
                continue;
            UserUnit newdt = new UserUnit();
            newdt.copyNotNullProperty(p);
            newObjs.add(newdt);
        }
        //delete
        boolean found = false;
        Set<UserUnit> oldObjs = new HashSet<UserUnit>();
        oldObjs.addAll(getUserUnits());
        
        for(Iterator<UserUnit> it=oldObjs.iterator(); it.hasNext();){
            UserUnit odt = it.next();
            found = false;
            for(UserUnit newdt :newObjs){
                if(odt.getUserUnitId().equals( newdt.getUserUnitId())){
                    found = true;
                    break;
                }
            }
            if(! found)
                removeUserUnit(odt);
        }
        oldObjs.clear();
        //insert or update
        for(UserUnit newdt :newObjs){
            found = false;
            for(Iterator<UserUnit> it=getUserUnits().iterator();
             it.hasNext();){
                UserUnit odt = it.next();
                if(odt.getUserUnitId().equals( newdt.getUserUnitId())){
                    odt.copy(newdt);
                    found = true;
                    break;
                }
            }
            if(! found)
                addUserUnit(newdt);
        }   
    }

}
