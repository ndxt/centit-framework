package com.centit.framework.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public interface CentitUserDetailsService 
		extends UserDetailsService
				/*AuthenticationUserDetailsService<Authentication>*/{
	/**
	 * 获取用户 权限，角色名数组
	 * @param loginname
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public Collection<? extends GrantedAuthority> loadUserAuthorities(String loginname) throws UsernameNotFoundException;
	
	/**
	 * 获取用户信息 by loginname，用户的登录名
	 * @param loginname
	 * @return
	 */
    public CentitUserDetails loadDetailsByLoginName(String loginname);
    
    /**
     * 获取用户信息 by userCode，用户的主键
     * @param userCode
     * @return
     */
    public CentitUserDetails loadDetailsByUserCode(String userCode);
    
    /**
     * 获取用户信息 by RegEmail，用户的主键
     * @param regEmail
     * @return
     */
    public CentitUserDetails loadDetailsByRegEmail(String regEmail);

    /**
     * 获取用户信息 by RegCellPhone，用户的主键
     * @param regCellPhone
     * @return
     */
    public CentitUserDetails loadDetailsByRegCellPhone(String regCellPhone);
    /**
     * 设置用户参数 （用户登录时 回写表单中的附加信息，比如用户选择的站点、语言等等信息）
     * @param userCode
     * @param paramCode
     * @param paramValue
     * @param paramClass
     * @param paramName
     */
    public void saveUserSetting(String userCode, String paramCode,String paramValue,
			String paramClass, String paramName);

}
