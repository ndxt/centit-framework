package com.centit.framework.security;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.model.security.CentitUserDetailsService;
import com.centit.support.algorithm.CollectionsOpt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDetailsServiceImpl implements
    CentitUserDetailsService,
    AuthenticationUserDetailsService<Authentication>
{
    private static Pattern pattern = Pattern.compile("[0-9]*");
    private PlatformEnvironment platformEnvironment;

    public void setPlatformEnvironment(PlatformEnvironment platformEnvironment) {
        this.platformEnvironment = platformEnvironment;
    }

    //来自 UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String userLoginName) throws UsernameNotFoundException {
        UserDetails ud = null;
        if(userLoginName.indexOf('@')>=0){//邮箱
            ud = loadDetailsByRegEmail(userLoginName);
        } else{
            Matcher isNum = pattern.matcher(userLoginName);
            if(userLoginName.length() == 11 && isNum.matches()){
                ud = loadDetailsByRegCellPhone(userLoginName);
            }else{
                ud=loadDetailsByLoginName(userLoginName);
            }
        }
        if(ud==null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return ud;
    }

    @Override
    public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
        return loadUserByUsername(token.getName());
    }

    @Override
    public Collection<? extends GrantedAuthority> loadUserAuthorities(String loginname) throws UsernameNotFoundException {
        CentitUserDetails ud = loadDetailsByLoginName(loginname);
        if(ud==null) {
            return null;
        }
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
