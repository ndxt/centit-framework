package com.centit.framework.security.model;

import org.springframework.security.core.session.SessionRegistry;

/**
 * Created by codefan on 16-11-22.
 */
public interface CentitSessionRegistry extends SessionRegistry {
    public CentitUserDetails  getCurrentUserDetails(String /**sessionId*/ accessToken);
}
