package com.centit.framework.security;

import com.centit.framework.security.model.JsonCentitUserDetails;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

public class AdUserDetailsContextMapper implements UserDetailsContextMapper {

    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService uM) {
        userDetailsService = uM;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx,
                                          String username,Collection<? extends GrantedAuthority> authority) {
        JsonCentitUserDetails ud = (JsonCentitUserDetails) userDetailsService.loadUserByUsername(username);
        if(authority!=null) {
            for(GrantedAuthority gr : authority) {
                ud.addAuthorities(gr);
            }
        }
        return ud;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {

    }


}
