package com.centit.framework.session;

import com.centit.framework.common.SysParametersUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.Session;
import org.springframework.session.web.http.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http request/response 映射 session 策略
 * 同时支持cookie和header的session映射策略
 */
public class SmartHttpSessionStrategy implements MultiHttpSessionStrategy {

    private HttpSessionStrategy browser = new CookieHttpSessionStrategy();

    private HttpSessionStrategy api = new HeaderHttpSessionStrategy();

    private RequestMatcher browserMatcher;

//    @Autowired
//    public SmartHttpSessionStrategy(ContentNegotiationStrategy contentNegotiationStrategy) {
//        this(new CookieHttpSessionStrategy(), new HeaderHttpSessionStrategy());
//        MediaTypeRequestMatcher matcher = new MediaTypeRequestMatcher(contentNegotiationStrategy,
//            Arrays.asList(MediaType.TEXT_HTML));
//        matcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
//
//        RequestHeaderRequestMatcher javascript = new RequestHeaderRequestMatcher("X-Requested-With");
//
//        this.browserMatcher = new OrRequestMatcher(Arrays.asList(matcher, javascript));
//    }

    public SmartHttpSessionStrategy() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookiePath(SysParametersUtils.getStringValue("session.cookie.path"));
        CookieHttpSessionStrategy cookieHttpSessionStrategy = new CookieHttpSessionStrategy();
        cookieHttpSessionStrategy.setCookieSerializer(cookieSerializer);
        browser = cookieHttpSessionStrategy;
    }

    public SmartHttpSessionStrategy(HttpSessionStrategy browser, HttpSessionStrategy api) {
        this.browser = browser;
        this.api = api;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        String sessionId = api.getRequestedSessionId(request);
        return StringUtils.isNotBlank(sessionId) ? sessionId : browser.getRequestedSessionId(request);
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

    private HttpSessionStrategy getStrategy(HttpServletRequest request) {
        return this.browserMatcher.matches(request) ? this.browser : this.api;
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
//        HttpSessionStrategy sessionStrategy = getStrategy(request);
//        return sessionStrategy instanceof MultiHttpSessionStrategy ?
//            ((MultiHttpSessionStrategy)sessionStrategy).wrapRequest(request, response) : request;
        try {
            return ((MultiHttpSessionStrategy) browser).wrapRequest(request, response);
        } catch (Exception e){
            return request;
        }
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
//        HttpSessionStrategy sessionStrategy = getStrategy(request);
//        return sessionStrategy instanceof MultiHttpSessionStrategy ?
//            ((MultiHttpSessionStrategy)sessionStrategy).wrapResponse(request, response) : response;
        try {
            return ((MultiHttpSessionStrategy) browser).wrapResponse(request, response);
        } catch (Exception e){
            return response;
        }
    }

    @Autowired(required = false)
    @Qualifier(value = "browserHttpSessionStrategy")
    public void setBrowser(HttpSessionStrategy httpSessionStrategy){
        browser = httpSessionStrategy;
    }

    @Autowired(required = false)
    @Qualifier(value = "apiHttpSessionStrategy")
    public void setApi(HttpSessionStrategy httpSessionStrategy){
        api = httpSessionStrategy;
    }
}
