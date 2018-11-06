package com.centit.framework.security.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.IRoleInfo;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserUnit;
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
public abstract class AbstractCentitUserDetails implements CentitUserDetails, java.io.Serializable{

    private String loginIp;
    private String currentStationId;
    private Map<String, String> userSettings;
    private Map<String, String> userOptList;


    @JSONField(serialize = false)
    private List<GrantedAuthority> arrayAuths;

    @Override
    @JSONField(serialize = false)
    public String getUserCode(){
        return getUserInfo().getUserCode();
    }

    @Override
    public boolean isEnabled() {
        return "T".equals(getUserInfo().getIsValid());
    }

    protected void makeUserAuthorities(){
        arrayAuths = new ArrayList<>();
        if (this.getUserRoles().size() < 1)
            return;

        boolean havePublicRole = false;
        for (IRoleInfo role : this.getUserRoles()) {
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                    + StringUtils.trim(role.getRoleCode())));
            if(SecurityContextUtils.PUBLIC_ROLE_CODE.equalsIgnoreCase(role.getRoleCode())){
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
    public IUserUnit getCurrentStation() {
        List<? extends IUserUnit> uus = this.getUserUnits();
        if (uus != null) {
            for (IUserUnit uu : uus) {
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

    public String getCurrentStationId(){
        return currentStationId;
    }

    @Override
    @JSONField(serialize = false)
    public String getCurrentUnitCode(){
        IUserUnit cs = getCurrentStation();
        return cs != null? cs.getUnitCode() : getUserInfo().getPrimaryUnit();
    }

    @Override
    public String getLoginIp() {
        return loginIp;
    }

    @Override
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    /**
     * 获取当前用户，当前机构的顶级机构，用于处理帐套
     * @return 最上层机构代码，根据用户的当前结构设置可能有变化
     */
    @Override
    @JSONField(serialize = false)
    public String getTopUnitCode(){
        IUserUnit cs = getCurrentStation();
        IUnitInfo unitInfo = cs == null ?
            CodeRepositoryUtil.getUnitInfoByCode(getUserInfo().getPrimaryUnit()):
            CodeRepositoryUtil.getUnitInfoByCode(cs.getUnitCode());
        if(unitInfo == null){
            return cs == null ? getUserInfo().getPrimaryUnit() : cs.getUnitCode();
        }
        int pos = unitInfo.getUnitPath().indexOf('/');
        return pos<1 ? unitInfo.getUnitPath() :
            unitInfo.getUnitPath().substring(0,pos);
    }

    @Override
    @JSONField(serialize = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.arrayAuths;
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return this.getUserInfo().getUserPin();
    }

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
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return this.getUserInfo().getLoginName();
    }

    @Override
    public boolean equals(Object other) {
       if(other instanceof CentitUserDetails)
           return this.getUserInfo().getUserCode().equals(
                   ((CentitUserDetails) other).getUserInfo().getUserCode());

       return false;
    }

    @Override
    public int hashCode() {
        return this.getUserInfo().getUserCode().hashCode();
    }

}
