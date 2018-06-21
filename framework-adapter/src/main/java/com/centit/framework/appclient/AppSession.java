package com.centit.framework.appclient;

import com.centit.framework.common.ObjectException;
import com.centit.framework.common.ResponseJSON;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class AppSession {

    public final static String SECURITY_CONTEXT_TOKENNAME = "accessToken";
    private String appServerUrl;
    private String userCode;
    private String password;

    private String accessToken;
    private Date    tokenCheckTime;
    private boolean needAuthenticated;

    private GenericObjectPool<CloseableHttpClient>  httpClientPool;

    public AppSession(String appServerUrl,boolean needAuthenticated,String userCode,String password){
        this.appServerUrl = appServerUrl;
        this.needAuthenticated = needAuthenticated;
        this.userCode = userCode;
        this.password = password;

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        httpClientPool =  new GenericObjectPool<>(new PooledHttpClientFactory(),
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
            throws ClientProtocolException, ObjectException, IOException{
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
            throws ClientProtocolException, IOException,ObjectException{
        Map<String,String> param = new HashMap<>();
        param.put("userCode", userCode);
        param.put("password", password);
        String jsonStr = HttpExecutor.formPost(HttpExecutorContext.create(httpclient), appServerUrl + "/system/mainframe/loginasclient", param);
        ResponseJSON jsonData = ResponseJSON.valueOfJson(jsonStr);
        if(jsonData==null || jsonData.getCode()!=0){
            throw new ObjectException(jsonData==null?"访问服务器失败":jsonData.getMessage());
        }
        accessToken = jsonData.getDataAsString(/*SecurityContextUtils.*/SECURITY_CONTEXT_TOKENNAME);
    }

    public String completeQueryUrl(String queryUrl){

        return needAuthenticated
                ?appServerUrl+queryUrl+
                    (queryUrl.indexOf('?')>=0 ? "&":"?")+
                    /*SecurityContextUtils.*/SECURITY_CONTEXT_TOKENNAME+
                    "="+accessToken
                :appServerUrl+queryUrl;
    }

    public CloseableHttpClient getHttpClient() throws Exception{
        return httpClientPool.borrowObject();
    }

    public void releaseHttpClient(CloseableHttpClient httpClient){
        httpClientPool.returnObject(httpClient);
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


}
