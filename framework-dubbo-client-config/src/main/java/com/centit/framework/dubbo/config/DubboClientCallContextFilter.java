package com.centit.framework.dubbo.config;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.filter.HttpThreadWrapper;
import com.centit.framework.filter.RequestThreadLocal;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 客户端调用远程服务之前设置CallContext信息
 */
public class DubboClientCallContextFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(DubboClientCallContextFilter.class);

    //todo:考虑是否需要在调用完成后清除ThreadLocal中的对象
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        //判断当前调用过程是服务端还是客户端
        boolean providerSide = RpcContext.getServiceContext().isProviderSide();
        if (!providerSide){
            //当前版本不支持获取RequestThreadLocal.getLocalThreadWrapperRequest()
            HttpThreadWrapper httpThreadWrapper = RequestThreadLocal.getHttpThreadWrapper();
            HttpServletRequest request = null;
            if (null != httpThreadWrapper){
                request = httpThreadWrapper.getRequest();
            }
            if (null == request){
                logger.warn("客户端未从RequestThreadLocal中获取到request对象...");
            }else {
               /* String requestJsonString = JSONObject.toJSONString(request);
                logger.info("requestJsonString字节数："+requestJsonString.getBytes().length);*/
                invocation.setAttachment("request", request);
            }
            return invoker.invoke(invocation);
        }


        if (providerSide){
            Map<String, String> attachments = invocation.getAttachments();
            HttpServletRequest request = (HttpServletRequest) invocation.getObjectAttachment("request");

            if (null == request){
                logger.warn("服务端未从attachments中获取到request对象...");
            }else {
                HttpThreadWrapper httpThreadWrapper = new HttpThreadWrapper(request,null);
                RequestThreadLocal.setHttpThreadWrapper(httpThreadWrapper);
                logger.debug("服务端从attachments中获取到request对象，并设置到RequestThreadLocal中");
            }
            return invoker.invoke(invocation);
        }
        return invoker.invoke(invocation);
    }
}
