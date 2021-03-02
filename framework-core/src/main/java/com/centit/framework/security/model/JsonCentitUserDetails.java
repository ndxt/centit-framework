package com.centit.framework.security.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.security.SecurityContextUtils;
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
public class JsonCentitUserDetails implements CentitUserDetails, java.io.Serializable{

    private static final long serialVersionUID = 20181227L;

    private String loginIp;
    private String currentStationId;
    private Map<String, String> userSettings;
    private Map<String, String> userOptList;

    protected JSONObject userInfo;
    private JSONArray userRoles;
    private JSONArray userUnits;

    private String topUnitCode;

    @JSONField(serialize = false)
    private List<GrantedAuthority> arrayAuths;

    @Override
    @JSONField(serialize = false)
    public String getUserCode(){
        return getUserInfo().getString("userCode");
    }

    @Override
    public boolean isEnabled() {
        return "T".equals(getUserInfo().getString("isValid"));
    }

    protected void makeUserAuthorities(){
        arrayAuths = new ArrayList<>();
        JSONArray rolesJson = this.getUserRoles();
        if (rolesJson == null || rolesJson.size() < 1) {
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                + SecurityContextUtils.PUBLIC_ROLE_CODE));
            return;
        }

        boolean havePublicRole = false;
        for (Object obj : rolesJson) {
            JSONObject role  =(JSONObject) obj;
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                    + StringUtils.trim(role.getString("roleCode"))));
            if(SecurityContextUtils.PUBLIC_ROLE_CODE.equalsIgnoreCase(role.getString("roleCode"))){
                havePublicRole = true;
            }
        }

        if(!havePublicRole){
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                    + SecurityContextUtils.PUBLIC_ROLE_CODE));
        }

        //排序便于后面比较
        Collections.sort(arrayAuths,
            Comparator.comparing(GrantedAuthority::getAuthority));
        //lastUpdateRoleTime = new Date(System.currentTimeMillis());
    }

    public void addAuthorities(GrantedAuthority ga){
        String sRole = ga.getAuthority();
        if(!sRole.startsWith(CentitSecurityMetadata.ROLE_PREFIX )){
            sRole = CentitSecurityMetadata.ROLE_PREFIX + sRole;
        }
        SimpleGrantedAuthority sga = new SimpleGrantedAuthority(sRole);
        if(arrayAuths.contains( sga)){
            return;
        }
        arrayAuths.add(sga);
        Collections.sort(arrayAuths,
            Comparator.comparing(GrantedAuthority::getAuthority));
    }

    @Override
    @JSONField(serialize = false)
    public JSONObject getCurrentStation() {
        JSONArray uus = this.getUserUnits();
        if (uus != null) {
            for (Object uu : uus) {
                JSONObject userUnit = (JSONObject) uu;
                if (StringUtils.equals(currentStationId, userUnit.getString("userUnitId"))) {
                    return userUnit;
                }

                if (StringUtils.isBlank(currentStationId) &&
                    "T".equals(userUnit.getString("isPrimary"))) {
                    return userUnit;
                }
            }
            if(uus.size()>0){
                return (JSONObject)uus.get(0);
            }
        }
        return null;
    }

    @Override
    public void setCurrentStationId(String userUnitId) {
        currentStationId = userUnitId;
    }

    public String getCurrentStationId(){
        return currentStationId;
    }

    @Override
    @JSONField(serialize = false)
    public String getCurrentUnitCode(){
        JSONObject cs = getCurrentStation();
        return cs != null? cs.getString("unitCode"): getUserInfo().getString("primaryUnit");
    }

    @Override
    public String getLoginIp() {
        return loginIp;
    }

    @Override
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
    @Override
    @JSONField(serialize = false)
    public String getTopUnitCode(){
        if(StringUtils.isNotBlank(topUnitCode)){
            return "all";
        }
        return topUnitCode;
    }

    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.arrayAuths;
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return this.getUserInfo().getString("userPin");
    }

    /**
     * 这儿的username是用户的登录名，userInfo中的userName是用户的中文名称
     * @return 登录名
     */
    @Override
    public String getUsername() {
        return this.getUserInfo().getString("loginName");
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

    public void putUserSettingsParams(String paramCode, String paramValue) {
        if(userSettings==null)
            userSettings = new HashMap<>();
        userSettings.put(paramCode, paramValue);
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
        return this.getUserInfo().getString("userPin");
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
        return this.getUserInfo().getString("loginName");
    }

    @Override
    public boolean equals(Object other) {
       if(other instanceof CentitUserDetails)
           return this.getUserInfo().getString("userCode").equals(
                   ((CentitUserDetails) other).getUserInfo().getString("userCode"));

       return false;
    }

    @Override
    public int hashCode() {
        return this.getUserInfo().getString("userCode").hashCode();
    }

    @Override
    public JSONObject getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(JSONObject userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 获得用户授权角色
     *
     * @return 获得用户授权角色代码
     */
    @Override
    public JSONArray getUserRoles() {
        return this.userRoles;
    }

    public void setUserRoles(JSONArray roles) {
        this.setAuthoritiesByRoles(roles);
    }

    public void setAuthoritiesByRoles(JSONArray roles) {
        this.userRoles = roles;
        makeUserAuthorities();
    }

    @Override
    public JSONArray getUserUnits() {
        return userUnits;
    }

    public void setUserUnits(JSONArray userUnits) {
        this.userUnits = userUnits;
    }
}
