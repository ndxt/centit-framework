package com.centit.framework.security.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

/**
 * Created by codefan on 2018-10-24.
 *
 * 需要添加 session 持久化的功能
 * 需要套维持两类Key - Value 对
 * 一、 token  - SessionInformation
 * 二、 userCode - token list
 */
public class RedisSessionRegistryImpl implements SessionRegistry,
        ApplicationListener<SessionDestroyedEvent> {

    // ~ Instance fields
    // ================================================================================================

    protected final Logger logger = LoggerFactory.getLogger(MemorySessionRegistryImpl.class);


    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        removeSessionInformation(sessionId);
    }

    /**
     * Obtains all the known principals in the <code>SessionRegistry</code>.
     *
     * @return each of the unique principals, which can then be presented to
     * {@link #getAllSessions(Object, boolean)}.
     */
    @Override
    public List<Object> getAllPrincipals() {
        return null;
    }

    /**
     * Obtains all the known sessions for the specified principal. Sessions that have been
     * destroyed are not returned. Sessions that have expired may be returned, depending
     * on the passed argument.
     *
     * @param principal              to locate sessions for (should never be <code>null</code>)
     * @param includeExpiredSessions if <code>true</code>, the returned sessions will also
     *                               include those that have expired for the principal
     * @return the matching sessions for this principal (should not return null).
     */
    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        return null;
    }

    /**
     * Obtains the session information for the specified <code>sessionId</code>. Even
     * expired sessions are returned (although destroyed sessions are never returned).
     *
     * @param sessionId to lookup (should never be <code>null</code>)
     * @return the session information, or <code>null</code> if not found
     */
    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return null;
    }

    /**
     * Updates the given <code>sessionId</code> so its last request time is equal to the
     * present date and time. Silently returns if the given <code>sessionId</code> cannot
     * be found or the session is marked to expire.
     *
     * @param sessionId for which to update the date and time of the last request (should
     *                  never be <code>null</code>)
     */
    @Override
    public void refreshLastRequest(String sessionId) {

    }

    /**
     * Registers a new session for the specified principal. The newly registered session
     * will not be marked for expiration.
     *
     * @param sessionId to associate with the principal (should never be <code>null</code>
     *                  )
     * @param principal to associate with the session (should never be <code>null</code>)
     */
    @Override
    public void registerNewSession(String sessionId, Object principal) {

    }

    /**
     * Deletes all the session information being maintained for the specified
     * <code>sessionId</code>. If the <code>sessionId</code> is not found, the method
     * gracefully returns.
     *
     * @param sessionId to delete information for (should never be <code>null</code>)
     */
    @Override
    public void removeSessionInformation(String sessionId) {

    }
}

