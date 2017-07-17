package com.centit.framework.mybatis;

import org.apache.ibatis.annotations.Select;

public interface UserInfoDao {
	@Select(value="select userCode,userPin,userName,isValid,userType,loginName,englishName,userDesc "
			+ "from f_userinfo where userCode = #{userCode}")
	public UserInfo getUser(String userCode);
}
