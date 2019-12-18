package com.centit.framework.appclient;

import com.alibaba.fastjson.JSON;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class AppSession {

    public final static String SECURITY_CONTEXT_TOKENNAME = "accessToken";

    private String appLoginUrl;
    private String appServerUrl;
    private String userCode;
    private String password;

    private String accessToken;
    private Date   tokenCheckTime;
    private String csrfToken;
    private boolean needAuthenticated;

    private GenericObjectPool<CloseableHttpClient>  httpClientPool;

    public AppSession(String appServerUrl,boolean needAuthenticated,String userCode,String password){
        this.appServerUrl = appServerUrl;
        this.needAuthenticated = needAuthenticated;
        this.userCode = userCode;
        this.password = password;

        GenericObjectPoolConfig<CloseableHttpClient> config =
            new GenericObjectPoolConfig<>();

        httpClientPool =  new GenericObjectPool<>(
            new PooledHttpClientFactory(),
                config);
    }

    public AppSession(String appServerUrl,String userCode,String password){
        this(appServerUrl,true,userCode,password);
    }

    public AppSession(String appServerUrl){
        this(appServerUrl,false,null,null);
    }

    public AppSession(){
        this(null,false,null,null);
    }

    public boolean checkAccessToken(CloseableHttpClient httpclient)
            throws ObjectException, IOException{
        if(!needAuthenticated){
            return true;
        }
        boolean alive= this.tokenCheckTime != null && DatetimeOpt.currentUtilDate().before(
                DatetimeOpt.addMinutes(this.tokenCheckTime, 110));
        if(alive)
            return true;
        askAccessToken(httpclient);
        tokenCheckTime = DatetimeOpt.currentUtilDate();
        return true;
    }

    public void askAccessToken(CloseableHttpClient httpclient)
            throws IOException,ObjectException{
        Map<String,String> param = new HashMap<>();
        param.put("userCode", userCode);
        param.put("password", password);
        HttpExecutorContext httpContext = createHttpExecutorContext(httpclient);
        String jsonStr = HttpExecutor.formPost(httpContext,
            getAppLoginUrl() + "/mainframe/loginasclient", param);
        HttpReceiveJSON jsonData = HttpReceiveJSON.valueOfJson(jsonStr);
        if(jsonData==null || jsonData.getCode()!=0){
            throw new ObjectException(jsonData==null?"访问服务器失败":jsonData.getMessage());
        }
        accessToken = jsonData.getDataAsString(/*SecurityContextUtils.*/SECURITY_CONTEXT_TOKENNAME);
        HttpReceiveJSON csrfData = HttpReceiveJSON.valueOfJson(
            HttpExecutor.simpleGet(httpContext, getAppLoginUrl() + "/mainframe/csrf"));
        if(csrfData!=null){
            csrfToken = csrfData.getDataAsString("token");
        }
    }

    public String completeQueryUrl(String queryUrl){
        return queryUrl.indexOf("://") > 0? queryUrl : appServerUrl+queryUrl;
        /*return needAuthenticated
                ?baseUrl+
                    (queryUrl.indexOf('?')>=0 ? "&":"?")+
                    *//*SecurityContextUtils.*//*SECURITY_CONTEXT_TOKENNAME+
                    "="+accessToken
                :baseUrl;*/
    }

    public CloseableHttpClient allocHttpClient() throws Exception{
        return httpClientPool.borrowObject();
    }

    public HttpExecutorContext createHttpExecutorContext(CloseableHttpClient httpClient) {
        HttpExecutorContext httpExecutorContext = HttpExecutorContext.create(httpClient);
        if(StringUtils.isNotBlank(accessToken)) {
            httpExecutorContext.header("x-auth-token", accessToken);
        }
        if(StringUtils.isNotBlank(csrfToken)) {
            httpExecutorContext.header("x-csrf-token", csrfToken);
        }

        httpExecutorContext//.header("X-Requested-With", "XMLHttpRequest")
            .header("accept", "application/json");
        return httpExecutorContext;
    }

    public void releaseHttpClient(CloseableHttpClient httpClient){
        httpClientPool.returnObject(httpClient);
    }

    public String simpleGet(CloseableHttpClient httpClient,
                                         String httpGetUrl, Map<String,Object> queryParam)
        throws IOException {

        return  HttpExecutor.simpleGet(createHttpExecutorContext(httpClient),
                completeQueryUrl(httpGetUrl), queryParam);
    }

    public String simpleGet(CloseableHttpClient httpClient,
                                         String httpGetUrl)
        throws IOException {
        return  HttpExecutor.simpleGet(createHttpExecutorContext(httpClient),
                completeQueryUrl(httpGetUrl), "");
    }

    public HttpReceiveJSON getResponseData(CloseableHttpClient httpClient,
                                         String httpGetUrl, String queryParam)
        throws IOException {

        return HttpReceiveJSON.valueOfJson(
            HttpExecutor.simpleGet(createHttpExecutorContext(httpClient),
            completeQueryUrl(httpGetUrl), queryParam));
    }

    public HttpReceiveJSON getResponseData(CloseableHttpClient httpClient,
                                         String httpGetUrl, Map<String,Object> queryParam)
        throws IOException {

        return HttpReceiveJSON.valueOfJson(
            HttpExecutor.simpleGet(createHttpExecutorContext(httpClient),
                completeQueryUrl(httpGetUrl), queryParam));
    }

    public HttpReceiveJSON getResponseData(CloseableHttpClient httpClient,
                                         String httpGetUrl)
        throws IOException {

        return HttpReceiveJSON.valueOfJson(
            HttpExecutor.simpleGet(createHttpExecutorContext(httpClient),
                completeQueryUrl(httpGetUrl), ""));
    }

    public String formPost(CloseableHttpClient httpClient, String httpPostUrl,
                           Object formData , boolean asPut)
        throws IOException {
        return HttpExecutor.formPost(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPostUrl), formData, asPut);
    }

    public String formPost(CloseableHttpClient httpClient, String httpPostUrl,
                           Object formData)
        throws IOException {
        return HttpExecutor.formPost(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPostUrl), formData, false);
    }

    public String formPut(CloseableHttpClient httpClient, String httpPutUrl, Object formData)
        throws IOException {
        return HttpExecutor.formPut(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPutUrl), formData);
    }

    public String jsonPost(CloseableHttpClient httpClient, String httpPostUrl,
                               Object formData , boolean asPut)
        throws IOException {
        return HttpExecutor.jsonPost(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPostUrl), formData, asPut);
    }

    public String jsonPost(CloseableHttpClient httpClient, String httpPostUrl,
                               Object formData)
        throws IOException {
        return HttpExecutor.jsonPost(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPostUrl), formData, false);
    }

    public String jsonPut(CloseableHttpClient httpClient, String httpPutUrl, Object formData)
        throws IOException {
        String jsonString = null;
        if(formData != null){
            if( formData instanceof String){
                jsonString = (String) formData;
            }else{
                jsonString = JSON.toJSONString(formData);
            }
        }
        return HttpExecutor.jsonPut(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpPutUrl), jsonString);
    }

    public String doDelete(CloseableHttpClient httpClient, String httpDeleteUrl)
        throws IOException {
        return HttpExecutor.simpleDelete(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpDeleteUrl), "" );
    }

    public String doDelete(CloseableHttpClient httpClient, String httpDeleteUrl, Map<String,Object> queryParam)
        throws IOException {
        return HttpExecutor.simpleDelete(
            createHttpExecutorContext(httpClient),
            completeQueryUrl(httpDeleteUrl), queryParam);
    }

    public String getAppServerUrl() {
        return appServerUrl;
    }

    public void setAppServerUrl(String appServerUrl) {
        this.appServerUrl = appServerUrl;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNeedAuthenticated() {
        return needAuthenticated;
    }

    public void setNeedAuthenticated(boolean needAuthenticated) {
        this.needAuthenticated = needAuthenticated;
    }

    public String getAppLoginUrl() {
        if(StringUtils.isBlank(appLoginUrl)){
            return this.appServerUrl;
        }
        return appLoginUrl;
    }

    public void setAppLoginUrl(String appLoginUrl) {
        this.appLoginUrl = appLoginUrl;
    }
}
