package com.centit.framework.staticsystem.service.impl;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;

public class UserDetailsServiceImpl implements 
	CentitUserDetailsService,UserDetailsService, 
	AuthenticationUserDetailsService<Authentication>
{
	private PlatformEnvironment platformEnvironment;
	
	public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
		this.platformEnvironment = platformEnvironment;
	}
	
	@Override
	public UserDetails loadUserByUsername(String userLoginName) throws UsernameNotFoundException {
		return loadDetailsByLoginName(userLoginName);
	}

	@Override
	public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
		return loadDetailsByLoginName(token.getName());
	}

	@Override
	public Collection<? extends GrantedAuthority> loadUserAuthorities(String loginname) throws UsernameNotFoundException {
		CentitUserDetails ud = loadDetailsByLoginName(loginname);
		if(ud==null)
			return null;
		return ud.getAuthorities();
	}

	@Override
	public CentitUserDetails loadDetailsByLoginName(String loginName) {
		return platformEnvironment.loadUserDetailsByLoginName(loginName);
	}

	@Override
	public CentitUserDetails loadDetailsByUserCode(String userCode) {
		return platformEnvironment.loadUserDetailsByUserCode(userCode);
	}
	
	@Override
	public CentitUserDetails loadDetailsByRegEmail(String regEmail) {
		return platformEnvironment.loadUserDetailsByRegEmail(regEmail);
	}
	
	@Override
	public CentitUserDetails loadDetailsByRegCellPhone(String regCellPhone) {
		return platformEnvironment.loadUserDetailsByRegCellPhone(regCellPhone);
	}

	@Override
	public void saveUserSetting(String userCode, String paramCode, String paramValue, String paramClass,
			String paramName) {

	}
}
