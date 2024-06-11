package com.centit.framework.session.jdbc;

import com.centit.framework.session.CentitSessionRepo;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;

import java.util.Map;

public class CentitSessionJdbcRepo implements CentitSessionRepo {

    private FindByIndexNameSessionRepository sessionRepository;
    public CentitSessionJdbcRepo(FindByIndexNameSessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }
    @Override
    public void kickSessionByName(String loginName) {
        Map<String, ? extends Session> mapSession = this.sessionRepository.findByPrincipalName(loginName);
        if(mapSession!=null) {
            for (Map.Entry<String, ? extends Session> ent : mapSession.entrySet()) {
                this.sessionRepository.deleteById(ent.getValue().getId());
            }
        }
    }

    @Override
    public void kickSessionByPrincipal(String principalName) {
        kickSessionByName(principalName);
    }

    @Override
    public Session findById(String id) {
        return this.sessionRepository.findById(id);
    }
}
