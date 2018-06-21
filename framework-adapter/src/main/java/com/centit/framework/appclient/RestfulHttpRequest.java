package com.centit.framework.appclient;

import com.centit.framework.common.ResponseJSON;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;
@SuppressWarnings("unused")
public class RestfulHttpRequest {

    public static ResponseJSON getResponseData(String httpGetUrl) {
        CloseableHttpClient httpClient = HttpExecutor.createHttpClient();
        ResponseJSON resJson = getResponseData(httpClient,httpGetUrl);
        try {
            httpClient.close();
        } catch (IOException e) {
        }
        return resJson;
    }

    public static ResponseJSON getResponseData(CloseableHttpClient httpclient,String httpGetUrl) {
        ResponseJSON resJson=null;
        try {
            resJson = ResponseJSON.valueOfJson(
                    HttpExecutor.simpleGet(HttpExecutorContext.create(httpclient), httpGetUrl, "") );
        } catch (IOException e) {
        }
        return resJson;
    }

    public static <T> List<T>  getResponseObjectList(CloseableHttpClient httpClient,
                                               String httpGetUrl,Class<T> clazz ) {
        try {
            ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                    httpClient,httpGetUrl);

            if(resJson==null)
                return null;
            return resJson.getDataAsArray(clazz);

        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T  getResponseObject(CloseableHttpClient httpClient,
                                               String httpGetUrl,Class<T> clazz ) {
        try {
            ResponseJSON resJson = RestfulHttpRequest.getResponseData(
                    httpClient,httpGetUrl);

            if(resJson==null)
                return null;
            return resJson.getDataAsObject(clazz);

        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T>  getResponseObjectList(AppSession appSession,
                                                     String httpGetUrl,Class<T> clazz ) {

        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.getHttpClient();
            return getResponseObjectList(
                    httpClient,
                    appSession.completeQueryUrl(httpGetUrl),clazz);

        } catch (Exception e) {
            return null;
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }

    }

    public static <T> T  getResponseObject(AppSession appSession,
                                           String httpGetUrl,Class<T> clazz ) {

        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.getHttpClient();
            return getResponseObject(
                    httpClient,
                    appSession.completeQueryUrl(httpGetUrl),clazz);

        } catch (Exception e) {
            return null;
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }

    public static ResponseJSON  getResponseData (AppSession appSession,
                                                 String httpGetUrl) {

        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.getHttpClient();
            return getResponseData(
                    httpClient,
                    appSession.completeQueryUrl(httpGetUrl));

        } catch (Exception e) {
            return null;
        } finally {
            if(httpClient!=null)
                appSession.releaseHttpClient(httpClient);
        }
    }
}
