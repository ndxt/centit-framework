package com.centit.framework.system.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;

/**
 * UserInfo entity.
 * @author MyEclipse Persistence Tools
 */

public class CentitUserDetailsImpl
		extends UserInfo implements CentitUserDetails
{

    private static final long serialVersionUID = 1L;

    // role
    // private Date lastUpdateRoleTime;
    @JSONField(serialize = false)
    private List<GrantedAuthority> arrayAuths;
    
    private Map<String, String> userSettings;
    private Map<String, String> userOptList;
    
    public Map<String, String> getUserSettings() {
    	if(userSettings==null)
    		userSettings = new HashMap<String, String>();
        return userSettings;
    }

    public void putUserSettingsParams(String paramCode, String paramValue) {
        if(userSettings==null)
            userSettings = new HashMap<String, String>();
        userSettings.put(paramCode, paramValue);
    }
    public void setUserSettings(Map<String, String> userSettings) {
        this.userSettings = userSettings;
    }

    public Map<String, String> getUserOptList() {
    	if(userOptList==null)
    		userOptList = new HashMap<String, String>();
        return userOptList;
    }

    public void setUserOptList(Map<String, String> userOptList) {
        this.userOptList = userOptList;
    }

    public boolean checkOptPower(String optId, String optMethod){
        String s = userOptList.get(optId + "-" + optMethod);
        if (s == null) {
            return false;
        }
        return "T".equals(s); 
    }
    /**
     * default constructor
     */
    public CentitUserDetailsImpl(UserInfo user) {
        super(user.getUserCode(), user.getUserPin(),user.getUserType(),
                user.getIsValid(), user.getLoginName(), user.getUserName(), 
                user.getUserDesc(), user.getLoginTimes(), user.getActiveTime(),
                user.getLoginIp(), user.getAddrbookId());
        arrayAuths = null;
    }

    public CentitUserDetailsImpl() {
        arrayAuths = null;
    }

    /**
     * minimal constructor
     */
    public CentitUserDetailsImpl(String userCode, String userstate, String loginname, String username) {
        super(userCode, userstate, loginname, username);
        arrayAuths = null;
    }

    /**
     * full constructor
     */
    public CentitUserDetailsImpl(String userCode, String userpin,  String usertype,String userstate, String loginname, String username,
                                 String userdesc, Long logintimes, Date activeime, String loginip, Long addrbookid) {
        super(userCode, userpin,usertype, userstate, loginname, username, userdesc, logintimes, activeime, loginip, addrbookid);
        arrayAuths = null;
    }

    // Property accessors
    public void setAuthoritiesByRoles(List<RoleInfo> roles) {
        if (roles.size() < 1)
            return;
        arrayAuths = new ArrayList<GrantedAuthority>();
        for (RoleInfo role : roles) {
            arrayAuths.add(new SimpleGrantedAuthority(CentitSecurityMetadata.ROLE_PREFIX
                    + StringUtils.trim(role.getRoleCode())));
        }
        //排序便于后面比较
        Collections.sort(arrayAuths,
                new Comparator<GrantedAuthority>(){
                    public int compare(GrantedAuthority o1, GrantedAuthority o2) {
                        return o1.getAuthority().compareTo(o2.getAuthority());
                    }
                  }); 
        //lastUpdateRoleTime = new Date(System.currentTimeMillis());
    }

    /*private void loadAuthoritys() {
        lastUpdateRoleTime = new Date(System.currentTimeMillis());
        // load user roles and translate to Authorities;
        List<RoleInfo> roles = sysusrodao.getSysRolesByUsid(this.getUserCode());
        setAuthoritiesByRoles(roles);
    }
     */
    @Override
    @JSONField(serialize = false)
    public Collection<GrantedAuthority> getAuthorities() {
        /*if (arrayAuths == null || lastUpdateRoleTime == null
                || (new Date(System.currentTimeMillis())).getTime() - lastUpdateRoleTime.getTime() > 10 * 60 * 1000)
            loadAuthoritys();*/
        return arrayAuths;
    }
    
    /**
     * @return
     */
    @Override
    public List<String> getUserRoleCodes(){
    	List<String> userRoles = new ArrayList<String>();
    	if(arrayAuths==null)
    		return userRoles;
    	for(GrantedAuthority auth:arrayAuths)
    		userRoles.add(auth.getAuthority().substring(2));
    	return userRoles;
    }
    
    @Override
    public String getUsername() {
        return this.getLoginName();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return "T".equals(this.getIsValid());
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return "T".equals(this.getIsValid());
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return "T".equals(this.getIsValid());
    }
 
    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return this.getUserPin();
    }
    
    @Override
    public String getUserSettingValue(String paramCode){
        if(userSettings==null)
            return null;        
        return userSettings.get(paramCode);
    }
    
	public void setUserSettingValue(String paramCode, String paramValue) {
		if(userSettings==null)
			userSettings=new HashMap<>();
		userSettings.put(paramCode, paramValue);
	}
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("CentitUserDetail:");
        sb.append(super.toString());
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object other) {
       if(other instanceof CentitUserDetails)
           return this.getUserCode().equals(((CentitUserDetails) other).getUserCode());
       if(other instanceof UserInfo)
           return this.getUserCode().equals(((UserInfo) other).getUserCode());
       return false;            
    }

    @Override
    public int hashCode() {
        return this.getUserCode().hashCode();
    }

    @Override
    @JSONField(serialize = false)
	public Object getCredentials() {
		return this.getUserPin();
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
		return this.getLoginName();
	}
}
