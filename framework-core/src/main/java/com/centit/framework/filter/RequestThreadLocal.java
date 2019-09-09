package com.centit.framework.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * 将HttpServletRequest请求与本地线程绑定，方便在非Controller层获取HttpServletRequest实例
 *
 * @author sx
 * 2014-10-14
 */
public abstract class RequestThreadLocal {
    private static ThreadLocal<HttpThreadWrapper> threadLocal = new ThreadLocal<>();

    public static void setHttpThreadWrapper(HttpThreadWrapper wrapper) {
        threadLocal.set(wrapper);
    }

    public static HttpThreadWrapper getHttpThreadWrapper() {
        return threadLocal.get();
    }

    public static HttpServletRequest getLocalThreadWrapperRequest(){
        HttpThreadWrapper localThread = threadLocal.get();
        if(localThread != null) {
            return localThread.getRequest();
        }
        return null;
    }
}
