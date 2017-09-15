package com.centit.framework.appclient;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.impl.client.CloseableHttpClient;

import com.centit.support.network.HttpExecutor;

public class PooledHttpClientFactory implements PooledObjectFactory<CloseableHttpClient>{

    @Override
    public PooledObject<CloseableHttpClient> makeObject() throws Exception {
        CloseableHttpClient httpClient = HttpExecutor.createKeepSessionHttpClient();
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
