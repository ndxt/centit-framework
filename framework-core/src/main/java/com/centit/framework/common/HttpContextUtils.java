package com.centit.framework.common;

/**
 * @author zfg
 */

import com.centit.framework.security.model.CentitUserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class HttpContextUtils {

    //用來存储RPC调用时的session相关信息
    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

    public static HttpSession getSession() {
        if (getRequest() == null) {
            return null;
        }
        HttpSession session = getRequest().getSession();
        return session;
    }

    /**
     * 获取sessionId
     *
     * @return
     */
    public static String getSessionId() {
        HttpSession session = getSession();
        if (null != session) {
            //从http session中获取
            return session.getId();
        } else {
            //从线程本地存储中获取，判断当前线程是否有数据
            Map<String, Object> data = HttpContextUtils.threadLocal.get();
            return null != data ? (String) data.get("sessionid") : null;
        }
    }

    private static CentitUserDetails getUserByThreadLocal() {
        //从线程本地存储中获取，判断当前线程是否有数据
        Map<String, Object> data = HttpContextUtils.threadLocal.get();
        return null != data ? (CentitUserDetails) data.get("userinfo") : null;
    }

    public static CentitUserDetails getCurrentUserInfo() {
        HttpSession session = getSession();
        if (null != session) {
            //从http session中获取
            CentitUserDetails centitUserDetails = WebOptUtils.innerGetUserDetail(session);
            if (centitUserDetails == null) {
                return getUserByThreadLocal();
            }
            return centitUserDetails;
        } else {
            return getUserByThreadLocal();
        }
    }

    public static String getTraceId() {
        Map<String, Object> data = HttpContextUtils.threadLocal.get();
        return null != data ? (String) data.get("traceid") : null;
    }

    public static void clear() {
        threadLocal.remove();
    }
}
