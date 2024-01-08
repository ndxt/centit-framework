package com.centit.framework.dubbo.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.HttpContextUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.filter.HttpThreadWrapper;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.algorithm.UuidOpt;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端调用远程服务之前设置CallContext信息
 */
//@Activate(group = {Constants.PROVIDER_PROTOCOL, Constants.CONSUMER_PROTOCOL})
public class DubboClientCallContextFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(DubboClientCallContextFilter.class);

    //todo:考虑是否需要在调用完成后清除ThreadLocal中的对象
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //判断当前调用过程是服务端还是客户端
        boolean providerSide = false;
        if (null != RpcContext.getServiceContext().getUrl()) {
            providerSide = RpcContext.getServiceContext().isProviderSide();
        } else {
            return invoker.invoke(invocation);
        }
        if (!providerSide) {
            //当前版本不支持获取RequestThreadLocal.getLocalThreadWrapperRequest()
            HttpThreadWrapper httpThreadWrapper = RequestThreadLocal.getHttpThreadWrapper();
            HttpServletRequest request = null;
            if (null != httpThreadWrapper) {
                request = httpThreadWrapper.getRequest();
            }
            if (request == null) {
                //logger.warn("客户端未从RequestThreadLocal中获取到request对象...");
                String sessionId = invocation.getAttachment(WebOptUtils.REQUEST_ACCESS_TOKEN);
                logger.info("从Attachment中获取sessionId {}", sessionId);
            } else {
                logger.info("消费端调用开始");
                invocation.setAttachment(WebOptUtils.REQUEST_ACCESS_TOKEN, request.getRequestedSessionId());
                CentitUserDetails centitUserDetails = WebOptUtils.getCurrentUserDetails(request);
                if (null != centitUserDetails) {
                    invocation.setAttachment("userinfo", JSON.toJSONString(centitUserDetails));
                }
            }
            String traceId = invocation.getAttachment(WebOptUtils.CORRELATION_ID);
            if (StringUtils.isBlank(traceId)) {
                String uuidAsString22 = UuidOpt.getUuidAsString22();
                invocation.setAttachment(WebOptUtils.CORRELATION_ID, uuidAsString22);
            }
        }

        if (providerSide) {
            String sessionId = invocation.getAttachment(WebOptUtils.REQUEST_ACCESS_TOKEN);
            String userDetails = invocation.getAttachment("userinfo");
            String traceId = invocation.getAttachment(WebOptUtils.CORRELATION_ID);
            if (StringUtils.isNotBlank(sessionId)) {
                if (StringUtils.isBlank(traceId)) {
                    String uuidAsString22 = UuidOpt.getUuidAsString22();
                    invocation.setAttachment(WebOptUtils.CORRELATION_ID, uuidAsString22);
                }
            }
            if (StringUtils.isNotBlank(userDetails)) {
                logger.info("生产端调用开始");
                CentitUserDetails centitUserDetails = JSON.parseObject(JSONObject.parse(userDetails).toString(), CentitUserDetails.class);
                Map<String, Object> data = new HashMap<>();
                data.put(WebOptUtils.REQUEST_ACCESS_TOKEN, sessionId);
                data.put("userinfo", centitUserDetails);
                data.put(WebOptUtils.CORRELATION_ID, traceId);
                HttpContextUtils.threadLocal.set(data);
            }
        }
        return invoker.invoke(invocation);
    }
}
