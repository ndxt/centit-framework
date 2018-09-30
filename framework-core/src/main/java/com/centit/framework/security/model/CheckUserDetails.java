package com.centit.framework.security.model;

public interface CheckUserDetails {
    boolean check(CentitUserDetails userDetails, Object token);
}
