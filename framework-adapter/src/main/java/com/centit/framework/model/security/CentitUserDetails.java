package com.centit.framework.model.security;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.centit.framework.model.basedata.*;
import com.centit.support.algorithm.DatetimeOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * FUserinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 系统用户信息表
public class CentitUserDetails implements Authentication, UserDetails, java.io.Serializable{

    private static final long serialVersionUID = 20181227L;

    private String loginIp;
    private Map<String, String> userSettings;
    private Map<String, String> userOptList;

    protected UserInfo userInfo;
    private List<UserRole> userRoles;
    private List<UserUnit> userUnits;

    private String topUnitCode;
    private String topUnitName;
    private String currentUnitName;

    private String tenantRole;

    @JSONField(serialize = false)
    private transient List<GrantedAuthority> arrayAuths;

    @JSONField(serialize = false)
    public String getUserCode(){
        if(userInfo==null)
            return null;
        return userInfo.getUserCode();
    }

    /**
     * T:可用
     * W：未加入任何租户，也表示可用状态
     * @return
     */
    @Override
    public boolean isEnabled() {
        return StringUtils.equalsAny(getUserInfo().getIsValid(),"T","W");
    }

    protected void makeUserAuthorities(){
        if(this.userRoles == null)
            return;
        this.arrayAuths = new ArrayList<>();
        for (UserRole role : this.userRoles) {
            arrayAuths.add(new SimpleGrantedAuthority("R_" //CentitSecurityMetadata.ROLE_PREFIX
                    + StringUtils.trim(role.getRoleCode())));

        }
        //排序便于后面比较
        Collections.sort(arrayAuths,
            Comparator.comparing(GrantedAuthority::getAuthority));
        //lastUpdateRoleTime = new Date(System.currentTimeMillis());
    }

    public void addAuthorities(GrantedAuthority ga){
        String sRole = ga.getAuthority();
        if(!sRole.startsWith("R_" )){
            sRole = "R_" + sRole;
        }
        SimpleGrantedAuthority sga = new SimpleGrantedAuthority(sRole);
        if(arrayAuths.contains( sga)){
            return;
        }
        arrayAuths.add(sga);
        Collections.sort(arrayAuths,
            Comparator.comparing(GrantedAuthority::getAuthority));
    }

    public String getTopUnitName() {
        return topUnitName;
    }

    public void setTopUnitName(String topUnitName) {
        this.topUnitName = topUnitName;
    }

    public String getCurrentUnitName() {
        return currentUnitName;
    }

    public void setCurrentUnitName(String currentUnitName) {
        this.currentUnitName = currentUnitName;
    }

    public String getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(String tenantRole) {
        this.tenantRole = tenantRole;
    }

    @JSONField(serialize = false)
    public UserUnit getCurrentStation() {
        List<UserUnit> uus = this.getUserUnits();
        String currentStationId = userInfo.getCurrentStationId();//.getString("currentStationId");
        if (uus != null) {
            if(StringUtils.isNotBlank(currentStationId)){
                for (UserUnit userUnit : uus) {
                    if (StringUtils.equals(currentStationId, userUnit.getUserUnitId())) {
                        return userUnit;
                    }
                }
            }
            for (UserUnit userUnit : uus) {
                if ("T".equals(userUnit.getRelType())) {
                    return userUnit;
                }
            }
            if(uus.size()>0){
                return uus.get(0);
            }
        }
        return null;
    }

    public void setCurrentStationId(String userUnitId) {
        userInfo.setCurrentStationId(userUnitId);
    }

    public String getCurrentStationId(){
        return userInfo.getCurrentStationId();
    }


    public String getCurrentUnitCode(){
        UserUnit cs = getCurrentStation();
        return cs != null? cs.getUnitCode() : getUserInfo().getPrimaryUnit();
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }


    public void setTopUnitCode(String topUnitCode) {
        this.topUnitCode = topUnitCode;
    }
    /**
     * 用户登录时需要设置这个值
     * @return 最上层机构代码，根据用户的当前结构设置可能有变化
     */
    public String getTopUnitCode(){
        return topUnitCode;
    }

    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.arrayAuths ==null || this.arrayAuths.size()==0)
            makeUserAuthorities();
        return this.arrayAuths;
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return this.getUserInfo().getUserPin();
    }

    /**
     * 这儿的username是用户的登录名，userInfo中的userName是用户的中文名称
     * @return 登录名
     */
    @Override
    public String getUsername() {
        return this.getUserInfo().getLoginName();
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
        return userInfo.getPwdExpiredTime() == null
            || DatetimeOpt.currentUtilDate().before(userInfo.getPwdExpiredTime());
    }

    public void setUserSettings(Map<String, String> userSettings) {
        this.userSettings = userSettings;
    }

    public void putUserSettingsParams(String paramCode, String paramValue) {
        if(userSettings==null)
            userSettings = new HashMap<>();
        userSettings.put(paramCode, paramValue);
    }

    public Map<String, String> getUserSettings() {
        if(userSettings==null) {
            userSettings = new HashMap<>(2);
        }
        return this.userSettings;
    }

    public String getUserSettingValue(String paramCode) {
         if(userSettings==null) {
             return null;
         }
         return userSettings.get(paramCode);
    }

    public void setUserSettingValue(String paramCode, String paramValue) {
        if(userSettings==null) {
            userSettings = new HashMap<>(10);
        }
        userSettings.put(paramCode, paramValue);
    }

    public void setUserOptList(Map<String, String> userOptList) {
        this.userOptList = userOptList;
    }


    public Map<String, String> getUserOptList() {
        if(userOptList==null) {
            userOptList = new HashMap<>(10);
        }
        return userOptList;
    }

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
        return this.getUserInfo().getUserPin();
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
    public void setAuthenticated(boolean isAuthenticated)
        throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return this.getUserInfo().getLoginName();
    }

    @Override
    public boolean equals(Object other) {
       if(other instanceof CentitUserDetails) {
           return this.getUserInfo().getUserCode().equals(
                   ((CentitUserDetails) other).getUserInfo().getUserCode());
       }

       return false;
    }

    @Override
    public int hashCode() {
        return this.getUserInfo().getUserCode().hashCode();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 获得用户授权角色
     *
     * @return 获得用户授权角色代码
     */
    public List<UserRole> getUserRoles() {
        return this.userRoles;
    }

    public void setUserRoles(List<UserRole> roles) {
        setAuthoritiesByRoles(roles);
    }

    public void setAuthoritiesByRoles(List<UserRole> roles) {
        this.userRoles = roles;
        makeUserAuthorities();
    }

    public void mapAuthoritiesByRoles(List<RoleInfo> roles) {
        this.userRoles = new ArrayList<>();
        if(roles !=null ) {
            Date yesterday = DatetimeOpt.addDays(DatetimeOpt.currentUtilDate(), -1);
            for(RoleInfo ri : roles){
                UserRole ur = new UserRole(new UserRoleId(this.getUserCode(), ri.getRoleCode()),
                    yesterday, "角色映射");
                this.userRoles.add(ur);
            }
            makeUserAuthorities();
        }
    }

    public List<UserUnit> getUserUnits() {
        return userUnits;
    }

    public void setUserUnits(List<UserUnit> userUnits) {
        this.userUnits = userUnits;
    }

    public JSONObject toJsonWithoutSensitive(){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(this);
        jsonObject.put("userInfo", this.userInfo.toJsonWithoutSensitive());
        return jsonObject;
    }
}
