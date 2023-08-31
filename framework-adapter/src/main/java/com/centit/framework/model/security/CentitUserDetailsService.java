package com.centit.framework.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public interface CentitUserDetailsService
        extends UserDetailsService
                /*AuthenticationUserDetailsService<Authentication>*/{
    /**
     * 获取用户 权限，角色名数组
     * @param loginname loginname
     * @return 用户 权限，角色名数组
     * @throws UsernameNotFoundException UsernameNotFoundException
     */
     Collection<? extends GrantedAuthority> loadUserAuthorities(String loginname) throws UsernameNotFoundException;

    /**
     * 获取用户信息 by loginname，用户的登录名
     * @param loginname loginname
     * @return  用户信息
     */
     CentitUserDetails loadDetailsByLoginName(String loginname);

    /**
     * 获取用户信息 by userCode，用户的主键
     * @param userCode userCode
     * @return 用户信息
     */
     CentitUserDetails loadDetailsByUserCode(String userCode);

    /**
     * 获取用户信息 by RegEmail，用户的主键
     * @param regEmail regEmail
     * @return 用户信息
     */
     CentitUserDetails loadDetailsByRegEmail(String regEmail);

    /**
     * 获取用户信息 by RegCellPhone，用户的主键
     * @param regCellPhone regCellPhone
     * @return 用户信息
     */
     CentitUserDetails loadDetailsByRegCellPhone(String regCellPhone);
    /**
     * 设置用户参数 （用户登录时 回写表单中的附加信息，比如用户选择的站点、语言等等信息）
     * @param userCode userCode
     * @param paramCode paramCode
     * @param paramValue paramValue
     * @param paramClass paramClass
     * @param paramName paramName
     */
     void saveUserSetting(String userCode, String paramCode,String paramValue,
            String paramClass, String paramName);

}
