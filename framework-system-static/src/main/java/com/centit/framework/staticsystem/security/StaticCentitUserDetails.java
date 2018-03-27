package com.centit.framework.staticsystem.security;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private String currentStationId;
    private Map<String, String> userSettings;
    private Map<String, String> userOptList;
    private List<RoleInfo> userRoles;

    @JSONField(serialize = false)
    private List<GrantedAuthority> arrayAuths;

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 获取用户基本信息，将用户信息 有继承 重构为组合
     * @return IUserInfo
     */
    @Override
    public UserInfo getUserInfo(){
        return this.userInfo;
    }

    @Override
    @JSONField(serialize = false)
    public String getUserCode(){
        return getUserInfo().getUserCode();
    }

    @Override
    public boolean isEnabled() {
        return "T".equals(this.userInfo.getIsValid());
    }

    @Override
    public List<RoleInfo> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<RoleInfo> userRoles) {
        this.userRoles = userRoles;
    }

    private void makeUserAuthorities(){
        arrayAuths = new ArrayList<>();
        if (this.userRoles.size() < 1)
            return;

        for (RoleInfo role : this.userRoles) {
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                    + StringUtils.trim(role.getRoleCode())));
        }
        //排序便于后面比较
        Collections.sort(arrayAuths,Comparator.comparing(GrantedAuthority::getAuthority));
        //lastUpdateRoleTime = new Date(System.currentTimeMillis());
    }

    // Property accessors
    public void setAuthoritiesByRoles(List<RoleInfo> roles) {
        setUserRoles(roles);
        makeUserAuthorities();
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
    public IUserUnit getCurrentStation() {
        List<UserUnit> uus = getUserInfo().getUserUnits();
        if (uus != null) {
            for (UserUnit uu : uus) {
                if (StringUtils.equals(currentStationId, uu.getUserUnitId())) {
                    return uu;
                }

                if (StringUtils.isBlank(currentStationId) && "T".equals(uu.getIsPrimary())) {
                    return uu;
                }
            }
        }
        return null;
    }

    @Override
    public void setCurrentStationId(String userUnitId) {
        currentStationId = userUnitId;
    }

    @Override
    public String getCurrentStationId(){
        IUserUnit cs = getCurrentStation();
        return cs != null? cs.getUnitCode() : getUserInfo().getPrimaryUnit();
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

    public void setUserSettings(Map<String, String> userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public Map<String, String> getUserSettings() {
        if(userSettings==null) {
            userSettings = new HashMap<>(2);
        }
        return this.userSettings;
    }

    @Override
    public String getUserSettingValue(String paramCode) {
         if(userSettings==null) {
             return null;
         }
         return userSettings.get(paramCode);
    }

    @Override
    public void setUserSettingValue(String paramCode, String paramValue) {
        if(userSettings==null) {
            userSettings = new HashMap<>(10);
        }
        userSettings.put(paramCode, paramValue);
    }

    public void setUserOptList(Map<String, String> userOptList) {
        this.userOptList = userOptList;
    }

    @Override
    public Map<String, String> getUserOptList() {
        if(userOptList==null) {
            userOptList = new HashMap<>(10);
        }
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
