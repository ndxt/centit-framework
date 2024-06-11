package com.centit.framework.session;

import org.springframework.session.Session;

public interface CentitSessionRepo {
    void kickSessionByName(String loginName);

    void kickSessionByPrincipal(String principalName);

    Session findById(String id);

}
