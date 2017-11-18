package com.centit.framework.staticsystem.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centit.framework.model.basedata.IUserInfo;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;

/**
 * FUserinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 系统用户信息表
public class UserInfo implements IUserInfo, java.io.Serializable{
    // Fields
    private static final long serialVersionUID = 1L;

 
    private String userCode; // 用户代码

    //密码不参与返回序列化
    @JSONField(serialize = false)
    private String userPin; // 用户密码

    private String isValid; // 状态   
    
    /**
     * 'G发布任务/R接收任务/S系统管理';
     */
    private String userType; // 用户类别

    private String loginName; // 用户登录名


    private String userName; // 用户姓名

 
    private String englishName;// 用户英文姓名
    
 
    private String userDesc; // 用户描述

 
    private Long loginTimes; // 登录次数

 
    private Date activeTime; // 最后一次登录时间

 
    private Date pwdExpiredTime; // 密码失效时间
    
 
    private String loginIp; // 登录地址


    private Long addrbookId; // 通讯id


    private String regEmail; // 注册email

 
    private String userPwd;

 
    private String regCellPhone;

 
    private String userWord;
    

    private String userTag;
    
 
    private Long userOrder; // 用户排序
    
    private String primaryUnit;
    
    private String userNamePinyin; //
    // 用户的主机构，只有在数据字典中有效

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updator;

 
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
     * @return  Date
     */
    public Date getPwdExpiredTime() {
        return pwdExpiredTime;
    }

    public void setPwdExpiredTime(Date pwdExpiredTime) {
        this.pwdExpiredTime = pwdExpiredTime;
    }

    /**
     * default constructor
     */
    public UserInfo() {
        primaryUnit = null;
        //this.userType = "U";
    }

    /**
     * minimal constructor
     * @param userCode String
     * @param userstate String
     * @param loginname String
     * @param username String
     */
    public UserInfo(String userCode, String userstate, String loginname,
                    String username) {
        this.userCode = userCode;
        this.isValid = userstate;
        this.userName = username;
        this.setLoginName(loginname);
        this.primaryUnit = null;
        //this.userType = "U";
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

    @Override
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

    /**
     * 获取创建人
     * @return 创建人Code
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 获取更新人
     * @return 更新人Code
     */
    public String getUpdator() {
        return updator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
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

    /**
     * 获取用户身份证号码;这个方法不是必须的可以直接返回 null
     *
     * @return 用户身份证号码
     */
    @Override
    public String getIdCardNo() {
        return null;
    }

    public void setUserOrder(Long userorder) {
        this.userOrder = userorder;
    }

    
    private List<UserUnit> userUnits;

    public UserInfo addUserUnit(UserUnit uu) {
        if(userUnits==null)
            userUnits = new ArrayList<UserUnit>();
        userUnits.add(uu);
        return this;
    }

    @Override
    public List<UserUnit> getUserUnits() {
        return userUnits;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getCreateDate() {
        return DatetimeOpt.currentUtilDate();
    }

    /**
     * 获取最后更新时间戳
     *
     * @return 最后更新时间戳
     */
    public Date getLastModifyDate() {
        return DatetimeOpt.currentUtilDate();
    }

    public void setUserUnits( List<UserUnit> userUnits) {
        this.userUnits = userUnits;
    }

    @Override
    public boolean equals(Object other) {
       if(other instanceof IUserInfo)
           return this.getUserCode().equals(((IUserInfo) other).getUserCode());

       return false;            
    }

    @Override
    public int hashCode() {
        return this.getUserCode().hashCode();
    }

}
