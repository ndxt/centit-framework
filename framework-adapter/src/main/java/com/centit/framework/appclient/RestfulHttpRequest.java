package com.centit.framework.appclient;

import com.alibaba.fastjson.JSON;
import com.centit.support.network.UrlOptUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 如果一个方法只有一个请求，可以使用这个类中的方法，它对 请求的前后的资源申请和释放做了统一处理
 */
@SuppressWarnings("unused")
public class RestfulHttpRequest {

    private static Logger logger = LoggerFactory.getLogger(RestfulHttpRequest.class);

    public static HttpReceiveJSON getResponseData(AppSession appSession,
                                                 String httpGetUrl){
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.getResponseData(
                httpClient,
                appSession.completeQueryUrl(httpGetUrl));

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public static <T> List<T>  getResponseObjectList(AppSession appSession,
                                                     String httpGetUrl,Class<T> clazz){
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            HttpReceiveJSON resJson =  appSession.getResponseData(
                httpClient,httpGetUrl);
            if(resJson==null)
                return null;
            return resJson.getDataAsArray(clazz);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public static <T> T  getResponseObject(AppSession appSession,
                                           String httpGetUrl,Class<T> clazz ) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            HttpReceiveJSON resJson =  appSession.getResponseData(
                    httpClient,httpGetUrl);
            if(resJson==null)
                return null;
            return resJson.getDataAsObject(clazz);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public static HttpReceiveJSON getResponseData(AppSession appSession,
                          String httpGetUrl,Map<String,Object> queryParam){
        return getResponseData(appSession,
            UrlOptUtils.appendParamsToUrl( httpGetUrl,queryParam));
    }

    public static <T> List<T>  getResponseObjectList(AppSession appSession,
                String httpGetUrl, Map<String,Object> queryParam,Class<T> clazz){
       return getResponseObjectList(appSession,
           UrlOptUtils.appendParamsToUrl( httpGetUrl,queryParam),
           clazz);
    }

    public static <T> T  getResponseObject(AppSession appSession,
                        String httpGetUrl, Map<String,Object> queryParam,Class<T> clazz ) {
        return getResponseObject(appSession,
            UrlOptUtils.appendParamsToUrl( httpGetUrl,queryParam),
            clazz);
    }

    public static String jsonPost(AppSession appSession,
                                  String httpPostUrl, Object formData , boolean asPut) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.jsonPost(httpClient,httpPostUrl, formData, asPut);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public static String jsonPost(AppSession appSession,
                                 String httpPostUrl, Object formData) {
        return jsonPost(appSession, httpPostUrl,  formData, false);
    }

    public static String jsonPut(AppSession appSession,
                                        String httpPostUrl, Object formData) {
        String jsonString = null;
        if(formData != null){
            if( formData instanceof String){
                jsonString = (String) formData;
            }else{
                jsonString = JSON.toJSONString(formData);
            }
        }

        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.jsonPut(httpClient,httpPostUrl, jsonString);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }


    public static String  formPost (AppSession appSession,
                                    String httpPostUrl, Object formData , boolean asPut) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.formPost(httpClient,httpPostUrl, formData, asPut);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public static String  formPost(AppSession appSession,
                                   String httpPostUrl, Object formData) {
        return formPost(appSession, httpPostUrl,  formData, false);
    }

    public static String formPut(AppSession appSession,
                                 String httpPostUrl, Object formData) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.formPut(httpClient,httpPostUrl, formData);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }

    public String doDelete(AppSession appSession, String httpDeleteUrl, String queryParam){
        CloseableHttpClient httpClient = null;
        try {
            httpClient = appSession.allocHttpClient();
            appSession.checkAccessToken(httpClient);
            return appSession.doDelete(httpClient,httpDeleteUrl,queryParam);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            return null;
        } finally {
            if(httpClient!=null) {
                appSession.releaseHttpClient(httpClient);
            }
        }
    }
}
