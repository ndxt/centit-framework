package com.centit.framework.session.redis;

import com.centit.framework.session.CentitSessionRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import java.util.Map;

public class CentitSessionRedisRepo implements CentitSessionRepo {

    private RedisIndexedSessionRepository sessionRepository;
    public CentitSessionRedisRepo(RedisIndexedSessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void kickSessionByName(String loginName, String escapeSessionId) {
        Map<String, ? extends Session> mapSession = this.sessionRepository.findByPrincipalName(loginName);
        if(mapSession!=null) {
            for (Map.Entry<String, ? extends Session> ent : mapSession.entrySet()) {
                if(!StringUtils.equals(escapeSessionId, ent.getValue().getId())) {
                    this.sessionRepository.deleteById(ent.getValue().getId());
                }
            }
        }
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
