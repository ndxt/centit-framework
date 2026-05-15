package com.centit.framework.appclient;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class PooledHttpClientFactory implements PooledObjectFactory<CloseableHttpClient>{

    /**
     * 连接超时时间（毫秒）- 默认30秒
     */
    private static final int CONNECTION_TIMEOUT = 30000;

    /**
     * 读取超时时间（毫秒）- 默认600秒
     */
    private static final int SOCKET_TIMEOUT = 600000;

    /**
     * 从连接池获取连接的超时时间（毫秒）- 默认10秒
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 10000;

    @Override
    public PooledObject<CloseableHttpClient> makeObject() throws Exception {
        // 创建请求配置，设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .build();

        // 使用 HttpClients 创建带有超时配置的 HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();

        return new DefaultPooledObject<>(httpClient);
    }

    @Override
    public void destroyObject(PooledObject<CloseableHttpClient> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<CloseableHttpClient> p) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void activateObject(PooledObject<CloseableHttpClient> p) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void passivateObject(PooledObject<CloseableHttpClient> p) throws Exception {
        // TODO Auto-generated method stub
    }

}

