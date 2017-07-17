package com.centit.framework.mybatis;

public class UserInfo implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    private String userCode; // 用户代码
	
	private String userPin; // 用户密码
	
	private String isValid; // 状态
	
	
	private String userType; // 用户类别
	
	
	private String loginName; // 用户登录名
	
	
	private String userName; // 用户姓名
	
	
	private String englishName;// 用户英文姓名
	
	private String userDesc; // 用户描述

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserPin() {
		return userPin;
	}

	public void setUserPin(String userPin) {
		this.userPin = userPin;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}
	
}
