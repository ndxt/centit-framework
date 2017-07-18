package com.centit.framework.filter;

/**
 * 将HttpServletRequest请求与本地线程绑定，方便在非Controller层获取HttpServletRequest实例
 *
 * @author sx
 * 2014-10-14
 */
public class RequestThreadLocal extends ThreadLocal<HttpThreadWrapper> {
    private static ThreadLocal<HttpThreadWrapper> threadLocal = new ThreadLocal<>();

    private RequestThreadLocal() {
        super();
    }

    public static void setHttpThreadWrapper(HttpThreadWrapper wrapper) {
        threadLocal.set(wrapper);
    }

    public static HttpThreadWrapper getHttpThreadWrapper() {
        return threadLocal.get();
    }
}
