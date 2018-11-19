package com.centit.framework.security.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by codefan on 16-11-22.
 *
 * 需要添加 session 持久化的功能
   org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
   org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
   org.springframework.session.security.web.authentication.SpringSessionRememberMeServices
 */
public class MemorySessionRegistryImpl implements SessionRegistry,
        ApplicationListener<SessionDestroyedEvent> {

    // ~ Instance fields
    // ================================================================================================

    protected final Logger logger = LoggerFactory.getLogger(MemorySessionRegistryImpl.class);

    /** <principal:Object,SessionIdSet> */
    private final ConcurrentMap<Object, Set<String>> principals = new ConcurrentHashMap<>();
    /** <sessionId:Object,SessionInformation> */
    private final Map<String, SessionInformation> sessionIds = new ConcurrentHashMap<>();

    // ~ Methods
    // ========================================================================================================

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<>(principals.keySet());
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal,
                                                   boolean includeExpiredSessions) {
        final Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            return Collections.emptyList();
        }

        List<SessionInformation> list = new ArrayList<>(
                sessionsUsedByPrincipal.size());

        for (String sessionId : sessionsUsedByPrincipal) {
            SessionInformation sessionInformation = getSessionInformation(sessionId);

            if (sessionInformation == null) {
                continue;
            }

            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }

        return list;
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        return sessionIds.get(sessionId);
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        removeSessionInformation(sessionId);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation info = getSessionInformation(sessionId);

        if (info != null) {
            info.refreshLastRequest();
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");

        if (logger.isDebugEnabled()) {
            logger.debug("Registering session " + sessionId + ", for principal "
                    + principal);
        }

        if (getSessionInformation(sessionId) != null) {
            //removeSessionInformation(sessionId);
            SessionInformation info = getSessionInformation(sessionId);
            if (info != null) {
                if(info.getPrincipal().equals(principal)){
                    info.refreshLastRequest();
                    return ;
                }else{
                    removeSessionInformation(sessionId);
                }
            }
        }

        sessionIds.put(sessionId,
                new SessionInformation(principal, sessionId, new Date()));

        Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
            Set<String> prevSessionsUsedByPrincipal = principals.putIfAbsent(principal,
                    sessionsUsedByPrincipal);
            if (prevSessionsUsedByPrincipal != null) {
                sessionsUsedByPrincipal = prevSessionsUsedByPrincipal;
            }
        }

        sessionsUsedByPrincipal.add(sessionId);

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '" + principal + "' : "
                    + sessionsUsedByPrincipal);
        }
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }

        if (logger.isTraceEnabled()) {
            logger.debug("Removing session " + sessionId
                    + " from set of registered sessions");
        }

        sessionIds.remove(sessionId);

        Set<String> sessionsUsedByPrincipal = principals.get(info.getPrincipal());

        if (sessionsUsedByPrincipal == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Removing session " + sessionId
                    + " from principal's set of registered sessions");
        }

        sessionsUsedByPrincipal.remove(sessionId);

        if (sessionsUsedByPrincipal.isEmpty()) {
            // No need to keep object in principals Map anymore
            if (logger.isDebugEnabled()) {
                logger.debug("Removing principal " + info.getPrincipal()
                        + " from registry");
            }
            principals.remove(info.getPrincipal());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '" + info.getPrincipal() + "' : "
                    + sessionsUsedByPrincipal);
        }
    }
}

