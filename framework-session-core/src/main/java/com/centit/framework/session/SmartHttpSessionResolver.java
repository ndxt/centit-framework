package com.centit.framework.session;

import com.centit.framework.common.WebOptUtils;
import com.centit.support.algorithm.CollectionsOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Http request/response 映射 session 策略
 * 同时支持cookie和header的session映射策略
 */
public class SmartHttpSessionResolver implements HttpSessionIdResolver {

    private CookieHttpSessionIdResolver browser;//= new CookieHttpSessionStrategy();
    private HeaderHttpSessionIdResolver api; // = new HeaderHttpSessionStrategy();

    private boolean cookieFirst;
    private boolean addAccessToken;

    public List<String> resolveAccessTokenSessionId(HttpServletRequest request) {
        if(addAccessToken) {
            String sessionId = request.getParameter(WebOptUtils.SESSION_ID_TOKEN);
            if(StringUtils.isNotBlank(sessionId))
                return CollectionsOpt.createList(sessionId);
        }
        return CollectionsOpt.createList();
    }

    public SmartHttpSessionResolver(boolean cookieFirst, String cookiePath,boolean addAccessToken) {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookiePath(cookiePath);
        browser = new CookieHttpSessionIdResolver();
        browser.setCookieSerializer(cookieSerializer);
        api = new HeaderHttpSessionIdResolver(WebOptUtils.SESSION_ID_TOKEN);
        this.cookieFirst = cookieFirst;
        this.addAccessToken=addAccessToken;
    }

    /**
     * Resolve the session ids associated with the provided {@link HttpServletRequest}.
     * For example, the session id might come from a cookie or a request header.
     *
     * @param request the current request
     * @return the session ids
     */
    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        if(cookieFirst){
            List<String> sessionIds = browser.resolveSessionIds(request);
            if(sessionIds!=null && sessionIds.size()>0)
                return sessionIds;
            sessionIds = api.resolveSessionIds(request);
            if(sessionIds!=null && sessionIds.size()>0)
                return sessionIds;
            return resolveAccessTokenSessionId(request);
        }else {
            List<String> sessionIds = api.resolveSessionIds(request);
            if(sessionIds!=null && sessionIds.size()>0)
                return sessionIds;
            sessionIds = resolveAccessTokenSessionId(request);
            if(sessionIds!=null && sessionIds.size()>0)
                return sessionIds;
            return browser.resolveSessionIds(request);
        }
    }

    /**
     * Send the given session id to the client. This method is invoked when a new session
     * is created and should inform a client what the new session id is. For example, it
     * might create a new cookie with the session id in it or set an HTTP response header
     * with the value of the new session id.
     *
     * @param request   the current request
     * @param response  the current response
     * @param sessionId the session id
     */
    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        api.setSessionId(request, response, sessionId);
        browser.setSessionId(request, response, sessionId);
    }

    /**
     * Instruct the client to end the current session. This method is invoked when a
     * session is invalidated and should inform a client that the session id is no longer
     * valid. For example, it might remove a cookie with the session id in it or set an
     * HTTP response header with an empty value indicating to the client to no longer
     * submit that session id.
     *
     * @param request  the current request
     * @param response the current response
     */
    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        api.expireSession(request, response);
        browser.expireSession(request, response);
    }
}
