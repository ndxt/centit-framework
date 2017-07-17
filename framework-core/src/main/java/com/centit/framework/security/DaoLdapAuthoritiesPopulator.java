package com.centit.framework.security;

import java.util.Collection;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import com.centit.framework.security.model.CentitUserDetailsService;

//@Component("daoLdapAuthoritiesPopulator")
public class DaoLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private CentitUserDetailsService userDetailsService;

    public void setUserDetailsService(CentitUserDetailsService uM) {
    	userDetailsService = uM;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(
            DirContextOperations userData, String username) {
        return userDetailsService.loadUserAuthorities(username);
    }
}
