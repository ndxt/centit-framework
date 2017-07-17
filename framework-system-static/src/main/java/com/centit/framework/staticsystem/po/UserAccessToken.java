package com.centit.framework.staticsystem.po;

import java.util.Date;


public class UserAccessToken implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
 
    private String tokenId;
    
    private String userCode;
    
    private String secretAccessKey;

    private String isValid;
    
    private Date createTime;

    // Constructors

    /**
     * default constructor
     */
    public UserAccessToken() {
    }


	public String getTokenId() {
		return tokenId;
	}


	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}


	public String getUserCode() {
		return userCode;
	}


	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}


	public String getSecretAccessKey() {
		return secretAccessKey;
	}


	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}


	public String getIsValid() {
		return isValid;
	}


	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

   
}
