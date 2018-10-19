package com.centit.framework.staticsystem.security;

import com.alibaba.fastjson.annotation.JSONField;
import com.centit.framework.security.model.AbstractCentitUserDetails;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * FUserinfo entity.
 *
 * @author MyEclipse Persistence Tools
 */
// 系统用户信息表
public class StaticCentitUserDetails extends AbstractCentitUserDetails {
    private static final long serialVersionUID = 1L;
    public StaticCentitUserDetails(){

    }

    public StaticCentitUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    protected UserInfo userInfo;
    private List<RoleInfo> userRoles;
    private List<UserUnit> userUnits;

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

    public void setAuthoritiesByRoles(List<RoleInfo> roles) {
        setUserRoles(roles);
        makeUserAuthorities();
    }

    @Override
    public List<RoleInfo> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<RoleInfo> userRoles) {
        this.userRoles = userRoles;
    }

    public StaticCentitUserDetails addUserUnit(UserUnit uu) {
        if(userUnits==null) {
            userUnits = new ArrayList<>(4);
        }
        userUnits.add(uu);
        return this;
    }


    public void setUserUnits(List<UserUnit> userUnits) {
        this.userUnits = userUnits;
    }

    @Override
    public List<? extends UserUnit> getUserUnits() {
        return userUnits;
    }

}
