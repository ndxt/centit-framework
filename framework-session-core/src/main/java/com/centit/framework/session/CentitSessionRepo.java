package com.centit.framework.session;

public interface CentitSessionRepo {
    void kickSessionByName(String loginName);

    void kickSessionByPrincipal(String principalName);
}
