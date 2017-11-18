package com.centit.framework.staticsystem.security;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.security.auth.Subject;
import java.util.*;

/**
 * FUserinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 系统用户信息表
public class StaticCentitUserDetails implements CentitUserDetails, java.io.Serializable{
    public StaticCentitUserDetails(){

    }

    public StaticCentitUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    // Fields
    private static final long serialVersionUID = 1L;

    protected UserInfo userInfo;

    private Map<String, String> userSettings;
    private Map<String, String> userOptList;

    @JSONField(serialize = false)
    private List<GrantedAuthority> arrayAuths;

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 获取用户基本信息，将用户信息 有继承 重构为组合
     * @return IUserInfo
     */
    public UserInfo getUserInfo(){
        return this.userInfo;
    }

    @Override
    public boolean isEnabled() {
        return "T".equals(this.userInfo.getIsValid());
    }

    public void setAuthoritiesByRoles(List<String> roleCodes) {
        if (roleCodes.size() < 1)
            return;
        arrayAuths = new ArrayList<>();
        for (String roleCode : roleCodes) {
            if(StringUtils.isBlank(roleCode))
                continue;
            String authCode = StringUtils.trim(roleCode);
            if(!authCode.startsWith(CentitSecurityMetadata.ROLE_PREFIX)){
                authCode = CentitSecurityMetadata.ROLE_PREFIX +authCode;
            }
            arrayAuths.add(new SimpleGrantedAuthority(authCode));
        }
        //排序便于后面比较
        Collections.sort(arrayAuths,(o1,o2) -> o1.getAuthority().compareTo(o2.getAuthority()));
        //lastUpdateRoleTime = new Date(System.currentTimeMillis());
    }
    
    @Override
    @JSONField(serialize = false)
    public List<String> getUserRoleCodes() {
        List<String> userRoles = new ArrayList<String>();
        if(arrayAuths==null)
            return userRoles;
        for(GrantedAuthority auth:arrayAuths)
            userRoles.add(auth.getAuthority().substring(2));
        return userRoles;
    }

    @Override
    public void setLoginIp(String loginHost) {
        this.userInfo.setLoginIp(loginHost);
    }

    @Override
    public void setActiveTime(Date loginTime) {
        this.userInfo.setActiveTime(loginTime);
    }


    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.arrayAuths;
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return this.userInfo.getUserPin();
    }

    @Override
    public String getUsername() {

        return this.userInfo.getLoginName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public Map<String, String> getUserSettings() {
        return new HashMap<>();
    }


    @Override
    public String getUserSettingValue(String paramCode) {
         if(userSettings==null)
                return null;

            return userSettings.get(paramCode);
    }

    @Override
    public void setUserSettingValue(String paramCode, String paramValue) {
        if(userSettings==null)
            userSettings=new HashMap<>();
        userSettings.put(paramCode, paramValue);
    }



    public void setUserOptList(Map<String, String> userOptList) {
        this.userOptList = userOptList;
    }

    @Override
    public Map<String, String> getUserOptList() {
        if(userOptList==null)
            userOptList = new HashMap<>();
        return userOptList;
    }

    @Override
    public boolean checkOptPower(String optId, String optMethod) {
        String s = userOptList.get(optId + "-" + optMethod);
        if (s == null) {
            return false;
        }
        return true;//"T".equals(s);
    }

    @Override
    @JSONField(serialize = false)
    public Object getCredentials() {
        return this.userInfo.getUserPin();
    }

    @Override
    @JSONField(serialize = false)
    public Object getDetails() {
        return this;
    }

    @Override
    @JSONField(serialize = false)
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return this.userInfo.getLoginName();
    }

    @Override
    public boolean equals(Object other) {
       if(other instanceof CentitUserDetails)
           return this.userInfo.getUserCode().equals(
                   ((CentitUserDetails) other).getUserInfo().getUserCode());

       return false;            
    }

    @Override
    public int hashCode() {
        return this.userInfo.getUserCode().hashCode();
    }

}
