package com.centit.framework.session;

import com.centit.framework.common.SysParametersUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.web.http.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http request/response 映射 session 策略
 * 同时支持cookie和header的session映射策略
 */
public class SmartHttpSessionStrategy implements MultiHttpSessionStrategy {

    private HttpSessionStrategy browser;//= new CookieHttpSessionStrategy();
    private HttpSessionStrategy api; // = new HeaderHttpSessionStrategy();

    public void setCookieFirst(boolean cookieFirst) {
        this.cookieFirst = cookieFirst;
    }

    private boolean cookieFirst;

    public SmartHttpSessionStrategy() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookiePath(SysParametersUtils.getStringValue("session.cookie.path"));
        CookieHttpSessionStrategy cookieHttpSessionStrategy = new CookieHttpSessionStrategy();
        cookieHttpSessionStrategy.setCookieSerializer(cookieSerializer);
        browser = cookieHttpSessionStrategy;
        api = new HeaderHttpSessionStrategy();
        cookieFirst = true;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        if(cookieFirst){
            String sessionId = browser.getRequestedSessionId(request);
            if(StringUtils.isNotBlank(sessionId))
                return sessionId;
            return api.getRequestedSessionId(request);
        }else {
            String sessionId = api.getRequestedSessionId(request);
            if(StringUtils.isNotBlank(sessionId))
                return sessionId;
            return browser.getRequestedSessionId(request);
        }
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
//        getStrategy(request).onNewSession(session, request, response);
        browser.onNewSession(session, request, response);
        api.onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
//        getStrategy(request).onInvalidateSession(request, response);
        browser.onInvalidateSession(request, response);
        api.onInvalidateSession(request, response);
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ((MultiHttpSessionStrategy) browser).wrapRequest(request, response);
        } catch (Exception e){
            return request;
        }
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ((MultiHttpSessionStrategy) browser).wrapResponse(request, response);
        } catch (Exception e){
            return response;
        }
    }
}
